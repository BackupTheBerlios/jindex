package org.jindex.documents.mail;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class Mail implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Mail() {
		from = "";
		to = "";
		date = "";
		subject = "";
		uid = "";
	}


	String from;

	String to;

	String date;

	String subject;

	long startline;

	int startHashCode;

	private String uid;

	private String internalFrom;
	
	String hostname; 
	
	List received;
	/**
	 * @return Returns the date.
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @param date
	 *            The date to set.
	 */
	public void setDate(String date) {
		date = date.replaceAll("Date:", "");
		this.date = date.trim();
	}

	/**
	 * @return Returns the from.
	 */
	public String getFrom() {
		return from;
	}

	/**
	 * @param from
	 *            The from to set.
	 */
	public void setFrom(String from) {
		from = from.replaceAll("From:", "");
		this.from = from.trim();
        int start =from.indexOf("<");
        int stop =from.indexOf(">");
        if(start > 0 && stop > 0)
            this.from = from.substring(start+1, stop);
	}

	/**
	 * @return Returns the subject.
	 */
	public String getSubject() {
		if (subject == null)
			return "";
		return subject;
	}

	/**
	 * @param subject
	 *            The subject to set.
	 */
	public void setSubject(String subject) {
		subject = subject.replaceAll("Subject:", "");
		this.subject = subject.trim();
	}

	/**
	 * @return Returns the to.
	 */
	public String getTo() {
		return to;
	}

	/**
	 * @param to
	 *            The to to set.
	 */
	public void setTo(String to) {
		to =to.replaceAll("To:", "");
		this.to = to.trim();
	}


	public String toString() {
		StringBuffer result = new StringBuffer();
		result.append(date + "\n");
		result.append(from + "\n");
		result.append(to + "\n");
		result.append(subject + "\n");
		return result.toString();
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public int getStartHashCode() {
		return startHashCode;
	}

	public long getStartline() {
		return startline;
	}

	public void setStartline(long charcounter) {
		this.startline = charcounter;
	}

	public void setInternalFrom(String str) {
		internalFrom = str;
		this.startHashCode = internalFrom.hashCode();

	}

	public String getInternalFrom() {
		return internalFrom;

	}

	public void addReceived(String host) {
		if(received == null)
			received = new LinkedList();
		host = host.trim();
		if(!host.equals("gw.propcard.dk"))
			received.add(host);
		
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
		
	}

	public String getHostname() {
		return hostname;
	}

	public List getReceived() {
		return received;
	}

}