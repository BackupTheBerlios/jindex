package documents.mbox;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;

public class EvolutionMailDocument {
	public static String[] fields = { "path", "type", "from", "subject", "mailcontents"};
	public static void indexMails(IndexWriter writer, File inboxfile) {
			

		int count = 0;
		try {
			BufferedReader in = new BufferedReader(new FileReader(inboxfile));

			StringBuffer msg = new StringBuffer();
			MailList list = null;

			String str;
			list = new MailList();
			Mail mail = null;
			Document doc;
			List docs = new LinkedList();
			while ((str = in.readLine()) != null) {
				if (str.startsWith("From ")) {
					count++;
					if (count > 1) {
						list.add(msg);
						//System.out.println(mail);
						doc = new Document();
						// TODO Path in mail needs to be fixed
						// doc.add(Field.Keyword("path", f.getPath()));

						doc.add(Field.Text("type", "mail"));
						// doc.add(Field.Text("icon", "icon data"));
						doc.add(Field.Keyword("path", inboxfile.getAbsolutePath()));
						doc.add(Field.Text("from", mail.getFrom()));
						doc.add(Field.Text("subject", mail.getSubject()));

						doc.add(Field.Text("maillcontents", msg.toString()));
						// doc.add(Field.Keyword("modified",
						// DateField.timeToString(f.lastModified())));
						// writer.addDocument(doc);
						docs.add(doc);
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
				// msg.append(str);
				// System.out.println(msg.length());

			}
			in.close();
			Iterator ite = docs.iterator();
			while(ite.hasNext()) {
				Document newdoc = (Document) ite.next();
				writer.addDocument(newdoc);
			}
			writer.optimize();
			System.out.println("Done");
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(count);

	}

}
