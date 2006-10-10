package org.jindex.documents;

import java.awt.Container;
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

import javax.swing.ImageIcon;

import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import sun.misc.BASE64Encoder;

import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class VideoDocument implements SearchDocument {
    public static String[] fields = { "path", "absolutepath", "type", "url",
            "modified", "name", "image-width", "image-height" };

    public static Document Document(File f) {
        Document doc = new Document();

        doc.add(Field.Keyword("path", f.getPath()));
        String path = f.getParent();
        // path = path.substring(0, path.length() - 1);
        doc.add(Field.Keyword("absolutepath", path));

        doc.add(Field.Keyword("name", f.getName()));

        doc.add(Field.Text("type", "image"));

        ImageIcon tmpicon = new ImageIcon(f.getPath());

        doc.add(Field.Text("image-width", String.valueOf(tmpicon.getIconWidth())));
        doc.add(Field.Text("image-height", String.valueOf(tmpicon.getIconHeight())));

        try {
           BASE64Encoder b64E = new BASE64Encoder();
           String encoded = b64E.encode(generateThumbnail(f.getPath()));
           doc.add(Field.UnIndexed("thumbnail",encoded));
        } catch (ImageFormatException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        doc.add(Field.Keyword("modified", DateField.timeToString(f
                .lastModified())));
        return doc;
    }

  
    public String[] getSearchFields() {
        return fields;
    }

    public static byte[] generateThumbnail(final String filename)
            throws ImageFormatException, IOException, InterruptedException {

        // load image from INFILE
        Image image = Toolkit.getDefaultToolkit().getImage(filename);
        MediaTracker mediaTracker = new MediaTracker(new Container());
        mediaTracker.addImage(image, 0);
        mediaTracker.waitForID(0);
        // determine thumbnail size from WIDTH and HEIGHT
        int thumbWidth = 96;
        int thumbHeight = 96;
        double thumbRatio = (double) thumbWidth / (double) thumbHeight;
        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);
        double imageRatio = (double) imageWidth / (double) imageHeight;
        if (thumbRatio < imageRatio) {
            thumbHeight = (int) (thumbWidth / imageRatio);
        } else {
            thumbWidth = (int) (thumbHeight * imageRatio);
        }
        // draw original image to thumbnail image object and
        // scale it to the new size on-the-fly
        BufferedImage thumbImage = new BufferedImage(thumbWidth, thumbHeight,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = thumbImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(image, 0, 0, thumbWidth, thumbHeight, null);
        // save thumbnail image to OUTFILE
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(bout);
        JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(thumbImage);
        int quality = 75;
        quality = Math.max(0, Math.min(quality, 100));
        param.setQuality(quality / 100.0f, false);
        encoder.setJPEGEncodeParam(param);
        encoder.encode(thumbImage);
        return bout.toByteArray();

        

    }

    public static byte[] getBytes(BufferedImage bim) {
        WritableRaster wr = bim.getWritableTile(0, 0);
        byte[] b = (byte[]) (wr.getDataElements(0, 0, bim.getWidth(), bim
                .getHeight(), null));
        bim.releaseWritableTile(0, 0);
        return b;
    }

}
