/**
 * Java-Gnome Bindings Library
 *
 * Copyright 1998-2005 the Java-Gnome Team, all rights reserved.
 *
 * The Java-Gnome bindings library is free software distributed under
 * the terms of the GNU Library General Public License version 2.
 */

package org.gnu.gtk;

import java.util.Vector;

import org.gnu.gdk.Pixbuf;
import org.gnu.glib.EventMap;
import org.gnu.glib.EventType;
import org.gnu.glib.GObject;
import org.gnu.glib.Handle;
import org.gnu.glib.Type;
import org.gnu.gtk.event.StatusIconEvent;
import org.gnu.gtk.event.StatusIconListener;
import org.gnu.gtk.GtkStockItem;
import org.gnu.gtk.IconSource;
import org.gnu.gtk.ImageType;

/**
 * The StatusIcon is a typical tray icon that is to be displayed on a user's
 * desktop panel. It possesses a lot of basic functionalities that you would
 * expect from a system tray icon such as blinking, icon display, and the
 * ability to register mouse clicks for showing popup menus.
 *
 * @since 2.10
 */
public class StatusIcon extends GObject {

	private static StatusIcon getStatusIcon(Handle handle){
	    if (handle == null) {
    	    return null;
		}

	    StatusIcon obj = (StatusIcon) GObject.getGObjectFromHandle(handle);

    	if (obj == null) {
	        obj = new StatusIcon(handle);
		}

	    return obj;
	}

	private StatusIcon(Handle handle) {
		super(handle);
	}

	/**
	 * Construct a new status icon.
	 */
	public StatusIcon() {
		super(gtk_status_icon_new());
	}

	/**
	 * Construct a status icon displaying the image
	 * that the given filename refers to.
	 *
	 * @param filename The filename of the image
	 */	
	public StatusIcon(String filename) {
		super(gtk_status_icon_new_from_file(filename));
	}

	/**
	 * Construct a status icon displaying the given pixbuf.
	 * The image will be scaled down to fit in the available space
	 * if necessary.
	 *
	 * @param pixbuf The pixbuf to use for the status icon creation.
	 */
	public StatusIcon(Pixbuf pixbuf) {
		super(gtk_status_icon_new_from_pixbuf(pixbuf.getHandle()));
	}

	/**
	 * Construct a status icon displaying the given GtkStockItem.
	 *
	 * @param stockID The id of the GtkStockItem to be displayed.
	 */
	public StatusIcon(GtkStockItem stockID) {
		super(gtk_status_icon_new_from_stock(stockID.getString()));
	}

	/**
	 * Construct a status icon displaying an image specified from
	 * the given icon's name.
	 *
	 * @param icon The icon to be displayed.
	 */
	public StatusIcon(IconSource icon) {
		super(gtk_status_icon_new_from_icon_name(icon.getIconName()));
	}

	/**
	 * Sets the status icon to display the given pixbuf.
	 *
	 * @param pixbuf The pixbuf to be displayed.
	 */
	public void set(Pixbuf pixbuf) {
		gtk_status_icon_set_from_pixbuf(getHandle(), pixbuf.getHandle());
	}

	/**
	 * Sets the status icon to display the given filename's image.
	 *
	 * @param filename The filename of the desired image.
	 */
	public void set(String filename) {
		gtk_status_icon_set_from_file(getHandle(), filename);
	}

	/**
	 * Sets the status icon to display the given GtkStockItem.
	 *
	 * @param stockID The id of the desired stock item to be displayed.
	 */
	public void set(GtkStockItem stockID) {
		gtk_status_icon_set_from_stock(getHandle(), stockID.getString());
	}

	/**
	 * Sets the status icon to display the given icon.
	 *
	 * @param icon The icon to be displayed.
	 */
	public void set(IconSource icon) {
		gtk_status_icon_set_from_icon_name(getHandle(), icon.getIconName());
	}

