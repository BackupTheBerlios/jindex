/*
 * Created on Jul 27, 2005
 */
package gui;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;

import documents.GaimLogDocument;

import utils.FileUtility;

public abstract class MainContentsGUI implements MainGUIInterface {
	private String openAction;
	Logger log = Logger.getLogger(MainContentsGUI.class);
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

	public byte[] getDefaultIcon() {
		if (maindoc != null) {
			String type = maindoc.get("type");
			String path = FileUtility.getIconFromMimeType(type);
			type = type = StringUtils.replace(type, "/", "-");
			if (path != null) {
				log.debug("Special path: " + path);
				return FileUtility.getIcon(path);
			}
			return FileUtility.getIcon("/images/icon_missing.png");
		}
		return null;
	}

}
