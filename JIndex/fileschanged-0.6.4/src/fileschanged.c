#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <unistd.h>
#include <signal.h>
#include <fam.h>
#include <errno.h>
#include <error.h>
#include <sys/stat.h>
#include "config.h"
#include "gettext.h"
#include "fileschanged.h"
#include "opts.h"
#include "node.h"
#include "list.h"
#include "monitor.h"
#include "filelist.h"
#include "wl.h"
#include "handlers.h"
extern struct arguments_t arguments;
struct fileschanged_appstate_t app;

static void 
show_changed_file (char *filename, struct stat *statbuf)
{
  if (arguments.fileschanged.showchanged)
    {
      show_event (FC_CHANGED_FILE, filename);
    }
  return;
}

static int 
fileschanged_main (FAMConnection *c, void *list)
{
  monitor_begin (c, list);
  //list_show (list);
  while (1)
    {
      monitor_handle_events (c, list, 0, -1);
      if ((arguments.fileschanged.filechangetimeout > 0) && (arguments.fileschanged.showchanged))
	{
	  wl_show_changed_files (arguments.fileschanged.filechangetimeout, 
				 show_changed_file);
	}
      if (app.exit_gracefully_flag)
	break;
    }

  return 0;
}

static void 
fileschanged_cleanup ()
{
  monitor_stop (&app.c, app.list_of_files_to_monitor);
  monitor_close (&app.c);
  list_free (app.list_of_files_to_monitor);
  wl_destroy ();
  return;
}

void 
sigHandler (int sig)
{
  if (( sig == SIGINT ) || (sig == SIGPIPE))
    app.exit_gracefully_flag = 1;
  else
    signal(sig, SIG_IGN);
}

static int 
init_locales (const char *name)
{
#ifdef ENABLE_NLS
  setlocale (LC_ALL, "");
  if (!bindtextdomain (PACKAGE_NAME, LOCALEDIR))
    error (1, 0, "Error: Couldn't bind textdomain");
  if (!textdomain (PACKAGE_NAME))
    error(1, 0, "Error: Couldn't set textdomain");
#endif
  return 0;
}

int 
main(int argc, char **argv)
{
  int retval;

  if (init_locales (PACKAGE_NAME))
    error(1, 0, "Error: Couldn't set locale");

  parse_opts (argc, argv, &arguments);
  signal (SIGINT, sigHandler);
  retval = monitor_open (&app.c);
  if (retval != 0)
    error(1, 0, _("Error: Couldn't connect to fam daemon."));

  FAMNoExists (&app.c);

  list_init (&app.list_of_files_to_monitor);
  wl_init ();

  //either from file, stdin, or from command line.
  filelist_populate (app.list_of_files_to_monitor);

  setlinebuf (stdout);

  fileschanged_main (&app.c, app.list_of_files_to_monitor);
  //gets here by SIGINT or SIGPIPE

  fileschanged_cleanup ();

  exit(0);
}

