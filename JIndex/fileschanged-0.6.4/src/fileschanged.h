#ifndef FILESCHANGED_H
#define FILESCHANGED_H
#include <fam.h>
struct fileschanged_appstate_t
{
	int exit_gracefully_flag;
	FAMConnection c;
	void *list_of_files_to_monitor;
};

/* For environments not already using gamin */
#ifndef HAVE_FAMNOEXISTS
#define FAMNoExists(X) ;
#endif

#endif
