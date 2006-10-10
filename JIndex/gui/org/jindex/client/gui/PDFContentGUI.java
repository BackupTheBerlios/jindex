/*
 * Created on Feb 8, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package org.jindex.client.gui;

import org.apache.lucene.document.Document;
import org.gnu.gtk.VBox;

import org.jindex.utils.LuceneUtility;

/**
 * @author sorenm
 * 
 */
public class PDFContentGUI extends MainContentsGUI {
	Document doc;
    public VBox imgcontent = new VBox(false, 0);
    public VBox maincontent = new VBox(true, 0);
	public PDFContentGUI(Document _doc) {
		super(_doc);
		doc = _doc;
		setOpenAction(doc.get("path"));

	}

	public String getTextContent() {
		String value = LuceneUtility.getText(doc, "file-name") + " in folder (" +  LuceneUtility.getText(doc, "absolutepath") + " )\n" + "Number of pages: " +  LuceneUtility.getText(doc, "numberofpages");
		return value;
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