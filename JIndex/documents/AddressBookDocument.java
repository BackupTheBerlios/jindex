package documents;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import com.sleepycat.bind.tuple.IntegerBinding;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.CursorConfig;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;
import com.sleepycat.je.VerifyConfig;

public class AddressBookDocument implements SearchDocument {
	public static String[] fields = { "path", "type", "url", "modified", "filecontents", "name" };

	public static Document Document(File f) throws java.io.FileNotFoundException {
		// test(f);
		Document doc = new Document();

		doc.add(Field.Keyword("path", f.getPath()));
		String path = f.getParent();
		// path = path.substring(0, path.length() - 1);
		doc.add(Field.Keyword("absolutepath", path));

		doc.add(Field.Keyword("name", f.getName()));

		doc.add(Field.Text("type", "type field"));
		doc.add(Field.Text("icon", "icon data"));
		doc.add(Field.Text("url", "url data"));
		doc.add(Field.Text("from", ""));
		doc.add(Field.Keyword("modified", DateField.timeToString(f.lastModified())));

		FileInputStream is = new FileInputStream(f);
		Reader reader = new BufferedReader(new InputStreamReader(is));
		doc.add(Field.Text("filecontents", reader));

		return doc;
	}

	private AddressBookDocument() {
	}

	public void test(File f) throws DatabaseException {
		EnvironmentConfig envConfig = new EnvironmentConfig();
		envConfig.setTransactional(true);
		envConfig.setAllowCreate(true);
		Environment exampleEnv = new Environment(new File("/home/sorenm/.evolution/addressbook/local/system"), envConfig);

		DatabaseConfig dbConfig = new DatabaseConfig();
		dbConfig.setTransactional(true);
		dbConfig.setAllowCreate(true);
		dbConfig.setSortedDuplicates(true);
		Transaction txn = exampleEnv.beginTransaction(null, null);
		List tmp = exampleEnv.getDatabaseNames();
		Iterator ite = tmp.iterator();
		while (ite.hasNext()) {
			String name = (String) ite.next();
			System.out.println("DB:" + name);
			txn = exampleEnv.beginTransaction(null, null);
			Database exampleDb = exampleEnv.openDatabase(txn, name,

			dbConfig);
			txn.commit();

			DatabaseEntry keyEntry = new DatabaseEntry("BEGIN:VCARD".getBytes());
			DatabaseEntry dataEntry = new DatabaseEntry(null);


			Cursor cursor = exampleDb.openCursor(null, null);


			OperationStatus status = cursor.getNext(keyEntry, dataEntry, LockMode.DEFAULT);
			System.out.println(status.toString());
			if (status != OperationStatus.NOTFOUND) {
				System.out.println("key=" + IntegerBinding.entryToInt(keyEntry) + " data=" + IntegerBinding.entryToInt(dataEntry));

				System.out.println(cursor.count());
			}
			cursor.close();

			exampleDb.close();
			System.out.println("******************************");
		}
		exampleEnv.close();

	}

	public static void main(String argv[]) {
		try {
			new AddressBookDocument().test(new File("."));
		} catch (DatabaseException e) {
			e.printStackTrace();
		}
	}

	public String[] getSearchFields() {
		return fields;
	}
}
