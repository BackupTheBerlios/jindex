package gui.trayicon;

import javax.swing.JPopupMenu;

import org.gnu.glade.LibGlade;
import org.gnu.gtk.Menu;

public class TrayIconPopupMenu extends JPopupMenu {
	public TrayIconPopupMenu(LibGlade firstApp) {
		Object tmp = firstApp.getWidget("PopupMenu");
		Menu popup = (Menu)tmp;
		System.out.println(tmp.getClass());
		popup.show();
	}

	
}
