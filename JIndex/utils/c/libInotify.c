#include <stdio.h>
#include <gtk/gtk.h>
#include <libgnomevfs/gnome-vfs-init.h>
#include <libgnomevfs/gnome-vfs-mime-handlers.h>

#include "utils_c_libInotify.h"
#include <linux/inotify.h>

#include <glib.h>

#include <libinotify/libinotify.h>

JNIEXPORT void JNICALL Java_utils_c_libInotify_registerWatch(JNIEnv *env, jclass class, jstring filename) {
	INotifyHandle *handle;
	guint32	        mask = IN_ALL_EVENTS;
	unsigned long   flags = 0;

	jmethodID callback = env->GetMethodID(env->GetObjectClass(obj), "callbackNotifyEvent", "(S)V");
		g_print ("monitor: adding... on uri:'%s'\n", filename);
		handle[0] = inotify_monitor_add (filename, mask, flags,
						 callback, NULL);
		if (handle[0] == NULL)
			fprintf (stderr, "failed to watch '%s'\n", filename);
}

JNIEXPORT void JNICALL Java_utils_c_libInotify_removeWatch (JNIEnv *env, jclass class, jstring filename) {
}


