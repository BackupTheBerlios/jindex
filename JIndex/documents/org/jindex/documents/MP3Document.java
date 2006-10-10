package org.jindex.documents;

import java.io.File;

import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Field;

import sabercat.Sabercat;

public class MP3Document implements SearchDocument {
	public static String fields[] = { "modified", "path", "album", "title", "comment", "year", "artist", "genre" };

	public static org.apache.lucene.document.Document Document(File f, String mimetype) {

		org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
		doc.add(Field.Text("path", f.getPath()));
		doc.add(Field.Text("modified", DateField.timeToString(f.lastModified())));
		Sabercat sabercat = new Sabercat(f);

		doc.add(Field.Text("album", sabercat.getAlbum()));
		doc.add(Field.Text("artist", sabercat.getArtist()));
		doc.add(Field.Text("year", sabercat.getYear()));
		doc.add(Field.Text("comment", sabercat.getComment()));
		doc.add(Field.Text("title", sabercat.getTitle()));
		doc.add(Field.Text("genre", sabercat.getGenre()));
		doc.add(Field.Text("type", mimetype));
		return doc;
	}

	public String[] getSearchFields() {
		return fields;
	}

}
