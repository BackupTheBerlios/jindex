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

import org.apache.commons.codec.binary.Base64;
import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGEncodeParam;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

public class ImageDocument implements SearchDocument {
	public static String[] fields = { "path", "absolutepath", "type", "url", "modified", "filename", "image-width", "image-height" };

	public static Document Document(File f, String mimetype) {

		Document doc = new Document();
/*		Metadata metadata = null;
		try {
			metadata = JpegMetadataReader.readMetadata(f);
		} catch (JpegProcessingException e1) {
			e1.printStackTrace();
		}
		
		if (metadata != null) {
			Directory exifDirectory = metadata.getDirectory(ExifDirectory.class);
			String cameraMake = exifDirectory.getString(ExifDirectory.TAG_MAKE);
			String cameraModel = exifDirectory.getString(ExifDirectory.TAG_MODEL);
			String artist = exifDirectory.getString(ExifDirectory.TAG_ARTIST);
			String created = exifDirectory.getString(ExifDirectory.TAG_DATETIME);
			String orientation = exifDirectory.getString(ExifDirectory.TAG_ORIENTATION);
			Directory iptcDirectory = metadata.getDirectory(IptcDirectory.class);
			String caption = iptcDirectory.getString(IptcDirectory.TAG_CAPTION);

			log.debug(cameraMake);
			log.debug(cameraModel);
			log.debug(artist);
			log.debug(created);
			log.debug(orientation);
			*/
			doc.add(Field.Keyword("path", f.getPath()));
			doc.add(Field.Keyword("absolutepath", f.getAbsolutePath()));

			doc.add(Field.Keyword("filename", f.getName()));

			doc.add(Field.Text("type", mimetype));

			ImageIcon tmpicon = new ImageIcon(f.getPath());

			doc.add(Field.Text("image-width", String.valueOf(tmpicon.getIconWidth())));
			doc.add(Field.Text("image-height", String.valueOf(tmpicon.getIconHeight())));

			try {
				Base64 b64E = new Base64();
				byte[] encoded = b64E.encode(generateThumbnail(f.getPath()));
				doc.add(Field.UnIndexed("thumbnail", new String(encoded)));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			doc.add(Field.Keyword("modified", DateField.timeToString(f.lastModified())));
//		}
		return doc;
	}

	private ImageDocument() {
	}

	public String[] getSearchFields() {
		return fields;
	}

	public static byte[] generateThumbnail(final String filename) throws IOException, InterruptedException {

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
        if(thumbHeight <= 0)
            thumbHeight = 1;
        if(thumbWidth<= 0)
            thumbWidth = 1;
        
		// draw original image to thumbnail image object and
		// scale it to the new size on-the-fly
		BufferedImage thumbImage = new BufferedImage(thumbWidth, thumbHeight, BufferedImage.TYPE_INT_RGB);
		Graphics2D graphics2D = thumbImage.createGraphics();
		graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		graphics2D.drawImage(image, 0, 0, thumbWidth, thumbHeight, null);
		// save thumbnail image to OUTFILE
		ByteArrayOutputStream bout = new ByteArrayOutputStream();

		JPEGImageEncoder encoder1 = JPEGCodec.createJPEGEncoder(bout);
		JPEGEncodeParam param1 = encoder1.getDefaultJPEGEncodeParam(thumbImage);
		int quality = 75;
		quality = Math.max(0, Math.min(quality, 100));
		param1.setQuality(quality / 100.0f, false);
		encoder1.setJPEGEncodeParam(param1);
		encoder1.encode(thumbImage);

		return bout.toByteArray();

	}

	public static byte[] getBytes(BufferedImage bim) {
		WritableRaster wr = bim.getWritableTile(0, 0);
		byte[] b = (byte[]) (wr.getDataElements(0, 0, bim.getWidth(), bim.getHeight(), null));
		bim.releaseWritableTile(0, 0);
		return b;
	}

}
