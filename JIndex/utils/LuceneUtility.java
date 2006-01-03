/*
 * Created on Sep 24, 2005
 */
package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;

public class LuceneUtility {
	private final static String HOME = System.getProperty("HOME");

	public static String getText(Document doc, String name) {
		String result = doc.get(name);
		if (result == null)
			return "";
		return result.trim();

	}

	public synchronized static IndexWriter getWriter() {
		IndexWriter writer = null;
		try {
			writer = new IndexWriter(HOME + "/index", new StandardAnalyzer(), false);
			System.out.println("opened writer");
		} catch (IOException e) {
			if (!new File(HOME + "/index").exists()) {
				try {
					writer = new IndexWriter(HOME + "/index", new StandardAnalyzer(), true);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		return writer;
	}

	public static synchronized void addDocument(Document document) {
		addDocument(document, true);
	}

	public static synchronized void removeEntry(String filename) {

		try {
			IndexReader reader = IndexReader.open(HOME + "/index");
			try {
				System.out.println("Index contains : " + reader.numDocs() + " documents");
				System.out.println("Removing old entry: " + filename);
				int delcounter = reader.delete(new Term("path", filename));
				System.out.println("deleted " + delcounter + " documents");
			} catch (FileNotFoundException fe) {
				// skip might be the first run, so no index does exsits..
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				System.out.println("closing writer..");
				reader.close();
			}
		} catch (FileNotFoundException fe) {
			// skip might be the first run, so no index does exsits..
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String getHOME() {
		return HOME;
	}

	public static void addDocument(Document document, boolean removeOldDoc) {
		try {
			if(removeOldDoc)
				removeEntry(document.get("path"));
			IndexWriter writer = getWriter();
			try {
				if (writer != null) {
					writer.addDocument(document);
				} else {
					System.out.println("Writer is null, this is vey bad...");
				}
			} finally {
				System.out.println("closing writer..");
				if (writer != null)
					writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void addDocuments(List documents) {
		try {
		
			IndexWriter writer = getWriter();
			try {
				if (writer != null) {
					Iterator ite = documents.iterator();
					while (ite.hasNext()) {
						Document newdoc = (Document) ite.next();
						writer.addDocument(newdoc);
					}
				} else {
					System.out.println("Writer is null, this is vey bad...");
				}
			} finally {
				writer.optimize();
				System.out.println("closing writer..");
				if (writer != null)
					writer.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
