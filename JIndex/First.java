import gui.GaimLogGUI;
import gui.ImageContentGUI;
import gui.JavaDocumentGUI;
import gui.MailGUI;
import gui.MainContentsGUI;
import gui.MainGUIInterface;
import gui.OpenOfficeDocumentGUI;
import gui.PDFContentGUI;
import gui.UnknownfiletypeGUI;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.ImageIcon;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.gnu.gdk.Pixbuf;
import org.gnu.gdk.PixbufLoader;
import org.gnu.glade.GladeXMLException;
import org.gnu.glade.LibGlade;
import org.gnu.glib.JGException;
import org.gnu.gnome.AppBar;
import org.gnu.gnome.Program;
import org.gnu.gtk.CellRendererPixbuf;
import org.gnu.gtk.CellRendererText;
import org.gnu.gtk.ComboBox;
import org.gnu.gtk.DataColumn;
import org.gnu.gtk.DataColumnObject;
import org.gnu.gtk.DataColumnPixbuf;
import org.gnu.gtk.DataColumnString;
import org.gnu.gtk.Entry;
import org.gnu.gtk.Gtk;
import org.gnu.gtk.ListStore;
import org.gnu.gtk.StatusBar;
import org.gnu.gtk.TreeIter;
import org.gnu.gtk.TreePath;
import org.gnu.gtk.TreeView;
import org.gnu.gtk.TreeViewColumn;
import org.gnu.gtk.VBox;
import org.gnu.gtk.Viewport;
import org.gnu.gtk.Window;
import org.gnu.gtk.event.KeyEvent;
import org.gnu.gtk.event.KeyListener;
import org.gnu.gtk.event.LifeCycleEvent;
import org.gnu.gtk.event.LifeCycleListener;
import org.gnu.gtk.event.TreeViewEvent;
import org.gnu.gtk.event.TreeViewListener;
import org.jdesktop.jdic.tray.SystemTray;
import org.jdesktop.jdic.tray.TrayIcon;

import utils.LuceneUtility;

import documents.FileDocument;
import documents.GaimLogDocument;
import documents.ImageDocument;
import documents.JavaDocument;
import documents.MP3Document;
import documents.OpenOfficeDocument;
import documents.PDFDocument;
import documents.mbox.EvolutionMailDocument;

public class First implements TreeViewListener {

	private static String INDEXFILE = System.getProperty("HOME") + "/index";

	private LibGlade firstApp;

	Window window; // Main window

	TrayIcon ticon;

	SystemTray trayicon;

	VBox contentpane = null;

	// Tree view
	TreeView resulttable = null;

	ListStore ls = null;

	DataColumnPixbuf ColThumbImage;

	DataColumnString ColData;

	DataColumnObject ColObj;

	ComboBox searchtypecombo;

	private AppBar statusbar;

