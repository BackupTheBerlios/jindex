package org.jindex.documents;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Date;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

public class FileDocument implements SearchDocument {
	Logger log = Logger.getLogger(FileDocument.class);
    public static String[] fields = { "path", "type", "url", "modified", "filecontents", "name" };
	public static Document Document(File f) throws java.io.FileNotFoundException {

		Document doc = new Document();

		doc.add(getField("path", f.getPath()));
		String path = f.getParent();
//		path = path.substring(0, path.length() - 1);
		doc.add(getField("absolutepath", path));

		doc.add(getField("name", f.getName()));

		doc.add(getField("type", "unknown mime type"));
		doc.add(getField("icon", "icon data"));
		doc.add(getField("url", "url data"));
		doc.add(getField("from", ""));
                
		doc.add(getField("modified", new Date(f.lastModified()).toLocaleString()));

		FileInputStream is = new FileInputStream(f);
		Reader reader = new BufferedReader(new InputStreamReader(is));
                
                doc.add(new Field("filecontents", reader));

		return doc;
	}

	private FileDocument() {
	}

    public String[] getSearchFields() {
        
        log.debug(fields);
        return fields;
    }
    private static Field getField(String name, String value) {
        return new Field(name, value, Field.Store.YES, Field.Index.TOKENIZED);
    }
}
