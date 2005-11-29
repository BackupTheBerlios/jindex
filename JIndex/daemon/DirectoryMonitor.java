package daemon;

/* Java FAM - 
 .
 */

/* $Id$ */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import utils.FileUtility;

import com.arosii.io.fam.FAM;
import com.arosii.io.fam.FAMConnection;
import com.arosii.io.fam.FAMEvent;
import com.arosii.io.fam.FAMRequest;

import documents.mbox.MBoxProcessor;

/**
 * DirectoryMonitor example
 * 
 * Based on code from Lars Pedersen, <a href="mailto:lp@arosii.dk">lp@arosii.dk</a>
 */
public class DirectoryMonitor implements Runnable {
	static List filequeue = new LinkedList();

	private volatile Thread thread = null;

	private static final int sleepInterval = 500;

	private String path = null;

	private FAMConnection fam = null;

	private Map monitorlist = null;

	private IndexFiles indexThread;


	public static List updateIndex(List filelist) {
		List completefileslist = new LinkedList();
		for (int j = 0; j < filelist.size(); j++) {
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

	public DirectoryMonitor(String path) throws IOException {
		indexThread = new IndexFiles();
		indexThread.start();
		File directory = new File(path);
		if (!directory.exists())
			throw new FileNotFoundException(path);

		if (!directory.isDirectory())
			throw new IOException(path + ": not a directory");

		this.path = directory.getCanonicalPath();
		monitorlist = new IdentityHashMap();
	}

	public void start() {
		fam = FAM.open();

		FAMRequest famreq = null;
		ArrayList files = ConfigReader.getWatches();
		Iterator ite = files.iterator();
		while (ite.hasNext()) {
			Watch w = (Watch) ite.next();
			System.out.println("Read "+w.getFilename()+" from config file");
			File file = new File(w.getFilename());
			if (file.exists()) {
				if (file.isFile()) {
					System.out.println("Added file monitor for file '"+file.getAbsolutePath()+"'");
					famreq = fam.monitorFile(file.getAbsolutePath(), file.getAbsolutePath());
				}
				else if (file.isDirectory())
					famreq = fam.monitorDirectory(file.getAbsolutePath(), file.getAbsolutePath());
				else 
					System.out.println("Error does file exsists ? "+file.exists());
				monitorlist.put(file.getAbsolutePath(), famreq);
			}
		}

		// FAMRequest famreq = fam.monitorDirectory(path,path);
		// monitorlist.put(path,famreq);

		thread = new Thread(this);
		thread.start();
	}

	public synchronized void stop() {
		Set set = monitorlist.keySet();
		Iterator ite = set.iterator();
		while (ite.hasNext()) {
			String watchpath = (String) ite.next();
			System.out.println("Shutting down watch for: " + watchpath);
			FAMRequest famreq = (FAMRequest) monitorlist.get(watchpath);
			famreq.cancelMonitor();
		}
		fam.close();

		indexThread.interrupt();
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
					Thread.sleep(sleepInterval);
				} catch (InterruptedException e) {
				}
				continue;
			}

			FAMEvent event = fam.nextEvent();
			if (event == null) {
				continue;
			}
			File f = new File(event.getFilename());
			if (!f.exists())
				f = new File(event.getUserdata() + "/" + event.getFilename());

			//System.out.println("Got event '" + codeToString(event.getCode()) + "'");
			if (event.getCode() == FAM.Changed) {
				System.out.println("Write: " + f.getAbsolutePath());
				appendToQueue(f.getAbsolutePath());
			}

			if (event.getCode() == FAM.Deleted) {
				// delete event'
				System.out.println("Delete: " + f.getAbsolutePath());
				// try to remove it as a dir, might not work if is a file
				boolean success = removeDirectoryToMonitor(f.getAbsolutePath());
				System.out.println("Deleted with success: " + success);

			}
			if (event.getCode() == FAM.Created) {
				// called when ever a files is created, should be used for
				// directories.
				System.out.println("Make dir: " + f.getAbsolutePath());
				if (f.isDirectory()) {
					addDirectoryToMonitor(f.getAbsolutePath());
				}

				//				
				// System.out.println(".."+FAM.Acknowledge);
				// System.out.println(".."+FAM.Changed);
				// System.out.println(".."+FAM.Created);
				// System.out.println(".."+FAM.Deleted);
				// System.out.println(".."+FAM.EndExist);
				// System.out.println(".."+FAM.Exists);
				// System.out.println(".."+FAM.Moved);
				// System.out.println(".."+FAM.StartExecuting);
				// System.out.println(".."+FAM.StopExecuting);

			}
			if (event.getCode() == FAM.Exists) {
				// check for sub dirs and create listener...
				if (f.isDirectory() && !f.getAbsolutePath().equals(path)) {
					addDirectoryToMonitor(f.getAbsolutePath());
				}
				appendToQueue(f.getAbsolutePath());
			}
			if (event.getCode() == FAM.EndExist) {
				// System.out.println("FAM.EndExist");
			}
		}
	}

	public boolean addDirectoryToMonitor(String path) {
		Iterator ite = monitorlist.keySet().iterator();
		while (ite.hasNext()) {
			String name = (String) ite.next();
			if (name.equals(path))
				return false;
		}
		if (!monitorlist.containsKey(path)) {
			FAMRequest request = fam.monitorDirectory(path, path);
			monitorlist.put(path, request);
			System.out.println("Directory added: " + path);
			return true;
		}
		System.out.println("Directory skipped: " + path);
		return false;

	}

	public boolean removeDirectoryToMonitor(String path) {
		System.out.println("Delete dir: " + path);
		Iterator ite = monitorlist.keySet().iterator();
		while (ite.hasNext()) {
			String name = (String) ite.next();
			if (name.equals(path)) {
				FAMRequest req = (FAMRequest) monitorlist.get(name);
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

					switch (c) {
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
		while (ite.hasNext()) {
			File file = (File) ite.next();
			if (file.getAbsoluteFile().equals(appendfile.getAbsoluteFile()))
				added = true;
		}
		if (!added) {
			System.out.println("Adding file '" + inputLine + "' to appendQueue");
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

	public String codeToString(int code) {

		if (code == FAM.Acknowledge)
			return "Acknowledge";

		if (code == FAM.Changed)
			return "Changed";

		if (code == FAM.Changed)
			return "Changed";

		if (code == FAM.Created)
			return "Created";

		if (code == FAM.Deleted)
			return "Deleted";

		if (code == FAM.EndExist)
			return "EndExist";

		if (code == FAM.Exists)
			return "Exists";

		if (code == FAM.Moved)
			return "Moved";

		if (code == FAM.StartExecuting)
			return "StartExecuting";

		if (code == FAM.StopExecuting)
			return "StopExecuting";

		return "unknown event (" + code + ")";
	}
}
