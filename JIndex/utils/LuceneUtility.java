/*
 * Created on Sep 24, 2005
 */
package utils;

import org.apache.lucene.document.Document;

public class LuceneUtility {

    public static String getText(Document doc, String name) {
        String result = doc.get(name);
      if(result == null)
          return "";
      return result.trim();
        
    }

}
