/*
 * Created on 24/09/2006
 */
package org.jindex.client.gui;

import org.apache.lucene.document.Document;

public class GUIFactory {
    public static MainContentsGUI getInterface(Document doc) {
        if (doc.get("type").equals("text/gaimlog")) {
            return new GaimLogGUI(doc);
        }
        else if (doc.get("type").equals("text/tomboy")) {
            return  new TomboyDocumentGUI(doc);
        }
        else if (doc.get("type").equals("audio/mp3") || doc.get("type").equals("audio/mpeg")) {
            return new MP3LogGUI(doc);
        }
        else if (doc.get("type").equals("image")) {
            System.out.println("Added image");
            return new ImageContentGUI(doc);
        }
        else if (doc.get("type").equals("application/pdf")) {
            return  new PDFContentGUI(doc);
        }
        else if (doc.get("type").equals("text/x-java")) {
            return  new JavaDocumentGUI(doc);
        }
        else if (doc.get("type").equals("text/html")) {
            return  new HTMLDocumentGUI(doc);
        }
        else if (doc.get("type").equals("application/vnd.sun.xml.writer")) {
            return new OpenOfficeDocumentGUI(doc);
        }
        else if (doc.get("type").equals("application/vnd.ms-excel")) {
            return  new ExcelDocumentGUI(doc);

        }
        if (doc.get("type").equals("Application")) {
            return  new ApplicationDocumentGUI(doc);
        }
        else if (doc.get("type").equals("mail")) {
            return new MailGUI(doc);
        }
        else if (doc.get("type").equalsIgnoreCase("EvolutionAddressBook")) {
            return  new EvolutionAddressBookGUI(doc);
        }
        else {
            return new UnknownfiletypeGUI(doc);
        }
    }
}
