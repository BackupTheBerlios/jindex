/*
 * Created on Jul 27, 2005
 */
package org.jindex.client.gui;

import org.jindex.client.cache.ImageCache;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.jindex.utils.FileUtility;

public abstract class MainContentsGUI implements MainGUIInterface {

    private String openAction;
    Logger log = Logger.getLogger(MainContentsGUI.class);
    Document maindoc = null;

    public MainContentsGUI() {
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
            log.debug("Type: " + type);
            String path = FileUtility.getIconFromMimeType(type);
            type = type = StringUtils.replace(type, "/", "-");
            if (path != null) {
                log.debug("Special path: " + path);
                return ImageCache.getInstance().getImage(maindoc.get("type"), path);
            }
            return ImageCache.getInstance().getImage(maindoc.get("type"), "/images/icon_missing.png");
        }
        return null;
    }

    public String getDefaultIconPath() {
        if (maindoc != null) {
            String type = maindoc.get("type");
            log.debug("Type: " + type);
            String path = FileUtility.getIconFromMimeType(type);
            type = type = StringUtils.replace(type, "/", "-");
            if (path != null) {
                log.debug("Special path: " + path);
                return path;
            }
            return "/images/icon_missing.png";
        }
        return null;
    }
}
