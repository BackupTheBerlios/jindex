package org.jindex.documents.office;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.codec.binary.Base64;
import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import org.jindex.utils.FileUtility;

public class OpenOfficeDocument {

	public static String[] fields = { "absolutepath", "path", "type", "modified", "filecontents", "file-name" };

	public static Document Document(File f, String mimetype) {
		try {
			Document doc = new Document();

			doc.add(getField("path", f.getPath()));
			java.lang.String path = f.getParent();
			doc.add(getField("absolutepath", path));

			doc.add(getField("file-name", f.getName()));
			doc.add(getField("type", mimetype));

			ZipFile zipfile = new ZipFile(f);
			ZipEntry thumbnail = zipfile.getEntry("Thumbnails/thumbnail.png");
			if (thumbnail != null) {
				InputStream is = zipfile.getInputStream(thumbnail);

				Base64 b64E = new Base64();
				byte[] encoded = b64E.encode(FileUtility.getBytesFromFile(is));
				doc.add(getField("thumbnail", new String(encoded)));
			}
			doc.add(getField("modified", DateField.timeToString(f.lastModified())));
			doc.add(new Field("filecontents", new FileReader(f)));
			return doc;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private OpenOfficeDocument() {
	}

	public String[] getSearchFields() {
		return fields;
	}
    private static Field getField(String name, String value) {
        return new Field(name, value.getBytes(), Field.Store.YES);
    }
}
