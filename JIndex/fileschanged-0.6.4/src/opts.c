#include <argp.h>
#include <stdlib.h>
#include <string.h>
#include <dlfcn.h>
#include "gettext.h"
#include "config.h"
#include "opts.h"

const char *argp_program_version = PACKAGE_NAME " " PACKAGE_VERSION;
const char *argp_program_bug_address = "<" PACKAGE_BUGREPORT ">";

static char doc[] = N_("Monitors FILEs for alterations.  "
	"Display the filenames of FILEs that were created, changed, deleted, "
	"started execution or finished executing.\vFILEs must exist when "
	"monitoring begins, or they will not be monitored.");

static char args_doc[] = N_("[FILE]...");

struct arguments_t arguments;

static struct argp_option options[] = 
{
    {"show", 's', N_("EVENT[,...]"), 0, 
      N_("Display created, changed, deleted, executing, "
	 "or executed files (Default is \"created,changed\")") ,1},
      {"show-deleted", 'd', 0, OPTION_HIDDEN,
	N_("Display deleted files"), 1},
      {"show-changed", 'C', 0, OPTION_HIDDEN, 
	N_("(Default) Display changed files"), 1},
      {"show-created", 'c', 0, OPTION_HIDDEN, 
	N_("(Default) Display newly created files"), 1},
      {"show-executing", 'e', 0, OPTION_HIDDEN, 
	N_("Display executing files"), 1},
      {"show-executed", 'E', 0, OPTION_HIDDEN, 
	N_("Display files that have stopped executing"), 1},
      {"show-all", 'a', 0, 0, 
	N_("Display all file events and the associated action"), 2},
      {"files-to-monitor", 'f', 0, 0, 
	N_("(Default) Monitor the FILEs on the command line"), 3},
      {"filelist", 'l', N_("FILE"), 0, 
	N_("Monitor the list of filenames inside FILE"), 3},
      {"recursive", 'r', 0, 0, 
	N_("Monitor subdirectories of directories"), 4},
      {"dereference", 'L', 0, 0, 
	N_("Don't monitor symlinks, monitor what's pointed to"), 4},
      {"timeout", 't', "N", 0, 
	N_("Delay showing changed files for N seconds (Def=2)"), 4},
      {"display-action", 'p', 0, 0, 
	N_("Display action when showing altered files"), 5},
      {"exec", 'x', N_("PROG"), 0,
	N_("Run PROG when file is altered (PROG action filename)"), 6},
      { 0 }
};

static void 
free_arguments ()
{
  free (arguments.fileschanged.exec_cmd);
  free (arguments.fileschanged.filelist_filename);
  if (arguments.args)
    free (arguments.args);
}

static error_t 
parse_opt (int key, char *arg, struct argp_state *state) 
{
  struct arguments_t *arguments = state->input;

  switch (key) { 
  case 's':
      {
	char *tmp;
	for(tmp = strtok(arg, ","); tmp!=NULL;
	    tmp=strtok(NULL,","))
	  {
	    if (strcasecmp (tmp, _("created")) == 0)
	      parse_opt ('c', NULL, state);
	    else if (strcasecmp (tmp, _("changed")) == 0)
	      parse_opt ('C', NULL, state);
	    else if (strcasecmp (tmp, _("deleted")) == 0)
	      parse_opt ('d', NULL, state);
	    else if (strcasecmp (tmp, _("executing")) == 0)
	      parse_opt ('e', NULL, state);
	    else if (strcasecmp (tmp, _("executed")) == 0)
	      parse_opt ('e', NULL, state);
	    else if (strcasecmp (tmp, _("all")) == 0)
	      parse_opt ('a', NULL, state);
	    else
	      argp_failure (state, 1, 1, _("Error!  "
					   "'%s' is an unrecognized EVENT."), 
			    tmp);
	  }
	break;
      }
  case 'a':
    arguments->fileschanged.showcreated = 1;
    arguments->fileschanged.showchanged = 1;
    arguments->fileschanged.showdeleted = 1;
    arguments->fileschanged.showexecuting = 1;
    arguments->fileschanged.showfinishedexecuting = 1;
    arguments->fileschanged.showaction = 1;
    break;
  case 'c':
    arguments->fileschanged.showcreated = 1;
    break;
  case 'C':
    arguments->fileschanged.showchanged = 1;
    break;
  case 'e':
    arguments->fileschanged.showexecuting = 1;
    break;
  case 'E':
    arguments->fileschanged.showfinishedexecuting = 1;
    break;
  case 'r':
    arguments->fileschanged.recursive = 1;
    break;
  case 'd':
    arguments->fileschanged.showdeleted = 1;
    break;
  case 'f':
    arguments->fileschanged.filestomonitor = 1;
    arguments->fileschanged.filelist = 0;
    break;
  case 'l':
    arguments->fileschanged.filelist = 1;
    arguments->fileschanged.filestomonitor = 0;
    arguments->fileschanged.filelist_filename = strdup (arg);
    break;
  case 'p':
    arguments->fileschanged.showaction = 1;
    break;
  case 't':
    arguments->fileschanged.filechangetimeout = atoi (arg);
    if (arguments->fileschanged.filechangetimeout <= 1)
      arguments->fileschanged.filechangetimeout = -1;
    break;
  case 'x':
    if (arguments->fileschanged.exec_cmd)
      free (arguments->fileschanged.exec_cmd);
    arguments->fileschanged.exec_cmd = strdup (arg);
    break;
  case 'L':
    arguments->fileschanged.dereference_symlinks = 1;
    break;
  case ARGP_KEY_INIT:
    free_arguments ();
    arguments->args = (char**) malloc (1 * sizeof (char**));
    break;
  case ARGP_KEY_ARG:
    arguments->arraylen++; 
    arguments->args = (char **)realloc (arguments->args, 
				       arguments->arraylen * sizeof (char*));
    arguments->args[state->arg_num] = arg;
    break;
  case ARGP_KEY_END:
    break;
  default:
    return ARGP_ERR_UNKNOWN;
  }
  return 0;
}

static void 
set_default_arguments (struct fileschanged_arguments_t *fileschanged)
{
  //set some default values if options not set
  if ((fileschanged->showcreated == 0)  &&
      (fileschanged->showchanged == 0)   &&
      (fileschanged->showdeleted == 0)   &&
      (fileschanged->showexecuting == 0) &&
      (fileschanged->showfinishedexecuting == 0))
    {
      fileschanged->showcreated = 1; // if not showing anything
      fileschanged->showchanged = 1; // then show "changed" files.
    }

  if (fileschanged->filechangetimeout == 0)
    fileschanged->filechangetimeout = 2;

  if ((fileschanged->filestomonitor == 0) && 
      (fileschanged->filelist == 0))
    fileschanged->filestomonitor = 1;

  if (fileschanged->filechangetimeout > 0)
    fileschanged->filechangetimeout--; // make things line up.

  return;
}

static struct argp argp = { options, parse_opt, args_doc, doc }; 

void 
parse_opts(int argc, char **argv, struct arguments_t *arguments)
{
  int retval;
  setenv ("ARGP_HELP_FMT", "no-dup-args-note", 1);
  retval = argp_parse (&argp, argc, argv, 0, 0, arguments); 
  if (retval < 0)
    {
      argv[1] = "--help";
      retval = argp_parse (&argp, 2, argv, 0, 0, arguments); 
      //doesn't get here.
    }
  atexit (free_arguments);
  set_default_arguments (&arguments->fileschanged);
  return;
}

