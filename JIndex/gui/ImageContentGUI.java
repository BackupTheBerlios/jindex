/*
 * Created on Feb 8, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package gui;

import org.apache.commons.codec.binary.Base64;
import org.apache.lucene.document.Document;
import org.gnu.gdk.Color;
import org.gnu.gdk.PixbufLoader;
import org.gnu.gtk.HBox;
import org.gnu.gtk.Label;
import org.gnu.gtk.StateType;
import org.gnu.gtk.VBox;
import org.gnu.gtk.Widget;

/**
 * @author sorenm
 * 
 */
public class ImageContentGUI extends MainContentsGUI {
    Document doc;
    String textstring = "";
    public ImageContentGUI(Document _doc) {
        super(_doc);
        doc = _doc;
        setOpenAction(doc.get("path"));

    }
    public Widget getGnomeGUI(boolean alternaterow) {
        // start contentpane design
        
        
        HBox content = new HBox(false, 0);
        VBox textcontent = new VBox(false, 0);
        org.gnu.gtk.Image img = new org.gnu.gtk.Image("images/gaim/im-icq.gif");
        Base64 b64 = new Base64();
        
        String code = doc.get("thumbnail").trim();
        
        byte [] image = b64.decode(code.getBytes());
         PixbufLoader loader = new PixbufLoader();
         loader.write(image);
         img = new org.gnu.gtk.Image(loader.getPixbuf());
        
        //img = new org.gnu.gtk.Image(icon);
         textstring = "Size: "+doc.get("image-height")+"x"+doc.get("image-width");
        Label fileinfo = new Label(textstring);
        textstring += "\n"+doc.get("name");
        Label filepath = new Label(doc.get("name"));
        fileinfo.setAlignment(0, 0);
        filepath.setAlignment(0, 0);
        textcontent.packStart(filepath,true, true, 1);
        textcontent.packStart(fileinfo,true, true, 1);

       
        

        if(alternaterow) {
            content.setBackgroundColor(StateType.ACTIVE, new Color(23,23,23));
            content.setBackgroundColor(StateType.NORMAL, new Color(23,23,23));
            content.setBackgroundColor(StateType.INSENSITIVE, new Color(23,23,23));
            content.setBackgroundColor(StateType.SELECTED, Color.ORANGE);
            content.setBaseColor(StateType.SELECTED, new Color(23,123,223));
            textcontent.setBackgroundColor(StateType.NORMAL, new Color(23,23,23));
        }

        content.packStart(img,false, true, 1);
        content.packStart(textcontent, true, false, 1);
        return content;
        // end contentpane design
    }
//    public ImageContentGUI getGUI() {
//        JPanel imgpane = getImagepane();
//        JPanel infopane = getDescriptionpane();
//        JTextArea textpane = getFreePane();
//
//        JLabel imagelabel = null;
//        try {
//
//            BASE64Decoder decoder = new BASE64Decoder();
//            String code = doc.get("thumbnail").trim();
//            ImageIcon icon = new ImageIcon(decoder.decodeBuffer(code));
//            
//            imagelabel = new JLabel(icon);
//        } catch (ImageFormatException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        imgpane.add(imagelabel, BorderLayout.CENTER);
//        String result = doc.get("contents");
//        if (result != null) {
//            textpane.setText(result.substring(100));
//        }
//
//        infopane.add(new JLabel(doc.get("name").trim() + " in folder ("
//                + doc.get("absolutepath") + " )"));
//        infopane.add(new JLabel("Width: "+doc.get("image-width").trim()));
//        infopane.add(new JLabel("Height: "+doc.get("image-height").trim()));
//        infopane.validate();
//        return this;
//    }
  
    public String getTextContent() {
		return textstring;
	}


    public byte[] getIcon() {
       // org.gnu.gtk.Image img = new org.gnu.gtk.Image("images/gaim/im-icq.gif");
        Base64 b64 = new Base64();
        
        String code = doc.get("thumbnail").trim();
        
        byte [] image = b64.decode(code.getBytes());
        return image;
	}
	public String[] getOpenAction() {
		// TODO Auto-generated method stub
		return null;
	}
}