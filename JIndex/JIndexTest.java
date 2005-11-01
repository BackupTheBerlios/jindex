import gui.ImageContentGUI;
import gui.MP3LogGUI;
import gui.PDFContentGUI;
import gui.UnknownfiletypeGUI;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.gnu.gdk.Color;
import org.gnu.gtk.Entry;
import org.gnu.gtk.Gtk;
import org.gnu.gtk.HBox;
import org.gnu.gtk.HSeparator;
import org.gnu.gtk.Label;
import org.gnu.gtk.PolicyType;
import org.gnu.gtk.ScrolledWindow;
import org.gnu.gtk.StateType;
import org.gnu.gtk.Table;
import org.gnu.gtk.VBox;
import org.gnu.gtk.Widget;
import org.gnu.gtk.Window;
import org.gnu.gtk.WindowType;
import org.gnu.gtk.event.KeyEvent;
import org.gnu.gtk.event.KeyListener;
import org.gnu.gtk.event.LifeCycleEvent;
import org.gnu.gtk.event.LifeCycleListener;
import org.jdesktop.jdic.filetypes.Association;
import org.jdesktop.jdic.filetypes.AssociationService;

import documents.FileDocument;
import documents.GaimLogDocument;
import documents.ImageDocument;
import documents.MP3Document;
import documents.PDFDocument;

public class JIndexTest {
    private static String INDEXFILE = System.getProperty("HOME") + "/index";

    VBox contentpane;

    VBox mainpane;

    ScrolledWindow scrolled_window;

    public Widget createEntryWidget() {
        mainpane = new VBox(false, 0);
        mainpane.setBackgroundColor(StateType.NORMAL, Color.WHITE);

        contentpane = new VBox(false, 0);
        contentpane.setBackgroundColor(StateType.NORMAL, Color.WHITE);
        final HBox b = new HBox(false, 0);
        b.setBackgroundColor(StateType.NORMAL, Color.WHITE);
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
                    contentpane.destroy();
                    contentpane = new VBox(false, 0);
                    doSearchGUI(entry.getText());
                    // mainpane.add(contentpane);
                    scrolled_window.addWithViewport(contentpane);
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
        scrolled_window.setMinimumSize(500, 600);
        scrolled_window.setBackgroundColor(StateType.NORMAL, Color.WHITE);
        mainpane.add(scrolled_window);
        return mainpane;
    }

    public JIndexTest(String[] args) {
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
        Widget wg = createEntryWidget();
        // wg.setBackgroundColor(StateType.NORMAL, Color.WHITE);
        w.add(wg);
        w.showAll();
        // w.setBackgroundColor(StateType.NORMAL, Color.WHITE);
        Gtk.main();
    }

    public static void main(String[] args) {
        new JIndex(args);
    }

    public void doSearchGUI(String searchquery) {
       
        // SearchFiles files = new SearchFiles(queryfield.getText());
        Query query = null;
        if (!searchquery.equals("")) {
            try {

                Searcher searcher = new IndexSearcher(INDEXFILE);

                Analyzer analyzer = new StandardAnalyzer();

                String[] fields = new String[0];

                fields = concatArrays(MP3Document.fields, fields);
                fields = concatArrays(GaimLogDocument.fields, fields);
                fields = concatArrays(FileDocument.fields, fields);
                fields = concatArrays(ImageDocument.fields, fields);
                fields = concatArrays(PDFDocument.fields, fields);

                query = MultiFieldQueryParser.parse(searchquery, fields,
                        analyzer);
                // query = QueryParser.parse(searchquery, "contents", analyzer);

                System.out.println("Searching for: "
                        + query.toString("contents"));

                Hits hits = null;

                hits = searcher.search(query);
                System.out.println(hits.length() + " total matching documents");
                Table table = new Table(hits.length(),2, true);
                for (int i = 0; i < hits.length(); i++) {
                    Document doc = null;
                    doc = hits.doc(i);
                    boolean alternaterow;
                    if (i % 2 == 0)
                        alternaterow = true;
                    else
                        alternaterow = false;
                    // System.out.println("Found: " + doc.get("type"));
                    if (doc.get("type").equals("text/gaimlog")) {
                        // box.add(new GaimLogGUI(doc));
                        // box.add(new GaimLogGUI(doc).getGUI());
                        // box.add(new GaimLogGUI(doc));
                    } else if (doc.get("type").equals("audio/mp3")) {
                        // System.out.println("Adding audio info");
                        // box.add(new MP3LogGUI(doc));
                        contentpane.packStart(new MP3LogGUI(doc)
                                .getGnomeGUI(alternaterow), false, true, 0);
                    } else if (doc.get("type").equals("image")) {
                        contentpane.packStart(new ImageContentGUI(doc)
                                .getGnomeGUI(alternaterow), false, true, 0);
                        System.out.println("Added image");
                    } else if (doc.get("type").equals("application/pdf")) {
                        System.out.println("Added PDF");
                        PDFContentGUI content = new PDFContentGUI(doc);
                        content.getGnomeGUI(alternaterow);
                        System.out.println(i);
//                        table.attach(content.imgcontent, i, 1, i, 1);
//                        table.attach(content.maincontent, i, 2, i, 2);
                        
                        table.attach(new Label("test"), i, 1, i, 1);
                        table.attach(new Label("tes34t"), i, 2, i, 2);

                        //contentpane.packStart(, false, true, 0);
                    } else

                    if (doc.get("type").equals("mail")) {
                        System.out.println("mail info");
                        // content += new MailGUI(doc).getHTML();
                    } else {
//                        contentpane.packStart(new UnknownfiletypeGUI(doc)
//                                .getGnomeGUI(alternaterow, table), false, true, 0);

                    }
                    //contentpane.packStart(new HSeparator(), false, true, 0);
                }
                contentpane.packStart(table, false, true, 0);
                searcher.close();
            } catch (IOException e2) {
                e2.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
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
}
