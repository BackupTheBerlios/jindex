/*
 * Created on Feb 8, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package gui;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.apache.lucene.document.Document;
import org.gnu.gdk.Color;
import org.gnu.gdk.Colormap;
import org.gnu.gdk.PixbufLoader;
import org.gnu.gtk.Adjustment;
import org.gnu.gtk.AttachOptions;
import org.gnu.gtk.GtkStockItem;
import org.gnu.gtk.HBox;
import org.gnu.gtk.HScale;
import org.gnu.gtk.IconSize;
import org.gnu.gtk.Image;
import org.gnu.gtk.ImageStockData;
import org.gnu.gtk.Label;
import org.gnu.gtk.ResizeMode;
import org.gnu.gtk.StateType;
import org.gnu.gtk.Table;
import org.gnu.gtk.VBox;
import org.gnu.gtk.Widget;

import sun.misc.BASE64Decoder;

/**
 * @author sorenm
 * 
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class UnknownfiletypeGUI extends MainContentsGUI {
	Document doc;
	public UnknownfiletypeGUI(Document _doc) {
		super();
		doc = _doc;
		setOpenAction(doc.get("path"));

	}
     public Widget getGnomeGUI(boolean alternaterow) {
            // start contentpane design
       
            HBox content = new HBox(false, 0);
            VBox textcontent = new VBox(false, 0);
            
            Image img = new Image(GtkStockItem.MISSING_IMAGE,IconSize.BUTTON);
            img.setMinimumSize(48, 48);
           System.out.println((doc.get("type")));
            textcontent.add(new Label(doc.get("name").trim()+" in folder ("+doc.get("absolutepath")+" )"));
            String result = doc.get("filecontents");
            System.out.println(result);
            if(result != null) {
                Label filepath = new Label(result.substring(100));
                textcontent.add(filepath);
                textcontent.add(new Label(doc.get("type")));
            }

        
            if(alternaterow) {
                content.setBackgroundColor(StateType.ACTIVE, new Color(23,23,23));
                content.setBackgroundColor(StateType.NORMAL, new Color(23,23,23));
                content.setBackgroundColor(StateType.INSENSITIVE, new Color(23,23,23));
                content.setBackgroundColor(StateType.SELECTED, Color.ORANGE);
                content.setBaseColor(StateType.SELECTED, new Color(23,123,223));
                textcontent.setBackgroundColor(StateType.NORMAL, new Color(23,23,23));
            }
            
            content.packStart(img,true, true, 30);
            content.packEnd(textcontent, true, true, 20);
      
            return content;
            // end contentpane design
        }
     
	public JPanel getGUI() {
		JPanel imgpane = getImagepane();
		JPanel infopane = getDescriptionpane();
		JTextArea textpane = getFreePane();
		
		JLabel imagelabel = null;
		imagelabel = new JLabel(new ImageIcon("emblem-sound.png"));
		imgpane.add(imagelabel, BorderLayout.CENTER);
		String result = doc.get("contents");
		if(result != null) {
			textpane.setText(result.substring(100));	
		}
		
		infopane.add(new JLabel(doc.get("name").trim()+" in folder ("+doc.get("absolutepath")+" )"));
		return this;
	}
       
	
  
       
}