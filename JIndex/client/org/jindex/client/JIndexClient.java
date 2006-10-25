package org.jindex.client;

import org.gnu.gdk.PixbufLoader;
import org.gnu.gtk.StatusBar;
import org.jindex.client.gui.GUIFactory;
import org.jindex.client.gui.MainContentsGUI;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.gnu.glade.GladeXMLException;
import org.gnu.glade.LibGlade;
import org.gnu.gtk.ComboBox;
import org.gnu.gtk.DataColumnObject;
import org.gnu.gtk.DataColumnPixbuf;
import org.gnu.gtk.DataColumnString;
import org.gnu.gtk.Entry;
import org.gnu.gtk.Gtk;
import org.gnu.gtk.ListStore;
import org.gnu.gtk.StatusIcon;
import org.gnu.gtk.TreeView;
import org.gnu.gtk.VBox;
import org.gnu.gtk.Window;
import org.gnu.gtk.event.KeyEvent;
import org.gnu.gtk.event.KeyListener;
import org.gnu.gtk.event.LifeCycleEvent;
import org.gnu.gtk.event.LifeCycleListener;
import org.gnu.gtk.event.StatusIconEvent;
import org.gnu.gtk.event.StatusIconListener;
import org.gnu.gdk.PixbufLoader;
import org.jindex.client.menu.MenuBar;
import org.jindex.documents.AddressBookDocument;
import org.jindex.documents.ApplicationDocument;
import org.jindex.documents.FileDocument;
import org.jindex.documents.GaimLogDocument;
import org.jindex.documents.HTMLDocument;
import org.jindex.documents.ImageDocument;
import org.jindex.documents.JavaDocument;
import org.jindex.documents.MP3Document;
import org.jindex.documents.TomboyDocument;
import org.jindex.documents.mail.EvolutionMailDocument;
import org.jindex.documents.office.PDFDocument;
import org.jindex.utils.FileUtility;

public class JIndexClient {
    private static Logger log = Logger.getLogger(JIndexClient.class);
    
    private static String INDEXFILE = System.getProperty("HOME") + "/index";
    
    private static LibGlade firstApp;
    
    Window window; // Main window
    
    VBox contentpane = null;
    
    // Tree view
    SearchResultTable resulttable = null;
    
    TreeView resulttable1 = null;
    
    ListStore ls = null;
    
    DataColumnPixbuf ColThumbImage;
    
    DataColumnString ColData;
    
    DataColumnObject ColObj;
    
    ComboBox searchtypecombo;
    
    private StatusBar statusbar;

    private MenuBar menubar;
    
    public JIndexClient() throws FileNotFoundException, GladeXMLException,
            IOException {
        InputStream is = this.getClass().getResourceAsStream(
                "/org/jindex/glade/jindex.glade");
        firstApp = new LibGlade(is, this, null);
        window = (Window) firstApp.getWidget("mainwindow");
        addWindowCloser();
        final Entry searchfield = (Entry) firstApp.getWidget("queryfield");
        searchtypecombo = (ComboBox) firstApp.getWidget("searchtypecombo");
        statusbar = (StatusBar) firstApp.getWidget("statusbar");
        menubar = new MenuBar(firstApp);
        
        setStatusBarMessage("JIndex started");
        
        resulttable1 = (TreeView) firstApp.getWidget("resultview");
        resulttable = new SearchResultTable(resulttable1);
        
        initCombo();
        searchfield.addListener(new KeyListener() {
            
            public boolean keyEvent(KeyEvent event) {
                if (event.getKeyval() == 65293
                        && event.getType() == KeyEvent.Type.KEY_PRESSED) {// catch
                    doSearchGUI(searchfield.getText());
                    return true;
                }
                return false;
            }
            
        });
        
        registerTrayIcon();
        
        IndexReader reader = IndexReader.open(INDEXFILE);
        log.debug("Number of documents in index is " + reader.numDocs());
        reader.close();
    }
    
    private void registerTrayIcon() {
        //StatusIcon si = new StatusIcon("images/stock_search.png");
        PixbufLoader test = new PixbufLoader();
        test.write(FileUtility.getIcon("/images/stock_search.png"));
        StatusIcon si = new StatusIcon(test.getPixbuf());
        si.setTooltip("JIndex searcher");
        si.addListener(new StatusIconListener() {
            public void statusIconEvent(StatusIconEvent e) {
                if (e.isOfType(StatusIconEvent.Type.POPUP_MENU)) {
                    log.debug("clicked");
                }
                if (e.isOfType(StatusIconEvent.Type.ACTIVATE)) {
                    log.debug("Show window or not");
                    if (!window.isActive()) {
                        window.show();
                    } else {
                        window.hide();
                    }
                }
            }
        });
        
    }
    
