package client;

import gui.EvolutionAddressBookGUI;
import gui.ExcelDocumentGUI;
import gui.GaimLogGUI;
import gui.ImageContentGUI;
import gui.JavaDocumentGUI;
import gui.MP3LogGUI;
import gui.MailGUI;
import gui.OpenOfficeDocumentGUI;
import gui.PDFContentGUI;
import gui.TomboyDocumentGUI;
import gui.UnknownfiletypeGUI;
import gui.trayicon.TrayIconPopupMenu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.ImageIcon;

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
import org.gnu.gnome.AppBar;
import org.gnu.gtk.ComboBox;
import org.gnu.gtk.DataColumnObject;
import org.gnu.gtk.DataColumnPixbuf;
import org.gnu.gtk.DataColumnString;
import org.gnu.gtk.Entry;
import org.gnu.gtk.Gtk;
import org.gnu.gtk.ListStore;
import org.gnu.gtk.TreeView;
import org.gnu.gtk.VBox;
import org.gnu.gtk.Window;
import org.gnu.gtk.event.KeyEvent;
import org.gnu.gtk.event.KeyListener;
import org.gnu.gtk.event.LifeCycleEvent;
import org.gnu.gtk.event.LifeCycleListener;
import org.jdesktop.jdic.tray.SystemTray;
import org.jdesktop.jdic.tray.TrayIcon;

import documents.AddressBookDocument;
import documents.FileDocument;
import documents.GaimLogDocument;
import documents.ImageDocument;
import documents.JavaDocument;
import documents.MP3Document;
import documents.TomboyDocument;
import documents.mbox.EvolutionMailDocument;
import documents.office.PDFDocument;

public class JIndexClient {

	private static String INDEXFILE = System.getProperty("HOME") + "/index";

	private static LibGlade firstApp;

	Window window; // Main window

	TrayIcon ticon;

	SystemTray trayicon;

	VBox contentpane = null;

	// Tree view
	SearchResultTable resulttable = null;

	TreeView resulttable1 = null;

	ListStore ls = null;

	DataColumnPixbuf ColThumbImage;

	DataColumnString ColData;

	DataColumnObject ColObj;

	ComboBox searchtypecombo;

	private AppBar statusbar;

