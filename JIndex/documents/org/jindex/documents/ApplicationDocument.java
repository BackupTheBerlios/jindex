package org.jindex.documents;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

public class ApplicationDocument implements SearchDocument {
	static Logger log = Logger.getLogger(ApplicationDocument.class);
    public static String[] fields = {"exec-command","icon", "applicationname", "path", "type", "url", "modified", "filecontents", "file-name" };
	public static Document Document(File f) throws java.io.FileNotFoundException {
		log.debug("Indexing application");
		Document doc = new Document();

		Properties prop = new Properties();
		try {
			prop.load(new FileInputStream(f));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		

		doc.add(new Field("applicationname",StringUtils.trimToEmpty(prop.getProperty("Name")).getBytes(), Field.Store.YES));
		doc.add(new Field("exec-command",StringUtils.trimToEmpty(prop.getProperty("Exec")).getBytes(), Field.Store.YES));
		doc.add(new Field("comment",StringUtils.trimToEmpty(prop.getProperty("Comment")).getBytes(), Field.Store.YES));

                doc.add(new Field("icon",StringUtils.trimToEmpty(prop.getProperty("Icon")).getBytes(), Field.Store.YES));
		doc.add(new Field("path", f.getPath().getBytes(), Field.Store.YES));
		String path = f.getParent();
//		path = path.substring(0, path.length() - 1);
		doc.add(new Field("absolutepath", path.getBytes(), Field.Store.YES));

		doc.add(new Field("file-name", f.getName().getBytes(), Field.Store.YES));

		doc.add(new Field("type", "Application".getBytes(), Field.Store.YES));
		doc.add(new Field("modified", DateField.timeToString(f.lastModified()).getBytes(), Field.Store.YES));

		return doc;
	}
	
    public String[] getSearchFields() {
        log.debug(fields);
        return fields;
    }
}
