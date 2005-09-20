#define _GNU_SOURCE
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <dirent.h>
#include "listdirs.h"
#include "list.h"
#include "xrealpath.h"
static int _listdirs (struct node_t *dir, void *dirs, void *ancestors);

static int 
_listdirs (struct node_t *dir, void *dirs, void *ancestors)
{
  struct dirent **namelist = NULL;
  struct node_t *found = NULL;
  struct node_t newdir;
  int n;
  unsigned int count;
  int i;
  int retval;
  char *d = NULL;
  list_find (ancestors, dir, &found);
  if (found)
    return 0;
  list_add (ancestors, dir);
  list_add (dirs, dir);
  n = scandir (dir->filename, &namelist, 0, alphasort);
  for(i = 0; i < n; i++)
    {
      if (strcmp (namelist[i]->d_name, ".") == 0)
	continue;
      if (strcmp (namelist[i]->d_name, "..") == 0)
	continue;
      asprintf (&d, "%s/%s", dir->filename, namelist[i]->d_name);
      retval = node_new (&newdir, d);
      free (d); d = NULL;
      if (retval == 0)
	{
	  if (S_ISDIR(newdir.statbuf.st_mode))
	    retval = _listdirs (&newdir, dirs,ancestors);
	  node_free (&newdir);
	}
      free (namelist[i]);
    }
  free (namelist);
  list_count (ancestors, &count);
  list_remove_element (ancestors, count - 1);

  return 0;
}

int 
listdirs (struct node_t *dir ,void *dirs)
{
  int retval;
  void *ancestors = NULL;
  list_init (&ancestors);
  retval = _listdirs (dir, dirs, ancestors);
  list_free (ancestors);
  return retval;
}
