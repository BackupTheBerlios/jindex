/*
 * Created on Feb 8, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.apache.lucene.document.Document;
import org.gnu.gdk.Bitmap;
import org.gnu.gdk.Pixbuf;
import org.gnu.gtk.HBox;
import org.gnu.gtk.Label;
import org.gnu.gtk.VBox;
import org.gnu.gtk.Widget;

import sun.misc.BASE64Decoder;
import sun.misc.UUDecoder;

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
public class ImageContentGUI extends MainContentsGUI {
    Document doc;

    public ImageContentGUI(Document _doc) {
        super();
        doc = _doc;
        setOpenAction(doc.get("path"));

    }
    public Widget getGnomeGUI() {
        // start contentpane design
        HBox content = new HBox(true, 0);
        VBox textcontent = new VBox(false, 0);
        org.gnu.gtk.Image img = new org.gnu.gtk.Image("images/gaim/im-icq.gif");
        BASE64Decoder decoder = new BASE64Decoder();
        String code = doc.get("thumbnail").trim();
        Pixbuf icon=null;
        try {
            byte [] image = decoder.decodeBuffer(code);
           
            System.out.println(image.length);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //img = new org.gnu.gtk.Image(icon);
        Label fileinfo = new Label("123 /home/sorenm/test.doc");
        Label fileinfo1 = new Label("Word document type");
        textcontent.add(fileinfo);
        textcontent.add(fileinfo1);
        
        
        content.add(img);
        content.add(textcontent);
        return content;
        // end contentpane design
    }
    public JPanel getGUI() {
        JPanel imgpane = getImagepane();
        JPanel infopane = getDescriptionpane();
        JTextArea textpane = getFreePane();

        JLabel imagelabel = null;
        try {

            BASE64Decoder decoder = new BASE64Decoder();
            String code = doc.get("thumbnail").trim();
            ImageIcon icon = new ImageIcon(decoder.decodeBuffer(code));
            
            imagelabel = new JLabel(icon);
        } catch (ImageFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        imgpane.add(imagelabel, BorderLayout.CENTER);
        String result = doc.get("contents");
        if (result != null) {
            textpane.setText(result.substring(100));
        }

        infopane.add(new JLabel(doc.get("name").trim() + " in folder ("
                + doc.get("absolutepath") + " )"));
        infopane.add(new JLabel("Width: "+doc.get("image-width").trim()));
        infopane.add(new JLabel("Height: "+doc.get("image-height").trim()));
        infopane.validate();
        return this;
    }
  
  

}