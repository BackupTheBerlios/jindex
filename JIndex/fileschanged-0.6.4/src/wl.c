#include <string.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
#include <time.h>
#include <argz.h>
#include "wl.h"
#include "opts.h"
int wl_index = 0;
char*wl = NULL;
size_t wl_max = 0;

void 
wl_init()
{
  return;
}

static char *
argz_search (char *argz, size_t len, char *search)
{
  char *last_entry = NULL;
  char *entry;
  while ((entry = argz_next (argz, len, last_entry)))
    {
      if (strcmp (search, entry)==0)
	return entry;
      last_entry = entry;
    }
  return NULL;
}

void 
wl_add_file (char *filename)
{
  char *found;
  found = argz_search (wl, wl_max, filename);
  if (!found)
    argz_add (&wl, &wl_max, filename);
  return;
}

void 
wl_destroy()
{
  free (wl);
  wl = NULL;
  return;
}

void 
wl_show_changed_files(unsigned int filechangetimeout, void (*handler)(char *, struct stat *statbuf))
{
  static time_t lastchecked;
  if ((lastchecked != time (NULL)) && (wl_max > 0)) //check every sec.
    {
      int retval;
      struct stat statbuf;
      char *filename;
      //printf ("searching tree for files that are old and written to...\n");
      char *last_filename = NULL;
      while ((filename = argz_next (wl, wl_max, last_filename)))
	{
	  wl_index++;
	  //printf ("trying index %d with max %d\n", wl_index, wl_max);
	  //printf ("checking %s...\n", filename);
	  retval = lstat (filename, &statbuf);
	  if ((retval == 0) && 
	      (statbuf.st_ctime < (time (NULL) - filechangetimeout)))
	    {
	      handler (filename, &statbuf);

	      //printf ("removing\n");
	      argz_delete (&wl, &wl_max, filename);
	      if (wl_max == 0)
		break;
	      wl_index--;
	    }
	  else if (retval == -1) //can't stat file.  must be gone.
	    {
	      argz_delete (&wl, &wl_max, filename);
	      if (wl_max == 0)
		break;
	      wl_index--;
	    }

	  last_filename = filename;
	}
      lastchecked = time (NULL);
    }
  return;
}
