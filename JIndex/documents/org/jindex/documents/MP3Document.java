package org.jindex.documents;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Field;
import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;

import sabercat.Sabercat;

public class MP3Document implements SearchDocument {
	public static String fields[] = { "modified", "path", "album", "title", "comment", "year", "artist", "genre" };

	public static org.apache.lucene.document.Document Document(File f, String mimetype) {

		org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
                try {
		doc.add(getField("path", f.getPath()));
		doc.add(getField("modified", DateField.timeToString(f.lastModified())));
//		Sabercat sabercat = new Sabercat(f);
                MP3File mp3file = new MP3File(f);
                
                System.out.println("Album title "+mp3file.getID3v1Tag().getAlbumTitle());
		doc.add(getField("album", mp3file.getID3v1Tag().getAlbumTitle()));
		doc.add(getField("artist", mp3file.getID3v1Tag().getArtist()));
		doc.add(getField("year", mp3file.getID3v1Tag().getYear()));
		doc.add(getField("comment", mp3file.getID3v1Tag().getComment()));
		doc.add(getField("title", mp3file.getID3v1Tag().getTitle()));
		doc.add(getField("genre", mp3file.getID3v1Tag().getSongGenre()));
                
                System.out.println(mp3file.getID3v1Tag().getGenre());
		doc.add(getField("type", mimetype));
                
                } catch(IOException e) {
                    e.printStackTrace();
                } catch(TagException e) {
                    e.printStackTrace();
                }
		return doc;
	}

	public String[] getSearchFields() {
		return fields;
	}
    private static Field getField(String name, String value) {
        System.out.println(name+"= "+value);
        return new Field(name, value, Field.Store.YES, Field.Index.TOKENIZED);
    }
}
