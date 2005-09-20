#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include "node.h"
#include "xrealpath.h"
#include "opts.h"

extern struct arguments_t arguments;

void 
node_free(struct node_t *node)
{
  free (node->filename); node->filename = NULL;
  return;
}

int 
node_copy(struct node_t *dst, struct node_t *src)
{
  memcpy (dst, src, sizeof(struct node_t));
  dst->filename = strdup (src->filename);
  if (!dst->filename)
    return -1;
  return 0;
}

int 
node_new (struct node_t *node, char *filename)
{
  int retval;
  if ((!node) || (!filename))
    return -1;
  memset (node, 0, sizeof(struct node_t));
  if (arguments.fileschanged.dereference_symlinks)
    retval = stat (filename, &node->statbuf);
  else
    retval = lstat (filename, &node->statbuf);
  if (retval != 0)
    return -2;
  if (!S_ISLNK(node->statbuf.st_mode))
    node->filename = xrealpath (filename);
  else
    node->filename = strdup (filename);
  if (!node->filename)
    return -2;
  return 0;
}

int 
node_compare(struct node_t *n1, struct node_t *n2)
{
  if ((!n1) || (!n2))
    return 0;
  if ((!(n1->filename)) || (!(n2->filename)))
    return 0;
  return strcmp (n1->filename, n2->filename);
}

int 
node_is_empty(struct node_t *node)
{
  if (!(node->filename))
    return 1;
  return 0;
}
