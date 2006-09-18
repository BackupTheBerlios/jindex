 %module libJindex_new
 %{
 /* Put header files here or function declarations like below */
 extern char *getMimeType(char *filename);
 extern char *getIconFromMimeType(char *filename);
 %}
 
extern char *getMimeType(char *filename);
extern char *getIconFromMimeType(char *filename);