	/**
	 * Gets the type of image that this status icon is currently storing.
	 *
	 * @return The image representation being used.
	 */
	public ImageType getStorageType() {
		return ImageType.intern(gtk_status_icon_get_storage_type(getHandle()));
	}

	/**
	 * Gets the pixbuf that this status icon is currently displaying.
	 *
	 * @return The pixbuf that this icon is displaying.
	 */
	public Pixbuf getPixbuf() {
		return new Pixbuf(gtk_status_icon_get_pixbuf(getHandle()));
	}

	/**
	 * Gets the stock item that this status icon is currently displaying.
	 *
	 * @return The stock item being displayed.
	 */
	public GtkStockItem getStock() {
		return new GtkStockItem(gtk_status_icon_get_stock(getHandle()));
	}

	/**
	 * Gets the name of the icon that is currently being displayed
	 *
	 * @return The name of the displayed icon.
	 */
	public String getIconName() {
		return gtk_status_icon_get_icon_name(getHandle());
	}

	/**
	 * Gets the number of pixels that is available for the image.
	 * 
	 * @return The number of pixels available for the image.
	 */
	public int getSize() {
		return gtk_status_icon_get_size(getHandle());
	}

	/**
	 * Sets the text to be displayed as a tooltip.
	 *
	 * @param text The text to be displayed.
	 */
	public void setTooltip(String text) {
		gtk_status_icon_set_tooltip(getHandle(), text);
	}

	/**
	 * Shows or hides the status icon.
	 *
	 * @param visible <code>true</code> if the status icon should be shown
	 */
	public void setVisible(boolean visible) {
		gtk_status_icon_set_visible(getHandle(), visible);
	}

	/**
	 * Returns as to whether the status icon is visible or not. Note that being
	 * visible does not actually guarantee that the user can see the icon.
	 * 
	 * @return <code>true</code> if the status icon is visible
	 * @see #isEmbedded()
	 */
	public boolean getVisible() {
		return gtk_status_icon_get_visible(getHandle());
	}

	/**
	 * Makes the status icon start or stop blinking.
	 *
	 * @param blinking <code>true</code> if the icon should blink
	 */
	public void setBlinking(boolean blinking) {
		gtk_status_icon_set_blinking(getHandle(), blinking);
	}

	/**
	 * Returns as to whether the icon is currently blinking or not.
	 *
	 * @return <code>true</code> if the icon is currently blinking
	 */
	public boolean getBlinking() {
		return gtk_status_icon_get_blinking(getHandle());
	}

	/**
	 * Returns as to whether the status icon is embedded in a
	 * notification area or not.
	 *
	 * @return <code>true</code> if the icon is embedded within
	 * a notification area
	 */
	public boolean isEmbedded() {
		return gtk_status_icon_is_embedded(getHandle());
	}

	/**
	 * Retrieve the runtime Type used by the GLib library.
	 */
	public static Type getType() {
		return new Type(gtk_status_icon_get_type());
	}

	private Vector listeners = null;

	protected static int findListener(Vector list, Object listener) {
		if (null == list || null == listener) {
			return -1;
		}
		return list.indexOf(listener);
	}

	/**
	 * Register an object to handle status icon events.
	 *
	 * @param listener The listener to be added to this status icon.
	 */
	public void addListener(StatusIconListener listener) {
		// Don't add the listener a second time if it is in the Vector.
		int i = findListener(listeners, listener);
		if (i == -1) {
			if (null == listeners) {
				evtMap.initialize(this, StatusIconEvent.Type.ACTIVATE);
				evtMap.initialize(this, StatusIconEvent.Type.POPUP_MENU);
				evtMap.initialize(this, StatusIconEvent.Type.SIZE_CHANGED);
				listeners = new Vector();
			}
			listeners.addElement(listener);
		}
	}
	
