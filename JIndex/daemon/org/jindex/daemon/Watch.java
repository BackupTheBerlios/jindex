package org.jindex.daemon;

public class Watch {
	String filename = "";
	boolean isDirectory;
	public Watch(String filename) {
		super();
		this.filename = filename;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public boolean isDirectory() {
		return isDirectory;
	}
	public void setDirectory(boolean isDirectory) {
		this.isDirectory = isDirectory;
	}

	public String toString() {
		return filename;
	}
}
