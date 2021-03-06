package org.jindex.documents.office;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;

import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import com.lowagie.text.pdf.PdfReader;

import org.jindex.documents.SearchDocument;


public class PDFDocument implements SearchDocument {
	public static String[] fields = { "path", "type", "url", "modified", "filecontents", "file-name", "numberofpages", "producer", "creator", "creationdate" };	

	public static Document getDocument(File f, String mimetype) {
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
		
		doc.add(getField("path", f.getPath()));
		java.lang.String path = f.getParent();
		doc.add(getField("absolutepath", path));

		doc.add(getField("file-name", f.getName()));

		doc.add(getField("type", mimetype));
		
		doc.add(getField("numberofpages", ""+numberofpages));
		doc.add(getField("producer", ""+producer));
		doc.add(getField("creator", ""+creator));
		doc.add(getField("creationdate", ""+creationdate));
		doc.add(getField("modified", DateField.timeToString(f.lastModified())));
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
		    private static Field getField(String name, String value) {
        return new Field(name, value.getBytes(), Field.Store.YES);
    }

}
