import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

import metadata.FormatDescription;
import metadata.FormatIdentification;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicException;
import net.sf.jmimemagic.MagicMatch;
import net.sf.jmimemagic.MagicMatchNotFoundException;
import net.sf.jmimemagic.MagicParseException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;

import documents.ExcelDocument;
import documents.FileDocument;
import documents.GaimLogDocument;
import documents.MP3Document;
import documents.mbox.MBoxProcessor;

class IndexFiles {
//	static Logger log = Logger.getLogger(IndexFiles.class);
    private final static String HOME = System.getenv("HOME");
	public static void main(String[] args) throws IOException {
		try {
			BasicConfigurator.configure();
			Date start = new Date();
			IndexWriter writer = new IndexWriter(HOME+"/index", new StandardAnalyzer(), true);
			indexDocs(writer, new File(HOME+"/mp3"));
			indexDocs(writer, new File(HOME+"/bin"));
			indexDocs(writer, new File(HOME+"/Documents"));
			//indexDocs(writer, new File(HOME+"/.evolution/mail/local"));
			indexDocs(writer, new File(HOME+"/.gaim/logs"));

			writer.optimize();
			writer.close();

			Date end = new Date();

			System.out.print(end.getTime() - start.getTime());
			System.out.println(" total milliseconds");

		} catch (IOException e) {
			System.out.println(" caught a " + e.getClass() + "\n with message: " + e.getMessage());
		}
	}

	public static void indexDocs(IndexWriter writer, File file) throws IOException {
		// do not try to index files that cannot be read

		if (file.canRead()) {
			if (file.isDirectory()) {
				String[] files = file.list();
				// an IO error could occur
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						indexDocs(writer, new File(file, files[i]));
					}
				}
			} else {

				try {
					Magic parser = new Magic();
					parser.initialize();
					MagicMatch match = null;
					//FormatDescription desc = FormatIdentification.identify(file);
					try {
						match = parser.getMagicMatch(file);
					} catch (MagicMatchNotFoundException e) {
					} 
					if (match != null) {
						if (match.getMimeType().equals("audio/mpeg")) {
							//System.out.println("adding MP3 File" + file);
							writer.addDocument(MP3Document.Document(file));
						}  else
						if(match.getMimeType().equals("application/msword")) {
							writer.addDocument(ExcelDocument.Document(file));
						} else {
							System.out.println("adding as normal file with file desc" + file);
							writer.addDocument(FileDocument.Document(file));
						}
					} else {
						if (file.getName().equals("Inbox")) {
							//MBoxProcessor.ProcessMBoxFile(file, writer);
							TestMain.indexMails(writer);
						}

						else {
							if (file.getAbsolutePath().indexOf(".gaim/logs") > 0) {
								System.out.println("adding gaim log file" + file);
								writer.addDocument(GaimLogDocument.Document(file));
							} else {
								System.out.println("adding as normal file with NO ile desc" + file);
								writer.addDocument(FileDocument.Document(file));
							}
						}
					}

				}
				// at least on windows, some temporary files raise this
				// exception with an "access denied" message
				// checking if the file can be read doesn't help
				catch (FileNotFoundException fnfe) {
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MagicParseException e) {
					e.printStackTrace();
				} catch (MagicException e) {
					e.printStackTrace();
				}
			}
		}
	}

	
}