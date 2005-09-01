package documents;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
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

public class GaimLogDocument implements SearchDocument {
	public static String[] fields = { "protocol", "startdate", "starttime", "endtime", "from", "alias", "contents" };
	private final static String HOME = System.getProperty("HOME");
	public static org.apache.lucene.document.Document Document(File f) throws IOException {
		org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
		doc.add(Field.Text("path", f.getPath()));
		doc.add(Field.Keyword("modified", DateField.timeToString(f.lastModified())));
		FileInputStream is = new FileInputStream(f);
		Reader reader = new BufferedReader(new InputStreamReader(is));

		BufferedReader in = new BufferedReader(reader);

		String imwith = "";
		String line = in.readLine();
		String lastline = line;
		String tmpstr = "";
		/*
		 * TODO Optimize the readlast line function, this can't be good....
		 */

		while (lastline != null) {
			tmpstr = lastline;
			lastline = in.readLine();
			if (lastline == null || lastline.equals(""))
				break;
		}
		lastline = tmpstr;

		imwith = getName(line);
		in.close();

		doc.add(Field.Text("url", "url data"));
		doc.add(Field.Text("type", "text/gaimlog"));
		String protocol = getProtocol(line);
		System.out.println("gaim protocol:" + protocol);
		doc.add(Field.Text("protocol", protocol));

		String starttime[] = getStartTimes(line);
		doc.add(Field.Text("startdate", starttime[0]));
		doc.add(Field.Text("starttime", starttime[1]));
		doc.add(Field.Text("endtime", getEndTime(lastline)));

		try {

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			File file = new File(HOME+"/.gaim/blist.xml");
			org.w3c.dom.Document feed = factory.newDocumentBuilder().parse(file);

			NodeList nodelist = XPathAPI
					.selectNodeList(feed, "gaim/blist/group/contact/buddy[name='" + imwith + "']/*");

			String buddyname = getNodeValue(nodelist, "name");
			String alias = getNodeValue(nodelist, "alias");
			String icon = getNodeValueBasedOnAttribute(nodelist, "setting", "name", "buddy_icon");

			if (!buddyname.equals(""))
				doc.add(Field.Text("from", buddyname));
			else
				doc.add(Field.Text("from", imwith));
			doc.add(Field.Text("alias", alias));
			if (!icon.equals(""))
				doc.add(Field.Text("icon", HOME+"/.gaim/icons/" + icon));
			//}
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		is = new FileInputStream(f);
		reader = new BufferedReader(new InputStreamReader(is));
		doc.add(Field.Text("contents", reader));

		// return the document
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
		result[0] = st.nextToken(); //date
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

	private GaimLogDocument() {
	}

    public String[] getSearchFields() {
        return fields;
    }
}
