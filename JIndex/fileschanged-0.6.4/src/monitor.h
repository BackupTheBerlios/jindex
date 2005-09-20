#ifndef FILESCHANGED_MONITOR_H
#define FILESCHANGED_MONITOR_H
#include <fam.h>

int monitor_open(FAMConnection *c);
int monitor_close(FAMConnection *c);
int monitor_begin(FAMConnection *c, void *list);
int monitor_stop(FAMConnection *c, void *list);
int monitor_pause_toggle(FAMConnection *c, void *list);
int monitor_handle_events(FAMConnection *c, void *list, int secs_to_wait_for_pending, int secs_to_handle_pending);


#endif
