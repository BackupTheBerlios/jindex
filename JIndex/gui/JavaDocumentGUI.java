/*
 * Created on Feb 8, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package gui;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.poi.util.StringUtil;

import utils.FileUtility;

/**
 * @author sorenm
 */
public class JavaDocumentGUI extends MainContentsGUI {
	Document doc;
	public JavaDocumentGUI(Document _doc) {
		super(_doc);
		doc = _doc;
		setOpenAction(doc.get("path"));

	}

	public String getTextContent() {
		String textstring = "<span font_desc=\"sans bold 10\">" + doc.get("file-name").trim() + "</span>\n";
		textstring += "In folder (" + doc.get("absolutepath") + " )";
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