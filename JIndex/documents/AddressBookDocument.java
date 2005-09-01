package documents;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import com.sleepycat.bind.tuple.IntegerBinding;
import com.sleepycat.je.Cursor;
import com.sleepycat.je.Database;
import com.sleepycat.je.DatabaseConfig;
import com.sleepycat.je.DatabaseEntry;
import com.sleepycat.je.DatabaseException;
import com.sleepycat.je.Environment;
import com.sleepycat.je.EnvironmentConfig;
import com.sleepycat.je.LockMode;
import com.sleepycat.je.OperationStatus;
import com.sleepycat.je.Transaction;

import java.io.*;

public class AddressBookDocument implements SearchDocument {
	public static String[] fields = { "path", "type", "url", "modified", "contents", "name" };

	public static Document Document(File f) throws java.io.FileNotFoundException {
//test(f);
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

	private AddressBookDocument() {
	}
	public void test(File f) throws DatabaseException {
		 EnvironmentConfig envConfig = new EnvironmentConfig();
	        envConfig.setTransactional(true); 
	        envConfig.setAllowCreate(false);    
	        Environment exampleEnv;
			
				exampleEnv = new Environment(new File("/home/sorenm/.evolution/addressbook/local/system"), envConfig);
	
	        DatabaseConfig dbConfig = new DatabaseConfig();
	        //dbConfig.setTransactional(true); 
	        dbConfig.setAllowCreate(true);
	        dbConfig.setSortedDuplicates(true);
	        Database exampleDb = exampleEnv.openDatabase(null, 
	                                                     "addressbook1",
	                                   
	                                                     dbConfig);
	        
	        System.out.println(exampleDb.getDatabaseName());
	        
	        	        
	        DatabaseEntry keyEntry = new DatabaseEntry();
	        DatabaseEntry dataEntry = new DatabaseEntry();

	        Cursor cursor = exampleDb.openCursor(null, null);
cursor.getFirst(keyEntry, dataEntry, LockMode.DEFAULT);
            while (cursor.getNext(keyEntry, dataEntry, LockMode.DEFAULT) ==
                   OperationStatus.SUCCESS) {
                System.out.println("key=" + 
                                   IntegerBinding.entryToInt(keyEntry) +
                                   " data=" + 
                                   IntegerBinding.entryToInt(dataEntry));

            }
            cursor.close();
            exampleDb.close();
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
