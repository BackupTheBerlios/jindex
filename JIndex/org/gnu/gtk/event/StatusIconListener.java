/**
 * Java-Gnome Bindings Library
 *
 * Copyright 1998-2005 the Java-Gnome Team, all rights reserved.
 *
 * The Java-Gnome bindings library is free software distributed under
 * the terms of the GNU Library General Public License version 2.
 */

package org.gnu.gtk.event;

/**
 * Listener for the {@link org.gnu.gtk.StatusIcon} widget.
 */
public interface StatusIconListener {
	/**
	 * This method is called whenever a status icon event occurs.
	 */
	public void statusIconEvent(StatusIconEvent event);
}
