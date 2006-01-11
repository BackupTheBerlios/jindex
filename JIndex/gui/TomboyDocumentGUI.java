/*
 * Created on Feb 8, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package gui;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.apache.lucene.document.Document;

import utils.FileUtility;

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

	public byte[] getIcon() {
		 return FileUtility.getIcon("/images/tomboy/stock_notes.png");
	}
}