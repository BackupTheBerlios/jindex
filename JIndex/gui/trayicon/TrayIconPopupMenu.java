package gui.trayicon;

import javax.swing.JPopupMenu;

import org.apache.log4j.Logger;
import org.gnu.glade.LibGlade;
import org.gnu.gtk.Menu;

import documents.GaimLogDocument;

public class TrayIconPopupMenu extends JPopupMenu {
	Logger log = Logger.getLogger(TrayIconPopupMenu.class);
	public TrayIconPopupMenu(LibGlade firstApp) {
		Object tmp = firstApp.getWidget("PopupMenu");
		Menu popup = (Menu)tmp;
		log.debug(tmp.getClass());
		popup.show();
	}

	
}
