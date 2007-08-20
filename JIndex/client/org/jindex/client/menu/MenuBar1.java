/*
 * MenuBar.java
 *
 * Created on 22 October 2006, 10:17
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jindex.client.menu;

import org.apache.log4j.Logger;
import org.gnu.glade.LibGlade;
import org.gnu.gtk.ImageMenuItem;
import org.gnu.gtk.event.MenuItemEvent;
import org.gnu.gtk.event.MenuItemListener;



/**
 *
 * @author sorenm
 */
public class MenuBar {
    private static Logger log = Logger.getLogger(MenuBar.class);
    
    
    LibGlade app;
    /** Creates a new instance of MenuBar */
    public MenuBar(LibGlade application) {
        this.app = application;
        
        init();
        
    }
    public void init() {
        /*
        ImageMenuItemItem obj = (ImageMenuItem) app.getWidget("preferences");
        obj.addListener(new MenuItemListenerner() {
            public void menuItemEvent(MenuItemEvent menuItemEvent) {
                log.debug(menuItemEvent.toString());
            }
        });
        */
        
        ImageMenuItem quit = (ImageMenuItem) app.getWidget("quit");
        quit.addListener(new MenuItemListener() {
   public void menuItemEvent(MenuItemEvent arg0) {
         System.exit(0);
            }
        });
    }
}
