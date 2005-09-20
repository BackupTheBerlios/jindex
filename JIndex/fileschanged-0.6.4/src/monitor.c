#define _GNU_SOURCE
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <sys/select.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <time.h>
#include <unistd.h>
#include "fileschanged.h"
#include "monitor.h"
#include "node.h"
#include "list.h"
#include "opts.h"
#include "handlers.h"

int 
monitor_open (FAMConnection *c)
{
  return FAMOpen (c);
}

int 
monitor_close (FAMConnection *c)
{
  return FAMClose (c);
}

int 
monitor_begin(FAMConnection *c, void *list)
{
  int retval;
  unsigned int i;
  unsigned int count;
  struct node_t *node;
  list_count (list, &count);
  for(i = 0; i < count; i++)
    {
      list_get_element (list, i, &node);
      if (S_ISDIR(node->statbuf.st_mode))
	{
	  //printf ("%04d monitoring directory: '%s'\n", i, node->filename);
	  retval = FAMMonitorDirectory (c, node->filename, &node->request,
					(void*)node);
	  //printf ("FAMMonitorDirectory returns %d (reqnum %d)\n", retval, node->request.reqnum);
	}
      else if (S_ISREG(node->statbuf.st_mode))
	{
	  //printf ("%04d monitoring file: '%s'\n", i, node->filename);
	  retval = FAMMonitorFile (c, node->filename, &node->request,
				   (void*)node);
	  //printf ("FAMMonitorFile returns %d (reqnum %d)\n", retval, node->request.reqnum);
	}
      monitor_handle_events (c, list, 0, 30);
    }
  return 0;
}

static int 
monitor_do (FAMConnection *c, void *list, int (*FAMFunc)(FAMConnection *fc, const FAMRequest *fr))
{
  unsigned int i;
  unsigned int count;
  struct node_t *node;
  list_count (list, &count);
  for(i = 0; i < count; i++)
    {
      list_get_element (list, i, &node);
      FAMFunc (c, &node->request);
      monitor_handle_events (c, list, 0, 30);
    }
  return 0;
}

int 
monitor_stop (FAMConnection *c, void *list)
{
  return monitor_do (c, list, FAMCancelMonitor);
}

int 
monitor_pause_toggle (FAMConnection *c, void *list)
{
  int retval;
  static int paused;
  int (*FAMFunc)(FAMConnection *fc, const FAMRequest *fr);
  if (paused)
    FAMFunc = FAMResumeMonitor;
  else
    FAMFunc = FAMSuspendMonitor;
  retval = monitor_do (c, list, FAMFunc);
  paused = !paused;
  return retval;
}

static int 
monitor_handle_event (FAMConnection *c, void *list, int time_limit)
{
  int retval;
  FAMEvent e;
  time_t ending_time;
  time_t current_time;
  ending_time = time(NULL) + time_limit;
  while ((ending_time >= (current_time = time (NULL))) || (time_limit == -1))
    {
      if (FAMPending (c))
	{
	  retval = FAMNextEvent (c, &e);
	  if (retval < 0)
	    return -1;
	}
      else
	{
	  //second chance for the FAM server.  yay.
	  sleep (0);
	  if (!FAMPending (c))
	    break;
	}
      switch (e.code)
	{
	case FAMExists:
	case FAMEndExist:
	case FAMAcknowledge:
	case FAMMoved:
	  continue;
	case FAMChanged:
	case FAMDeleted:
	case FAMStartExecuting:
	case FAMStopExecuting:
	case FAMCreated:
	    {
	      retval = handle_event (c, list, &e, current_time);
	      if (retval < 0)
		return -1;
	      break;
	    }
	}
    }
  return 0;
}

int 
monitor_handle_events (FAMConnection *c, void *list, int secs_to_wait_for_pending, int secs_to_handle_pending)
{
  fd_set rfds;
  int numfds;
  struct timeval *tv_ptr = NULL;
  struct timeval tv;
  int retval;
  tv.tv_sec = secs_to_wait_for_pending; //wait for fam events for 2 seconds.
  tv.tv_usec = 500; //always add a bit here.
  tv_ptr = &tv;
  FD_ZERO (&rfds);
  FD_SET (c->fd,&rfds);
  numfds = select (FD_SETSIZE, &rfds, NULL, NULL, tv_ptr);
  if (numfds == 1)
    {//if a fam event happened,
      retval = monitor_handle_event (c, list, secs_to_handle_pending); 
      //get notifications for 30secs tops.
      if (retval < 0)
	{
	  return -1;
	}
    }
  return 0;
}

