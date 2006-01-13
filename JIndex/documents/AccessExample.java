package documents;
import java.io.FileNotFoundException;
import java.util.StringTokenizer;

import com.sleepycat.db.Cursor;
import com.sleepycat.db.Database;
import com.sleepycat.db.DatabaseConfig;
import com.sleepycat.db.DatabaseEntry;
import com.sleepycat.db.DatabaseException;
import com.sleepycat.db.DatabaseType;
import com.sleepycat.db.OperationStatus;
class AccessExample {
    private static final int EXIT_SUCCESS = 0;
    private static final int EXIT_FAILURE = 1;

    public AccessExample() {
    }

 
    public static void main(String[] argv) {
        String databaseName = "/tmp/addressbook.db";

        try {
            AccessExample app = new AccessExample();
            app.run(databaseName);
        } catch (FileNotFoundException fnfe) {
            System.err.println("AccessExample: " + fnfe.toString());
            System.exit(EXIT_FAILURE);
        } catch (DatabaseException e) {
			e.printStackTrace();
		}
        System.exit(EXIT_SUCCESS);
    }

    public void run(String databaseName)
        throws FileNotFoundException, DatabaseException {

        // Create the database object.
        // There is no environment for this simple example.
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setErrorStream(System.err);
        dbConfig.setErrorPrefix("AccessExample");
        dbConfig.setType(DatabaseType.HASH);
        dbConfig.setAllowCreate(true);
        Database table = new Database(databaseName, null, dbConfig);


        // Acquire an iterator for the table.
        Cursor cursor;
        cursor = table.openCursor(null, null);

        // Walk through the table, printing the key/data pairs.
        // See class StringDbt defined below.
        //
        StringEntry key = new StringEntry();

        StringEntry data = new StringEntry();
        while (cursor.getNext(key, data, null) == OperationStatus.SUCCESS) {
        		analyzeData(data.getString());
        }
        cursor.close();
        table.close();
    }

    private void analyzeData(String string) {
		StringTokenizer st  =new StringTokenizer(string, "\n");
		while(st.hasMoreTokens()) {
			String line = st.nextToken().toString().trim();
//			System.out.println("Line: "+line);
			StringTokenizer st1  =new StringTokenizer(line, ":");
			if(st1.countTokens() >= 2) {
			while(st1.hasMoreTokens()) {
				String key = st1.nextToken();
				String data = st1.nextToken();
				if("EMAIL".equalsIgnoreCase(key)) {
					System.out.println("Email >"+data);
				}
				if("FN".equalsIgnoreCase(key)) {
					System.out.println("Fullname >"+data);
				}
				if("X-AIM".equalsIgnoreCase(key)) {
					System.out.println("AIM >"+data);
				}
				if("PHOTO;ENCODING=b;TYPE=PNG".equalsIgnoreCase(key)) {
					System.out.println("Photo >"+data);
				}
				
				if("END".equalsIgnoreCase(key) && "VCARD".equalsIgnoreCase(data)) {
					System.out.println("END ***************");
				}
			}
			}
		}
		
	}

	// Here's an example of how you can extend DatabaseEntry in a
    // straightforward way to allow easy storage/retrieval of strings,
    // or whatever kind of data you wish.  We've declared it as a static
    // inner class, but it need not be.
    //
    static /*inner*/
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
}