package org.jindex.client;

import org.jindex.client.gui.MainContentsGUI;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
import org.gnu.gdk.Pixbuf;
import org.gnu.gdk.PixbufLoader;
import org.gnu.glib.Handle;
import org.gnu.glib.JGException;
import org.gnu.gtk.CellRendererPixbuf;
import org.gnu.gtk.CellRendererText;
import org.gnu.gtk.DataColumn;
import org.gnu.gtk.DataColumnObject;
import org.gnu.gtk.DataColumnPixbuf;
import org.gnu.gtk.DataColumnString;
import org.gnu.gtk.ListStore;
import org.gnu.gtk.TreeIter;
import org.gnu.gtk.TreeModel;
import org.gnu.gtk.TreePath;
import org.gnu.gtk.TreeView;
import org.gnu.gtk.TreeViewColumn;
import org.gnu.gtk.event.TreeViewEvent;
import org.gnu.gtk.event.TreeViewListener;
import org.jindex.utils.FileUtility;

public class SearchResultTable extends TreeView implements TreeViewListener {
    Logger log = Logger.getLogger(SearchResultTable.class);
    private DataColumnPixbuf ColThumbImage;
    private DataColumnString ColData;
    private DataColumnObject ColObj;
    private ListStore ls;
    private TreeView table;
    
    public SearchResultTable() {
        super();
        initTable();
    }
    /**
     * 
     */
    public void initTable() {
        ColThumbImage = new DataColumnPixbuf();
        ColData = new DataColumnString();
        ColObj = new DataColumnObject();
        DataColumn[] crap = new DataColumn[] { ColData, ColObj, ColThumbImage };
        ///ls = new ListStore(new DataColumn[] { ColThumbImage, ColData, ColObj });
        //ls = new ListStore(new DataColumn[] { ColData, ColObj });
        ls = new ListStore(crap);
        
        
        table.setEnableSearch(true); /*
         * allows to use keyboard to search
         * items matching the pressed keys
         */
        
        table.setAlternateRowColor(true); /* no comments smile */
        table.setModel(ls);
        
        TreeViewColumn col0 = new TreeViewColumn();
        col0.setFixedWidth(20);
        CellRendererPixbuf render1 = new CellRendererPixbuf();
        col0.packStart(render1, true);
        col0.addAttributeMapping(render1, CellRendererPixbuf.Attribute.PIXBUF, ColThumbImage);
        
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
        table.appendColumn(col0);
        table.appendColumn(col2);
        table.appendColumn(col3);
        table.setHoverSelection(true);
        table.addListener(this);
        table.setHeadersVisible(false);
        
    }
    /**
     * Adds a GUI component to the result table
     * 
     * @param gui 
     */
    public void addToTable(MainContentsGUI gui) {
        
        TreeIter row = ls.appendRow();
        if (!(gui.getIcon() == null)) {
            PixbufLoader test = new PixbufLoader();
            test.write(gui.getIcon());
            ls.setValue(row, ColThumbImage, test.getPixbuf());
            test.close();
        } else {
            PixbufLoader test = new PixbufLoader();
            test.write(FileUtility.getIcon("/images/icon_missing.png"));
            
            ls.setValue(row, ColThumbImage, test.getPixbuf());
            
        }

        ls.setValue(row, ColData, gui.getTextContent());
        ls.setValue(row, ColObj, gui);
        table.showAll();
    }
    
    /**
     * 
     * @param event 
     */
    public void treeViewEvent(TreeViewEvent event) {
        if (event.getTreeIter() != null) {
            TreePath[] tp = table.getSelection().getSelectedRows();
            if (tp.length == 1) {
                TreeIter item = ls.getIter(tp[0].toString());
                MainContentsGUI command = (MainContentsGUI) ls.getValue(item, ColObj);
                try {
                    log.debug(command);
                    Process p = Runtime.getRuntime().exec(command.getOpenAction());
//                    char[] error = new char[2048];
  //                  InputStreamReader isr = new InputStreamReader(p.getErrorStream());
    //                isr.read(error);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    /**
     * 
     * @param arg0 
     */
    public SearchResultTable(Handle arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }
    /**
     * 
     * @param arg0 
     */
    public SearchResultTable(TreeModel arg0) {
        super(arg0);
        // TODO Auto-generated constructor stub
    }
    /**
     * 
     * @param table 
     */
    public SearchResultTable(TreeView table) {
        super();
        this.table = table;
        initTable();
    }
    public void clear() {
        while (true) {
            TreeIter item = ls.getIter("0");
            if (item == null)
                break;
            ls.removeRow(item);
        }
    }
    
}
