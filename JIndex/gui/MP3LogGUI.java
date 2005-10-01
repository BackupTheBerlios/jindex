/*
 * Created on Feb 8, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package gui;

import org.apache.lucene.document.Document;
import org.gnu.gdk.Color;
import org.gnu.gtk.HBox;
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
public class MP3LogGUI extends MainContentsGUI {
	Document doc;
	public MP3LogGUI(Document _doc) {
		super();
		doc = _doc;
		setOpenAction(doc.get("path"));
	}
	
    public Widget getGnomeGUI(boolean alternaterow) {
        // start contentpane design
        HBox content = new HBox(true, 0);
        VBox textcontent = new VBox(false, 0);
        org.gnu.gtk.Image img = new org.gnu.gtk.Image("emblem-sound.png");
    
        String result = doc.get("title").trim()+" performed by "+doc.get("artist").trim()+"\n";
        Label fileinfo = new Label(result);
        Label fileinfo1 = new Label(doc.get("title").trim());
        textcontent.add(fileinfo);
        textcontent.add(fileinfo1);
        
        
        content.add(img);
        content.add(textcontent);
        if(alternaterow) {
            content.setBackgroundColor(StateType.NORMAL, new Color(23,23,23));
        }
        return content;
        // end contentpane design
    }
	
	
}