package documents;

import java.io.File;

import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

public class ImageDocument implements SearchDocument{
	public static String[] fields = { "path", "absolutepath", "type", "url", "modified", "name" };

	public static Document Document(File f) throws java.io.FileNotFoundException {

		Document doc = new Document();

		doc.add(Field.Keyword("path", f.getPath()));
		String path = f.getParent();
//		path = path.substring(0, path.length() - 1);
		doc.add(Field.Keyword("absolutepath", path));

		doc.add(Field.Keyword("name", f.getName()));

		doc.add(Field.Text("type", "image"));
		doc.add(Field.Text("icon", "icon data"));
		//doc.add(Field.Text("url", "url data"));
		doc.add(Field.Keyword("modified", DateField.timeToString(f.lastModified())));
		return doc;
	}

	private ImageDocument() {
	}

    public String[] getSearchFields() {
        return fields;
    }
		

}
