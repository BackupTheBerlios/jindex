/*
 * Created on Feb 8, 2005
 */
package gui;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.apache.lucene.document.Document;

import utils.FileUtility;

/**
 * @author sorenm
 * 
 */
public class GaimLogGUI extends MainContentsGUI {
	Document doc;

	public GaimLogGUI(Document _doc) {
		super(_doc);
		doc = _doc;
		setOpenAction(doc.get("path"));
	}

	public byte[] getIcon() {
		String icon = doc.get("icon");
		 if (icon == null) {
			icon = new File(".").getAbsolutePath() + "/images/gaim/im-" + doc.get("protocol") + ".png";
		} else {
			try {
				System.out.println(new URL(icon).openStream().available());
			} catch (MalformedURLException e1) {
			} catch (IOException e1) {
				icon = new File(".").getAbsolutePath() + "/images/gaim/im-" + doc.get("protocol") + ".png";
			}
		}
		 System.out.println(icon);
		
		byte[] image=null;
		try {
			image = FileUtility.getBytesFromFile(new File(icon));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
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
		// TODO Auto-generated method stub
		return null;
	}

	// public JPanel getGUI() {
	// JPanel imgpane = getImagepane();
	// JPanel infopane = getDescriptionpane();
	// JTextArea textpane = getFreePane();
	//		
	// String result="";
	//		
	// String icon = doc.get("icon");
	// if(icon==null) {
	// icon = "file://"+new
	// File(".").getAbsolutePath()+"/images/gaim/im-"+doc.get("protocol")+".png";
	// } else {
	// icon = "file://"+icon;
	// try {
	// System.out.println(new URL(icon).openStream().available());
	// } catch (MalformedURLException e1) {
	// } catch (IOException e1) {
	// icon = "file://"+new
	// File(".").getAbsolutePath()+"/images/gaim/im-"+doc.get("protocol")+".png";
	// }
	// }
	//			
	// JLabel imagelabel = null;
	// try {
	// imagelabel = new JLabel(new ImageIcon(new URL(icon)));
	// } catch (MalformedURLException e) {
	// e.printStackTrace();
	// }
	// imgpane.add(imagelabel, BorderLayout.CENTER);
	// String imwith = doc.get("alias");
	// if (imwith.equals(""))
	// imwith = doc.get("from");
	//		
	// result = "";
	//
	// result += " Conversation with " + imwith+"\n";
	// result += " Started: " + doc.get("starttime")+" on the
	// "+doc.get("startdate")+"\n";
	// result += " Ended at " + doc.get("endtime")+"\n";
	// textpane.setText(result);
	// infopane.add(new JLabel(imwith));
	// return null;
	// }

}