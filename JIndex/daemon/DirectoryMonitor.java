package daemon;

import java.io.File;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import com.arosii.io.fam.FAM;
import com.arosii.io.fam.FAMConnection;
import com.arosii.io.fam.FAMEvent;
import com.arosii.io.fam.FAMRequest;

import daemon.config.ConfigReader;

/**
 * 
 * Based on code from Lars Pedersen, <a href="mailto:lp@arosii.dk">lp@arosii.dk</a>
 */
public class DirectoryMonitor implements Runnable {
	// public class DirectoryMonitor extends Thread {
	static Logger log = Logger.getLogger(DirectoryMonitor.class);

	static List<File> filequeue = new LinkedList<File>();

	private volatile static Thread thread = null;

	private static final int sleepInterval = 1500;

	private String path = null;

	private static FAMConnection fam = null;

	private static Map<String, FAMRequest> monitorlist = null;

	private static IndexFiles indexThread;

	public static List<File> updateIndex(List<File> filelist) {
		List<File> completefileslist = new LinkedList<File>();
		for (int j = 0; j < filelist.size(); j++) {
			File file = filelist.get(j);
			if (file.canRead()) {
				if (file.isDirectory()) {
					String[] files = file.list();
					if (files != null) {
						LinkedList<File> tmpfiles = new LinkedList<File>();
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

	public DirectoryMonitor() {
		indexThread = new IndexFiles();
		indexThread.start();
		// File directory = new File(path);
		// if (!directory.exists())
		// throw new FileNotFoundException(path);
		//
		// if (!directory.isDirectory())
		// throw new IOException(path + ": not a directory");

		// this.path = directory.getCanonicalPath();
		monitorlist = new IdentityHashMap<String, FAMRequest>();
	}

	public void start() {
		fam = FAM.open();

		FAMRequest famreq = null;
		ArrayList files = ConfigReader.getWatches();
		Iterator ite = files.iterator();
		while (ite.hasNext()) {
			Watch w = (Watch) ite.next();
			log.debug("Read " + w.getFilename() + " from config file");
			File file = new File(w.getFilename());
			if (file.exists()) {
				if (file.isFile()) {
					log.debug("Added file monitor for file: '"
							+ file.getAbsolutePath() + "'");
					famreq = fam.monitorFile(file.getAbsolutePath(), file
							.getAbsolutePath());
				} else if (file.isDirectory()) {
					log.debug("Added Directory monitor for dir: '"
							+ file.getAbsolutePath() + "'");
					famreq = fam.monitorDirectory(file.getAbsolutePath(), file
							.getAbsolutePath());
				} else {
					log.debug("Error does file exsists ? " + file.exists());
				}
				monitorlist.put(file.getAbsolutePath(), famreq);
			}
		}

		// FAMRequest famreq = fam.monitorDirectory(path,path);
		// monitorlist.put(path,famreq);

		thread = new Thread(this);
		thread.start();
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

			// + "'");
			if (event.getCode() == FAM.Changed) {
				appendToQueue(f.getAbsolutePath());
			}

			if (event.getCode() == FAM.Deleted) {
				// delete event'
				// try to remove it as a dir, might not work if is a file
				boolean success = removeDirectoryToMonitor(f.getAbsolutePath());
				log.debug("Deleted '" + f.getAbsolutePath() + " with success: "
						+ success);

			}
			if (event.getCode() == FAM.Created) {
				// called when ever a files is created, should be used for
				// directories.
				log.debug("Make dir: " + f.getAbsolutePath());
				if (f.isDirectory()) {
					addDirectoryToMonitor(f.getAbsolutePath());
				}

				//				
				// log.debug(".."+FAM.Acknowledge);
				// log.debug(".."+FAM.Changed);
				// log.debug(".."+FAM.Created);
				// log.debug(".."+FAM.Deleted);
				// log.debug(".."+FAM.EndExist);
				// log.debug(".."+FAM.Exists);
				// log.debug(".."+FAM.Moved);
				// log.debug(".."+FAM.StartExecuting);
				// log.debug(".."+FAM.StopExecuting);

			}
			if (event.getCode() == FAM.Exists) {
				// check for sub dirs and create listener...
				if (f.isDirectory() && !f.getAbsolutePath().equals(path)) {
					addDirectoryToMonitor(f.getAbsolutePath());
				}
				log.debug(f.getAbsolutePath());
				appendToQueue(f.getAbsolutePath());
			}
			if (event.getCode() == FAM.EndExist) {
				// log.debug("FAM.EndExist");
			}
		}
	}

	public boolean addDirectoryToMonitor(String path) {
		Iterator<String> ite = monitorlist.keySet().iterator();
		while (ite.hasNext()) {
			String name = ite.next();
			if (name.equals(path))
				return false;
		}
		if (!monitorlist.containsKey(path)) {
			FAMRequest request = fam.monitorDirectory(path, path);
			monitorlist.put(path, request);
			log.debug("Directory added: " + path);
			return true;
		}
		log.debug("Directory skipped: " + path);
		return false;

	}

	public boolean removeDirectoryToMonitor(String path) {
		log.debug("Delete dir: " + path);
		Iterator<String> ite = monitorlist.keySet().iterator();
		while (ite.hasNext()) {
			String name = ite.next();
			if (name.equals(path)) {
				FAMRequest req = monitorlist.get(name);
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
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void start() {
				setPriority(8);
				setDaemon(true);

				Set<String> set = monitorlist.keySet();
				Iterator<String> ite = set.iterator();
				while (ite.hasNext()) {
					String watchpath = ite.next();
					log.debug("Shutting down watch for: " + watchpath);
					FAMRequest famreq = monitorlist.get(watchpath);
					famreq.cancelMonitor();
				}
				fam.close();

				indexThread.interrupt();
				Thread moribund = thread;
				thread = null;
				moribund.interrupt();
				log.debug("Closing down indexer..");
				log.debug("Added shutdownhook in start");
				System.out.flush();
			}
		});
		try {
			DirectoryMonitor mon;
			try {
				mon = new DirectoryMonitor();
				mon.start();
				while (true) {
					Thread.sleep(10000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} finally {
			log.debug("Closing down indexer..");
		}
	}

	public static synchronized void appendToQueue(String inputLine) {
		File appendfile = new File(inputLine);
		Iterator<File> ite = filequeue.iterator();
		boolean added = false;
		while (ite.hasNext()) {
			File file = ite.next();
			if (file.getAbsolutePath().equals(appendfile.getAbsolutePath())) {
				added = true;
			}
		}
		if (!added) {
			addTofileQueue(appendfile);
		}
	}
	private synchronized static void addTofileQueue(File file) {
		filequeue.add(file);
	}
	public static synchronized List<File> getFileFromQueue() {
		List<File> value = new LinkedList<File>();
		value.addAll(filequeue);
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
