package daemon;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
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
import documents.MP3Document;
import documents.PDFDocument;

class IndexFiles extends Thread {
    // static Logger log = Logger.getLogger(IndexFiles.class);
    long numMillisecondsToSleep = 5000; // 5 seconds
    private final static String HOME = System.getProperty("HOME");
    private static boolean updateindex = false;

    public void run() {
        updateindex = true;

      while(true) {
          try {
            Thread.sleep(numMillisecondsToSleep);
            List files = JIndexDaemon.getFileFromQueue();
            for(int i=0; i < files.size(); i++) {
                System.out.println("Processing file "+i);
                File file = (File) files.get(i);
                indexDocs(file);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
      }
    }

    public static void main(String[] args) {
        try {

            AssociationService assocService = new AssociationService();
            URL url = new URL("file:///home/sorenm/movies/Tripping.The.Rift.S02E04.DSR.XviD-LOKi/tripping.the.rift.204.dsr-loki.avi");
            Association assoc = assocService.getAssociationByContent(url);
            System.out.println("foud mime type: "+assoc.getMimeType());
            
            
            if (HOME == null) {
                System.out
                        .println("Needs to be stated with -DHOME=~/ or something");
                System.exit(-1);
            }
            if (args.length <= 0) {
                System.out
                        .println("Needs to be executed with a argument\nThe argument should be a file or directory");
                System.exit(-1);
            }
            Date start = new Date();
            // indexDocs(writer, new File(HOME+"/mp3"));
            if (args[0].equals("updateindex"))
                updateindex = true;

            indexDocs(new File(args[1]));
            
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

        } catch (IOException e) {
            System.out.println(" caught a " + e.getClass()
                    + "\n with message: " + e.getMessage());
        }
    }

    public static void removeEntry(String filename) {

        try {
            IndexReader reader = IndexReader.open(HOME + "/index");
            try {
                System.out.println("Index contains : "+reader.numDocs()+" documents");
                System.out.println("Removing old entry: " + filename);
                int delcounter = reader.delete(new Term("path", filename));
                System.out.println("deleted "+delcounter+" documents");
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void indexDocs(File file)
            throws IOException {
        // do not try to index files that cannot be read
  
        if (file.canRead()) {
            if (file.isDirectory()) {
                String[] files = file.list();
                // an IO error could occur
                if (files != null) {
                    for (int i = 0; i < files.length; i++) {
                        indexDocs(new File(file, files[i]));
                    }
                }
            } else {
                // if update, remove the old entry before continuing
                if (updateindex)
                    removeEntry(file.getAbsolutePath());
                IndexWriter writer = new IndexWriter(HOME + "/index",
                        new StandardAnalyzer(), false);
                try {
                 	AssociationService assocService = new AssociationService();
                 	URL url = new URL(file.toURL().toString());
                 	Association assoc = assocService.getAssociationByContent(url);
                 	System.out.println("foud mime type: "+assoc.getMimeType());
                 	String mimetype = assoc.getMimeType();                    

                    if (mimetype != null) {
                        if (mimetype.equals("audio/mpeg")) {
                            System.out.println("adding MP3 File" + file);
                            writer.addDocument(MP3Document.Document(file));
                        } else if (mimetype.equals("application/msword")) {
                            writer.addDocument(ExcelDocument.Document(file));
                        } else if (StringUtils.contains(mimetype, "image/")) {
                            writer.addDocument(ImageDocument.Document(file));
                        } else if (StringUtils.contains(mimetype,
                                "application/pdf")) {
                            writer.addDocument(PDFDocument.Document(file));
                        }

                        else {
                            // System.out.println("adding as normal file with
                            // file desc" + file);
                            writer.addDocument(FileDocument.Document(file));
                        }
                    } else {
                        if (file.getName().equals("Inbox")) {
                            // MBoxProcessor.ProcessMBoxFile(file, writer);
                            //TestMain.indexMails(writer);
                        } else if (file.getName().equals("addressbook.db")) {
                            writer.addDocument(AddressBookDocument
                                    .Document(file));
                        }

                        else {
                            if (file.getAbsolutePath().indexOf(".gaim/logs") > 0) {
                                // System.out.println("adding gaim log file" +
                                // file);
                                writer.addDocument(GaimLogDocument
                                        .Document(file));
                            } else {
                                // System.out.println("adding as normal file
                                // with NO ile desc" + file);
                                writer.addDocument(FileDocument.Document(file));
                            }
                        }
                    }
                  
                } catch (FileNotFoundException fnfe) {
                } catch (IOException e) {
                    e.printStackTrace();
                }
                writer.close();
            }
        }
      
    }

}