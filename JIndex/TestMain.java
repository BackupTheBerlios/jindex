import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.Serializable;
import java.net.URL;
import java.nio.Buffer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;


public class TestMain  {

	
	public TestMain() {

			int count = 0;
			try {
//				try {
//					Processor proc = Manager.createProcessor(new URL("file:///home/sorenm/doom_trailer_072505_qthighwide.mov"));
//					System.out.println(proc.getDuration().getSeconds());
//
//				} catch (NoProcessorException e1) {
//					e1.printStackTrace();
//				}
				
//				XMLEncoder e = new XMLEncoder(
//		                new BufferedOutputStream(
//		                    new FileOutputStream("/tmp/Test.xml")));
//				MailHash hash = new MailHash(100,1);
//				System.out.println(hash.getLength());
//				write(hash,"/tmp/hashtest.obj");
				BufferedReader in = new BufferedReader(new FileReader("/tmp/Inbox"));
				
				StringBuffer msg = new StringBuffer();
				MailList list = null;
				try {
					list = (MailList) read("/tmp/maillist.obj");
					if (list == null)
						list = new MailList();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
				int position = 0;
				for(int i=0; i < list.size(); i++) {
					MailHash hash = (MailHash)list.get(i);
					position += hash.getLength();
				}

				
				// Read a character
				String str;
				int i = 0;
				position = 0;
				while ((str = in.readLine()) != null) {
					if (str.startsWith("From ")) {
						count++;
						// list.add(msg);
						if (count > 1) {
							System.out.println("Msg size: " + msg.length());
							System.out.println("Msg position: " + position);
							System.out.println("Msg hashcode: " + msg.substring(0, 100).hashCode());
							position += msg.length();
							list.add(new MailHash(msg.length(),msg.substring(0, 100).hashCode()));
						}
						
						msg = new StringBuffer();
					}
					msg.append(str);
				}
				in.close();
				
				write(list,"/tmp/maillist.obj");
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println(count);
	
	
	}

	public static void main(String argv[]) {

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			Connection forb = DriverManager.getConnection("jdbc:oracle:thin:@192.168.146.223:1521:prod", "eclub2", "secret");
			long now = System.currentTimeMillis();
			forb.getMetaData().supportsSelectForUpdate();
			System.out.println(System.currentTimeMillis() - now);

			now = System.currentTimeMillis();
			forb.getMetaData().getDatabaseProductName();
			System.out.println(System.currentTimeMillis() - now);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		new TestMain();
	}



	private static void write(Serializable object, String filename) throws IOException {
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new FileOutputStream(filename));

			out.writeObject(object);
			out.flush();
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException exception) {
				}
			}
		}
	}

	private static Object read(String filename) throws ClassNotFoundException, IOException {
		ObjectInputStream in = null;

		try {
			in = new ObjectInputStream(new FileInputStream(filename));

			return in.readObject();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException exception) {
				}
			}
		}
	}

	
}
