package daemon;
/* Java FAM - 
.
 */

/* $Id$ */

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

// FAM imports
import com.arosii.io.fam.*;

import daemon.JIndexDaemon;

/**
 * DirectoryMonitor example
 *
 * @author Lars Pedersen, <a href="mailto:lp@arosii.dk">lp@arosii.dk</a>
 */
public class DirectoryMonitor implements Runnable {
    static List filequeue = new LinkedList();
	private volatile Thread thread = null;
	private static final int sleepInterval = 500;

	private String path = null;

	private FAMConnection fam = null;


	private Map monitorlist = null;
	 

	public static List updateIndex(List filelist) {
		List completefileslist = new LinkedList();
		for (int j = 0; j < filelist.size(); j++) {
//			System.out.println(filelist.get(j).getClass());
			File file = (File) filelist.get(j);
			if (file.canRead()) {
				if (file.isDirectory()) {
					String[] files = file.list();
					if (files != null) {
						LinkedList tmpfiles = new LinkedList();
						for (int i = 0; i < files.length; i++) {
							tmpfiles.add(new File(files[i]));
						}
						completefileslist.addAll(updateIndex(tmpfiles));
					}
				} else {
					completefileslist.add(file);
				}

			}
		}
		return completefileslist;
	}
	
	/**
	 *
	 */
	public DirectoryMonitor(String path) throws IOException {
		new IndexFiles().start();
		File directory = new File(path);
		if (!directory.exists())
			throw new FileNotFoundException(path);
		
		if (!directory.isDirectory())
			throw new IOException(path + ": not a directory");

		this.path = directory.getCanonicalPath();
		monitorlist  = new IdentityHashMap();
	}

	/**
	 *
	 */
	public void start() {
		fam = FAM.open();
		FAMRequest famreq = fam.monitorDirectory(path,path);
		monitorlist.put(path,famreq);
		
		thread = new Thread(this);
		thread.start();
	}

	/**
	 *
	 */
	public synchronized void stop() {
		Set set = monitorlist.keySet();
		Iterator ite = set.iterator();
		while(ite.hasNext()) {
			String watchpath  = (String) ite.next();
			System.out.println("Shutting down watch for: "+watchpath);
			FAMRequest famreq =(FAMRequest) monitorlist.get(watchpath);
			//FAMRequest famreq = (FAMRequest) ite.next();
			famreq.cancelMonitor();
		}
		fam.close();
		
		Thread moribund = thread;
		thread = null;
		moribund.interrupt();
	}

	/**
	 *
	 */
	public void run() {

		while (thread == Thread.currentThread()) {

			boolean eventPending = fam.pending();
			if (!eventPending) {
				try {
					thread.sleep(sleepInterval);
				} catch (InterruptedException e) {}
				
				continue;
			}

			FAMEvent event = fam.nextEvent();
			if (event == null) {
				continue;
			}
			File f = new File(event.getFilename());
			if(!f.exists())
				f = new File(event.getUserdata()+"/"+event.getFilename());
			
			if(event.getCode() == FAM.Changed) {
				// write event, used for files not directories since creating a directory
				// doesnt fire this code..
				System.out.println("Write: "+f.getAbsolutePath());
				appendToQueue(f.getAbsolutePath());
//				System.out.println("Received event: " + event.getCode());
//				System.out.println("Received event: " + event.getFilename());
			}
			if(event.getCode() == FAM.Deleted) {
				// delete event'
				System.out.println("Delete.: "+f.getAbsolutePath());
				// try to remove it as a dir, might not work if is a file
				removeDirectoryToMonitor(f.getAbsolutePath());
//				System.out.println("Received event: " + event.getCode());
//				System.out.println("Received event: " + event.getFilename());
				
			}
			if(event.getCode() == FAM.Created) {
				// called when ever a files is created, should be used for 
				// directories. 
				System.out.println("Make dir: "+f.getAbsolutePath());
				if(f.isDirectory()) {
					addDirectoryToMonitor(f.getAbsolutePath());
				}
				
//				
//				System.out.println(".."+FAM.Acknowledge);
//				System.out.println(".."+FAM.Changed);
//				System.out.println(".."+FAM.Created);
//				System.out.println(".."+FAM.Deleted);
//				System.out.println(".."+FAM.EndExist);
//				System.out.println(".."+FAM.Exists);
//				System.out.println(".."+FAM.Moved);
//				System.out.println(".."+FAM.StartExecuting);
//				System.out.println(".."+FAM.StopExecuting);
				
			}
			if(event.getCode() == FAM.Exists) {
				// check for sub dirs and create listener...
//				System.out.println("FAM.Exists: "+f.getAbsolutePath());
				if(f.isDirectory() &&  !f.getAbsolutePath().equals(path)) {
					addDirectoryToMonitor(f.getAbsolutePath());
					
				}
			}
		}
	}

	public boolean addDirectoryToMonitor(String path) {
		Iterator ite = monitorlist.keySet().iterator();
		while(ite.hasNext()) {
			String name = (String) ite.next();
			if(name.equals(path))
				return false;
		}
		if (!monitorlist.containsKey(path)) {
			FAMRequest request = fam.monitorDirectory(path, path);
			monitorlist.put(path, request);
			System.out.println("Directory added: "+path);
			return true;
		}
		System.out.println("Directory skipped: "+path);
		return false;

	}
	public boolean removeDirectoryToMonitor(String path) {
		System.out.println("Delete dir: "+path);
		Iterator ite = monitorlist.keySet().iterator();
		while(ite.hasNext()) {
			String name = (String) ite.next();
			if(name.equals(path)) {
				FAMRequest req  =(FAMRequest) monitorlist.get(name);
				req.cancelMonitor();
				return true;
			}
		}
		return true;
	}

	/**
	 * 
	 */
	public static void main(String[] args) {

		if (args.length == 0) {
			System.err.println("Usage: DirectoryMonitor <directory>");
			System.exit(0);
		}

		try {
			DirectoryMonitor mon = new DirectoryMonitor(args[0]);
			mon.start();
			
			boolean loop = true;
			while (loop) {
				System.out.println("");
				System.out.println("Enter 'q' to Quit");
				
				try {
					int c = System.in.read();
					
					switch(c) {
					case 'q':
						loop = false;
						break;
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			mon.stop();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	  public static synchronized void appendToQueue(String inputLine) {
  		File appendfile = new File(inputLine);
  		Iterator ite = filequeue.iterator();
  		boolean added = false;
  		while(ite.hasNext()) {
  			File file = (File) ite.next();
  			if(file.getAbsoluteFile().equals(appendfile.getAbsoluteFile()))
  					added = true;
  		}
      if(!added) {
      		System.out.println("Adding file '"+inputLine+"' to appendQueue");
      		filequeue.add(appendfile);
      }
  }

  public static synchronized List getFileFromQueue() {
      List value = new LinkedList();
      value.addAll(filequeue);
      for (int i = 0; i < value.size(); i++) {
			System.out.println("Processing file " + value.get(i));
		}
      
      filequeue.clear();
      return value;
  }
}
