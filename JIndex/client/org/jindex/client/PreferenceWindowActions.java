/*
 * PreferenceWindowActions.java
 *
 * Created on 25 October 2006, 14:42
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jindex.client;

import org.apache.log4j.Logger;
import org.gnu.glade.LibGlade;
import org.gnu.glib.EventType;
import org.gnu.glib.Type;
import org.gnu.gtk.Button;
import org.gnu.gtk.Dialog;
import org.gnu.gtk.MenuItem;
import org.gnu.gtk.Window;
import org.gnu.gtk.event.ButtonEvent;
import org.gnu.gtk.event.ButtonListener;
import org.gnu.gtk.event.MenuItemEvent;
import org.gnu.gtk.event.MenuItemListener;
import org.jindex.client.window.NewSearchTypeDialog;

/**
 *
 * @author sorenm
 */
public class PreferenceWindowActions {
    
    /** Creates a new instance of PreferenceWindowActions */
    public PreferenceWindowActions() {
    }
    private static Logger log = Logger.getLogger(PreferenceWindowActions.class);
    
    private LibGlade app;
    
    private Window window;
    
    /** Creates a new instance of PreferenceWindowActions */
    public PreferenceWindowActions(LibGlade app) {
        this.app = app;
        this.window = (Window) app.getWidget("configwindow");
        addListeners();
    }
    public void addListeners() {
        Button addItem = (Button) app.getWidget("add_button");
        addItem.addListener(new ButtonListener() {
            public void buttonEvent(ButtonEvent buttonEvent) {
                
                log.debug("addItem clicked");
                
            }
        });
        
        Button removeButton = (Button) app.getWidget("remove_button");
        removeButton.addListener(new ButtonListener() {
            public void buttonEvent(ButtonEvent buttonEvent) {
                
                log.debug("removeButton clicked");
                
            }
        });
        
        Button okButton  = (Button) app.getWidget("ok_button");
        okButton.addListener(new ButtonListener() {
            public void buttonEvent(ButtonEvent buttonEvent) {
                
                log.debug("okButton clicked");
                
            }
        });
        
        
        Button cancelButton = (Button) app.getWidget("cancel_button");
        cancelButton.addListener(new ButtonListener() {
            public void buttonEvent(ButtonEvent buttonEvent) {
                
                log.debug("cancelButton clicked");
                
            }
        });
        
        Button addButton = (Button) app.getWidget("add_button");
        addButton.addListener(new ButtonListener() {
            public void buttonEvent(ButtonEvent buttonEvent) {
                if(buttonEvent.isOfType(ButtonEvent.Type.CLICK)) {
                    NewSearchTypeDialog dialog = new NewSearchTypeDialog();
                    dialog.showAll();
                    log.debug("addButton clicked");
                }
                
            }
        });
        
    }
}
