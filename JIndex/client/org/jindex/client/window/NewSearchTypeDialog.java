/*
 * NewSearchTypeDialog.java
 *
 * Created on 26 October 2006, 10:07
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package org.jindex.client.window;

import org.gnu.glib.Handle;
import org.gnu.gtk.Button;
import org.gnu.gtk.Dialog;
import org.gnu.gtk.GtkStockItem;
import org.gnu.gtk.Window;
import org.gnu.gtk.WindowType;

/**
 *
 * @author sorenm
 */
public class NewSearchTypeDialog extends Dialog {
    /** Creates a new instance of NewSearchTypeDialog */
    public NewSearchTypeDialog() {
        super();
        addButton(GtkStockItem.CANCEL, 2);
       addButton(GtkStockItem.OK, 1);
        
    }
    
}
