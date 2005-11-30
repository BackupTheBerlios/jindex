package utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.jdesktop.jdic.filetypes.Association;
import org.jdesktop.jdic.filetypes.AssociationService;

public class FileUtility {
	 public static byte[] getBytesFromFile(File file) throws IOException {
	        InputStream is = new FileInputStream(file);
	    
	        // Get the size of the file
	        long length = file.length();
	    
	        // You cannot create an array using a long type.
	        // It needs to be an int type.
	        // Before converting to an int type, check
	        // to ensure that file is not larger than Integer.MAX_VALUE.
	        if (length > Integer.MAX_VALUE) {
	            // File is too large
	        }
	    
	        // Create the byte array to hold the data
	        byte[] bytes = new byte[(int)length];
	    
	        // Read in the bytes
	        int offset = 0;
	        int numRead = 0;
	        while (offset < bytes.length
	               && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
	            offset += numRead;
	        }
	    
	        // Ensure all the bytes have been read in
	        if (offset < bytes.length) {
	            throw new IOException("Could not completely read file "+file.getName());
	        }
	    
	        // Close the input stream and return bytes
	        is.close();
	        return bytes;
	    }
	 public static boolean isFileFormatSupport(File file) {
		 	ArrayList formats = new ArrayList();
		 	formats.add("audio/mpeg");
		 	formats.add("application/msword");
		 	formats.add("image/jpeg");
		 	formats.add("application/pdf");
		 	formats.add("text/x-java");
		 	
		 	AssociationService assocService = new AssociationService();
			URL url=null;
			try {
				url = new URL(file.toURL().toString());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			Association assoc = assocService.getAssociationByContent(url);
			System.out.println("foud mime type: " + assoc.getMimeType());
			String mimetype = assoc.getMimeType();
			if(formats.contains(mimetype))
				return true;
					
				return false;
	 }
}
