#include <stdio.h>
#include <gtk/gtk.h>
#include <libgnomevfs/gnome-vfs-init.h>
#include <libgnomevfs/gnome-vfs-mime-handlers.h>

#include "utils_c_libJIndex.h"



JNIEXPORT jstring JNICALL Java_utils_c_libJIndex_getMimeType(JNIEnv *env, jclass class, jstring filename) {
    const gchar *result = NULL;
	const char* afile_g = (*env)->GetStringUTFChars(env, filename, 0);
	gboolean started = gnome_vfs_init ();
	if(started) {
		g_printf("Started\n");
		g_printf("Trying to get mimetype for: %s\n",afile_g);
		result = gnome_vfs_get_mime_type(afile_g);
		g_printf("got result\n");
		g_printf("about to shutdown\n"); 
		gnome_vfs_shutdown ();
		g_printf("Shutdown completed\n");   
	}
    
      (*env)->ReleaseStringUTFChars(env, filename, afile_g);
    return (*env)->NewStringUTF(env, result);
}

/*
 * Class:     utils_c_libJIndex_new
 * Method:    getIconFromMimeType
 * Signature: (Ljava/lang/String;)Ljava/lang/String;
 */
JNIEXPORT jstring JNICALL Java_utils_c_libJIndex_getIconFromMimeType(JNIEnv *env, jclass class, jstring mimetype) {

    GnomeVFSMimeApplication *application;
   const char* mimetype_g = (*env)->GetStringUTFChars(env, mimetype, 0);
   const gchar *result_g = NULL;
    GtkIconTheme* iconTheme;
GtkIconInfo* iconInfo;

   gnome_vfs_init ();
   application = gnome_vfs_mime_get_default_application (mimetype_g);
	if (application == NULL) {
	   g_print ("No default application.\n");
	} else {	
	    result_g =  gnome_vfs_mime_application_get_icon (application);


            iconTheme = gtk_icon_theme_get_default();
            
iconInfo = gtk_icon_theme_lookup_icon(iconTheme, result_g, 32, GTK_ICON_LOOKUP_NO_SVG);

    g_printf("filename: %s", gtk_icon_info_get_filename(iconInfo));


	}
	gnome_vfs_shutdown ();
	g_printf("filename: %s", result_g);
        (*env)->ReleaseStringUTFChars(env, mimetype, mimetype_g);
        
return (*env)->NewStringUTF(env, gtk_icon_info_get_filename(iconInfo));

        //return (*env)->NewStringUTF(env, result_g);

}
