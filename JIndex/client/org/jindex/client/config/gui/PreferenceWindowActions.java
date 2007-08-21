/*
 * PreferenceWindowActions.java
 *
 * Created on 25 October 2006, 14:42
 *
 */

package org.jindex.client.config.gui;

import java.util.ArrayList;
import java.util.Iterator;
import org.apache.log4j.Logger;
import org.gnu.glade.LibGlade;
import org.gnu.glib.EventType;
import org.gnu.glib.Type;
import org.gnu.gtk.Button;
import org.gnu.gtk.CellRendererText;
import org.gnu.gtk.CheckButton;
import org.gnu.gtk.DataColumn;
import org.gnu.gtk.DataColumnObject;
import org.gnu.gtk.DataColumnString;
import org.gnu.gtk.Dialog;
import org.gnu.gtk.ListStore;
import org.gnu.gtk.MenuItem;
import org.gnu.gtk.Notebook;
import org.gnu.gtk.TreeIter;
import org.gnu.gtk.TreeView;
import org.gnu.gtk.TreeViewColumn;
import org.gnu.gtk.Window;
import org.gnu.gtk.event.ButtonEvent;
import org.gnu.gtk.event.ButtonListener;
import org.gnu.gtk.event.MenuItemEvent;
import org.gnu.gtk.event.MenuItemListener;
import org.gnu.gtk.event.NotebookEvent;
import org.gnu.gtk.event.NotebookListener;
import org.jindex.client.config.EvolutionConfig;
import org.jindex.client.window.NewSearchTypeDialog;
import org.jindex.daemon.config.ConfigReader;

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
    
    private EvolutionConfig evoconfig;
    /** Creates a new instance of PreferenceWindowActions */
    public PreferenceWindowActions(LibGlade app) {
        this.app = app;
        this.window = (Window) app.getWidget("configwindow");
        evoconfig = new EvolutionConfig();
        addListeners();
        fillList();
    }
    public void addEvolutionListeners() {
        CheckButton mailbutton = (CheckButton) app.getWidget("configwindow.evolution.mail");
        mailbutton.addListener(new ButtonListener() {
            public void buttonEvent(ButtonEvent buttonEvent) {
                if(buttonEvent.isOfType(ButtonEvent.Type.CLICK)) {
                    evoconfig.setMail(!evoconfig.isMail());
                    log.debug("Evoconfig mail: "+evoconfig.isMail());
                }
            }
        });
        
        CheckButton addressBookButton = (CheckButton) app.getWidget("configwindow.evolution.addressbook");
        addressBookButton.addListener(new ButtonListener() {
            public void buttonEvent(ButtonEvent buttonEvent) {
                if(buttonEvent.isOfType(ButtonEvent.Type.CLICK)) {
                    evoconfig.setAddress(!evoconfig.isAddress());
                    log.debug("Evoconfig address: "+evoconfig.isAddress());
                }
                
            }
        });
        
        CheckButton calendarButton = (CheckButton) app.getWidget("configwindow.evolution.calendar");
        calendarButton.addListener(new ButtonListener() {
            public void buttonEvent(ButtonEvent buttonEvent) {
                if(buttonEvent.isOfType(ButtonEvent.Type.CLICK)) {
                    evoconfig.setCalendar(!evoconfig.isCalendar());
                    log.debug("Evoconfig calendar: "+evoconfig.isCalendar());
                }
            }
        });
        
        
    }
    public void addListeners() {
        
        Notebook configNotebook = (Notebook) app.getWidget("configwindow.notebook");
        configNotebook.addListener(new NotebookListener() {
            public void notebookEvent(NotebookEvent notebookEvent) {
                if(notebookEvent.isOfType(NotebookEvent.Type.SELECT_PAGE)) {
                    log.debug("Selected page number: "+notebookEvent.getPageNumber());
                } else  if(notebookEvent.isOfType(NotebookEvent.Type.SWITCH_PAGE)) {
                    log.debug("Switch page number: "+notebookEvent.getPageNumber());
                }
            }
        });
        Button okbutton = (Button) app.getWidget("configwindow.okbutton");
        okbutton.addListener(new ButtonListener() {
            public void buttonEvent(ButtonEvent buttonEvent) {
                
                if(buttonEvent.isOfType(ButtonEvent.Type.CLICK))
                    log.debug("addItem clicked");
                
            }
        });
        
        Button cancelButton = (Button) app.getWidget("configwindow.cancelbutton");
        cancelButton.addListener(new ButtonListener() {
            public void buttonEvent(ButtonEvent buttonEvent) {
                if(buttonEvent.isOfType(ButtonEvent.Type.CLICK))
                    log.debug("cancelbutton clicked");
                
            }
        });
    }
    
    public void fillList() {
        ArrayList filelist;
        table = (TreeView) app.getWidget("configwindow.files.table");
        
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
        
        filelist = ConfigReader.getWatches();
        Iterator ite = filelist.iterator();
        while(ite.hasNext()) {
            addToTable(ite.next().toString());
        }
    }
    public void addToTable(String typename) {
        
        TreeIter row = ls.appendRow();
        ls.setValue(row, ColData, typename);
        ls.setValue(row, ColObj, null);
        table.showAll();
    }
}
