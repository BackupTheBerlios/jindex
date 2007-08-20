package org.jindex.documents;

import common.Logger;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

public class HTMLDocument {
    static Logger log = Logger.getLogger(JavaDocument.class);
    public static String[] fields = { "path", "absolutepath", "type",  "modified", "filecontents", "file-name" };
    
    public static Document Document(File f, String mimetype) {
        try {
            Document doc = new Document();
            log.debug("Indexing HTML file: "+f.getName());
            doc.add(getField("path", f.getPath()));
            doc.add(getField("absolutepath", f.getAbsolutePath()));
            
            doc.add(getField("file-name", f.getName()));
            doc.add(getField("type", mimetype));
            
            doc.add(getField("modified", DateField.timeToString(f.lastModified())));
            
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line = "";
            StringBuffer sb = new StringBuffer();
            while( (line = br.readLine()) != null) {
                sb.append(line);
            }
            String result = sb.toString().replaceAll("</?[a-zA-Z]+\\b[^>]*>", " ");
            doc.add(getField("filecontents", result));
            return doc;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void main(String argv[] ) {
        HTMLDocument.Document(new File("/home/sorenm/test.html"), "text/html");
    }
        private static Field getField(String name, String value) {
      return new Field(name, value, Field.Store.YES, Field.Index.TOKENIZED);
    }
}
