/*
 * Created on Feb 8, 2005
 *
 * To change the template for this generated file go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
package gui;

import java.io.File;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;

import utils.FileUtility;
import utils.JStringUtils;

/**
 * @author sorenm
 */
public class ApplicationDocumentGUI extends MainContentsGUI {
	Document doc;
	public ApplicationDocumentGUI(Document _doc) {
		super(_doc);
		doc = _doc;
		setOpenAction(doc.get("exec-command"));

	}

	public String getTextContent() {
		String appname  = JStringUtils.encodeXMLEntities(doc.get("applicationname").trim())+"\n";
		String textstring = "<span font_desc=\"sans bold 10\">" + appname + "</span>\n";
		textstring += JStringUtils.encodeXMLEntities(StringUtils.trimToEmpty(doc.get("comment")));
		return textstring;
	}

	public String[] getOpenAction() {
		String cmd = doc.get("exec-command");
		cmd = StringUtils.replace(cmd,"%U","").trim();
		StringTokenizer st = new StringTokenizer(cmd, " ");
		String[] value = new String[st.countTokens()];
		int i =0;
		while(st.hasMoreTokens()) {
			value[i] = st.nextToken();
			i++;
		}
		return value;
	}

	public byte[] getIcon() {
		String icon = doc.get("icon");
		System.out.println("Loading icon :"+icon);
		if (icon != null) {
				if (new File(icon).exists())
					return FileUtility.getExternalIcon(icon);
				else if (new File("/usr/share/pixmaps/" + icon).exists())
					return FileUtility.getExternalIcon("/usr/share/pixmaps/" + icon);

		} else
			return super.getDefaultIcon();
		return null;
	}
	
}