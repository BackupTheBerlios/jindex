#ifndef FILESCHANGED_HANDLERS_H
#define FILESCHANGED_HANDLERS_H
#include <fam.h>
#include <time.h>
enum handler_enum_t
{
	FC_CREATED_FILE,
	FC_CREATED_DIR,
	FC_CHANGED_FILE,
	FC_CHANGED_DIR,
	FC_DELETED_FILE,
	FC_DELETED_DIR,
	FC_STARTEXECUTING_FILE,
	FC_STOPEXECUTING_FILE,
	FC_HANDLER_MAX,
};

struct handler_t
{
	enum handler_enum_t id;
	const char *name;
	int (*handler)(FAMConnection *c, void *list, enum handler_enum_t id, char *filename);
};

int handle_event(FAMConnection *c, void *list, FAMEvent *e, time_t time_of_event);
void show_event(enum handler_enum_t id, char *filename);

#endif
