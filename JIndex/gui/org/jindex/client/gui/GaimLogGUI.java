/*
 * Created on Feb 8, 2005
 */
package org.jindex.client.gui;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;

import org.jindex.documents.GaimLogDocument;

import org.jindex.utils.FileUtility;

/**
 * @author sorenm
 * 
 */
public class GaimLogGUI extends MainContentsGUI {
	Document doc;
	Logger log = Logger.getLogger(GaimLogGUI.class);
	public GaimLogGUI(Document _doc) {
		super(_doc);
		doc = _doc;
		setOpenAction(doc.get("path"));
	}

	public byte[] getIcon() {
		byte[] outicon=null;
		
		String icon = doc.get("icon");
		if(icon != null) {
			//outicon = FileUtility.getBytesFromFile(new URL("file://"+icon).openStream());
			return FileUtility.getIcon(icon);
		}
		
		if(outicon == null) {
			log.debug("Getting image via protocal '"+doc.get("protocol")+"'.");
			icon = new File(".").getAbsolutePath() + "/images/gaim/im-" + doc.get("protocol") + ".png";
			try {
				outicon = FileUtility.getBytesFromFile(new File(icon));
				return FileUtility.getIcon(icon);
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		return outicon;
	}
	

	public String getTextContent() {
		String imwith = doc.get("alias");
		if (imwith.equals(""))
			imwith = doc.get("from");
		String textstring = "<span font_desc=\"sans bold 10\">Conversation with " + imwith.trim() + "</span>\n";
		textstring += "Started: " + doc.get("starttime") + " on the " + doc.get("startdate") + "\n";
		textstring += "Ended at " + doc.get("endtime") + "\n";
		return textstring;
	}

	public String[] getOpenAction() {
		String[] value = new String[2];
		value[0] = "gnome-open";
		value[1] = doc.get("path");
		return value;
	}
}