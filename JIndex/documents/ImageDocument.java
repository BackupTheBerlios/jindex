package documents;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.swing.ImageIcon;

import org.apache.commons.codec.binary.Base64;
import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifDirectory;
import com.drew.metadata.iptc.IptcDirectory;


public class ImageDocument implements SearchDocument {
    public static String[] fields = { "path", "absolutepath", "type", "url",
            "modified", "name", "image-width", "image-height" };

    public static Document Document(File f)
            throws java.io.FileNotFoundException {

        Document doc = new Document();
Metadata metadata = null;
        //JpegMetadataReader.readMetadata(f);
        
        Directory exifDirectory = metadata.getDirectory(ExifDirectory.class);
        String cameraMake = exifDirectory.getString(ExifDirectory.TAG_MAKE);
        String cameraModel = exifDirectory.getString(ExifDirectory.TAG_MODEL);
        String artist = exifDirectory.getString(ExifDirectory.TAG_ARTIST);
        String created = exifDirectory.getString(ExifDirectory.TAG_DATETIME);
        String orientation = exifDirectory.getString(ExifDirectory.TAG_ORIENTATION);
        Directory iptcDirectory = metadata.getDirectory(IptcDirectory.class);
        String caption = iptcDirectory.getString(IptcDirectory.TAG_CAPTION);
        System.out.println(cameraMake);
        System.out.println(cameraModel);
        System.out.println(artist);
        System.out.println(created);
        System.out.println(orientation);
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
            Base64 b64E = new Base64();
           byte[] encoded = b64E.encode(generateThumbnail(f.getPath()));
           doc.add(Field.UnIndexed("thumbnail",new String(encoded)));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        doc.add(Field.Keyword("modified", DateField.timeToString(f
                .lastModified())));
        return doc;
    }

    private ImageDocument() {
    }

    public String[] getSearchFields() {
        return fields;
    }

    public static byte[] generateThumbnail(final String filename)
            throws IOException, InterruptedException {

//        // load image from INFILE
//        Image image = Toolkit.getDefaultToolkit().getImage(filename);
//        MediaTracker mediaTracker = new MediaTracker(new Container());
//        mediaTracker.addImage(image, 0);
//        mediaTracker.waitForID(0);
//        // determine thumbnail size from WIDTH and HEIGHT
//        int thumbWidth = 96;
//        int thumbHeight = 96;
//        double thumbRatio = (double) thumbWidth / (double) thumbHeight;
//        int imageWidth = image.getWidth(null);
//        int imageHeight = image.getHeight(null);
//        double imageRatio = (double) imageWidth / (double) imageHeight;
//        if (thumbRatio < imageRatio) {
//            thumbHeight = (int) (thumbWidth / imageRatio);
//        } else {
//            thumbWidth = (int) (thumbHeight * imageRatio);
//        }
//        // draw original image to thumbnail image object and
//        // scale it to the new size on-the-fly
//        BufferedImage thumbImage = new BufferedImage(thumbWidth, thumbHeight,
//                BufferedImage.TYPE_INT_RGB);
//        Graphics2D graphics2D = thumbImage.createGraphics();
//        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
//                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
//        graphics2D.drawImage(image, 0, 0, thumbWidth, thumbHeight, null);
//        // save thumbnail image to OUTFILE
//        ByteArrayOutputStream bout = new ByteArrayOutputStream();
//        
//        ImageWriter encoder = (ImageWriter)ImageIO.getImageWritersByFormatName("JPEG").next();
//        ImageWriteParam param = new ImageWriteParam(null);
//
//        param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
//        param.setCompressionQuality(0.85f);;
//
//        image.getGraphics().
//        encoder.setOutput(bout);
//        encoder.write((IIOMetadata) null, new IIOImage(image,null,null), param);
//
//        bout.close();
//        
//        JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(bout);
//        JPEGEncodeParam param = encoder.getDefaultJPEGEncodeParam(thumbImage);
//        int quality = 75;
//        quality = Math.max(0, Math.min(quality, 100));
//        param.setQuality((float) quality / 100.0f, false);
//        encoder.setJPEGEncodeParam(param);
//        encoder.encode(thumbImage);
//        
//        
//        return bout.toByteArray();

        return null;

    }

    public static byte[] getBytes(BufferedImage bim) {
        WritableRaster wr = bim.getWritableTile(0, 0);
        byte[] b = (byte[]) (wr.getDataElements(0, 0, bim.getWidth(), bim
                .getHeight(), null));
        bim.releaseWritableTile(0, 0);
        return b;
    }

}
