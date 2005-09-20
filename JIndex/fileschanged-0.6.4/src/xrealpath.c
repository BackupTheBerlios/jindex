#include <stdio.h>
#include <errno.h>
#include <limits.h>
#include <stdlib.h>
#include "xrealpath.h"

char *
xrealpath(char *filepath)
{
  char *buffer;
  size_t bufsiz = 1024;

  buffer = (char*) malloc (bufsiz);
  while (realpath (filepath, buffer) == NULL)
    {
      if (errno == ENOENT)
	{
	  fprintf (stderr, "Error: file '%s' not found.\n", filepath);
	  free (buffer);
	  return NULL;
	}
      else if (errno == ELOOP)
	{
	  fprintf (stderr, "Error: file '%s' is a faulty symbolic link.\n", 
		   filepath);
	  free (buffer);
	  return NULL;
	}
      perror ("realpath");
      free (buffer);
      bufsiz *= 2;
      buffer = (char*) malloc (bufsiz);
    }
  return buffer;
}
