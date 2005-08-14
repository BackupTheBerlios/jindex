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
import java.net.MalformedURLException;
import java.net.URL;

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
public class GaimLogGUI extends MainContentsGUI implements MouseListener {
	Document doc;
	String filepath="file:///home/sorenm/workspace/Spider/";
	public GaimLogGUI(Document _doc) {
		super();
		doc = _doc;
		
	}



	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		System.out.println(arg0.getX());
		try {
			Runtime.getRuntime().exec("gedit "+doc.get("path"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(doc.get("path"));
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * @return
	 */
	public String getHTML() {
		String result="";
		
		result += "<tr>";
		String icon = doc.get("icon");
		if(icon==null) {
			icon = filepath+"im-"+doc.get("protocol")+".png";
		} else {
			icon = "file://"+icon;
		}
			
		result += "<td>";
		result += "<img src='"+icon+"' height=24 width=24></img>";
		result += "</td>";
		String imwith = doc.get("alias");
		if (imwith.equals(""))
			imwith = doc.get("from");
		
		//if (imwith.equals(""))
		//	System.out.println(doc.get("contents"));
		result += "<td>";
		result += " Conversation with " + imwith+"<br>\n";
		result += " Started: " + doc.get("starttime")+" on the "+doc.get("startdate")+"<br>\n";
		result += " Ended at " + doc.get("endtime")+"<br>\n<hr>";
		result += " <a href='" + doc.get("path")+"'>Open</a><br>\n<hr>";
		result += "</td>";
		result += "</tr>";
		//System.out.println(result);
		return result;
	}
	public JPanel getGUI() {
		JPanel imgpane = getImagepane();
		JPanel infopane = getDescriptionpane();
		JTextArea textpane = getFreePane();
		
		String result="";
		
		result += "<tr>";
		String icon = doc.get("icon");
		if(icon==null) {
			icon = filepath+"im-"+doc.get("protocol")+".png";
		} else {
			icon = "file://"+icon;
		}
			
		result += "<td>";
		result += "<img src='"+icon+"' height=24 width=24></img>";
		System.out.println(icon);
		JLabel imagelabel = null;
		try {
			imagelabel = new JLabel(new ImageIcon(new URL(icon)));
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		imgpane.add(imagelabel, BorderLayout.CENTER);
		result += "</td>";
		String imwith = doc.get("alias");
		if (imwith.equals(""))
			imwith = doc.get("from");
		
		//if (imwith.equals(""))
		//	System.out.println(doc.get("contents"));
		result = "";

		result += " Conversation with " + imwith+"\n";
		result += " Started: " + doc.get("starttime")+" on the "+doc.get("startdate")+"\n";
		result += " Ended at " + doc.get("endtime")+"\n";
		//result += " <a href='" + doc.get("path")+"'>Open</a><br>\n<hr>";
		//System.out.println(result);
		textpane.setText(result);
		infopane.add(new JLabel(imwith));
		return this;
	}
       
}