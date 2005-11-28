/*
 * Created on Feb 8, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package gui;

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
			textstring = doc.get("name").trim() + " in folder (" + doc.get("absolutepath") + " )";
			return textstring;
		}

		public String[] getOpenAction() {
			// TODO Auto-generated method stub
			return null;
		}
}