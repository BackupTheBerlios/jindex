package documents;
import java.io.File;
import java.io.IOException;

import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Field;

import sabercat.Sabercat;
/** A utility for making Lucene Documents from a File. */

public class MP3Document {
	/**
	 * Makes a document for a File.
	 * <p>
	 * The document has three fields:
	 * <ul>
	 * <li><code>path</code> --containing the pathname of the file, as a
	 * stored, tokenized field;
	 * <li><code>modified</code> --containing the last modified date of the
	 * file as a keyword field as encoded by <a
	 * href="lucene.document.DateField.html">DateField </a>; and
	 * <li><code>contents</code> --containing the full contents of the file,
	 * as a Reader field;
	 * 
	 * @throws IOException
	 */
	public static String fields[] = {"modified",
			"path",
			"album",
			"title",
			"comment",
			"year",
			"artist",
			"genre"};
	public static org.apache.lucene.document.Document Document(File f) throws IOException {

		org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
		doc.add(Field.Text("path", f.getPath()));
		doc.add(Field.Text("modified", DateField.timeToString(f.lastModified())));
		Sabercat sabercat = new Sabercat(f); 
		
		doc.add(Field.Text("album",sabercat.getAlbum()));
		doc.add(Field.Text("artist",sabercat.getArtist()));
		doc.add(Field.Text("year",sabercat.getYear()));
		doc.add(Field.Text("comment",sabercat.getComment()));
		doc.add(Field.Text("title",sabercat.getTitle()));
		doc.add(Field.Text("genre",sabercat.getGenre()));
		doc.add(Field.Text("type","audio/mp3"));
		return doc;
	}

	
}

