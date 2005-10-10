import gui.ImageContentGUI;
import gui.MP3LogGUI;
import gui.PDFContentGUI;
import gui.UnknownfiletypeGUI;

import java.io.FileNotFoundException;
import java.io.IOException;

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
import org.gnu.glade.GladeXMLException;
import org.gnu.glade.LibGlade;
import org.gnu.gtk.Entry;
import org.gnu.gtk.Gtk;
import org.gnu.gtk.HSeparator;
import org.gnu.gtk.Label;
import org.gnu.gtk.StateType;
import org.gnu.gtk.Table;
import org.gnu.gtk.VBox;
import org.gnu.gtk.Viewport;
import org.gnu.gtk.Widget;
import org.gnu.gtk.event.KeyEvent;
import org.gnu.gtk.event.KeyListener;

import documents.FileDocument;
import documents.GaimLogDocument;
import documents.ImageDocument;
import documents.MP3Document;
import documents.PDFDocument;

public class First {
	private static String INDEXFILE = System.getProperty("HOME") + "/index";

	private LibGlade firstApp;
	Viewport viewport;
	VBox contentpane = null;

	Table resulttable = null;

	public First() throws FileNotFoundException, GladeXMLException, IOException {
		firstApp = new LibGlade("glade/jindex.glade", this);
		final Entry searchfield = (Entry) firstApp.getWidget("queryfield");
		viewport = (Viewport) firstApp.getWidget("viewport1");
		resulttable = (Table) firstApp.getWidget("resulttable");
		resulttable.setBackgroundColor(StateType.NORMAL, new Color(200, 200, 200));
		resulttable.setBackgroundColor(StateType.ACTIVE, new Color(200, 200, 200));

		resulttable.setBaseColor(StateType.NORMAL, new Color(200, 200, 200));
		resulttable.setBaseColor(StateType.ACTIVE, new Color(200, 200, 200));

		resulttable.setForegroundColor(StateType.NORMAL, new Color(200, 200, 200));
		resulttable.setForegroundColor(StateType.ACTIVE, new Color(200, 200, 200));

//		int column = 0;
//		int row = 1;
//		resulttable.attach(new Label("palle123"), column, column + 1, row, row + 1);
//		column = 1;
//		row = 1;
//		resulttable.attach(new Label("palle"), column, column + 1, row, row + 1);
//
//		column = 0;
//		row = 2;
//		resulttable.attach(new Label("Søren"), column, column + 1, row, row + 1);
//		column = 1;
//		row = 2;
//		resulttable.attach(new Label("Mathiasen"), column, column + 1, row, row + 1);
//
//		resulttable.showAll();
		searchfield.addListener(new KeyListener() {

			public boolean keyEvent(KeyEvent event) {
				if (event.getKeyval() == 65293 && event.getType() == KeyEvent.Type.KEY_PRESSED) {// catch
					doSearchGUI(searchfield.getText());
					return true;
				}
				return false;
			}

		});
	}

	public static void main(String[] args) {
		First g;

		try {
			Gtk.init(args);
			g = new First();
			Gtk.main();
		} catch (Exception e) {
			e.printStackTrace();
		}
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

				query = MultiFieldQueryParser.parse(searchquery, fields, analyzer);
				// query = QueryParser.parse(searchquery, "contents", analyzer);

				System.out.println("Searching for: " + query.toString("contents"));

				Hits hits = null;

				hits = searcher.search(query);
				System.out.println(hits.length() + " total matching documents");
				//resulttable.destroy();
				resulttable = new Table(2,2, true);
				
				for (int i = 0; i < hits.length(); i++) {
					int row = i;
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
						contentpane.packStart(new MP3LogGUI(doc).getGnomeGUI(alternaterow), false, true, 0);
					} else if (doc.get("type").equals("image")) {
						contentpane.packStart(new ImageContentGUI(doc).getGnomeGUI(alternaterow), false, true, 0);
						System.out.println("Added image");
					} else if (doc.get("type").equals("application/pdf")) {
						System.out.println("Added PDF");
						contentpane.packStart(new PDFContentGUI(doc).getGnomeGUI(alternaterow), false, true, 0);
					} else

					if (doc.get("type").equals("mail")) {
						System.out.println("mail info");
						// content += new MailGUI(doc).getHTML();
					} else {
						UnknownfiletypeGUI gui = new UnknownfiletypeGUI(doc);
						gui.getGnomeGUI(alternaterow);
						addToTable(i, gui.getImageIconPane(), gui.getMainContent());
						//contentpane.packStart(new UnknownfiletypeGUI(doc).getGnomeGUI(alternaterow), false, true, 0);

					}
					//contentpane.packStart(new HSeparator(), false, true, 0);
					//if(i==1) break;
				}
				
				searcher.close();
			
			} catch (IOException e2) {
				e2.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			resulttable.resizeChildren();
			resulttable.showAll();
			viewport.showAll();
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

	public void addToTable(int row, VBox one, VBox two) {
		System.out.println("Row: "+row);
		
		if(!(one == null && two ==null )) {
			int column = 0;
			System.out.println("resulttable.attach(one, "+column+", "+(column + 1)+", "+row+", "+(row + 1)+");");
			//resulttable.attach(one, column, column + 1, row, row + 1);
			resulttable.attach(new Label("test: "+row), column, column + 1, row, row + 1);
			column = 1;
			System.out.println("resulttable.attach(two, "+column+", "+(column + 1)+", "+row+", "+(row + 1)+");");
			//resulttable.attach(two, column, column + 1, row, row + 1);
			resulttable.attach(new Label("test: "+row), column, column + 1, row, row + 1);
		} else {
			System.out.println("Component one or two is null");
		}
	}
}
