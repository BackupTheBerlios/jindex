/*
 * Created on Feb 8, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package gui;

import java.io.UnsupportedEncodingException;

import javax.mail.internet.MimeUtility;

import org.apache.lucene.document.Document;

import utils.JStringUtils;

/**
 * @author sorenm
 */
public class MailGUI extends MainContentsGUI {
	Document doc;

	public MailGUI(Document _doc) {
		super(_doc);
		doc = _doc;
		setOpenAction(doc.get("path"));
		setOpenAction("evolution");

		// " \"email://local@local/Inbox;uid="+doc.get("uid")+"\"");
	}

	public String getTextContent() {
		String textstring = "Wrong mimeencoding";
		try {
			String from = JStringUtils.encodeXMLEntities(MimeUtility.decodeText(doc.get("from").trim()));
			String subject = JStringUtils.encodeXMLEntities(MimeUtility.decodeText(doc.get("subject")));
			textstring = "<span font_desc=\"sans bold 10\">" + from + "</span>\n";
			textstring += subject;

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return textstring;
	}

	public String[] getOpenAction() {
		String[] value = new String[2];
		value[0] = "evolution";
		value[1] = "email://local@local/Inbox;uid=" + doc.get("uid");
		return value;
	}

	// public byte[] getIcon() {
	// String type = doc.get("type");
	// type =type = StringUtils.replace(type,"/","-");
	// File f = new File("images/mimetypes/gnome-mime-"+type+".png");
	// try {
	// return FileUtility.getBytesFromFile(f);
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// return null;
	// }
}