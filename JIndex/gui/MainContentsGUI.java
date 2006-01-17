/*
 * Created on Jul 27, 2005
 */
package gui;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;

import utils.FileUtility;

public abstract class MainContentsGUI implements MainGUIInterface {
	private String openAction;

	Document maindoc = null;

	public MainContentsGUI() {
		super();
	}
	public MainContentsGUI(Document doc) {
		maindoc = doc;
	}
	public void setDocument(Document doc) {
		maindoc = doc;
	}

	/**
	 * @param openAction
	 *            The openAction to set.
	 */
	public void setOpenAction(String openAction) {
		this.openAction = openAction;
	}

	public byte[] getIcon() {
		if (maindoc != null) {
			String type = maindoc.get("type");
			String path = FileUtility.getIconFromMimeType(type);
			type = type = StringUtils.replace(type, "/", "-");
			if (path != null) {
				System.out.println("Special path: " + path);
				return FileUtility.getIcon(path);
			}
			return FileUtility.getIcon("/images/icon_missing.png");
		}
		return null;
	}

}
