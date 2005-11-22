/*
 * Created on Jul 27, 2005
 */
package gui;

import java.io.File;
import java.io.IOException;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.document.Document;

import utils.FileUtility;

public abstract class MainContentsGUI implements MainGUIInterface {
	private String openAction;

	Document maindoc = null;

	public MainContentsGUI(Document doc) {
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
			type = type = StringUtils.replace(type, "/", "-");
			File f = new File("images/mimetypes/gnome-mime-" + type + ".png");
			try {
				return FileUtility.getBytesFromFile(f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	
}
