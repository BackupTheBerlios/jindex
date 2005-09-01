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
import org.gnu.gdk.Pixbuf;
import org.gnu.gtk.GtkStockItem;
import org.gnu.gtk.HBox;
import org.gnu.gtk.Image;
import org.gnu.gtk.Label;
import org.gnu.gtk.VBox;
import org.gnu.gtk.Widget;

import sun.misc.BASE64Decoder;

/**
 * @author sorenm
 * 
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class MP3LogGUI extends MainContentsGUI implements MouseListener {
	Document doc;
	String filepath="file:///home/sorenm/workspace/Spider/";
	public MP3LogGUI(Document _doc) {
		super();
		doc = _doc;
		setOpenAction(doc.get("path"));
/*		initComponents();
	
			
		IconLable.setIcon(new ImageIcon("emblem-sound.png"));
		IconLable.setText("");
		String title=doc.get("title").trim();
		String artist=doc.get("artist").trim();
		jLabel1.setText("<html>"+artist+"<br>"+title+"</html>");
		*/
	}
	public JPanel getGUI() {
		JPanel imgpane = getImagepane();
		JPanel infopane = getDescriptionpane();
		JTextArea textpane = getFreePane();
		
		JLabel imagelabel = null;
		imagelabel = new JLabel(new ImageIcon("emblem-sound.png"));
		imgpane.add(imagelabel, BorderLayout.CENTER);
		String result = doc.get("title").trim()+" performed by "+doc.get("artist").trim()+"\n";
		textpane.setText(result);
		infopane.add(new JLabel(doc.get("title").trim()));
		return this;
	}
    public Widget getGnomeGUI() {
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
        return content;
        // end contentpane design
    }
	
	public String getHTML() {
		String result="";
		
		result += "<tr>";
		String icon = "emblem-sound.png";
		
		result += "<td>";
		result += "<img src='"+filepath+icon+"' height=24 width=24></img>";
		result += "</td>";
		
		//if (imwith.equals(""))
		//	System.out.println(doc.get("contents"));
		result += "<td>";
		result += doc.get("title").trim()+" performed by "+doc.get("artist").trim()+"<br>\n";
		//result += " Started: " + doc.get("starttime")+" on the "+doc.get("startdate")+"<br>\n";
		//result += " Ended at " + doc.get("endtime")+"<br>\n<hr>";
		result += "</td>";
		result += "</tr>";
		//System.out.println(result);
		return result;
	}
       

	 
    
    
   
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		try {
			Runtime.getRuntime().exec("totem "+doc.get("path"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
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
       
}