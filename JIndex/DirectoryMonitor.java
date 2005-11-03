/* Java FAM - 
.
 */

/* $Id$ */

import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

// FAM imports
import com.arosii.io.fam.*;

/**
 * DirectoryMonitor example
 *
 * @author Lars Pedersen, <a href="mailto:lp@arosii.dk">lp@arosii.dk</a>
 */
public class DirectoryMonitor implements Runnable {

	private volatile Thread thread = null;
	private static final int sleepInterval = 500;

	private String path = null;

	private FAMConnection fam = null;
	private FAMRequest famreq = null;

	

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
		File directory = new File(path);
		if (!directory.exists())
			throw new FileNotFoundException(path);
		
		if (!directory.isDirectory())
			throw new IOException(path + ": not a directory");

		this.path = directory.getCanonicalPath();
	}

	/**
	 *
	 */
	public void start() {

		fam = FAM.open();
		List tmp = new LinkedList();
		tmp.add(new File(path));
		List tmp1 = updateIndex(tmp);
		Iterator ite = tmp1.iterator();
		while(ite.hasNext()) {
			File f = (File) ite.next();
			fam.monitorFile(f.getAbsolutePath());	
		}
		//famreq = fam.monitorDirectory(path, null);
		
		thread = new Thread(this);
		thread.start();
	}

	/**
	 *
	 */
	public synchronized void stop() {

		famreq.cancelMonitor();
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
			
			System.out.println("Received event: " + event.getCode());
		}
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

}
