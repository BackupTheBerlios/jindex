/*
 * Created on Feb 8, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package gui;

import org.apache.lucene.document.Document;
import org.gnu.gdk.Color;
import org.gnu.gdk.PixbufLoader;
import org.gnu.gdk.PixbufRotation;
import org.gnu.gtk.GtkStockItem;
import org.gnu.gtk.HBox;
import org.gnu.gtk.IconSize;
import org.gnu.gtk.Image;
import org.gnu.gtk.Label;
import org.gnu.gtk.StateType;
import org.gnu.gtk.VBox;
import org.gnu.gtk.Widget;

/**
 * @author sorenm
 * 
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
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

//		public byte[] getIcon() {
//			Image img = new Image(GtkStockItem.MISSING_IMAGE, IconSize.LARGE_TOOLBAR);
//			return null;
//		}
}