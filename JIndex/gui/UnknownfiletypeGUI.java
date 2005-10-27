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
		super();
		doc = _doc;
		setOpenAction(doc.get("path"));

	}
	
	public Widget getGnomeGUI(boolean alternaterow) {
		// start contentpane design
		HBox content = new HBox(false, 0);
		VBox textcontent = new VBox(false, 0);
		imgcontent = new VBox(false, 0);
		maincontent = new VBox(false, 0);

		Image img = new Image(GtkStockItem.MISSING_IMAGE, IconSize.LARGE_TOOLBAR);
		img.setMinimumSize(48, 48);
		System.out.println((doc.get("type")));
		textstring = doc.get("name").trim() + " in folder (" + doc.get("absolutepath") + " )";
		textcontent.add(new Label(textstring));
		String result = doc.get("filecontents");
		System.out.println(result);
		if (result != null) {
			Label filepath = new Label(result.substring(100));
			textcontent.add(filepath);
			textcontent.add(new Label(doc.get("type")));
		}

		if (alternaterow) {
			content.setBackgroundColor(StateType.ACTIVE, new Color(23, 23, 23));
			content.setBackgroundColor(StateType.NORMAL, new Color(23, 23, 23));
			content.setBackgroundColor(StateType.INSENSITIVE, new Color(23, 23, 23));
			content.setBackgroundColor(StateType.SELECTED, Color.ORANGE);
			content.setBaseColor(StateType.SELECTED, new Color(23, 123, 223));
			textcontent.setBackgroundColor(StateType.NORMAL, new Color(23, 23, 23));
		}
		imgcontent.add(img);
		
		maincontent.add(textcontent);
		maincontent.add(getOpenButton());
		content.packStart(imgcontent, true, true, 30);
		content.packEnd(maincontent, true, true, 20);

		return content;
		// end contentpane design
	}
	public VBox getImageIconPane() {
		return imgcontent;
		
	}
	public VBox getMainContent() {
		return maincontent;
		
	}
		public String getTextContent() {
			textstring = doc.get("name").trim() + " in folder (" + doc.get("absolutepath") + " )";
			return textstring;
		}

		public byte[] getIcon() {
			Image img = new Image(GtkStockItem.MISSING_IMAGE, IconSize.LARGE_TOOLBAR);
			return null;
		}
}