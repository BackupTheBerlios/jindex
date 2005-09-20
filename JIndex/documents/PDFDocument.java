package documents;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;

import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import com.lowagie.text.pdf.PdfReader;


public class PDFDocument implements SearchDocument {
	public static String[] fields = { "path", "type", "url", "modified", "filecontents", "name", "numberofpages", "producer", "creator", "creationdate" };	

	public static Document Document(File f) {
		try {
		Document doc = new Document();
		
		PdfReader reader = new PdfReader(f.toURL());
		 int numberofpages = reader.getNumberOfPages();

         HashMap map = reader.getInfo();
         String producer = "";
         String creator = "";
         String creationdate = "";
         if(map != null) {
		 producer = (String) map.get("Producer");
		 creator = (String) map.get("Creator");
		 creationdate = (String) map.get("CreationDate");
         }
//		 Producer=OpenOffice.org 1.9.93
//		 Creator=Writer
//		 CreationDate=D:20050419102404+02'00
		
		doc.add(Field.Text("path", f.getPath(), true));
		java.lang.String path = f.getParent();
//		path = path.substring(0, path.length() - 1);
		doc.add(Field.Text("absolutepath", path, true));

		doc.add(Field.Text("name", f.getName(), true));

		doc.add(Field.Text("type", "application/pdf"));
		doc.add(Field.Text("icon", "icon data"));
		doc.add(Field.Text("url", "url data"));
		
		doc.add(Field.Text("numberofpages", ""+numberofpages));
		doc.add(Field.Text("producer", ""+producer));
		doc.add(Field.Text("creator", ""+creator));
		doc.add(Field.Text("creationdate", ""+creationdate));
		doc.add(Field.Keyword("modified", DateField.timeToString(f.lastModified())));
		return doc;
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private PDFDocument() {
	}

    public String[] getSearchFields() {
       return fields;
    }
		

}
