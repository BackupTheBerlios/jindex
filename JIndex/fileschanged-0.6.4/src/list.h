#ifndef FILESCHANGED_LIST_H
#define FILESCHANGED_LIST_H
#include "node.h"
struct list_t
{
	unsigned int count;
	struct node_t *list;
};
int list_init(void **list);
int list_add(void *list, struct node_t *node);
int list_get_element(void *list, unsigned int element, struct node_t **node);
int list_find(void *list, struct node_t *node, struct node_t **found);
void list_count(void *list, unsigned int *count);
void list_show(void *list);
void list_sort(void *list);
void list_free(void *list);
int list_remove_element(void *list, unsigned int element);
#endif
