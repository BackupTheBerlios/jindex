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
import java.awt.Image;
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

import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

/**
 * @author sorenm
 * 
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class PDFContentGUI extends MainContentsGUI {
	Document doc;
	public PDFContentGUI(Document _doc) {
		super();
		doc = _doc;
		setOpenAction(doc.get("path"));

	}
	public JPanel getGUI() {
		JPanel imgpane = getImagepane();
		JPanel infopane = getDescriptionpane();
		JTextArea textpane = getFreePane();
		
		JLabel imagelabel = null;
		try {
			imagelabel = new JLabel(new ImageIcon("PDF"));
		} catch (ImageFormatException e) {
			e.printStackTrace();
		} 
		
		imgpane.add(imagelabel, BorderLayout.CENTER);
		String result = doc.get("contents");
		if(result != null) {
			textpane.setText(result.substring(100));	
		}
		String label = doc.get("name").trim()+" in folder ("+doc.get("absolutepath")+" )\n"+"Number of pages: "+doc.get("numberofpages");
		infopane.add(new JLabel(label));
		return this;
	}
       

	  
  
       
}