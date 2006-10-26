/*
 * MenuBar.java
 *
 * Created on 25 October 2006, 14:25
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jindex.client.menu;

import org.apache.log4j.Logger;
import org.gnu.glade.LibGlade;
import org.gnu.gtk.MenuItem;
import org.gnu.gtk.Window;
import org.gnu.gtk.event.MenuItemEvent;
import org.gnu.gtk.event.MenuItemListener;
import org.jindex.client.PreferenceWindowActions;

/**
 *
 * @author sorenm
 */
public class MenuBar {
    private static Logger log = Logger.getLogger(MenuBar.class);
    
    private LibGlade app;
    
    /** Creates a new instance of MenuBar */
    public MenuBar(LibGlade app) {
        this.app = app;
        addListeners();
    }
    public void addListeners() {
        MenuItem prefItem = (org.gnu.gtk.MenuItem) app.getWidget("preferences");
        prefItem.addListener(new MenuItemListener() {
            public void menuItemEvent(MenuItemEvent menuItemEvent) {
                Window window = (Window) app.getWidget("configwindow");
                PreferenceWindowActions prefAction = new PreferenceWindowActions(app);
                window.showAll();
                log.debug("Item clicked");
                
            }
        });
        
        MenuItem quitItem = (org.gnu.gtk.MenuItem) app.getWidget("quit");
        quitItem.addListener(new MenuItemListener() {
            public void menuItemEvent(MenuItemEvent menuItemEvent) {
                log.debug("Item clicked");
                System.exit(0);
                
            }
        });
    }
}
