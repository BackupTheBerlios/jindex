package org.jindex.documents.mail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Provider;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.URLName;

import org.apache.log4j.Logger;
import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;

import org.jindex.documents.GaimLogDocument;

public class MBoxProcessor {
	static Logger log = Logger.getLogger(MBoxProcessor.class);
	public static void ProcessMBoxFile(File f, IndexWriter writer) throws java.io.FileNotFoundException {

		// Add the path of the file as a field named "path". Use a Text field,
		// so
		// that the index stores the path, and so that the path is searchable

		Session session = Session.getDefaultInstance(new Properties());
		Provider[] providers = session.getProviders();
		Provider provider = null;

		for (int i = 0; i < providers.length; i++) {
			Provider p = providers[i];
			log.debug(p.getProtocol());
			if (p.getProtocol().equals("mbox"))
				provider = p;
		}

		Store s;
		try {

			URLName url = new URLName("mbox:///home/sorenm/.evolution/mail/local/Sent");
			Store store = session.getStore(url);
			Folder root = store.getDefaultFolder();
			log.debug("url open");
			root.open(Folder.READ_ONLY);
				log.debug("url open done");
			s = session.getStore(session.getProvider("mbox"));
			
			log.debug("Getting mbox: " + f.getPath());
			s.connect();
			root = s.getFolder(f.getPath());

			// root.addMessageChangedListener(this);
			log.debug("Got Folder");
			log.debug(root.getFullName());
			root.open(Folder.READ_ONLY);
			// root.
			log.debug("Opened");

			Message[] messages = root.getMessages();
			log.debug("Found " + root.getMessageCount() + " messages..");
			for (int i = 1; i < messages.length; i++) {
				// for (int i = 1; i < root.getMessageCount(); i++) {

				// process messages
				// Message message = messages[i];
				Message message = root.getMessage(i);
				Address[] addr = message.getFrom();
				String from = "";
				for (int j = 0; j < addr.length; j++) {
					from = addr[j] + " ";
				}

				log.debug("From = " + from);
				// make a new, empty document
				Document doc = new Document();
				doc.add(getField("path", f.getPath()));

				// doc.add(getField("type", match.getMimeType()));
				doc.add(getField("type", "mail"));
				doc.add(getField("icon", "icon data"));
				doc.add(getField("url", "url data"));
				doc.add(getField("from", from));
				doc.add(getField("subject", message.getSubject()));

				doc.add(getField("contents", message.getSubject() + " " + from));
				// Add the last modified date of the file a field named
				// "modified". Use
				// a
				// Keyword field, so that it's searchable, but so that no
				// attempt is
				// made
				// to tokenize the field into words.
				doc.add(getField("modified", DateField.timeToString(f.lastModified())));
				writer.addDocument(doc);
			}

		} catch (MessagingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// Add the contents of the file a field named "contents". Use a Text
		// field, specifying a Reader, so that the text of the file is
		// tokenized.
		// ?? why doesn't FileReader work here ??
		FileInputStream is = new FileInputStream(f);
		Reader reader = new BufferedReader(new InputStreamReader(is));
		// doc.add(getField("contents", reader));

		// return the document
		// return doc;
	}

	private MBoxProcessor() {
	}
            private static Field getField(String name, String value) {
        return new Field(name, value.getBytes(), Field.Store.YES);
    }
}
