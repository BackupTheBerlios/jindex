/**
 * Java-Gnome Bindings Library
 *
 * Copyright 1998-2005 the Java-Gnome Team, all rights reserved.
 *
 * The Java-Gnome bindings library is free software distributed under
 * the terms of the GNU Library General Public License version 2.
 */

package org.gnu.gtk.event;

import org.gnu.glib.EventType;
import org.gnu.gtk.event.GtkEvent;

/**
 * An event representing action by a {@link org.gnu.gtk.StatusIcon}.
 */
public class StatusIconEvent extends GtkEvent {

	/**
	 * Type of a StatusIconEvent
	 */
	public static class Type extends EventType {
		private Type(int id, String name) {
			super(id, name);
		}

		/**
		 * Emitted when the status icon has been activated by the user.
		 */
		public static final Type ACTIVATE = new Type(1, "ACTIVATE");

		/**
		 * Emitted when the status icon's context menu has been brought up.
		 */
		public static final Type POPUP_MENU = new Type(2, "POPUP_MENU");

		/**
		 * Emitted when the notification area has been resized.
		 */
		public static final Type SIZE_CHANGED = new Type(3, "SIZE_CHANGED");
	}

	/**
	 * Creates a new StatusIconEvent. This is used internally by java-gnome. Users
	 * only have to deal with listeners.
	 */
	public StatusIconEvent(Object source, StatusIconEvent.Type type) {
		super(source, type);
	}
	
	/**
	 * @return True if the type of this event is the same as that stated.
	 */
	public boolean isOfType(StatusIconEvent.Type aType) {
		return (type.getID() == aType.getID());
	}
}
