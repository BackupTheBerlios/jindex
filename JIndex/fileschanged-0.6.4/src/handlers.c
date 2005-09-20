#define _GNU_SOURCE
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <signal.h>
#include "handlers.h"
#include "list.h"
#include "node.h"
#include "opts.h"
#include "wl.h"
extern struct arguments_t arguments;

//private prototypes
static int handle_created_file (FAMConnection *c, void *list, enum handler_enum_t id, char *filename);
static int handle_created_dir (FAMConnection *c, void *list, enum handler_enum_t id, char *filename);
static int handle_changed_file (FAMConnection *c, void *list, enum handler_enum_t id, char *filename);
static int handle_changed_dir (FAMConnection *c, void *list, enum handler_enum_t id, char *filename);
static int handle_deleted_file (FAMConnection *c, void *list, enum handler_enum_t id, char *filename);
static int handle_deleted_dir (FAMConnection *c, void *list, enum handler_enum_t id, char *filename);
static int handle_startexecuting_file (FAMConnection *c, void *list, enum handler_enum_t id, char *filename);
static int handle_stopexecuting_file (FAMConnection *c, void *list, enum handler_enum_t id, char *filename);
static struct handler_t handlers[FC_HANDLER_MAX]=
{
    { FC_CREATED_FILE, "A", handle_created_file },
    { FC_CREATED_DIR, "A", handle_created_dir },
    { FC_CHANGED_FILE, "M", handle_changed_file },
    { FC_CHANGED_DIR, "M", handle_changed_dir },
    { FC_DELETED_FILE, "R", handle_deleted_file },
    { FC_DELETED_DIR, "R", handle_deleted_dir },
    { FC_STARTEXECUTING_FILE, "E", 
      handle_startexecuting_file },
    { FC_STOPEXECUTING_FILE, "X", 
      handle_stopexecuting_file },
};

static void 
set_id_for_file (FAMEvent *e, enum handler_enum_t *id)
{
  if (e->code == FAMCreated)
    *id = FC_CREATED_FILE;
  else if (e->code == FAMChanged)
    *id = FC_CHANGED_FILE;
  else if (e->code == FAMDeleted)
    *id = FC_DELETED_FILE;
  else if (e->code == FAMStartExecuting)
    *id = FC_STARTEXECUTING_FILE;
  else if (e->code == FAMStopExecuting)
    *id = FC_STOPEXECUTING_FILE;
  else
    *id = FC_HANDLER_MAX;
  return;
}

static void 
set_id_for_dir (FAMEvent *e, enum handler_enum_t *id)
{
  if (e->code == FAMCreated)
    *id = FC_CREATED_DIR;
  else if (e->code == FAMChanged)
    *id = FC_CHANGED_DIR;
  else if (e->code == FAMDeleted)
    *id = FC_DELETED_DIR;
  else
    *id = FC_HANDLER_MAX;
  return;
}

static int 
determine_handler (void *list, FAMEvent *e, enum handler_enum_t *id, char **filename)
{
  struct node_t *filenode = NULL;
  if ((!list) || (!e) || (!id) || (!filename))
    return -1;
  *id = FC_HANDLER_MAX;
  //lookup the node that the event corresponds to.
  filenode = (struct node_t *)e->userdata;
  if (S_ISREG(filenode->statbuf.st_mode))
    {
      //the simple case.
      //okay it's a file we were monitoring to get this event.
      *filename = strdup (filenode->filename);
      set_id_for_file (e, id);
    }
  else if (S_ISDIR(filenode->statbuf.st_mode))
    {
      //okay it's a directory we were monitoring to get this event.
      //it could still be a file in the directory we are monitoring.
      //if e.filename is an absolute path then it must be a directory.
      if (e->filename[0] == '/') //it's a directory
	{
	  *filename = strdup (filenode->filename);
	  set_id_for_dir (e, id);
	}
      else
	{
	  //okay it could STILL be a file or directory under one that we're monitoring.
	  //but if we put filenode->filename and e->filename together, and stat it,
	  //deleted files/dirs will fail if we stat them (with node_new).
	  if (e->code != FAMDeleted)
	    {
	      int isdir = 0;
	      char *newfilename = NULL;
	      struct node_t newnode;
	      int retval;
	      asprintf (&newfilename, "%s/%s", filenode->filename, e->filename);
	      retval = node_new (&newnode, newfilename);
	      free (newfilename); newfilename = NULL;
	      if (retval == 0)
		{
		  isdir = S_ISDIR(newnode.statbuf.st_mode);
		  *filename = strdup (newnode.filename);
		  node_free (&newnode);
		}
	      else if (retval < 0)
		return -2;
	      if (isdir)
		set_id_for_dir (e, id);
	      else //regular file
		set_id_for_file (e, id);
	    }
	  else
	    {
	      asprintf (filename, "%s/%s", filenode->filename, e->filename);
	      //it's a deleted file or directory.  how can we tell it's type?  it's gone now.
	      //if it were a directory we were monitoring, and we were in recursive mode,
	      //then we would have received a notification for that directory with an absolute name.
	      //but this is a relative name.

	      //here's the deal.  we only show notifications for directories when we're not in recursive mode.
	      if (arguments.fileschanged.recursive == 0)
		{
		  //i still don't know if it's a file or directory
		  //just say it's a file.  because we're treating the directory like a directory-file anyway.
		  *id = FC_DELETED_FILE;
		}
	      else
		{
		  //okay we're in recursive mode.  a directory would have it's own notification.
		  //it must be a file
		  *id = FC_DELETED_FILE;
		}
	    }
	}
    }
  if (*id == FC_HANDLER_MAX)
    return -2; //unhandled case.
  return 0;
}

