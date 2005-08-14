/*
 * Created on Feb 8, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package gui;

import java.awt.BorderLayout;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.apache.lucene.document.Document;

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