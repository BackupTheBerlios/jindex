/*
 * NewSearchTypeDialog.java
 *
 * Created on 26 October 2006, 10:07
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jindex.client.window;

import org.apache.log4j.Logger;
import org.gnu.glade.LibGlade;
import org.gnu.glib.Handle;
import org.gnu.gtk.Button;
import org.gnu.gtk.ComboBox;
import org.gnu.gtk.ComboBoxEntry;
import org.gnu.gtk.Dialog;
import org.gnu.gtk.Entry;
import org.gnu.gtk.FileChooserDialog;
import org.gnu.gtk.GtkStockItem;
import org.gnu.gtk.HBox;
import org.gnu.gtk.Label;
import org.gnu.gtk.VBox;
import org.gnu.gtk.Window;
import org.gnu.gtk.WindowType;
import org.gnu.gtk.event.ButtonEvent;
import org.gnu.gtk.event.ButtonListener;
import org.gnu.gtk.event.ComboBoxEvent;
import org.gnu.gtk.event.ComboBoxListener;

/**
 *
 * @author sorenm
 */
public class NewSearchTypeDialog {
    Logger log = Logger.getLogger(NewSearchTypeDialog.class);
    /** Creates a new instance of NewSearchTypeDialog */
    Dialog dialog;
    public NewSearchTypeDialog(final LibGlade app) {
        log.debug("NewSearchTypeDialog entered constructor");
        dialog = (Dialog) app.getWidget("addSearchTypeDialog");
        ComboBox combo = (ComboBox) app.getWidget("typeComboBox");
        // Evolution options box
        final VBox evobox = (VBox) app.getWidget("evolutionVBox");
        final HBox filebox = (HBox) app.getWidget("fileHbox");
        final VBox mainbox = (VBox) app.getWidget("selectTypeContainerBox");
        
        final Entry filenameEntry = (Entry) app.getWidget("filenameEntry");
        combo.addListener(new ComboBoxListener() {
            public void comboBoxEvent(ComboBoxEvent comboBoxEvent) {
                if(comboBoxEvent.isOfType(ComboBoxEvent.Type.CHANGED)) {
                    ComboBoxEntry entry = (ComboBoxEntry) comboBoxEvent.getSource();
                    System.out.println("selected: "+entry.getActiveText());
                    if("Evolution".equalsIgnoreCase(entry.getActiveText())) {
                        log.debug("Showing evolution");
                        evobox.showAll();
                        filebox.hideAll();
                    } else if("File or directory".equalsIgnoreCase(entry.getActiveText())) {
                        log.debug("Showing files");
                        evobox.hideAll();
                        filebox.showAll();
                    }
                }
            }
        });
        final Button openFileButton = (Button) app.getWidget("openFileDialogButton");
        openFileButton.addListener(new ButtonListener() {
            public void buttonEvent(ButtonEvent buttonEvent) {
                if(buttonEvent.isOfType(ButtonEvent.Type.CLICK)) {
                    final FileChooserDialog fileDialog = (FileChooserDialog) app.getWidget("filechooserdialog");
                    fileDialog.run();
                    String filename = fileDialog.getFilename();
                    log.debug(filename);
                    fileDialog.destroy();
                    filenameEntry.setText(filename);
                }
            }
        });
        dialog.setModal(true);
        dialog.show();
        dialog.present();
        int returnvalue = dialog.run();
    }
    
}
