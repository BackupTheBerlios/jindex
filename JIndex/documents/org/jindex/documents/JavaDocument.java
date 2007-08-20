package org.jindex.documents;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;


public class JavaDocument implements SearchDocument {
	static Logger log = Logger.getLogger(JavaDocument.class);
	public static String[] fields = { "path", "absolutepath", "type",  "modified", "filecontents", "file-name" };	

	public static Document Document(File f, String mimetype) {
		try {
		Document doc = new Document();
		log.debug("Indexing java file: "+f.getName());
		doc.add(getField("path", f.getPath()));
		doc.add(getField("absolutepath", f.getAbsolutePath()));

		doc.add(getField("file-name", f.getName()));
		doc.add(getField("type", mimetype));
		
		doc.add(getField("modified", DateField.timeToString(f.lastModified())));
		doc.add(new Field("filecontents", new FileReader(f)));
		return doc;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private JavaDocument() {
	}

    public String[] getSearchFields() {
       return fields;
    }
		    private static Field getField(String name, String value) {
        return new Field(name, value, Field.Store.YES, Field.Index.TOKENIZED);
    }

}
