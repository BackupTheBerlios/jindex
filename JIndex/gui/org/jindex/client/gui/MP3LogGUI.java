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
 * 
 */
public class MP3LogGUI extends MainContentsGUI {
	Document doc;
	public MP3LogGUI(Document _doc) {
		super(_doc);
		doc = _doc;
		setOpenAction(doc.get("path"));
	}
	

    public String getTextContent() {
		String textstring = "";
		String artist = doc.get("artist").trim();
		String title = doc.get("title").trim();
		String album = doc.get("album").trim();
		textstring += "<span font_desc=\"sans bold 10\">" + title + " from album "+album+"</span>\n";
		textstring += "<span font_desc=\"sans 10\">Artist:" + artist + "</span>\n";
		textstring += album;

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