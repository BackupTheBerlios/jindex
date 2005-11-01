package documents;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;


public class JavaDocument implements SearchDocument {
	public static String[] fields = { "path", "type", "url", "modified", "filecontents", "file-name" };	

	public static Document Document(File f) {
		try {
		Document doc = new Document();
		
		
		doc.add(Field.Keyword("path", f.getPath()));
		java.lang.String path = f.getParent();
		doc.add(Field.Text("absolutepath", path, true));

		doc.add(Field.Keyword("file-name", f.getName()));

		doc.add(Field.Text("type", "text/x-java"));
		doc.add(Field.Text("icon", "icon data"));
		doc.add(Field.Text("url", "url data"));
		
		doc.add(Field.Keyword("modified", DateField.timeToString(f.lastModified())));
		doc.add(Field.Text("filecontents", new FileReader(f)));
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
		

}
