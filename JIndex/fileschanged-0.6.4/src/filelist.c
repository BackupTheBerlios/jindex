#define _GNU_SOURCE
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <error.h>
#include "gettext.h"
#include <libgen.h>
#include <unistd.h>
#include "filelist.h"
#include "xrealpath.h"
#include "node.h"
#include "list.h"
#include "opts.h"
#include "listdirs.h"
extern struct arguments_t arguments;

static int 
for_every_filename (int (*for_every_file)(int (*)(void *, char *), void *list), int (*add_it_to_the)(void *list, char *filename), void *list)
{
  for_every_file (add_it_to_the, list);
  return 0;
}

static int 
on_the_command_line (int (*process_file)(void *list, char *filename), void *list)
{
  unsigned int i;
  int retval;
  for(i = 0; i < arguments.arraylen; i++)
    {
      retval = process_file (list, arguments.args[i]);
    }
  return 0;
}

static void 
chop (char *line)
{
  char *tmp;
  tmp = strpbrk (line,"\r\n");
  if (tmp)
    tmp[0] = '\0';
  return;
}

static int 
in_filelist_file (int (*process_file)(void *list, char *filename), void *list)
{
  //read the list of files from a file.
  FILE *fileptr;
  char *filename;
  char *line = NULL;
  size_t n = 0;
  int retval;
  filename = arguments.fileschanged.filelist_filename;
  if (strcmp (filename,"-") == 0)
    fileptr = stdin;
  else
    {
      fileptr = fopen(filename, "r");
      if (!fileptr)
	error(1, 0, _("Error: couldn't open '%s'\n"), filename);
    }
  while (getline (&line, &n, fileptr) > 0)
    {
      chop (line);
      if (line[0] != '#')
	retval = process_file (list, line);
      free (line); line = NULL;
      n = 0;
    }

  if (fileptr != stdin)
    fclose (fileptr);
  return 0;
}

static int 
add_it_to_the (void *list, char *filename)
{
  int retval;
  struct node_t node;
  retval = node_new (&node, filename);
  if (retval == 0)
    {
      if ((S_ISDIR(node.statbuf.st_mode)) && (arguments.fileschanged.recursive))
	{
	  void *dirs = NULL;
	  struct node_t *dir = NULL;
	  unsigned int count;
	  unsigned int i;
	  //add all dirs below and including this one.

	  list_init (&dirs);
	  retval = listdirs (&node, dirs);
	  list_count (dirs, &count);
	  for(i = 0; i < count; i++)
	    {
	      list_get_element (dirs, i, &dir);
	      retval = list_add (list,dir);
	    }
	  list_free(dirs);

	}
      else if ((S_ISREG(node.statbuf.st_mode)) || (S_ISDIR(node.statbuf.st_mode)))
	{
	  //add this file.
	  retval = list_add (list, &node);
	}

      //printf("%s\n",node.filename);

      node_free (&node);
    }

  return 0;
}

static void 
remove_duplicates (void *list)
{
  unsigned int i;
  unsigned int count;
  struct node_t *node = NULL;
  struct node_t *lastdir = NULL;
  list_count (list, &count);
  for(i = 0; i < count; i++)
    {
      list_get_element (list, i, &node);
      if (S_ISDIR(node->statbuf.st_mode))
	lastdir = node;
      else if ((S_ISREG(node->statbuf.st_mode)) && (lastdir))
	{
	  char *file;
	  file = strdup (node->filename);
	  if (file)
	    {
	      char *dir;
	      dir = dirname (file);
	      if (strcmp (dir, lastdir->filename) == 0)
		{
		  node_free (node);
		}

	      free (file);
	    }
	}
    }

  for(i = 0; i < count; i++)
    {
      list_get_element (list, i, &node);
      if (node_is_empty (node))
	{
	  list_remove_element (list, i);
	}
    }
  return;
}

int 
filelist_populate (void *list_of_files_to_monitor)
{
  int retval = 0;
  if (arguments.fileschanged.filelist)
    retval = for_every_filename (in_filelist_file, add_it_to_the, 
				 list_of_files_to_monitor);
  else if (arguments.fileschanged.filestomonitor)
    retval = for_every_filename (on_the_command_line, add_it_to_the, 
				 list_of_files_to_monitor);
  else
    return -1;
  //okay we have our list now.
  //now remove files for which there are directories.

  list_sort (list_of_files_to_monitor);
  remove_duplicates (list_of_files_to_monitor);

  return retval;
}
