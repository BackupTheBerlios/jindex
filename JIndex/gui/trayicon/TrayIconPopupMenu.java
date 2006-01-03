package gui.trayicon;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;

import org.gnu.glade.LibGlade;
import org.gnu.gnome.PopupMenu;
import org.gnu.gnome.WindowIcon;
import org.gnu.gtk.ComboBox;
import org.gnu.gtk.Menu;

public class TrayIconPopupMenu extends JPopupMenu {
	public TrayIconPopupMenu(LibGlade firstApp) {
		Object tmp = firstApp.getWidget("PopupMenu");
		Menu popup = (Menu)tmp;
		System.out.println(tmp.getClass());
		popup.show();
	}

	
}