    public void on_exit_activate() {
        log.debug("on_exit_activate");
        
    }
    
    public static void main(String[] args) {
        try {
            Gtk.init(args);
            new JIndexClient();
            Gtk.main();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            log.debug("Exit");
        }
        
    }
    
    public void addWindowCloser() {
        window.addListener(new LifeCycleListener() {
            public void lifeCycleEvent(LifeCycleEvent event) {
            }
            
            public boolean lifeCycleQuery(LifeCycleEvent event) {
                window.hide();
                return true;
            }
        });
    }
    
    public void doSearchGUI(String searchquery) {
        Query query = null;
        if (!searchquery.equals("")) {
            
            try {
                resulttable.clear();
                Searcher searcher = new IndexSearcher(INDEXFILE);
                Analyzer analyzer = new StopAnalyzer();
                String selectedSearchType = searchtypecombo.getActiveText();
                String[] fields = new String[0];
                fields = getSearchFields(selectedSearchType);
                
                
                query = MultiFieldQueryParser.parse(searchquery, fields, analyzer);
                log.debug("Searching for: " + query.toString("contents"));
                
                Hits hits = null;
                
                hits = searcher.search(query);
                long start = System.currentTimeMillis();
                log.debug(hits.length() + " total matching documents");
                for (int i = 0; i < hits.length(); i++) {
                    Document doc = null;
                    doc = hits.doc(i);
                    log.debug("Found: " + doc.get("type"));
                    MainContentsGUI gui = GUIFactory.getInterface(doc);
                    resulttable.addToTable(gui);
                }
                long stop = System.currentTimeMillis();
                log.debug("Displaying took: "+(stop-start)+" milliseconds");
                setStatusBarMessage("Found "+hits.length()+" doucuments ccontaining the word '"+searchquery+"' in "+(stop-start)+" milliseconds");
                searcher.close();
            } catch (IOException e2) {
                log.error(e2);
            } catch (ParseException e) {
                log.error(e);
            }
        }
    }
    
    public static String[] concatArrays(String[] src, String[] dest) {
        String[] result = new String[src.length + dest.length];
        int counter = 0;
        for (int i = 0; i < src.length; i++) {
            result[counter] = src[i];
            counter++;
        }
        for (int i = 0; i < dest.length; i++) {
            result[counter] = dest[i];
            counter++;
        }
        return result;
        
    }
    
    public void initCombo() {
        searchtypecombo.setActive(0);
        searchtypecombo.showAll();
    }
    
    private String[] getSearchFields(String selectedSearchType) {
        String[] fields = new String[0];
        if(selectedSearchType.equalsIgnoreCase("Music") || "All".equalsIgnoreCase(selectedSearchType))
            fields = concatArrays(MP3Document.fields, fields);
        
        if(selectedSearchType.equalsIgnoreCase("Chats") || "All".equalsIgnoreCase(selectedSearchType))
            fields = concatArrays(GaimLogDocument.fields, fields);
        
        if(selectedSearchType.equalsIgnoreCase("Files") || "All".equalsIgnoreCase(selectedSearchType))
            fields = concatArrays(FileDocument.fields, fields);
        
        if(selectedSearchType.equalsIgnoreCase("Images") || "All".equalsIgnoreCase(selectedSearchType))
            fields = concatArrays(ImageDocument.fields, fields);
        
        if(selectedSearchType.equalsIgnoreCase("Documents") || "All".equalsIgnoreCase(selectedSearchType))
            fields = concatArrays(PDFDocument.fields, fields);
        
        if(selectedSearchType.equalsIgnoreCase("Mails") || "All".equalsIgnoreCase(selectedSearchType))
            fields = concatArrays(EvolutionMailDocument.fields, fields);
        
        if(selectedSearchType.equalsIgnoreCase("Source code") || "All".equalsIgnoreCase(selectedSearchType))
            fields = concatArrays(JavaDocument.fields, fields);
        
        if(selectedSearchType.equalsIgnoreCase("Notes") || "All".equalsIgnoreCase(selectedSearchType))
            fields = concatArrays(TomboyDocument.fields, fields);
        
        if(selectedSearchType.equalsIgnoreCase("Addresses") || "All".equalsIgnoreCase(selectedSearchType))
            fields = concatArrays(AddressBookDocument.fields, fields);
        
        if(selectedSearchType.equalsIgnoreCase("Application") || "All".equalsIgnoreCase(selectedSearchType))
            fields = concatArrays(ApplicationDocument.fields, fields);
        
        if(selectedSearchType.equalsIgnoreCase("Webpages") || "All".equalsIgnoreCase(selectedSearchType))
            fields = concatArrays(HTMLDocument.fields, fields);
        return fields;
    }
    public void setStatusBarMessage(String msg) {
         statusbar.push(statusbar.getContextID(msg), msg);
    }
}