	public First() throws FileNotFoundException, GladeXMLException, IOException {
		firstApp = new LibGlade("glade/jindex.glade", this);
		addWindowCloser();
		final Entry searchfield = (Entry) firstApp.getWidget("queryfield");
		searchtypecombo = (ComboBox) firstApp.getWidget("searchtypecombo");
		
//		statusbar = (AppBar) firstApp.getWidget("statusbar");
//		statusbar.setStatusText("Appliction loaded");
		
		resulttable = (TreeView) firstApp.getWidget("resultview");
		resulttable.setHoverSelection(true);
		resulttable.addListener(this);
		initTable();

		initCombo();
		searchfield.addListener(new KeyListener() {

			public boolean keyEvent(KeyEvent event) {
				if (event.getKeyval() == 65293 && event.getType() == KeyEvent.Type.KEY_PRESSED) {// catch
					doSearchGUI(searchfield.getText());
					return true;
				}
				return false;
			}

		});
		window.setBooleanProperty("hidden", false);
		trayicon = SystemTray.getDefaultSystemTray();
		ImageIcon icon = new ImageIcon("images/stock_search.png");

		ticon = new TrayIcon(icon);
		ticon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (window.getBooleanProperty("hidden")) {
					window.setBooleanProperty("hidden", false);
					window.show();
				} else {
					window.setBooleanProperty("hidden", true);
					window.hide();
				}
			}

		});
		ticon.setCaption("JIndex");
		trayicon.addTrayIcon(ticon);

		IndexReader reader = IndexReader.open(INDEXFILE);
		System.out.println("Number of documents in index is " + reader.numDocs());
		reader.close();
	}

	public static void main(String[] args) {
		try {
			Gtk.init(args);
			new First();
			Gtk.main();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void addWindowCloser() {
		window = (Window) firstApp.getWidget("mainwindow");
		window.addListener(new LifeCycleListener() {
			public void lifeCycleEvent(LifeCycleEvent event) {
			}

			public boolean lifeCycleQuery(LifeCycleEvent event) {
				Gtk.mainQuit();
				trayicon.removeTrayIcon(ticon);
				return false;
			}
		});
	}

	public void doSearchGUI(String searchquery) {
		// SearchFiles files = new SearchFiles(queryfield.getText());
		Query query = null;
		if (!searchquery.equals("")) {
			try {
				while (true) {
					TreeIter item = ls.getIter("0");
					if (item == null)
						break;
					ls.removeRow(item);
				}
				Searcher searcher = new IndexSearcher(INDEXFILE);
				Analyzer analyzer = new StandardAnalyzer();

				String[] fields = new String[0];

				fields = concatArrays(MP3Document.fields, fields);
				fields = concatArrays(GaimLogDocument.fields, fields);
				fields = concatArrays(FileDocument.fields, fields);
				fields = concatArrays(ImageDocument.fields, fields);
				fields = concatArrays(PDFDocument.fields, fields);
				fields = concatArrays(EvolutionMailDocument.fields, fields);
				fields = concatArrays(JavaDocument.fields, fields);

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
						GaimLogGUI gui = new GaimLogGUI(doc);
						addToTable(i, gui);
					} else if (doc.get("type").equals("audio/mp3")) {
						// System.out.println("Adding audio info");
						// box.add(new MP3LogGUI(doc));
						// contentpane.packStart(new
						// MP3LogGUI(doc).getGnomeGUI(), false, true, 0);
					} else if (doc.get("type").equals("image")) {
						// contentpane.packStart(new
						// ImageContentGUI(doc).getGnomeGUI(alternaterow),
						// false, true, 0);
						// System.out.println("Added image");
						ImageContentGUI gui = new ImageContentGUI(doc);
						addToTable(i, gui);
					} else if (doc.get("type").equals("application/pdf")) {
						PDFContentGUI gui = new PDFContentGUI(doc);
						addToTable(i, gui);
					} else if (doc.get("type").equals("text/x-java")) {
						System.out.println("FOUND JAVA FILE");
						JavaDocumentGUI gui = new JavaDocumentGUI(doc);
						addToTable(i, gui);
					}
					else if(doc.get("type").equals("application/vnd.sun.xml.writer")) {
						OpenOfficeDocumentGUI gui = new OpenOfficeDocumentGUI(doc);
						addToTable(i, gui);
					}

					else

					if (doc.get("type").equals("mail")) {
						// content += new MailGUI(doc).getHTML();
						MailGUI gui = new MailGUI(doc);
						addToTable(i, gui);
					} else {
						System.out.println("found unknown file");
						UnknownfiletypeGUI gui = new UnknownfiletypeGUI(doc);
						addToTable(i, gui);
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

	public void initTable() {
		ColThumbImage = new DataColumnPixbuf();
		ColData = new DataColumnString();
		ColObj = new DataColumnObject();
		ls = new ListStore(new DataColumn[] { ColThumbImage, ColData, ColObj });

		resulttable.setEnableSearch(true); /*
											 * allows to use keyboard to search
											 * items matching the pressed keys
											 */

		resulttable.setAlternateRowColor(true); /* no comments smile */
		resulttable.setModel(ls);

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

		resulttable.setSearchDataColumn(ColData);
		/* append columns */
		resulttable.appendColumn(col0);
		resulttable.appendColumn(col2);
		resulttable.appendColumn(col3);
	}

	public void initCombo() {
		searchtypecombo.showAll();
	}

	public void addToTable(int commandNumber, MainContentsGUI gui) {
		
		TreeIter row = ls.appendRow();
		if (!(gui.getIcon() == null)) {
			PixbufLoader test = new PixbufLoader();
			test.write(gui.getIcon());

			ls.setValue(row, ColThumbImage, test.getPixbuf());
		} else {
			try {
				ls.setValue(row, ColThumbImage, new Pixbuf("images/icon_missing.svg"));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (JGException e) {
				e.printStackTrace();
			}
		}
		ls.setValue(row, ColData, gui.getTextContent());
		ls.setValue(row, ColObj, gui);
		resulttable.showAll();
	}

	public void treeViewEvent(TreeViewEvent event) {
		if (event.getTreeIter() != null) {
			TreePath[] tp = resulttable.getSelection().getSelectedRows();
			if (tp.length == 1) {
				TreeIter item = ls.getIter(tp[0].toString());
				MainContentsGUI command = (MainContentsGUI) ls.getValue(item, ColObj);
				try {
					System.out.println(command);
					Process p = Runtime.getRuntime().exec(command.getOpenAction());
					char[] error = new char[2048];
					InputStreamReader isr = new InputStreamReader(p.getErrorStream());
					isr.read(error);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}