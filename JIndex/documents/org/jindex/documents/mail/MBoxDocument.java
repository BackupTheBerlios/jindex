
package org.jindex.documents.mail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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

import org.apache.log4j.Logger;
import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import org.jindex.documents.GaimLogDocument;

public class MBoxDocument {
	static Logger log = Logger.getLogger(MBoxDocument.class);
	public static Document Document(File f) throws java.io.FileNotFoundException {

		Document doc = new Document();

		doc.add(Field.Keyword("path", f.getPath()));


		doc.add(Field.Text("type", "type field"));
		doc.add(Field.Text("icon", "icon data"));
		doc.add(Field.Text("url", "url data"));
		doc.add(Field.Text("from", ""));

		doc.add(Field.Keyword("modified", DateField.timeToString(f.lastModified())));

		
		Session session = Session.getDefaultInstance(new Properties());
		Provider[] providers = session.getProviders();
		Provider provider = null;

		for (int i = 0; i < providers.length; i++) {
			Provider p = providers[i];
			if (p.getProtocol().equals("mbox"))
				provider = p;
		}
		
		Store s;
		try {
			s = session.getStore(provider);
		Folder root;
		
			root = s.getFolder("/home/sorenm/evolution/local/Inbox/mbox");
			root.open(Folder.READ_ONLY);
		
		Message[] messages = root.getMessages();
		for (int i = 0; i < messages.length; i++) {
			// process messages
			Message message = messages[i];
			Address[] addr =message.getFrom();
			log.debug(addr[0]);
		}

		} catch (MessagingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();

		
		}
		FileInputStream is = new FileInputStream(f);
		Reader reader = new BufferedReader(new InputStreamReader(is));
		doc.add(Field.Text("contents", reader));

		// return the document
		return doc;
	}

	public static void main(String args[]) {
		try {
			MBoxDocument.Document(new File("/home/sorenm/evolution/local/Inbox/mbox"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private MBoxDocument() {
	}
}

