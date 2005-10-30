package gui;

import org.gnu.gtk.Widget;

public interface MainGUIInterface {
	public Widget getGnomeGUI();
	public void setOpenAction(String openAction);
	public void executeOpenAction();
}