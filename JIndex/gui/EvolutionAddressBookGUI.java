/*
 * Created on Feb 8, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package gui;

import java.io.UnsupportedEncodingException;

import javax.mail.internet.MimeUtility;

import org.apache.commons.codec.binary.Base64;
import org.apache.lucene.document.Document;

import utils.JStringUtils;

/**
 * @author sorenm
 */
public class EvolutionAddressBookGUI extends MainContentsGUI {
	Document doc;

	public EvolutionAddressBookGUI(Document _doc) {
		super(_doc);
		doc = _doc;
		setOpenAction(doc.get("path"));
		setOpenAction("evolution");
	}

	public String getTextContent() {
		String textstring = "Wrong mimeencoding";
		try {
			String email = doc.get("emailaddress");
			String fullname = JStringUtils.encodeXMLEntities(MimeUtility.decodeText(doc.get("fullname").trim()));
			// String subject =
			// JStringUtils.encodeXMLEntities(MimeUtility.decodeText(doc.get("subject")));
			textstring = "<span font_desc=\"sans bold 10\">" + fullname + "</span>\n";
			textstring += "<span font_desc=\"sans 10\">EMail:" + email + "</span>\n";
			// textstring += subject;

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return textstring;
	}

	public byte[] getIcon() {
		Base64 b64 = new Base64();

		String code = doc.get("photo");
		if (code != null) {
			System.out.println(code);
			byte[] image = b64.decode(code.getBytes());
			return image;
		}

		return super.getIcon();

	}

	public String[] getOpenAction() {
		String[] value = new String[2];
		value[0] = "evolution";
		value[1] = "email://local@local/Inbox;uid=" + doc.get("uid");
		return value;
	}
}