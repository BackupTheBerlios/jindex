/*
 * libInotify.java
 * 
 * Created on Aug 20, 2007, 1:54:26 PM
 * 
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package c;

/**
 *
 * @author sorenm
 */
public class libInotify {
	static {
		System.out.println("loading library");
		System.loadLibrary("Inotify");
	}

	public static native void registerWatch(String filename);
        public static native void removeWatch(String filename);

	public void callbackNotifyEvent(String filename) {
            
        }

}
