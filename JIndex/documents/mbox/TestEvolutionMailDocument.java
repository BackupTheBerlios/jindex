package documents.mbox;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.sql.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;

import utils.LuceneUtility;

public class TestEvolutionMailDocument {
	public static String[] fields = { "path", "type", "from", "subject", "mailcontents" };

	public static void main(String argv[]) {
		indexMails(new File("/home/sorenm/.evolution/mail/local/Inbox"));
	}

	public static void indexMails(File inboxfile) {
		int count = 0;
		IndexWriter writer;
		try {
			BufferedReader in = new BufferedReader(new FileReader(inboxfile));
			readLinefromFile(inboxfile.getAbsolutePath());

			StringBuffer msg = new StringBuffer();
			MailList list = null;

			String str;
			try {
				list = readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			long oldpos = 0;
			if (list != null) {
				System.out.println("A Maillist exsits");
				for (int i = 0; i < list.size(); i++) {
					Mail mail = (Mail) list.get(i);
					long startline = mail.getStartline();

					in.skip(startline - oldpos);
					String from = in.readLine();
					// System.out.println(from + " == " +
					// mail.getInternalFrom());
					if (from.equals(mail.getInternalFrom()))
						System.out.println("Match");
					else {
						System.out.println("No Match");
						System.out.println(from + " == " + mail.getInternalFrom());
					}
					oldpos = startline;
				}
			} else {
				System.out.println("Mail list was null :o(");
				list = new MailList();
			}
			Mail mail = null;
			int linecount = 0;
			Document doc;
			long charcounter = 0;
			List docs = new LinkedList();
			while ((str = in.readLine()) != null) {

				if (str.startsWith("From ")) {
					count++;
					if (count > 1) {
						list.add(mail);
						// System.out.println(mail);
						doc = new Document();
						// TODO Path in mail needs to be fixed
						// doc.add(Field.Keyword("path", f.getPath()));

						doc.add(Field.Text("type", "mail"));
						// doc.add(Field.Text("icon", "icon data"));
						doc.add(Field.Keyword("path", inboxfile.getAbsolutePath()));
						doc.add(Field.Text("from", mail.getFrom()));
						doc.add(Field.Text("subject", mail.getSubject()));

						doc.add(Field.Text("maillcontents", msg.toString()));
						System.out.println("Found UID: " + mail.getUid());
						doc.add(Field.Text("uid", mail.getUid()));
						// doc.add(Field.Keyword("modified",
						// DateField.timeToString(f.lastModified())));
						// writer.addDocument(doc);
						docs.add(doc);
					}

					mail = new Mail();
					msg = new StringBuffer();
					mail.setInternalFrom(str);
					mail.setStartline(charcounter);

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
				if (str.startsWith("X-Evolution:")) {
					// X-Evolution: 000050d3-0010
					String tmp = str.substring(str.indexOf(": ") + 2, str.lastIndexOf("-"));
					mail.setUid("" + Integer.parseInt(tmp, 16));
				}

				// msg.append(str);
				// System.out.println(msg.length());
				linecount++;
				charcounter += str.getBytes().length;
			}

			in.close();
			writeObject(list);
			System.out.println("Done");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(count);

	}

	public static void writeObject(MailList list) throws IOException {
		FileOutputStream out = new FileOutputStream("mailhash");
		ObjectOutputStream s = new ObjectOutputStream(out);
		s.writeObject(list);
		s.flush();
	}

	public static MailList readObject() throws ClassNotFoundException, IOException {
		FileInputStream in;
		try {
			in = new FileInputStream("mailhash");
			if (in == null)
				return null;
			ObjectInputStream s = new ObjectInputStream(in);
			if (s == null)
				return null;

			return (MailList) s.readObject();
		} catch (FileNotFoundException e) {
			return null;
		}

	}

	public static void readLinefromFile(String infile) {
		try {
			// Obtain a channel
			ReadableByteChannel channel = new FileInputStream(infile).getChannel();

			// Create a direct ByteBuffer; see also e158 Creating a ByteBuffer
			ByteBuffer buf = ByteBuffer.allocateDirect(4096);

			int numRead = 0;
			long test = System.currentTimeMillis();
			while (numRead >= 0) {
				// read() places read bytes at the buffer's position so the
				// position should always be properly set before calling read()
				// This method sets the position to 0
				buf.rewind();

				// Read bytes from the channel
				numRead = channel.read(buf);

				// The read() method also moves the position so in order to
				// read the new bytes, the buffer's position must be set back to
				// 0
				buf.rewind();

				// Read bytes from ByteBuffer; see also
				// e159 Getting Bytes from a ByteBuffer
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < numRead; i++) {
					// byte b = buf.get();
					char b = (char)buf.get();
					sb.append(b);
					// str += ((char)b);
					if (b == '\n') {
						//System.out.println(sb.toString());
						sb = new StringBuffer();
					}
				}
			}
			long now = System.currentTimeMillis();
			System.out.println("call took: " + (now - test));
			System.exit(0);
		} catch (Exception e) {
		}
	}
}
