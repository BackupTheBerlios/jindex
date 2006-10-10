package org.jindex.documents;

import java.io.File;
import java.io.IOException;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.lucene.document.DateField;
import org.apache.lucene.document.Field;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.jindex.utils.LuceneUtility;

public class MozillaBookmarkDocument {
	public static String[] fields = { "path", "absolutepath", "protocol", "startdate", "starttime", "endtime", "from", "alias", "filecontents" };
	public static void main(String argv[] ) {
		
	}
	
	public static org.apache.lucene.document.Document Document(File f) {
		org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
		doc.add(Field.Keyword("path", f.getPath()));
		doc.add(Field.Keyword("absolutepath", f.getParent()));
		doc.add(Field.Keyword("modified", DateField.timeToString(f.lastModified())));


		try {

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			File file = new File(LuceneUtility.getHOME() + "/.gaim/blist.xml");
			org.w3c.dom.Document feed = factory.newDocumentBuilder().parse(file);

			NodeList nodelist = XPathAPI.selectNodeList(feed, "*");

			String buddyname = getNodeValue(nodelist, "name");
			String alias = getNodeValue(nodelist, "alias");
			String icon = getNodeValueBasedOnAttribute(nodelist, "setting", "name", "buddy_icon");

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

	private static String getProtocol(String line) {
		String result = "";
		StringTokenizer st = new StringTokenizer(line, " ");
		while (st.hasMoreTokens())
			result = st.nextToken();
		result = result.replace('(', ' ');
		result = result.replace(')', ' ');

		return result.trim();
	}

	private static String getEndTime(String line) {
		String result = "";
		StringTokenizer st = new StringTokenizer(line, " ");
		result = st.nextToken();
		result = result.replace('(', ' ');
		result = result.replace(')', ' ');
		return result.trim();
	}

	private static String getName(String line) {

		if (line.indexOf("Conversation with ") == 0) {
			StringTokenizer st = new StringTokenizer(line, " ");
			st.nextToken();
			st.nextToken();
			String result = st.nextToken();
			return result;
		}
		if (line.indexOf("IM Sessions with ") == 0) {
			StringTokenizer st = new StringTokenizer(line, " ");
			st.nextToken();
			st.nextToken();
			st.nextToken();
			String result = st.nextToken();
			return result;
		}
		return "";
	}

	private static String[] getStartTimes(String line) {
		/*
		 * Returns the start date and time
		 */
		String result[] = new String[2];
		StringTokenizer st = new StringTokenizer(line, " ");
		st.nextToken();
		st.nextToken();
		st.nextToken();
		st.nextToken();
		result[0] = st.nextToken(); // date
		result[1] = st.nextToken(); // time
		return result;
	}

	private static String getNodeValue(NodeList node, String value) {
		for (int i = 0; i < node.getLength(); i++) {

			if (node.item(i).getNodeName().equals(value)) {
				return node.item(i).getChildNodes().item(0).getNodeValue();
			}
		}

		return "";
	}

	private static String getNodeValueBasedOnAttribute(NodeList node, String value, String attr, String attrvalue) {
		Node curnode;
		for (int i = 0; i < node.getLength(); i++) {
			curnode = node.item(i);
			if (curnode.getNodeName().equals(value)) {
				NamedNodeMap amap = curnode.getAttributes();
				if (amap.getNamedItem(attr).getNodeValue().equals(attrvalue)) {
					return curnode.getFirstChild().getNodeValue();
				}
			}
		}

		return "";
	}

	public String[] getSearchFields() {
		// TODO Auto-generated method stub
		return null;
	}

}
