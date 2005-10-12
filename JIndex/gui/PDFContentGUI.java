/*
 * Created on Feb 8, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package gui;

import org.apache.lucene.document.Document;
import org.gnu.gdk.Color;
import org.gnu.gtk.Alignment;
import org.gnu.gtk.GtkStockItem;
import org.gnu.gtk.HBox;
import org.gnu.gtk.IconSize;
import org.gnu.gtk.Image;
import org.gnu.gtk.Label;
import org.gnu.gtk.StateType;
import org.gnu.gtk.VBox;
import org.gnu.gtk.Widget;

import utils.LuceneUtility;

/**
 * @author sorenm
 * 
 */
public class PDFContentGUI extends MainContentsGUI {
	Document doc;
    public VBox imgcontent = new VBox(false, 0);
    public VBox maincontent = new VBox(true, 0);
	public PDFContentGUI(Document _doc) {
		super();
		doc = _doc;
		setOpenAction(doc.get("path"));

	}

	public Widget getGnomeGUI(boolean alternaterow) {
		// start contentpane design

		HBox content = new HBox(false, 0);
		HBox textcontent = new HBox(false, 0);
	

		Image img = new Image(GtkStockItem.MISSING_IMAGE, IconSize.LARGE_TOOLBAR);
		img.setMinimumSize(48, 48);
       
        Label label = new Label( LuceneUtility.getText(doc, "file-name") + " in folder (" +  LuceneUtility.getText(doc, "absolutepath") + " )\n" + "Number of pages: " +  LuceneUtility.getText(doc, "numberofpages"));
		textcontent.packStart(label, false, true, 0);
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

        imgcontent.packStart(img, false, true, 0);

      
        maincontent.add(textcontent);
        maincontent.add(getOpenButton());

		content.packStart(imgcontent, true, true, 1);
		content.packStart(maincontent, false, false, 30);


		return content;
		// end contentpane design
	}

}