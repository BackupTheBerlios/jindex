package org.jindex.documents.mail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;

import org.jindex.documents.GaimLogDocument;

public class TestEvolutionMailDocument {
	static Logger log = Logger.getLogger(TestEvolutionMailDocument.class);
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
				log.debug("A Maillist exsits");
				for (int i = 0; i < list.size(); i++) {
					Mail mail = (Mail) list.get(i);
					long startline = mail.getStartline();

					in.skip(startline - oldpos);
					String from = in.readLine();
					// log.debug(from + " == " +
					// mail.getInternalFrom());
					if (from.equals(mail.getInternalFrom()))
						log.debug("Match");
					else {
						log.debug("No Match");
						log.debug(from + " == " + mail.getInternalFrom());
					}
					oldpos = startline;
				}
			} else {
				log.debug("Mail list was null :o(");
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
						// log.debug(mail);
						doc = new Document();
						// TODO Path in mail needs to be fixed
						// doc.add(getField("path", f.getPath()));

						doc.add(getField("type", "mail"));
						// doc.add(getField("icon", "icon data"));
						doc.add(getField("path", inboxfile.getAbsolutePath()));
						doc.add(getField("from", mail.getFrom()));
						doc.add(getField("subject", mail.getSubject()));

						doc.add(getField("maillcontents", msg.toString()));
						log.debug("Found UID: " + mail.getUid());
						doc.add(getField("uid", mail.getUid()));
						// doc.add(getField("modified",
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
				// log.debug(msg.length());
				linecount++;
				charcounter += str.getBytes().length;
			}

			in.close();
			writeObject(list);
			log.debug("Done");
		} catch (IOException e) {
			e.printStackTrace();
		}
		log.debug(count);

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
						//log.debug(sb.toString());
						sb = new StringBuffer();
					}
				}
			}
			long now = System.currentTimeMillis();
			log.debug("call took: " + (now - test));
			System.exit(0);
		} catch (Exception e) {
		}
	}
            private static Field getField(String name, String value) {
        return new Field(name, value.getBytes(), Field.Store.YES);
    }
}
