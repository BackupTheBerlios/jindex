#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "list.h"

int 
list_init (void **list)
{
  struct list_t *l;
  l = (struct list_t*)malloc (sizeof (struct list_t));
  if (!l)
    return -1;
  l->count=0;
  l->list = (struct node_t *)malloc (1);
  if (!l->list)
    {
      free (l);
      return -2;
    }
  *list = (void*)l;
  return 0;
}

void 
list_free (void *list)
{
  unsigned int i;
  struct list_t *l;
  l = (struct list_t *) list;
  if (!l)
    return;
  for(i = 0; i < l->count; i++)
    {
      node_free (&l->list[i]);
    }
  l->count = 0;
  free (l->list); l->list = NULL;
  free (l);
  return;
}

int 
list_add (void *list, struct node_t *node)
{
  struct list_t *l;
  l = (struct list_t *) list;
  if ((!l) || (!node))
    return -1;
  l->count++;
  l->list = (struct node_t *)realloc (l->list, 
				      l->count * sizeof(struct node_t));
  node_copy (&l->list[l->count - 1], node);
  return 0;
}

int 
list_get_element (void *list, unsigned int element, struct node_t **node)
{
  struct list_t *l;
  l = (struct list_t *) list;
  if ((!l) || (!node))
    return -1;
  if (element > l->count)
    return -2;
  *node = (struct node_t *)&l->list[element];
  return 0;
}

int 
list_find (void *list, struct node_t *node, struct node_t **found)
{
  unsigned int i;
  int retval;
  struct list_t *l;
  l = (struct list_t *) list;
  if ((!l) || (!node) || (!found))
    return -1;
  for(i = 0; i < l->count; i++)
    {
      retval = node_compare (&l->list[i], node);
      if (retval == 0)
	{
	  *found = &l->list[i];
	  return 1;
	}
    }
  return 0;
}

void 
list_show (void *list)
{
  unsigned int i;
  struct list_t *l;
  l = (struct list_t *) list;
  if (!l) 
    return;
  printf("showing file listing:\n");
  if (l->count)
    {
      for(i = 0; i < l->count; i++)
	{
	  printf ("\t%s\n", l->list[i].filename);
	}
    }
  else
    printf ("\t(empty)\n");
  return;
}

void 
list_count (void *list, unsigned int *count)
{
  struct list_t *l;
  l = (struct list_t *) list;
  if ((!l) || (!count))
    return;
  *count = l->count;
  return;
}

void 
list_sort (void *list)
{
  struct list_t *l;
  l = (struct list_t *) list;
  if (!l)
    return;
  if (l->count == 0)
    return;
  return qsort (l->list, l->count, sizeof(struct node_t), 
		(int (*)(const void*, const void*)) node_compare);
}

int 
list_remove_element (void *list, unsigned int element)
{
  struct node_t *node;
  struct list_t *l;
  l = (struct list_t *) list;
  if (!l)
    return -1;
  l->count--;
  node = &l->list[element];
  memmove (node, node + sizeof(struct node_t), 
	   (l->count-element) * sizeof (struct node_t));
  return 0;
}
