#include <stdio.h>
#include <gtk/gtk.h>
#include <libgnomevfs/gnome-vfs-init.h>
#include <libgnomevfs/gnome-vfs-mime-handlers.h>
#include "utils_FileUtility.h"

/*

https://stage.maemo.org/svn/maemo/projects/haf/trunk/osso-gnome-vfs2/test/

*/
JNIEXPORT jstring JNICALL Java_utils_FileUtility_getMimeType(JNIEnv *env, jclass class, jstring filename) {
    const gchar* afile_g = (*env)->GetStringUTFChars(env, filename, 0);

    const gchar *result_g = gnome_vfs_get_mime_type(afile_g);

    (*env)->ReleaseStringUTFChars(env, filename, afile_g);
    return (*env)->NewStringUTF(env, result_g);

}

JNIEXPORT jstring JNICALL Java_utils_FileUtility_getIconFromMimeType (JNIEnv *env, jclass class, jstring filename) {
 //   GnomeVFSMimeApplication *application;
    const char* afile_g = (*env)->GetStringUTFChars(env, filename, 0);
//    const gchar *result_g;
    g_printf("filename: %s", afile_g);
/*

   application = gnome_vfs_mime_get_default_application (afile_g);
	if (application == NULL) {
	   g_print ("No default application.\n");
	} else {	
	    result_g =  gnome_vfs_mime_application_get_icon (application);
	}

//   gnome_vfs_init ();
/*
    application = gnome_vfs_mime_get_default_application (afile_g);
    const gchar *result_g =  gnome_vfs_mime_application_get_icon (application);
	g_printf("filename: %s", result_g);
*/
/*    (*env)->ReleaseStringUTFChars(env, filename, afile_g);
    return (*env)->NewStringUTF(env, result_g);
*/
	return NULL;
}