int 
handle_event(FAMConnection *c, void *list, FAMEvent *e, time_t time_of_event)
{
  char *filename = NULL;
  enum handler_enum_t id;
  int retval;
  retval = determine_handler (list, e, &id, &filename);
  if (retval != 0)
    {
      if (filename)
	free (filename);
      return -1;
    }
  retval = handlers[id].handler (c, list, id, filename);
  free (filename);
  return retval;
}

void 
show_event(enum handler_enum_t id, char *filename)
{
  if (arguments.fileschanged.exec_cmd)
    {
      struct sigaction sa;
      sa.sa_handler = SIG_IGN;
#ifdef SA_NOCLDWAIT
      sa.sa_flags = SA_NOCLDWAIT;
#else
      sa.sa_flags = 0;
#endif
      sigemptyset (&sa.sa_mask);
      sigaction (SIGCHLD, &sa, NULL);
      if (!fork ())
	{
	  execlp (arguments.fileschanged.exec_cmd, 
		  arguments.fileschanged.exec_cmd, 
		  handlers[id].name, filename, NULL);
	}
    }
  else
    {
      if (arguments.fileschanged.showaction)
	{
	  fprintf (stdout, "%s ", handlers[id].name);
	}
      fprintf (stdout, "%s\n", filename);
      fflush (stdout);
    }
  return;
}

static int 
handle_created_file(FAMConnection *c, void *list, enum handler_enum_t id, char *filename)
{
  if (arguments.fileschanged.showcreated)
    {
      show_event (id, filename);
    }
  return 0;
}

static int 
handle_created_dir(FAMConnection *c, void *list, enum handler_enum_t id, char *filename)
{
  //if we're recursing, then we should automatically start monitoring this dir.
  if (arguments.fileschanged.recursive)
    {
      struct node_t newdir;
      struct node_t *node = NULL;
      unsigned int count;
      int retval;
      retval = node_new (&newdir, filename);
      if (retval == 0)
	{
	  //printf("Adding '%s'\n",filename);
	  list_add (list, &newdir);
	  list_count (list, &count);
	  list_get_element (list,count - 1, &node);
	  FAMMonitorDirectory (c, newdir.filename, &newdir.request, 
			       (void*)node);
	  node_free (&newdir);
	}
      else if (retval < 0)
	return -1;
    }
  else
    {
      if (arguments.fileschanged.showcreated)
	{
	  show_event (id, filename);
	}
    }

  return 0;
}

static int 
handle_changed_file(FAMConnection *c, void *list, enum handler_enum_t id, char *filename)
{
  if (arguments.fileschanged.showchanged)
    {
      if (arguments.fileschanged.filechangetimeout > 0)
	{
	  wl_add_file (filename);
	}
      else
	{
	  show_event (id, filename);
	}
    }
  return 0;
}

static int 
handle_changed_dir(FAMConnection *c, void *list, enum handler_enum_t id, char *filename)
{
  if (arguments.fileschanged.recursive == 0)
    {
      if (arguments.fileschanged.showchanged)
	{
	  show_event (id, filename);
	}
    }
  return 0;
}

static int 
handle_deleted_file(FAMConnection *c, void *list, enum handler_enum_t id, char *filename)
{
  if (arguments.fileschanged.showdeleted)
    {
      show_event (id, filename);
    }
  return 0;
}

static int 
handle_deleted_dir(FAMConnection *c, void *list, enum handler_enum_t id, char *filename)
{
  if (arguments.fileschanged.showdeleted)
    {
      show_event (id, filename);
    }
  return 0;
}

static int 
handle_startexecuting_file(FAMConnection *c, void *list, enum handler_enum_t id, char *filename)
{
  if (arguments.fileschanged.showexecuting)
    {
      show_event (id, filename);
    }
  return 0;
}

static int 
handle_stopexecuting_file(FAMConnection *c, void *list, enum handler_enum_t id, char *filename)
{
  if (arguments.fileschanged.showfinishedexecuting)
    {
      show_event (id, filename);
    }
  return 0;
}
