package documents;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import java.io.*;

public class FileDocument {
	public static String[] fields = { "path", "type", "url", "modified", "contents", "name" };

	public static Document Document(File f) throws java.io.FileNotFoundException {

		Document doc = new Document();

		doc.add(Field.Keyword("path", f.getPath()));
		String path = f.getParent();
//		path = path.substring(0, path.length() - 1);
		doc.add(Field.Keyword("absolutepath", path));

		doc.add(Field.Keyword("name", f.getName()));

		doc.add(Field.Text("type", "type field"));
		doc.add(Field.Text("icon", "icon data"));
		doc.add(Field.Text("url", "url data"));
		doc.add(Field.Text("from", ""));
		doc.add(Field.Keyword("modified", DateField.timeToString(f.lastModified())));

		FileInputStream is = new FileInputStream(f);
		Reader reader = new BufferedReader(new InputStreamReader(is));
		doc.add(Field.Text("contents", reader));

		return doc;
	}

	private FileDocument() {
	}
}
