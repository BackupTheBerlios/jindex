/*
 * Created on 08/10/2006
 */
package org.jindex.client.cache;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import org.apache.log4j.Logger;
import org.jindex.utils.FileUtility;

public class ImageCache {

    private static final Logger log = Logger.getLogger(ImageCache.class);
    private static ImageCache INSTANCE = new ImageCache();
    private static HashMap imagemap;

    private ImageCache() {
        imagemap = new HashMap();
    }

    public static ImageCache getInstance() {
        return INSTANCE;
    }

    public byte[] getImage(String type, String path) {
        byte[] result = null;
        
            result = (byte[]) imagemap.get(type);
            if (result == null) {
                log.debug("Adding new entry to cache");
                result = FileUtility.getIcon(path);
                imagemap.put(type, result);
            }

        return result;
    }
}
