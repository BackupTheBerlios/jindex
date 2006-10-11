package org.jindex.daemon;

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
import org.jindex.documents.HTMLDocument;

import org.jindex.utils.FileUtility;
import org.jindex.utils.LuceneUtility;
import org.jindex.documents.AddressBookDocument;
import org.jindex.documents.ApplicationDocument;
import org.jindex.documents.GaimLogDocument;
import org.jindex.documents.ImageDocument;
import org.jindex.documents.JavaDocument;
import org.jindex.documents.MP3Document;
import org.jindex.documents.TomboyDocument;
import org.jindex.documents.mail.EvolutionMailDocument;
import org.jindex.documents.office.ExcelDocument;
import org.jindex.documents.office.OpenOfficeDocument;
import org.jindex.documents.office.PDFDocument;

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
                log.debug("We got old files...");
                Iterator ite = allreadyIndexedFiles.iterator();
                while(ite.hasNext()) {
                    JFile obj = (JFile)ite.next();
                    log.debug("Old filename: "+obj.getFilename());
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
                        log.debug("Shutting down...");
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } finally {
                log.debug("We write objects");
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
            log.debug("Mimetype: "+mimetype);
            if (mimetype != null) {
                indexDocument(mimetype, file);
            } else {
                log.debug("Skipping unknown file with file desc" + file);
                // writer.addDocument(FileDocument.Document(file));
            }
            // }
            if (writer != null) {
                writer.close();
            }
            
        }
    }
    
    public static  boolean indexDocument(String mimetype, File file) {
        try {
            if (mimetype.equals("audio/mpeg")) {
                LuceneUtility.addDocument(MP3Document.Document(file,  mimetype));
                return true;
            } else if (mimetype.equals("application/msword")) {
                LuceneUtility.addDocument(ExcelDocument.Document(file, mimetype));
                return true;
                
            } else if (mimetype.equals("application/vnd.ms-excel")) {
                LuceneUtility.addDocument(ExcelDocument.Document(file, mimetype));
                return true;
                
            } else if (StringUtils.contains(mimetype, "image/")) {
                LuceneUtility.addDocument(ImageDocument.Document(file, mimetype));
                return true;
                
            } else if (StringUtils.contains(mimetype, "application/pdf")) {
                LuceneUtility.addDocument(PDFDocument.getDocument(file, mimetype));
                return true;
                
            } else if (StringUtils.contains(mimetype, "text/x-java")) {
                LuceneUtility.addDocument(JavaDocument.Document(file, mimetype));
                return true;
                
            } else if (StringUtils.contains(mimetype, "text/html")) {
                log.debug("Found HTML document");
                LuceneUtility.addDocument(HTMLDocument.Document(file, mimetype));
                return true;
                
            } else if(StringUtils.contains(mimetype, "application/vnd.sun.xml.writer")) {
                LuceneUtility.addDocument(OpenOfficeDocument.Document(file, mimetype));
                return true;
            }
            
            if (StringUtils.contains(file.getAbsolutePath(),"/.evolution/mail/local")) {
                EvolutionMailDocument.indexMails(file);
                return true;
            }
            if (file.getName().equals("addressbook.db")) {
                //					LuceneUtility.addDocument(AddressBookDocument.Document(file));
                AddressBookDocument abook = new AddressBookDocument();
                abook.run(file.getAbsolutePath());
                return true;
            }
            if (file.getName().endsWith("desktop")) {
                LuceneUtility.addDocument(ApplicationDocument.Document(file));
                return true;
            }
            
            if (file.getAbsolutePath().indexOf(".gaim/logs") > 0) {
                // log.debug("adding gaim log file"
                // +
                // file);
                LuceneUtility.addDocument(GaimLogDocument.Document(file));
                return true;
            }
            if (file.getAbsolutePath().indexOf("/.tomboy/") > 0) {
                LuceneUtility.addDocument(TomboyDocument.Document(file));
                return true;
            } else {
                // log.debug("adding as normal file
                // with NO ile desc" + file);
                // writer.addDocument(FileDocument.Document(file));
            }
        } catch(IOException e) {
            log.error(e);
            return false;
        }
        return false;
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
                    log.debug("File was modified: " + tmp.getLastmodified() + " != " + file.getLastmodified());
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