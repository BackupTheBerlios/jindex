package daemon;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexWriter;

import utils.FileUtility;
import utils.LuceneUtility;
import documents.AddressBookDocument;
import documents.ApplicationDocument;
import documents.GaimLogDocument;
import documents.ImageDocument;
import documents.JavaDocument;
import documents.MP3Document;
import documents.TomboyDocument;
import documents.mbox.EvolutionMailDocument;
import documents.office.ExcelDocument;
import documents.office.OpenOfficeDocument;
import documents.office.PDFDocument;

class IndexFiles extends Thread {
	private static final Logger log = Logger.getLogger(IndexFiles.class);
	long numMillisecondsToSleep = 5000; // 5 seconds sleep

	static IndexWriter writer = null;

	static ArrayList allreadyIndexedFiles;

	public void run() {
		try {
			allreadyIndexedFiles = readObject();
			if (allreadyIndexedFiles == null)
				allreadyIndexedFiles = new ArrayList();
			else {
				System.out.println("We got old files...");
				Iterator ite = allreadyIndexedFiles.iterator();
				while(ite.hasNext()) {
					JFile obj = (JFile)ite.next();
					System.out.println("Old filename: "+obj.getFilename());
				}
				
			}
			try {
				while (true && !interrupted()) {
					try {
						Thread.sleep(numMillisecondsToSleep);
						List files = DirectoryMonitor.getFileFromQueue();
						if (files.size() > 0) {
							indexDocs(updateIndex(files));
						}
					} catch (InterruptedException e) {
						System.out.println("Shutting down...");
						break;
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			} finally {
				System.out.println("We write objects");
				writeObject(allreadyIndexedFiles);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	

	public synchronized static void indexDocs(File file) throws IOException {
		LinkedList list = new LinkedList();
		list.add(file);
		indexDocs(updateIndex(list));
	}

	public synchronized static void indexDocs(List filelist) throws IOException {
		// do not try to index files that cannot be read

		for (int j = 0; j < filelist.size(); j++) {
			File file = (File) filelist.get(j);

			log.debug("Getting mimetype for "+"file://"+file.getAbsolutePath());
			String mimetype = new FileUtility().getMimeType("file://"+file.getAbsolutePath());
			System.out.println("Mimetype: "+mimetype);
			if (mimetype != null) {
				if (mimetype.equals("audio/mpeg")) {
					LuceneUtility.addDocument(MP3Document.Document(file,  mimetype));
				} else if (mimetype.equals("application/msword")) {
					LuceneUtility.addDocument(ExcelDocument.Document(file, mimetype));

				} else if (mimetype.equals("application/vnd.ms-excel")) {
					LuceneUtility.addDocument(ExcelDocument.Document(file, mimetype));
					
				} else if (StringUtils.contains(mimetype, "image/")) {
					LuceneUtility.addDocument(ImageDocument.Document(file, mimetype));
					
				} else if (StringUtils.contains(mimetype, "application/pdf")) {
					LuceneUtility.addDocument(PDFDocument.getDocument(file, mimetype));
					
				} else if (StringUtils.contains(mimetype, "text/x-java")) {
					LuceneUtility.addDocument(JavaDocument.Document(file, mimetype));
					
				} else if(StringUtils.contains(mimetype, "application/vnd.sun.xml.writer")) {
					LuceneUtility.addDocument(OpenOfficeDocument.Document(file, mimetype));
				}

				if (StringUtils.contains(file.getAbsolutePath(),"/.evolution/mail/local")) {
					EvolutionMailDocument.indexMails(file);
				} else if (file.getName().equals("addressbook.db")) {
//					LuceneUtility.addDocument(AddressBookDocument.Document(file));
					AddressBookDocument abook = new AddressBookDocument();
					abook.run(file.getAbsolutePath());
				}else if (file.getName().endsWith("desktop")) {
					LuceneUtility.addDocument(ApplicationDocument.Document(file));
				}

				else if (file.getAbsolutePath().indexOf(".gaim/logs") > 0) {
					// System.out.println("adding gaim log file"
					// +
					// file);
					LuceneUtility.addDocument(GaimLogDocument.Document(file));
				} else if (file.getAbsolutePath().indexOf("/.tomboy/") > 0) {
					LuceneUtility.addDocument(TomboyDocument.Document(file));
				} else {
					// System.out.println("adding as normal file
					// with NO ile desc" + file);
					// writer.addDocument(FileDocument.Document(file));
				}
			} else {
				System.out.println("Skipping unknown file with file desc" + file);
				// writer.addDocument(FileDocument.Document(file));
			}
			// }
			if (writer != null) {
				writer.close();
			}

		}
	}

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
					JFile jfile = new JFile(file.getAbsolutePath(), file.lastModified());
					if (isFileUpdateAccordingToList(jfile))
						completefileslist.add(file);
					allreadyIndexedFiles.add(jfile);
				}

			}
		}
		return completefileslist;
	}

	/**
	 * Is file in list, if so remove it
	 * 
	 * @param file
	 * @return
	 */
	public static boolean isFileUpdateAccordingToList(JFile file) {
		for (int i = 0; i < allreadyIndexedFiles.size(); i++) {
			JFile tmp = (JFile) allreadyIndexedFiles.get(i);
			if (tmp.getFilename().equals(file.getFilename())) {
				allreadyIndexedFiles.remove(i);
				if (tmp.getLastmodified() != file.getLastmodified()) {
					System.out.println("File was modified: " + tmp.getLastmodified() + " != " + file.getLastmodified());
					return true;
				} 
				return false;
			}
		}
		return true;
	}

	public static void writeObject(ArrayList list) throws IOException {
		FileOutputStream out = new FileOutputStream("filelist");
		ObjectOutputStream s = new ObjectOutputStream(out);
		s.writeObject(list);
		s.flush();
	}

	public static ArrayList readObject() throws ClassNotFoundException, IOException {
		FileInputStream in;
		try {
			in = new FileInputStream("filelist");
			if (in == null)
				return null;
			ObjectInputStream s = new ObjectInputStream(in);
			if (s == null)
				return null;

			return (ArrayList) s.readObject();
		} catch (FileNotFoundException e) {
			return null;
		}

	}

}