/*
 * EvolutionConfig.java
 *
 * Created on 30 October 2006, 09:26
 *
 * Created by @author sorenm
 */

package org.jindex.client.config;

/**
 *
 * @author sorenm
 */
public class EvolutionConfig {
    private boolean mail;
    private boolean address;
    private boolean calendar;
    /** Creates a new instance of EvolutionConfig */
    public EvolutionConfig() {
    }

    public boolean isMail() {
        return mail;
    }

    public void setMail(boolean mail) {
        this.mail = mail;
    }

    public boolean isAddress() {
        return address;
    }

    public void setAddress(boolean address) {
        this.address = address;
    }

    public boolean isCalendar() {
        return calendar;
    }

    public void setCalendar(boolean calendar) {
        this.calendar = calendar;
    }
    
}
