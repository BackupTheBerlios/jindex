#ifdef HAVE_CONFIG_H
#  include "config.h"
#endif

#include <string.h>
#include <stdlib.h>
#include <locale.h>
#include <gst/gst.h>

char *filename = NULL;
GstElement *pipeline = NULL;
GstElement *source = NULL;

#define NEW_PIPE_PER_FILE

static gboolean
message_loop (GstElement * element, GstTagList ** tags)
{
  GstBus *bus;
  gboolean done = FALSE;

  bus = gst_element_get_bus (element);
  g_return_val_if_fail (bus != NULL, FALSE);
  g_return_val_if_fail (tags != NULL, FALSE);

  while (!done) {
    GstMessage *message;

    message = gst_bus_pop (bus);
    if (message == NULL)
      /* All messages read, we're done */
      break;

    switch (GST_MESSAGE_TYPE (message)) {
      case GST_MESSAGE_ERROR:
      case GST_MESSAGE_EOS:
        gst_message_unref (message);
        return TRUE;
      case GST_MESSAGE_TAG:
      {
        GstTagList *new_tags;

        gst_message_parse_tag (message, &new_tags);
        if (*tags)
          *tags = gst_tag_list_merge (*tags, new_tags, GST_TAG_MERGE_KEEP_ALL);
        else
          *tags = new_tags;
        break;
      }
      default:
        break;
    }
    gst_message_unref (message);
  }
  gst_object_unref (bus);
  return TRUE;
}

static void
make_pipeline ()
{
  GstElement *decodebin;

  if (pipeline != NULL)
    gst_object_unref (pipeline);

  pipeline = gst_pipeline_new (NULL);

  source = gst_element_factory_make ("filesrc", "source");
  g_assert (GST_IS_ELEMENT (source));
  decodebin = gst_element_factory_make ("decodebin", "decodebin");
  g_assert (GST_IS_ELEMENT (decodebin));

  gst_bin_add_many (GST_BIN (pipeline), source, decodebin, NULL);
  gst_element_link (source, decodebin);
}

static void
print_tag (const GstTagList * list, const gchar * tag, gpointer unused)
{
  gint i, count;

  count = gst_tag_list_get_tag_size (list, tag);

  for (i = 0; i < count; i++) {
    gchar *str;

    if (gst_tag_get_type (tag) == G_TYPE_STRING) {
      if (!gst_tag_list_get_string_index (list, tag, i, &str))
        g_assert_not_reached ();
    } else {
      str =
          g_strdup_value_contents (gst_tag_list_get_value_index (list, tag, i));
    }

    if (i == 0) {
      g_print ("  %15s: %s\n", gst_tag_get_nick (tag), str);
    } else {
      g_print ("                 : %s\n", str);
    }

    g_free (str);
  }
}

int
main (int argc, char *argv[])
{
  guint i = 1;

  setlocale (LC_ALL, "");

  gst_init (&argc, &argv);

  if (argc < 2) {
    g_print ("Please give filenames to read metadata from\n\n");
    return 1;
  }

  make_pipeline ();
  while (i < argc) {
    GstStateChangeReturn sret;
    GstState state;
    GstTagList *tags = NULL;

    filename = argv[i];
    g_object_set (source, "location", filename, NULL);

    GST_DEBUG ("Starting reading for %s", filename);

    /* Decodebin will only commit to PAUSED if it actually finds a type;
     * otherwise the state change fails */
    sret = gst_element_set_state (GST_ELEMENT (pipeline), GST_STATE_PAUSED);

    if (GST_STATE_CHANGE_ASYNC == sret) {
      if (GST_STATE_CHANGE_SUCCESS !=
          gst_element_get_state (GST_ELEMENT (pipeline), &state, NULL,
              5 * GST_SECOND)) {
        g_print ("State change failed for %s. Aborting\n", filename);
        break;
      }
    } else if (sret != GST_STATE_CHANGE_SUCCESS) {
      g_print ("%s - Could not read file\n", filename);
      goto next_file;
    }

    if (!message_loop (GST_ELEMENT (pipeline), &tags)) {
      g_print ("Failed in message reading for %s\n", argv[i]);
    }

    if (tags) {
      g_print ("Metadata for %s:\n", argv[i]);
      gst_tag_list_foreach (tags, print_tag, NULL);
      gst_tag_list_free (tags);
      tags = NULL;
    } else
      g_print ("No metadata found for %s\n", argv[i]);

    sret = gst_element_set_state (GST_ELEMENT (pipeline), GST_STATE_NULL);
#ifndef NEW_PIPE_PER_FILE
    if (GST_STATE_CHANGE_ASYNC == sret) {
      if (GST_STATE_CHANGE_FAILURE ==
          gst_element_get_state (GST_ELEMENT (pipeline), &state, NULL,
              GST_CLOCK_TIME_NONE)) {
        g_print ("State change failed. Aborting");
        break;
      }
    }
#endif

  next_file:
    i++;

#ifdef NEW_PIPE_PER_FILE
    make_pipeline ();
#endif
  }

  if (pipeline)
    gst_object_unref (pipeline);
  return 0;
}
