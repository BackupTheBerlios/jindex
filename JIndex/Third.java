import gui.GaimLogGUI;
import gui.ImageContentGUI;
import gui.MP3LogGUI;
import gui.PDFContentGUI;

import java.io.IOException;

import javax.swing.JPanel;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.gnu.gtk.Entry;
import org.gnu.gtk.Gtk;
import org.gnu.gtk.HBox;
import org.gnu.gtk.Label;
import org.gnu.gtk.PolicyType;
import org.gnu.gtk.ScrolledWindow;
import org.gnu.gtk.VBox;
import org.gnu.gtk.Widget;
import org.gnu.gtk.Window;
import org.gnu.gtk.WindowType;
import org.gnu.gtk.event.KeyEvent;
import org.gnu.gtk.event.KeyListener;
import org.gnu.gtk.event.LifeCycleEvent;
import org.gnu.gtk.event.LifeCycleListener;

import documents.FileDocument;
import documents.GaimLogDocument;
import documents.ImageDocument;
import documents.MP3Document;
import documents.PDFDocument;

public class Third {
    VBox contentpane;
    VBox mainpane;
    ScrolledWindow scrolled_window;
    public Widget createEntryWidget() {
        mainpane = new VBox(false, 0);
        contentpane = new VBox(true, 0);
        final HBox b = new HBox(false, 0);

        Label label = new Label("Query: ");
        b.add(label);

        final Entry entry = new Entry();
        entry.setText("Search string");
        entry.setVisible(true);
        entry.addListener(new KeyListener() {

            public boolean keyEvent(KeyEvent event) {
                if (event.getKeyval() == 65293
                        && event.getType() == KeyEvent.Type.KEY_PRESSED) {// catch
                                                                            // enter
                    System.out.println(entry.getText());
                    doSearchGUI(entry.getText());
                    //mainpane.add(contentpane);
                    
                    contentpane.showAll();
                    scrolled_window.showAll();
                    entry.setText("");
                    return true;
                }
                return false;
            }

        });

        b.add(entry);
        mainpane.add(b);

        
        scrolled_window = new ScrolledWindow(null, null);
        scrolled_window.setBorderWidth(10);
        scrolled_window.setPolicy(PolicyType.AUTOMATIC, PolicyType.AUTOMATIC);
        scrolled_window.show();
        scrolled_window.addWithViewport(contentpane);
        scrolled_window.setMinimumSize(500,600);
        
        mainpane.add(scrolled_window);
        return mainpane;
    }

    public Third(String[] args) {
        Gtk.init(args);
        Window w = new Window(WindowType.TOPLEVEL);
        w.addListener(new LifeCycleListener() {
            public void lifeCycleEvent(LifeCycleEvent event) {
            }

            public boolean lifeCycleQuery(LifeCycleEvent event) {
                Gtk.mainQuit();
                return false;
            }
        });
        w.setDefaultSize(200, 30);
        w.setBorderWidth(5);
        w.setTitle("JIndex");
        w.add(createEntryWidget());
        w.showAll();
        
        Gtk.main();
    }

    public static void main(String[] args) {
        new Third(args);
    }

    static JPanel box;

    private static String INDEXFILE = System.getProperty("HOME") + "/index";

    public void doSearchGUI(String searchquery) {

        // SearchFiles files = new SearchFiles(queryfield.getText());
        Query query = null;

        try {

            Searcher searcher = new IndexSearcher(INDEXFILE);

            Analyzer analyzer = new StandardAnalyzer();

            String[] fields = new String[0];

            fields = concatArrays(MP3Document.fields, fields);
            fields = concatArrays(GaimLogDocument.fields, fields);
            fields = concatArrays(FileDocument.fields, fields);
            fields = concatArrays(ImageDocument.fields, fields);
            fields = concatArrays(PDFDocument.fields, fields);

            query = MultiFieldQueryParser.parse(searchquery, fields, analyzer);
            // query = QueryParser.parse(searchquery, "contents", analyzer);

            System.out.println("Searching for: " + query.toString("contents"));

            Hits hits = null;

            hits = searcher.search(query);
            System.out.println(hits.length() + " total matching documents");

            for (int i = 0; i < hits.length(); i++) {
                Document doc = null;
                doc = hits.doc(i);

                // System.out.println("Found: " + doc.get("type"));
                if (doc.get("type").equals("text/gaimlog")) {
                    // box.add(new GaimLogGUI(doc));
                    box.add(new GaimLogGUI(doc).getGUI());
                    // box.add(new GaimLogGUI(doc));
                } else if (doc.get("type").equals("audio/mp3")) {
                    // System.out.println("Adding audio info");
                    // box.add(new MP3LogGUI(doc));
                    contentpane.add(new MP3LogGUI(doc).getGnomeGUI());
                } else if (doc.get("type").equals("image")) {
                    contentpane.add(new ImageContentGUI(doc).getGnomeGUI());
                    System.out.println("Added image");
                } else if (doc.get("type").equals("application/pdf")) {
                    System.out.println("Added PDF");
                    box.add(new PDFContentGUI(doc).getGUI());
                }

                if (doc.get("type").equals("mail")) {
                    System.out.println("mail info");
                    // content += new MailGUI(doc).getHTML();
                } else {
                    // box.add(new UnknownfiletypeGUI(doc).getGUI());
                }
            }
            searcher.close();
        } catch (IOException e2) {
            e2.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
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
}
