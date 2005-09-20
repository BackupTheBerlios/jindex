import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;



import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;


public class TestMain  {

	
	public static void indexMails(IndexWriter writer) {

			int count = 0;
			try {
				BufferedReader in = new BufferedReader(new FileReader(System.getenv("HOME")+"/.evolution/mail/local/Inbox"));
				
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
				Mail mail = null;
				while ((str = in.readLine()) != null) {
					if (str.startsWith("From ")) {
						count++;
						// list.add(msg);
						if (count > 1) {
//							System.out.println("Msg size: " + msg.length());
//							System.out.println("Msg position: " + position);
//							System.out.println("Msg hashcode: " + msg.substring(0, 100).hashCode());
							position += msg.length();
							list.add(new MailHash(msg.length(),msg.substring(0, 100).hashCode()));
							System.out.println(mail);
							Document doc = new Document();
							//TODO Path in mail needs to be fixed
							//doc.add(Field.Keyword("path", f.getPath()));

							doc.add(Field.Text("type", "mail"));
							doc.add(Field.Text("icon", "icon data"));
							doc.add(Field.Text("url", "url data"));
							doc.add(Field.Text("from", mail.getFrom()));
							doc.add(Field.Text("subject", mail.getSubject()));
							
							doc.add(Field.Text("contents", msg.toString()));
							//doc.add(Field.Keyword("modified", DateField.timeToString(f.lastModified())));
							writer.addDocument(doc);
						}
						
						
						mail = new Mail();						
						msg = new StringBuffer();
					}
					if (str.startsWith("From:")) {
					    mail.setFrom(str);
					}
					if (str.startsWith("To:")) {
					    mail.setTo(str);
					}
					if (str.startsWith("Date:")) {
					    mail.setDate(str);
					}
					if (str.startsWith("Subject:")) {
					    mail.setSubject(str);
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

	private static Object read(String filename) throws ClassNotFoundException {
		ObjectInputStream in = null;

		try {
			in = new ObjectInputStream(new FileInputStream(filename));

			return in.readObject();
		} catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException exception) {
				}
			}
		}
        return in;
	}
	

	
}
