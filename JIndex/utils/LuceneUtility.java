/*
 * Created on Sep 24, 2005
 */
package utils;

import java.io.IOException;

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

	public static synchronized IndexWriter getWriter() {
		IndexWriter writer = null;
		try {
			writer = new IndexWriter(HOME + "/index", new StandardAnalyzer(), false);
		} catch (IOException e) {
			try {
				writer = new IndexWriter(HOME + "/index", new StandardAnalyzer(), true);
			} catch (IOException e1) {
				e.printStackTrace();
			}
		}
		return writer;
	}

	public static synchronized void addDocument(Document document) {
		try {
			removeEntry(document.get("path"));
			IndexWriter writer = getWriter();
			writer.addDocument(document);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void removeEntry(String filename) {

		try {
			IndexReader reader = IndexReader.open(HOME + "/index");
			try {
				System.out.println("Index contains : " + reader.numDocs() + " documents");
				System.out.println("Removing old entry: " + filename);
				int delcounter = reader.delete(new Term("path", filename));
				System.out.println("deleted " + delcounter + " documents");
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
