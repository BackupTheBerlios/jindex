/*
 * Created on Feb 8, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package gui;

import org.apache.lucene.document.Document;

/**
 * @author sorenm
 */
public class TomboyDocumentGUI extends MainContentsGUI {
	Document doc;
	public TomboyDocumentGUI(Document _doc) {
		super(_doc);
		doc = _doc;
		setOpenAction(doc.get("path"));

	}

	public String getTextContent() {
		String textstring = "<span font_desc=\"sans bold 10\">" + doc.get("title").trim() + "</span>\n";
		textstring += "Last updated on (" + doc.get("last-changed") + " )";
		return textstring;
	}

	public String[] getOpenAction() {
		String[] value = new String[2];
		value[0] = "tomboy";
		value[1] = doc.get("path");
		return value;
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