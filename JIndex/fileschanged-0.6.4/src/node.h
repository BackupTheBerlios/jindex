#ifndef FILESCHANGED_NODE_H
#define FILESCHANGED_NODE_H
#include <fam.h>
#include <time.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <unistd.h>
struct node_t
{
	struct stat statbuf;
	char *filename;
	FAMRequest request;
	FAMEvent lastevent;
	time_t lastevent_time;
	unsigned int id;
};
void node_free(struct node_t *node);
int node_copy(struct node_t *dst, struct node_t *src);
int node_new(struct node_t *node, char *filename);
int node_compare(struct node_t *n1, struct node_t *n2);
int node_is_empty(struct node_t *node);
#endif
