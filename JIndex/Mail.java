public class Mail {
    /**
     * @return Returns the date.
     */
    public String getDate() {
        return date;
    }
    /**
     * @param date The date to set.
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
     * @param from The from to set.
     */
    public void setFrom(String from) {
        this.from = from;
    }
    /**
     * @return Returns the subject.
     */
    public String getSubject() {
        if(subject == null)
            return "";
        else
            return subject;
    }
    /**
     * @param subject The subject to set.
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
     * @param to The to to set.
     */
    public void setTo(String to) {
        this.to = to;
    }
    String from;
    String to;
    String date;
    String subject;
    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append(date+"\n");
        result.append(from+"\n");
        result.append(to+"\n");
        result.append(subject+"\n");
        return result.toString();
    }
   
}