package org.jindex.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import utils.c.libJIndex;


public class FileUtility {
	static Logger log  = Logger.getLogger(FileUtility.class);

	public FileUtility() {
		log.debug("loading library");
		System.loadLibrary("JIndex");
	}
	public static String getMimeType(String file) {
		return libJIndex.getMimeType(file);
	}
	public static String getIconFromMimeType(String mimetype) {
        return libJIndex.getIconFromMimeType(mimetype);   
    }
	
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
		byte[] bytes = new byte[(int) length];

		// Read in the bytes
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		// Ensure all the bytes have been read in
		if (offset < bytes.length) {
			throw new IOException("Could not completely read file " + file.getName());
		}

		// Close the input stream and return bytes
		is.close();
		return bytes;
	}

	public static final int transfer(InputStream in, OutputStream out) throws IOException {
		int totalBytes = 0;
		int bytesInBuf = 0;
		byte[] buf = new byte[1024];

		while ((bytesInBuf = in.read(buf)) != -1) {
			out.write(buf, 0, bytesInBuf);
			totalBytes += bytesInBuf;
		}

		return totalBytes;
	}
	
	
	/**
	 * Get an icon from a path.
	 * 
	 * @param path
	 * @return
	 */
	public static byte[] getIcon(String path) {
		InputStream is = FileUtility.class.getResourceAsStream(path);
		if(is != null) 
			return getBytesFromFile(is);
		return null;
	}
	
	public static byte[] getBytesFromFile(InputStream is) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
			transfer(is, out);
			out.close();
			return out.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static byte[] getExternalIcon(String file) {
		try {
			return getBytesFromFile(new File(file));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
