package documents;

import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import utils.LuceneUtility;

import com.sleepycat.db.Cursor;
import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseConfig;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.DatabaseType;
import com.sleepycat.db.OperationStatus;

public class AddressBookDocument implements SearchDocument {
	public static String[] fields = { "emailaddress", "fullname", "instantmessageaddress", "path", "type", "modified", "filecontents", "name" };

	public AddressBookDocument() {
	}

	public void run(String databaseName) {
		List docs = new LinkedList();
		try {
			// Create the database object.
			// There is no environment for this simple example.
			DatabaseConfig dbConfig = new DatabaseConfig();
			dbConfig.setErrorStream(System.err);
			dbConfig.setErrorPrefix("AccessExample");
			dbConfig.setType(DatabaseType.HASH);
			dbConfig.setAllowCreate(true);
			Database table;

			table = new Database(databaseName, null, dbConfig);

			// Acquire an iterator for the table.
			Cursor cursor;
			cursor = table.openCursor(null, null);

			// Walk through the table, printing the key/data pairs.
			// See class StringDbt defined below.
			//
			StringEntry key = new StringEntry();

			StringEntry data = new StringEntry();
			
			while (cursor.getNext(key, data, null) == OperationStatus.SUCCESS) {
				Document doc = analyzeData(data.getString());
				if(doc != null)
					docs.add(doc);
			}
			cursor.close();
			table.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}

		LuceneUtility.removeEntry(databaseName);
		LuceneUtility.addDocuments(docs);
	}

	private Document analyzeData(String string) {
		Document doc = new Document();
		boolean inphotomode = false;
		String photo = "";
		StringTokenizer st = new StringTokenizer(string, "\n");
		while (st.hasMoreTokens()) {
			String line = st.nextToken().toString().trim();
//			System.out.println("Line: "+line);
			StringTokenizer st1 = new StringTokenizer(line, ":");
			
			if (st1.countTokens() >= 2) {
				while (st1.hasMoreTokens()) {
					String key = st1.nextToken();
					String data = st1.nextToken();
					if ("EMAIL".equalsIgnoreCase(key)) {
						System.out.println("Email >" + data);
						doc.add(Field.Text("emailaddress", data));
					}
					if ("FN".equalsIgnoreCase(key)) {
						doc.add(Field.Text("fullname", data));
						System.out.println("Fullname >" + data);
					}
					if ("X-AIM".equalsIgnoreCase(key)) {
						System.out.println("AIM >" + data);
						doc.add(Field.Text("instantmessageaddress", data));
					}
					if (StringUtils.contains(key, "PHOTO;ENCODING=b;TYPE=")) {
//						System.out.println("Photo >\n\n" + data+"\n\n");
						inphotomode = true;
						photo = data;
						
					}

					if ("END".equalsIgnoreCase(key) && "VCARD".equalsIgnoreCase(data)) {
						System.out.println("END ***************");
						if(inphotomode) {
							doc.add(Field.UnIndexed("photo", photo));
							System.out.println("Added photo... \n"+photo);
						}
						doc.add(Field.Text("type", "EvolutionAddressBook"));
						return doc;
					}
				}
			} else {
				if(inphotomode) {
					photo += line;
				}
			}
		}
		return null;

	}

	// Here's an example of how you can extend DatabaseEntry in a
	// straightforward way to allow easy storage/retrieval of strings,
	// or whatever kind of data you wish. We've declared it as a static
	// inner class, but it need not be.
	//
	static/* inner */
	class StringEntry extends DatabaseEntry {
		StringEntry() {
		}

		StringEntry(String value) {
			setString(value);
		}

		void setString(String value) {
			byte[] data = value.getBytes();
			setData(data);
			setSize(data.length);
		}

		String getString() {
			return new String(getData(), getOffset(), getSize());
		}
	}

	public String[] getSearchFields() {
		return fields;
	}
}
