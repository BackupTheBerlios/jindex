/*
 * Created on Jul 27, 2005
 */
package gui;

import java.io.IOException;

import org.gnu.gtk.Button;
import org.gnu.gtk.GtkStockItem;
import org.gnu.gtk.HBox;
import org.gnu.gtk.Widget;
import org.gnu.gtk.event.ButtonEvent;
import org.gnu.gtk.event.ButtonListener;
public class MainContentsGUI implements MainGUIInterface {
	private String openAction;
	
	
	/**
	 * @return Returns the openAction.
	 */
	public String getOpenAction() {
		return openAction;
	}
	/**
	 * @param openAction The openAction to set.
	 */
	public void setOpenAction(String openAction) {
		this.openAction = openAction;
	}

	/**
	 * This method initializes 
	 * 
	 */
	public MainContentsGUI() {
		super();
	}

	
	/**
	 * @return
	 */
	private Widget getActionpane() {
		HBox actionpane = new HBox(false, 0);
		actionpane.packStart(getOpenButton());
		return actionpane;
	}
	
	/**
	 * @return
	 */
	public Widget getOpenButton() {
		HBox mainpane = new HBox(false, 0);
		Button openButton = new Button(GtkStockItem.OPEN);
				
		openButton.addListener(new ButtonListener() {
			public void buttonEvent(ButtonEvent event) {
				if (event.isOfType(ButtonEvent.Type.CLICK)) {
					executeOpenAction();
				}
			}
		});
		mainpane.add(openButton);
		return mainpane;
	}
	/**
	 * 
	 */
	public void executeOpenAction() {
		try {
			System.out.println("Execute: "+"gnome-open "+getOpenAction());
			Runtime.getRuntime().exec("gnome-open \""+getOpenAction()+"\"");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	public Widget getGnomeGUI() {
		System.out.println("getGnomeGUI not implemented..");
		return null;
	}
   }  
