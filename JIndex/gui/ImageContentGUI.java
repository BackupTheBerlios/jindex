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

    public JPanel getGUI() {
        JPanel imgpane = getImagepane();
        JPanel infopane = getDescriptionpane();
        JTextArea textpane = getFreePane();

        JLabel imagelabel = null;
        try {
            //byte[] thumb = generateThumbnail(doc.get("path"));
            //imagelabel = new JLabel(new ImageIcon(thumb, "image"));
            JFrame frame = (JFrame) new JFrame();
            frame.getContentPane().add(generateThumbnail(doc.get("path")));
            frame.pack();
            frame.show();
            imgpane.add(generateThumbnail(doc.get("path")), BorderLayout.CENTER);
            
        } catch (ImageFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //imgpane.add(imagelabel, BorderLayout.CENTER);
//        String result = doc.get("contents");
//        if (result != null) {
//            textpane.setText(result.substring(100));
//        }

        infopane.add(new JLabel(doc.get("name").trim() + " in folder ("
                + doc.get("absolutepath") + " )"));
        return this;
    }

    public JComponent generateThumbnail(final String filename)
            throws ImageFormatException, IOException, InterruptedException {

         // load image from INFILE
         Image image = Toolkit.getDefaultToolkit().getImage(filename);
         MediaTracker mediaTracker = new MediaTracker(new Container());
         mediaTracker.addImage(image, 0);
         mediaTracker.waitForID(0);
         // determine thumbnail size from WIDTH and HEIGHT
         int thumbWidth = 96;
         int thumbHeight = 96;
         double thumbRatio = (double)thumbWidth / (double)thumbHeight;
         int imageWidth = image.getWidth(null);
         int imageHeight = image.getHeight(null);
         double imageRatio = (double)imageWidth / (double)imageHeight;
         if (thumbRatio < imageRatio) {
         thumbHeight = (int)(thumbWidth / imageRatio);
         } else {
         thumbWidth = (int)(thumbHeight * imageRatio);
         }
         // draw original image to thumbnail image object and
         // scale it to the new size on-the-fly
         BufferedImage thumbImage = new BufferedImage(thumbWidth,
         thumbHeight, BufferedImage.TYPE_INT_RGB);
         Graphics2D graphics2D = thumbImage.createGraphics();
         graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
         RenderingHints.VALUE_INTERPOLATION_BILINEAR);
         graphics2D.drawImage(image, 0, 0, thumbWidth, thumbHeight, null);
         // save thumbnail image to OUTFILE
         ByteArrayOutputStream bout = new ByteArrayOutputStream();
         JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(bout);
         JPEGEncodeParam param = encoder.
         getDefaultJPEGEncodeParam(thumbImage);
         int quality = 75;
         quality = Math.max(0, Math.min(quality, 100));
         param.setQuality((float)quality / 100.0f, false);
         encoder.setJPEGEncodeParam(param);
         encoder.encode(thumbImage);
//         return bout.toByteArray();

     

        
         final BufferedImage _bi = ImageIO.read(new File(filename));
        JComponent _thumbNailer = new JComponent()
        {
            public void paint(Graphics g)
            {
                super.paint(g);
                Graphics2D g2d = (Graphics2D) g;
        
              
                
                    BufferedImage buf = ImageUtil.scaleImageToFit(_bi, 96, 96, null);
                    int x = (getWidth() / 2) - (buf.getWidth() / 2);
                    int y = (getHeight() / 2) - (buf.getHeight() / 2);
                    if (y < 0)
                    {
                        y = 0;
                    }
                    if (x < 5)
                    {
                        x = 5;
                    }
                    g2d.drawImage(buf, null, x, y);
             
            }
        };
        
        
        return _thumbNailer;

    }

    public static byte[] getBytes(BufferedImage bim) {
        WritableRaster wr = bim.getWritableTile(0, 0);
        byte[] b = (byte[]) (wr.getDataElements(0, 0, bim.getWidth(), bim
                .getHeight(), null));
        bim.releaseWritableTile(0, 0);
        return b;
    }

}