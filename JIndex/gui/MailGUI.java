/*
 * Created on Feb 8, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package gui;

import java.awt.BorderLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

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
public class MailGUI extends MainContentsGUI  {
	Document doc;
	String filepath="file:///home/sorenm/workspace/Spider/";
	public MailGUI(Document _doc) {
		super();
		doc = _doc;
	
	}
	public MailGUI getGUI() {
		JPanel imgpane = getImagepane();
		JPanel infopane = getDescriptionpane();
		JTextArea textpane = getFreePane();
		
		JLabel imagelabel = null;
		imagelabel = new JLabel(new ImageIcon("emblem-sound.png"));
		imgpane.add(imagelabel, BorderLayout.CENTER);
		String result = doc.get("title").trim()+" performed by "+doc.get("artist").trim()+"\n";
		textpane.setText(result);
		infopane.add(new JLabel(doc.get("from")));
		return this;
	}
	/**
	 * @return
	 */
	public String getHTML() {
		String result="";
		
		result += "<tr>";
		String icon = doc.get("icon");
		if(icon==null) {
			icon = "im-"+doc.get("protocol")+".png";
		}
		result += "<td>";
		result += "<img src='"+filepath+icon+"' height=24 width=24></img>";
		result += "</td>";
		String imwith = doc.get("alias");
		//System.out.println("From= "+doc.get("from")+"\talias="+doc.get("alias"));
		if(imwith != null) {
		if (imwith.equals(""))
			imwith = doc.get("from");
		}
		//if (imwith.equals(""))
		//	System.out.println(doc.get("contents"));
		result += "<td>";
		result += " Conversation with " + imwith+"<br>\n";
		result += " Started: " + doc.get("starttime")+" on the "+doc.get("startdate")+"<br>\n";
		result += " Ended at " + doc.get("endtime")+"<br>\n<hr>";
		result += "</td>";
		result += "</tr>";
		//System.out.println(result);
		return result;
	}
}