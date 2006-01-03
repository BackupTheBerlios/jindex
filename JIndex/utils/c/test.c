#include <stdio.h>
#include <gnome.h>
#include <libgnomeui/libgnomeui.h>


#include <libgnomeui/gnome-icon-lookup.h>
#include <libgnomeui/gnome-icon-theme.h>

#include <libgnomevfs/gnome-vfs-mime-handlers.h>
#include <libgnomevfs/gnome-vfs-utils.h>



#include "utils_FileUtility.h"

JNIEXPORT jstring JNICALL Java_utils_FileUtility_getMimeType
  (JNIEnv *env, jclass class, jstring filename) {

    const gchar* afile_g = (*env)->GetStringUTFChars(env, filename, 0);
    const gchar *result_g = gnome_mime_type(afile_g);
    (*env)->ReleaseStringUTFChars(env, filename, afile_g);
    return (*env)->NewStringUTF(env, result_g);

}

JNIEXPORT jstring JNICALL Java_utils_FileUtility_getIconFromMimeType
  (JNIEnv *env, jclass class, jstring filename) {
    const gchar* afile_g = (*env)->GetStringUTFChars(env, filename, 0);
    const gchar *result_g =  gnome_mime_get_value(afile_g,"icon-filename");
    (*env)->ReleaseStringUTFChars(env, filename, afile_g);
    return (*env)->NewStringUTF(env, result_g);
}



void main() {
	GnomeIconTheme *theme;
	char *icon, *path;
	theme = gnome_icon_theme_new ();
 	gnome_icon_theme_set_allow_svg (theme, TRUE);
 
 	icon = gnome_icon_lookup (theme, NULL, NULL, NULL, NULL,
 				  "text/plain",
 				  GNOME_ICON_LOOKUP_FLAGS_NONE, NULL);
 
 	path = gnome_icon_theme_lookup_icon (theme, icon, 24, NULL, NULL);

printf("mime type: %s\n", path);

}