package documents.mbox;

import java.io.Serializable;

public class Mail implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7340505799552480597L;


	String from = "";

	String to = "";

	String date = "";

	String subject = "";

	long startline;

	int startHashCode;

	private String uid = "";

	private String internalFrom = "";
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
		this.date = date;
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
		this.subject = subject;
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
		this.to = to;
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

}