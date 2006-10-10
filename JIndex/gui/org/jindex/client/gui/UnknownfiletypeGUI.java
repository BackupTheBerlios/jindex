/*
 * Created on Feb 8, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.jindex.client.gui;

import org.jindex.client.cache.ImageCache;

import org.apache.lucene.document.Document;
import org.gnu.gtk.VBox;

/**
 * @author sorenm
 */
public class UnknownfiletypeGUI extends MainContentsGUI {
	Document doc;
	VBox imgcontent;
	VBox maincontent;
	private String textstring;
	public UnknownfiletypeGUI(Document _doc) {
		super(_doc);
		doc = _doc;
		setOpenAction(doc.get("path"));

	}
	
		public String getTextContent() {
			String name = doc.get("name"); 
			textstring = name + " in folder (" + doc.get("absolutepath") + " )";
			return textstring;
		}

		public String[] getOpenAction() {
			String[] value = new String[2];
			value[0] = "gnome-open";
			value[1] = doc.get("path");
			return value;
		}
		public byte[] getIcon() {
            return ImageCache.getInstance().getImage(doc.get("type"), "/images/icon_missing.png");
		}
		 
}