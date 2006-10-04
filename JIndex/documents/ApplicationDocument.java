package documents;

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
		

		doc.add(Field.Text("applicationname",StringUtils.trimToEmpty(prop.getProperty("Name"))));
		doc.add(Field.Text("exec-command",StringUtils.trimToEmpty(prop.getProperty("Exec"))));
		doc.add(Field.Text("comment",StringUtils.trimToEmpty(prop.getProperty("Comment"))));
		log.debug(StringUtils.trimToEmpty(prop.getProperty("Icon")));
		doc.add(Field.Keyword("icon",StringUtils.trimToEmpty(prop.getProperty("Icon"))));
		doc.add(Field.Keyword("path", f.getPath()));
		String path = f.getParent();
//		path = path.substring(0, path.length() - 1);
		doc.add(Field.Keyword("absolutepath", path));

		doc.add(Field.Keyword("file-name", f.getName()));

		doc.add(Field.Text("type", "Application"));
		doc.add(Field.Keyword("modified", DateField.timeToString(f.lastModified())));

		return doc;
	}
	
    public String[] getSearchFields() {
        log.debug(fields);
        return fields;
    }
}
