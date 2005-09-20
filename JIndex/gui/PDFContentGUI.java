/*
 * Created on Feb 8, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.apache.lucene.document.Document;
import org.gnu.gdk.Color;
import org.gnu.gtk.*;
import org.gnu.gtk.GtkStockItem;
import org.gnu.gtk.HBox;
import org.gnu.gtk.IconSize;
import org.gnu.gtk.Label;
import org.gnu.gtk.StateType;
import org.gnu.gtk.VBox;
import org.gnu.gtk.Widget;

import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * @author sorenm
 * 
 */
public class PDFContentGUI extends MainContentsGUI {
	Document doc;

	public PDFContentGUI(Document _doc) {
		super();
		doc = _doc;
		setOpenAction(doc.get("path"));

	}

	public Widget getGnomeGUI(boolean alternaterow) {
		// start contentpane design

		HBox content = new HBox(false, 0);
		VBox textcontent = new VBox(false, 0);
		VBox imgcontent = new VBox(false, 0);
		VBox maincontent = new VBox(false, 0);

		Image img = new Image(GtkStockItem.MISSING_IMAGE, IconSize.BUTTON);
		img.setMinimumSize(48, 48);
		System.out.println((doc.get("type")));
		textcontent.add(new Label(doc.get("name").trim() + " in folder (" + doc.get("absolutepath") + " )\n" + "Number of pages: " + doc.get("numberofpages")));
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

}