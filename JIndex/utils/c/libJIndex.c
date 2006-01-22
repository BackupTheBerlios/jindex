#include <stdio.h>
#include <gnome.h>
#include <libgnomeui/libgnomeui.h>
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

