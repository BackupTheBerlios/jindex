package documents.mbox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import utils.LuceneUtility;

public class EvolutionMailDocument {
	public static String[] fields = { "path", "type", "date", "from", "subject", "mailcontents" };

	public static void indexMails(File inboxfile) {
		int count = 0;
		try {
			BufferedReader in = new BufferedReader(new FileReader(inboxfile));

			StringBuffer msg = new StringBuffer();
//			MailList list = null;

			String str;
//			list = new MailList();
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
						doc.add(Field.Keyword("path", inboxfile.getAbsolutePath()));
						doc.add(Field.Text("from", mail.getFrom()));
						doc.add(Field.Text("subject", mail.getSubject()));
						doc.add(Field.Keyword("date", mail.getDate()));
						doc.add(Field.Text("maillcontents", msg.toString()));
						doc.add(Field.Text("uid", mail.getUid()));
						// writer.addDocument(doc);
						docs.add(doc);
					}

					mail = new Mail();
					msg = new StringBuffer();
				}
				if (str.startsWith("From:")) {
					mail.setFrom(str);
				} else if (str.startsWith("To:")) {
					mail.setTo(str);
				} else if (str.startsWith("Date:")) {
					mail.setDate(str);
				} else if (str.startsWith("Subject:")) {
					mail.setSubject(str);
				} else if (str.startsWith("X-Evolution:")) {
					// X-Evolution: 000050d3-0010
					String tmp = str.substring(str.indexOf(": ") + 2, str.lastIndexOf("-"));
					mail.setUid("" + Integer.parseInt(tmp, 16));
				} else {
					//msg.append(str);
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

}
