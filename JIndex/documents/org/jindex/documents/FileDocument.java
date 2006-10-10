package org.jindex.documents;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.log4j.Logger;
import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

public class FileDocument implements SearchDocument {
	Logger log = Logger.getLogger(FileDocument.class);
    public static String[] fields = { "path", "type", "url", "modified", "filecontents", "name" };
	public static Document Document(File f) throws java.io.FileNotFoundException {

		Document doc = new Document();

		doc.add(Field.Keyword("path", f.getPath()));
		String path = f.getParent();
//		path = path.substring(0, path.length() - 1);
		doc.add(Field.Keyword("absolutepath", path));

		doc.add(Field.Keyword("name", f.getName()));

		doc.add(Field.Text("type", "unknown mime type"));
		doc.add(Field.Text("icon", "icon data"));
		doc.add(Field.Text("url", "url data"));
		doc.add(Field.Text("from", ""));
		doc.add(Field.Keyword("modified", DateField.timeToString(f.lastModified())));

		FileInputStream is = new FileInputStream(f);
		Reader reader = new BufferedReader(new InputStreamReader(is));
        doc.add(Field.Text("filecontents", reader, true));

		return doc;
	}

	private FileDocument() {
	}

    public String[] getSearchFields() {
        
        log.debug(fields);
        return fields;
    }
}
