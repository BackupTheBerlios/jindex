/*
 * Created on Feb 8, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package gui;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;

import utils.JStringUtils;

/**
 * @author sorenm
 */
public class MailGUI extends MainContentsGUI  {
	Document doc;
	public MailGUI(Document _doc) {
		super(_doc);
		doc = _doc;
		setOpenAction(doc.get("path"));
	}
	public String getTextContent() {
		String from = JStringUtils.encodeXMLEntities(doc.get("from").trim());
		String subject = JStringUtils.encodeXMLEntities(doc.get("subject"));
		String textstring = "<span font_desc=\"sans bold 10\">" + from + "</span>\n";
		textstring += subject;
		return textstring;
	}

//	public byte[] getIcon() {
//		String type = doc.get("type");
//		type =type = StringUtils.replace(type,"/","-");
//		File f = new File("images/mimetypes/gnome-mime-"+type+".png");
//		try {
//			return FileUtility.getBytesFromFile(f);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
}