import gui.GaimLogGUI;
import gui.ImageContentGUI;
import gui.MP3LogGUI;
import gui.PDFContentGUI;
import gui.UnknownfiletypeGUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.ImageConsumer;
import java.io.IOException;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.UIManager;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.jdesktop.jdic.tray.SystemTray;
import org.jdesktop.jdic.tray.TrayIcon;

import documents.FileDocument;
import documents.GaimLogDocument;
import documents.ImageDocument;
import documents.MP3Document;
import documents.PDFDocument;

/*
 * Created on Feb 2, 2005
 *
 * 
 */

/**
 * @author sorenm
 */
public class Spider extends JFrame implements KeyListener, WindowListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1801308180541862371L;
	private static String INDEXFILE = System.getenv("HOME")+"/index";
	JTextField queryfield;
	JFrame thisframe;
	JScrollPane scrollpane;
	JPanel box;
	SystemTray tray = SystemTray.getDefaultSystemTray();
    TrayIcon ti;
    
	public static void main(String args[]) {
//		  try {
//		        UIManager.setLookAndFeel(
//		            UIManager.getSystemLookAndFeelClassName());
//		    } catch (Exception e) { }
		new Spider();
	}

	public Spider() {
		thisframe = this;
		JPanel mainpane = new JPanel(new BorderLayout());
		JPanel searchpane = new JPanel(new BorderLayout());
		JPanel resultpane = new JPanel(new BorderLayout());
		JLabel ql = new JLabel("Query:");
		queryfield = new JTextField(40);
		queryfield.addKeyListener(this);

		searchpane.add(ql, BorderLayout.WEST);
		searchpane.add(queryfield, BorderLayout.EAST);
		// resultpane.add(new JScrollPane(result), BorderLayout.CENTER);

		//box = new Box(BoxLayout.Y_AXIS);
		box = new JPanel(new GridLayout(0,1));
		
//		box = new JEditorPane();
//		box.setEditable(false);
//		box.setContentType("text/html");
//		box.addHyperlinkListener(this);
//		// Here is a another way to create a horizontal box container
//		// box = Box.createVerticalBox();
		box.setBackground(new Color(255, 255, 255));

		scrollpane = new JScrollPane(box);
		scrollpane.setPreferredSize(new Dimension(500, 500));
		scrollpane.setBackground(new Color(255, 255, 255));
		scrollpane.setWheelScrollingEnabled(true);
		
		
		resultpane.add(scrollpane, BorderLayout.CENTER);
		resultpane.setBackground(new Color(255, 255, 255));
		mainpane.add(searchpane, BorderLayout.NORTH);
		mainpane.add(resultpane, BorderLayout.CENTER);
		mainpane.setBackground(new Color(255, 255, 255));
		this.getContentPane().setBackground(new Color(255, 255, 255));
		this.getContentPane().add(mainpane);
		this.pack();
		this.show();
		this.addWindowListener(this);
		
		
		
		ImageIcon i = new ImageIcon(Spider.class.getResource("images/stock_search.png"));

        ti = new TrayIcon(i, "JDIC Tray Icon API Demo - TrayIcon");

        ti.setIconAutoSize(true);
        ti.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	thisframe.setVisible(!thisframe.isVisible());
            }
        });
        ti.addBalloonActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
              JOptionPane.showMessageDialog(null, 
              "Balloon Message been clicked - TrayIcon", "Message",
              JOptionPane.INFORMATION_MESSAGE);
            }
        });
        
        tray.addTrayIcon(ti);
        
		}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyTyped(java.awt.event.KeyEvent)
	 */
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyPressed(java.awt.event.KeyEvent)
	 */
	public void keyPressed(KeyEvent arg0) {
		// TODO Auto-generated method stub
		if (arg0.getKeyCode() == 10) {
			doSearchGUI();
		}

	}
	public void doSearchGUI() {
		
		box.removeAll();
		// SearchFiles files = new SearchFiles(queryfield.getText());
		String searchquery = queryfield.getText();
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
				} else
				if (doc.get("type").equals("audio/mp3")) {
					// System.out.println("Adding audio info");
					// box.add(new MP3LogGUI(doc));
					box.add(new MP3LogGUI(doc).getGUI());
				} else
					if (doc.get("type").equals("image")) {
						System.out.println("Added image");
						box.add(new ImageContentGUI(doc).getGUI());
					} 
					else
						if (doc.get("type").equals("application/pdf")) {
							System.out.println("Added PDF");
							box.add(new PDFContentGUI(doc).getGUI());
						} 
					
				
				if (doc.get("type").equals("mail")) {
					System.out.println("mail info");
//					content += new MailGUI(doc).getHTML();
				} else {
					box.add(new UnknownfiletypeGUI(doc).getGUI());
				}
			}
			searcher.close();
		} catch (IOException e2) {
			e2.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		scrollpane.getVerticalScrollBar().setValue(0);
		scrollpane.getHorizontalScrollBar().setValue(0);
		scrollpane.validate();
		scrollpane.revalidate();
		
		this.pack();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.KeyListener#keyReleased(java.awt.event.KeyEvent)
	 */
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.WindowListener#windowOpened(java.awt.event.WindowEvent)
	 */
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.WindowListener#windowClosing(java.awt.event.WindowEvent)
	 */
	public void windowClosing(WindowEvent arg0) {
		// TODO Auto-generated method stub
		System.exit(0);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.WindowListener#windowClosed(java.awt.event.WindowEvent)
	 */
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		System.exit(0);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.WindowListener#windowIconified(java.awt.event.WindowEvent)
	 */
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.WindowListener#windowDeiconified(java.awt.event.WindowEvent)
	 */
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.WindowListener#windowActivated(java.awt.event.WindowEvent)
	 */
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.WindowListener#windowDeactivated(java.awt.event.WindowEvent)
	 */
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub

	}


	public String[] concatArrays(String[] src, String[] dest) {
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