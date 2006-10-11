/*
 * Created on Feb 8, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.jindex.client.gui;

import org.apache.lucene.document.Document;

/**
 * @author sorenm
 */
public class HTMLDocumentGUI extends MainContentsGUI {
	Document doc;
	public HTMLDocumentGUI(Document _doc) {
		super(_doc);
		doc = _doc;
		setOpenAction(doc.get("path"));

	}

	public String getTextContent() {
		String textstring = "<span font_desc=\"sans bold 10\">" + doc.get("file-name").trim() + "</span>\n";
		textstring += "In folder (" + doc.get("absolutepath") + " )";
		return textstring;
	}

	public String[] getOpenAction() {
		String[] value = new String[2];
		value[0] = "gnome-open";
		value[1] = doc.get("path");
		return value;
	}

	public byte[] getIcon() {
		return super.getDefaultIcon();
	}
}