#include <stdio.h>
#include <gtk/gtk.h>
#include <libgnomevfs/gnome-vfs-init.h>
#include <libgnomevfs/gnome-vfs-mime-handlers.h>
#include "utils_FileUtility.h"

/*

https://stage.maemo.org/svn/maemo/projects/haf/trunk/osso-gnome-vfs2/test/

*/
char *getMimeType(char *filename) {
    char *result;
    GnomeVFSURI *uri;
    char *uri_string;
    char *path;

    gboolean started = gnome_vfs_init ();
	if(started) {
		g_printf("Started\n");
		g_printf("Trying to get mimetype for: %s\n",filename);
		result = gnome_vfs_get_mime_type(filename);
		g_printf("got result\n");
		g_printf("about to shutdown\n"); 
//		gnome_vfs_shutdown ();
		g_printf("Shutdown completed\n");   
	}
    return result;
}

char* getIconFromMimeType (char *mimetype) {
 //   GnomeVFSMimeApplication *application;
//    const char* afile_g = (*env)->GetStringUTFChars(env, filename, 0);
//    const gchar *result_g;
    g_printf("filename: ");
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

int
main (int argc, char **argv)
{ }
