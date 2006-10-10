package org.jindex.daemon;

import java.io.Serializable;

public class JFile implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4473329973670069541L;
	/**
	 * File class used for maintaing a list of files allready index by the indexer.
	 * So the index can check the date on the file with the one stored, if they are different the index,
	 * otherise skip it.
	 */
	String filename;
	long lastmodified;
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public long getLastmodified() {
		return lastmodified;
	}
	public void setLastmodified(long lastmodified) {
		this.lastmodified = lastmodified;
	}
	public JFile(String filename, long lastmodified) {
		super();
		this.filename = filename;
		this.lastmodified = lastmodified;
	}
	
}