	public JIndexClient() throws FileNotFoundException, GladeXMLException,
			IOException {
		InputStream is = this.getClass().getResourceAsStream(
				"/glade/jindex.glade");
		firstApp = new LibGlade(is, this, null);
		window = (Window) firstApp.getWidget("mainwindow");
		addWindowCloser();
		final Entry searchfield = (Entry) firstApp.getWidget("queryfield");
		searchtypecombo = (ComboBox) firstApp.getWidget("searchtypecombo");

		// statusbar = (AppBar) firstApp.getWidget("ApplicationBar");
		// if(statusbar == null)
		// System.out.println("Statusbar is null..");
		// statusbar.setStatusText("Appliction loaded");
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
		trayicon = SystemTray.getDefaultSystemTray();

		ImageIcon icon = new ImageIcon(this.getClass().getResource(
				"/images/stock_search.png"));

		ticon = new TrayIcon(icon, "JIndex Desktop Search");
		ticon.addBalloonActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				System.out.println("Action!!!");
			}

		});
		ticon.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (!window.isActive()) {
					window.show();
				} else {
					window.hide();
				}

			}

		});
		ticon.setPopupMenu(new TrayIconPopupMenu(firstApp));
		ticon.setCaption("JIndex");
		trayicon.addTrayIcon(ticon);

		// StatusIcon statusicon = new
		// StatusIcon(GtkStockItem.DIALOG_AUTHENTICATION);
		// statusicon.setVisible(true);

		IndexReader reader = IndexReader.open(INDEXFILE);
		System.out.println("Number of documents in index is "
				+ reader.numDocs());
		reader.close();
	}

	public void on_exit_activate() {
		System.out.println("on_exit_activate");

	}

	public static void main(String[] args) {
		try {
			Gtk.init(args);
			new JIndexClient();
			Gtk.main();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			System.out.println("Exit");
		}

	}

	public void addWindowCloser() {
		window.addListener(new LifeCycleListener() {
			public void lifeCycleEvent(LifeCycleEvent event) {
			}

			public boolean lifeCycleQuery(LifeCycleEvent event) {
				window.hide();
				return false;
			}
		});
	}

	public void doSearchGUI(String searchquery) {
		// SearchFiles files = new SearchFiles(queryfield.getText());
		Query query = null;
		if (!searchquery.equals("")) {
			try {
				resulttable.clear();
				Searcher searcher = new IndexSearcher(INDEXFILE);
				// Analyzer analyzer = new StandardAnalyzer();
				Analyzer analyzer = new StopAnalyzer();

				String[] fields = new String[0];

				fields = concatArrays(MP3Document.fields, fields);
				fields = concatArrays(GaimLogDocument.fields, fields);
				fields = concatArrays(FileDocument.fields, fields);
				fields = concatArrays(ImageDocument.fields, fields);
				fields = concatArrays(PDFDocument.fields, fields);
				fields = concatArrays(EvolutionMailDocument.fields, fields);
				fields = concatArrays(JavaDocument.fields, fields);
				fields = concatArrays(TomboyDocument.fields, fields);
				fields = concatArrays(AddressBookDocument.fields, fields);

				query = MultiFieldQueryParser.parse(searchquery, fields,
						analyzer);
				// query = QueryParser.parse(searchquery, "contents", analyzer);

				System.out.println("Searching for: "
						+ query.toString("contents"));

				Hits hits = null;

				hits = searcher.search(query);
				System.out.println(hits.length() + " total matching documents");
				for (int i = 0; i < hits.length(); i++) {
					Document doc = null;
					doc = hits.doc(i);
					 System.out.println("Found: " + doc.get("type"));
					if (doc.get("type") != null) {
						if (doc.get("type").equals("text/gaimlog")) {
							GaimLogGUI gui = new GaimLogGUI(doc);
							resulttable.addToTable(gui);
						} else if (doc.get("type").equals("text/tomboy")) {
							TomboyDocumentGUI gui = new TomboyDocumentGUI(doc);
							resulttable.addToTable(gui);
						} else if (doc.get("type").equals("audio/mp3")) {
							MP3LogGUI gui = new MP3LogGUI(doc);
							resulttable.addToTable(gui);
						} else if (doc.get("type").equals("image")) {
							System.out.println("Added image");
							ImageContentGUI gui = new ImageContentGUI(doc);
							resulttable.addToTable(gui);
						} else if (doc.get("type").equals("application/pdf")) {
							PDFContentGUI gui = new PDFContentGUI(doc);
							resulttable.addToTable(gui);
						} else if (doc.get("type").equals("text/x-java")) {
							System.out.println("FOUND JAVA FILE");
							JavaDocumentGUI gui = new JavaDocumentGUI(doc);
							resulttable.addToTable(gui);
						} else if (doc.get("type").equals(
								"application/vnd.sun.xml.writer")) {
							OpenOfficeDocumentGUI gui = new OpenOfficeDocumentGUI(
									doc);
							resulttable.addToTable(gui);
						} else if(doc.get("type").equals("application/vnd.ms-excel")) {
							ExcelDocumentGUI gui = new ExcelDocumentGUI(
									doc);
							resulttable.addToTable(gui);
							
						}
						
						
						else if (doc.get("type").equals("mail")) {
							MailGUI gui = new MailGUI(doc);
							resulttable.addToTable(gui);
						} else if (doc.get("type").equalsIgnoreCase(
								"EvolutionAddressBook")) {
							EvolutionAddressBookGUI gui = new EvolutionAddressBookGUI(
									doc);
							resulttable.addToTable(gui);
							System.out.println("Found EvolutionAddressBook");
						} else {
							System.out.println("found unknown file");
							UnknownfiletypeGUI gui = new UnknownfiletypeGUI(doc);
							resulttable.addToTable(gui);
						}

					} else {
						System.err.println("Document type is null");
						System.err.println(doc);
					}
				}

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

	public void initCombo() {
		searchtypecombo.showAll();
	}

}