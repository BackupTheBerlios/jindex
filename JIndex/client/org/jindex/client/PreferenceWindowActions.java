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
import org.gnu.gtk.CellRendererText;
import org.gnu.gtk.DataColumn;
import org.gnu.gtk.DataColumnObject;
import org.gnu.gtk.DataColumnString;
import org.gnu.gtk.Dialog;
import org.gnu.gtk.ListStore;
import org.gnu.gtk.MenuItem;
import org.gnu.gtk.TreeIter;
import org.gnu.gtk.TreeView;
import org.gnu.gtk.TreeViewColumn;
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
    
    private ListStore ls;
    
    private DataColumnString ColData;
    
    private DataColumnObject ColObj;
    
    private TreeView table;
    
    /** Creates a new instance of PreferenceWindowActions */
    public PreferenceWindowActions(LibGlade app) {
        this.app = app;
        this.window = (Window) app.getWidget("configwindow");
        addListeners();
        fillList();
    }
    public void addListeners() {
        Button addItem = (Button) app.getWidget("add_button");
        addItem.addListener(new ButtonListener() {
            public void buttonEvent(ButtonEvent buttonEvent) {
                
                if(buttonEvent.isOfType(ButtonEvent.Type.CLICK))   
                    log.debug("addItem clicked");
                
            }
        });
        
        Button removeButton = (Button) app.getWidget("remove_button");
        removeButton.addListener(new ButtonListener() {
            public void buttonEvent(ButtonEvent buttonEvent) {
                if(buttonEvent.isOfType(ButtonEvent.Type.CLICK))
                    log.debug("removeButton clicked");
                
            }
        });
        
        Button okButton  = (Button) app.getWidget("ok_button");
        okButton.addListener(new ButtonListener() {
            public void buttonEvent(ButtonEvent buttonEvent) {
                if(buttonEvent.isOfType(ButtonEvent.Type.CLICK))
                    log.debug("okButton clicked");
                
            }
        });
        
        
        Button cancelButton = (Button) app.getWidget("cancel_button");
        cancelButton.addListener(new ButtonListener() {
            public void buttonEvent(ButtonEvent buttonEvent) {
                if(buttonEvent.isOfType(ButtonEvent.Type.CLICK))
                    log.debug("cancelButton clicked");
                
            }
        });
        
        Button addButton = (Button) app.getWidget("add_button");
        addButton.addListener(new ButtonListener() {
            public void buttonEvent(ButtonEvent buttonEvent) {
                if(buttonEvent.isOfType(ButtonEvent.Type.CLICK)) {
                    NewSearchTypeDialog dialog = new NewSearchTypeDialog(app);
                    log.debug("addButton clicked");
                }
                
            }
        });
        
    }
    public void fillList() {
        table = (TreeView) app.getWidget("configTypelist");
        
        ColData = new DataColumnString();
        ColObj = new DataColumnObject();
        ls = new ListStore(new DataColumn[] { ColData, ColObj });
        
        table.setEnableSearch(true); /*
         * allows to use keyboard to search
         * items matching the pressed keys
         */
        
        table.setAlternateRowColor(true); /* no comments smile */
        table.setModel(ls);
        
        
        TreeViewColumn col2 = new TreeViewColumn();
        CellRendererText render2 = new CellRendererText();
        col2.packStart(render2, true);
        col2.addAttributeMapping(render2, CellRendererText.Attribute.MARKUP, ColData);
        
        TreeViewColumn col3 = new TreeViewColumn();
        CellRendererText render3 = new CellRendererText();
        col3.packStart(render3, true);
        col3.setVisible(false);
        
        table.setSearchDataColumn(ColData);
        /* append columns */
        table.appendColumn(col2);
        table.appendColumn(col3);
        
        
        addToTable("Evolution mail");
        addToTable("File or directory");
    }
    public void addToTable(String typename) {
        
        TreeIter row = ls.appendRow();
        ls.setValue(row, ColData, typename);
        ls.setValue(row, ColObj, null);
        table.showAll();
    }
}
