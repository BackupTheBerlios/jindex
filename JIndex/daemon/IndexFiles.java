package daemon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.jdesktop.jdic.filetypes.Association;
import org.jdesktop.jdic.filetypes.AssociationService;

import documents.AddressBookDocument;
import documents.ExcelDocument;
import documents.FileDocument;
import documents.GaimLogDocument;
import documents.ImageDocument;
import documents.JavaDocument;
import documents.MP3Document;
import documents.PDFDocument;

class IndexFiles extends Thread {
	// static Logger log = Logger.getLogger(IndexFiles.class);
	long numMillisecondsToSleep = 5000; // 5 seconds sleep

	private final static String HOME = System.getProperty("HOME");

	private static boolean updateindex = false;

	static IndexWriter writer = null;

	public void run() {
		updateindex = true;

		while (true) {
			System.out.println("Sleeping...");
			try {
				Thread.sleep(numMillisecondsToSleep);
				List files = JIndexDaemon.getFileFromQueue();
				if (files.size() > 0) {
					indexDocs(updateIndex(files));
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
		// try {
		if (HOME == null) {
			System.out.println("Needs to be stated with -DHOME=~/ or something");
			System.exit(-1);
		}
		if (args.length <= 0) {
			System.out.println("Needs to be executed with a argument\nThe argument should be a file or directory");
			System.exit(-1);
		}
		Date start = new Date();
		// indexDocs(writer, new File(HOME+"/mp3"));
		if (args[0].equals("updateindex"))
			updateindex = true;

		// indexDocs(new File(args[1]));
		try {
			indexDocs(new File(args[1]));
		} catch (IOException e) {
			e.printStackTrace();
		}

		// indexDocs(writer, new File(HOME+"/Documents"));
		// //indexDocs(writer, new File(HOME+"/.evolution/mail/local"));
		// indexDocs(writer, new
		// File(HOME+"/.evolution/addressbook/local"));
		// indexDocs(writer, new File(HOME+"/.gaim/logs"));
		// indexDocs(writer, new File(HOME + "/Music"));
		// indexDocs(writer, new File(HOME + "/DigitalCameraPictures"));

		Date end = new Date();
		System.out.print(end.getTime() - start.getTime());
		System.out.println(" total milliseconds");

	}

	public synchronized static void removeEntry(String filename) {

		try {
			IndexReader reader = IndexReader.open(HOME + "/index");
			try {
				System.out.println("Index contains : " + reader.numDocs() + " documents");
				System.out.println("Removing old entry: " + filename);
				int delcounter = reader.delete(new Term("path", filename));
				System.out.println("deleted " + delcounter + " documents");
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (IOException e) {
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
		
			if (updateindex)
				removeEntry(file.getAbsolutePath());

			//if (writer == null)
			try {
				writer = new IndexWriter(HOME + "/index", new StandardAnalyzer(), false);
			} catch(IOException e) {
				writer = new IndexWriter(HOME + "/index", new StandardAnalyzer(), true);
			}

				AssociationService assocService = new AssociationService();
				URL url = new URL(file.toURL().toString());
				Association assoc = assocService.getAssociationByContent(url);
				System.out.println("foud mime type: " + assoc.getMimeType());
				String mimetype = assoc.getMimeType();

				if (mimetype != null) {
					if (mimetype.equals("audio/mpeg")) {
						System.out.println("adding MP3 File" + file);
						writer.addDocument(MP3Document.Document(file));
					} else if (mimetype.equals("application/msword")) {
						writer.addDocument(ExcelDocument.Document(file, mimetype));
					} else if (StringUtils.contains(mimetype, "image/")) {
						writer.addDocument(ImageDocument.Document(file, mimetype));
					} else if (StringUtils.contains(mimetype, "application/pdf")) {
						writer.addDocument(PDFDocument.Document(file, mimetype));
					} else if (StringUtils.contains(mimetype, "text/x-java")) {
						writer.addDocument(JavaDocument.Document(file, mimetype));
					}

					else {
					 	 System.out.println("Skipping unknown file with file desc" + file);
						//writer.addDocument(FileDocument.Document(file));
					}
				} else {
					if (file.getName().equals("Inbox")) {
						// MBoxProcessor.ProcessMBoxFile(file, writer);
						// TestMain.indexMails(writer);
					} else if (file.getName().equals("addressbook.db")) {
						writer.addDocument(AddressBookDocument.Document(file));
					}

					else {
						if (file.getAbsolutePath().indexOf(".gaim/logs") > 0) {
							// System.out.println("adding gaim log file"
							// +
							// file);
							writer.addDocument(GaimLogDocument.Document(file));
						} else {
							// System.out.println("adding as normal file
							// with NO ile desc" + file);
							writer.addDocument(FileDocument.Document(file));
						}
					}
				}
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
							tmpfiles.add(files[i]);
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
}