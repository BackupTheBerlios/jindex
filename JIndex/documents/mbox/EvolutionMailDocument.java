package documents.mbox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import utils.LuceneUtility;

public class EvolutionMailDocument {
	public static String[] fields = { "path", "type", "date", "from",
			"subject", "mailcontents" };

	public static void indexMails(File inboxfile) {
		int count = 0;
		try {
			BufferedReader in = new BufferedReader(new FileReader(inboxfile));

			StringBuffer msg = new StringBuffer();
			// MailList list = null;

			String str;
			// list = new MailList();
			Mail mail = null;
			Document doc;
			List docs = new LinkedList();
			while ((str = in.readLine()) != null) {
				if (str.startsWith("From ")) {
					count++;
					if (count > 1) {
						// System.out.println(mail);
						doc = new Document();
						doc.add(Field.Text("type", "mail"));
						doc.add(Field.Keyword("path", inboxfile
								.getAbsolutePath()));
						doc.add(Field.Text("from", mail.getFrom()));
						doc.add(Field.Text("subject", mail.getSubject()));
						doc.add(Field.Keyword("date", mail.getDate()));
						doc.add(Field.Text("maillcontents", msg.toString()));
						doc.add(Field.Text("uid", mail.getUid()));
						// writer.addDocument(doc);
						docs.add(doc);
						
						checkMail(mail.getHostname(), mail.getFrom(), mail.getReceived());
					}

					mail = new Mail();
					msg = new StringBuffer();
				}
				if (str.startsWith("From:")) {
					mail.setFrom(str);
					String tmp = mail.getFrom();
					String hostname = tmp.substring(tmp.indexOf("@") + 1, tmp
							.length());
					System.out.println("Hostname: " + hostname);
					mail.setHostname(hostname);
				} else if (str.startsWith("Received:")) {
					StringTokenizer st = new StringTokenizer(str, " ");
					st.nextToken();
					st.nextToken();
					String host = st.nextToken();
					System.out.println(host);
					mail.addReceived(host);

				} else if (str.startsWith("To:")) {

					mail.setTo(str);
				} else if (str.startsWith("Date:")) {
					mail.setDate(str);
				} else if (str.startsWith("Subject:")) {
					mail.setSubject(str);
				} else if (str.startsWith("X-Evolution:")) {
					// X-Evolution: 000050d3-0010
					String tmp = str.substring(str.indexOf(": ") + 2, str
							.lastIndexOf("-"));
					mail.setUid("" + Integer.parseInt(tmp, 16));
				} else {
					// msg.append(str);
				}
				// System.out.println(msg.length());

			}
			in.close();
			LuceneUtility.removeEntry(inboxfile.getAbsolutePath());
			LuceneUtility.addDocuments(docs);
			System.out.println("Done");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	public static void send(String s, BufferedReader in, PrintWriter out)
			throws java.io.IOException {
		// Send the SMTP command
		if (s != null) {
			System.out.println("sending: " + s);
			out.print(s + "\r\n");
			out.flush();
		}
		// Wait for the response
		String line = in.readLine();
		if (line != null) {
			System.out.println("Answer: " + line);
		}

	}

	/**
	 * Called when the send button is clicked. Actually sends the mail message.
	 * @param list 
	 * 
	 * @param event
	 *            The event.
	 */
	public static void checkMail(String host, String to, List list) {
		PrintWriter out;
		BufferedReader in;

		try {
			Iterator ite = list.iterator();
			while(ite.hasNext()) {
				String toHost = ite.next().toString();
			Socket s = new Socket(toHost, 25);
			out = new java.io.PrintWriter(s.getOutputStream());
			in = new java.io.BufferedReader(new java.io.InputStreamReader(s
					.getInputStream()));

			send(null, in, out);
			// send("HELO " +
			// java.net.InetAddress.getLocalHost().getHostName());
			send("MAIL FROM:test@test.com", in, out);
			send("RCPT TO:" + to, in, out);
			send("QUIT", in, out);
			s.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