	/**
	 * Removes a listener.
	 *
	 * @param listener The listener to be removed.
	 */
	public void removeListener(StatusIconListener listener) {
		int i = findListener(listeners, listener);
		if (i > -1) {
			listeners.remove(i);
		}
		if (0 == listeners.size()) {
			evtMap.uninitialize(this, StatusIconEvent.Type.ACTIVATE);
			evtMap.uninitialize(this, StatusIconEvent.Type.POPUP_MENU);
			evtMap.uninitialize(this, StatusIconEvent.Type.SIZE_CHANGED);
			listeners = null;
		}
	}

	protected void fireStatusIconEvent(StatusIconEvent event) {
		if (null == listeners) {
			return;
		}
		int size = listeners.size();
		int i = 0;
		while (i < size) {
			StatusIconListener sil = (StatusIconListener)listeners.elementAt(i);
			sil.statusIconEvent(event);
			i++;
		}
	}

	private void handleActivate() {
		fireStatusIconEvent(new StatusIconEvent(this, StatusIconEvent.Type.ACTIVATE));
	}

	private void handlePopupMenu(int x, int y) {
		fireStatusIconEvent(new StatusIconEvent(this, StatusIconEvent.Type.POPUP_MENU));
	}

	private void handleSizeChanged(int size) {
		fireStatusIconEvent(new StatusIconEvent(this, StatusIconEvent.Type.SIZE_CHANGED));
	}

	/**
	 * Returns the event listener class for a given signal.
	 */
	public Class getEventListenerClass(String signal) {
		Class cls = evtMap.getEventListenerClass(signal);
		if (cls == null) {
			cls = super.getEventListenerClass(signal);
		}
		return cls;
	}

	/**
	 * Gets the event type for a given signal
	 */
	public EventType getEventType(String signal) {
		EventType et = evtMap.getEventType(signal);
		if (et == null) {
			et = super.getEventType(signal);
		}
		return et;
	}

	private static EventMap evtMap = new EventMap();

	static {
		addEvents(evtMap);
	}

	private static void addEvents(EventMap anEvtMap) {
		anEvtMap.addEvent("activate", "handleActivate", StatusIconEvent.Type.ACTIVATE, StatusIconListener.class);
		anEvtMap.addEvent("popup_menu", "handlePopupMenu", StatusIconEvent.Type.POPUP_MENU, StatusIconListener.class);
		anEvtMap.addEvent("size_changed", "handleSizeChanged", StatusIconEvent.Type.SIZE_CHANGED, StatusIconListener.class);
	}

	private static final native Handle gtk_status_icon_new();
	private static final native Handle gtk_status_icon_new_from_pixbuf(Handle pixbuf);
	private static final native Handle gtk_status_icon_new_from_file(String filename);
	private static final native Handle gtk_status_icon_new_from_stock(String stockItem);
	private static final native Handle gtk_status_icon_new_from_icon_name(String iconName);
	private static final native void gtk_status_icon_set_from_pixbuf(Handle icon, Handle pixbuf);
	private static final native void gtk_status_icon_set_from_file(Handle icon, String filename);
	private static final native void gtk_status_icon_set_from_stock(Handle icon, String stockItem);
	private static final native void gtk_status_icon_set_from_icon_name(Handle icon, String iconName);
	private static final native int gtk_status_icon_get_storage_type(Handle icon);
	private static final native Handle gtk_status_icon_get_pixbuf(Handle icon);
	private static final native String gtk_status_icon_get_stock(Handle icon);
	private static final native String gtk_status_icon_get_icon_name(Handle icon);
	private static final native int gtk_status_icon_get_size(Handle icon);
	private static final native void gtk_status_icon_set_tooltip(Handle icon, String text);
	private static final native void gtk_status_icon_set_visible(Handle icon, boolean visible);
	private static final native boolean gtk_status_icon_get_visible(Handle icon);
	private static final native void gtk_status_icon_set_blinking(Handle icon, boolean blinking);
	private static final native boolean gtk_status_icon_get_blinking(Handle icon);
	private static final native boolean gtk_status_icon_is_embedded(Handle icon);
	private static final native int gtk_status_icon_get_type();
}
