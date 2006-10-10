package org.jindex.documents;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Field;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TomboyDocument implements SearchDocument {
	public static String[] fields = { "last-changed", "title", "content", "path", "absolutepath" };

	public static org.apache.lucene.document.Document Document(File f) {
		org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
		doc.add(Field.Keyword("path", f.getPath()));
		doc.add(Field.Keyword("absolutepath", f.getParent()));
		doc.add(Field.Keyword("modified", DateField.timeToString(f.lastModified())));
		doc.add(Field.Text("type", "text/tomboy"));
		try {

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			File file = f;
			org.w3c.dom.Document feed = factory.newDocumentBuilder().parse(file);

			NodeList nodelist = XPathAPI.selectNodeList(feed, "note/*");
			NodeList nodelist1 = XPathAPI.selectNodeList(feed, "note/text/*");

			String title = getNodeValue(nodelist, "title");
			String lastchanged = getNodeValue(nodelist, "last-change-date");
			String content = getNodeValue(nodelist1, "note-content");
			if(title != null)
				doc.add(Field.Text("title",title));
			if(lastchanged != null)
				doc.add(Field.Text("last-changed",lastchanged));
			if(content != null)
					doc.add(Field.Text("contents",content));
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (TransformerException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		return doc;
	}


	private static String getNodeValue(NodeList node, String value) {
		for (int i = 0; i < node.getLength(); i++) {
			if (node.item(i).getNodeName().equals(value)) {
				return node.item(i).getChildNodes().item(0).getNodeValue();
			}
		}

		return "";
	}

	

	public String[] getSearchFields() {
		// TODO Auto-generated method stub
		return null;
	}

}
