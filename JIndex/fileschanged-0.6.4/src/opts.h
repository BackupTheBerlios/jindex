#ifndef FILESCHANGED_OPTS_H
#define FILESCHANGED_OPTS_H
#ifdef __cplusplus
extern "C" {
#endif
#define _(__str) gettext(__str)
#define N_(__str) gettext_noop(__str)

struct fileschanged_arguments_t
{
	int recursive;
	int showdeleted;
	int showchanged;
	int showcreated;
	int showexecuting;
	int showfinishedexecuting;
	int filelist;
	char *filelist_filename;
	int filestomonitor;
	int filechangetimeout;
	int showaction;
	char *exec_cmd;
	int dereference_symlinks;
};
struct arguments_t {
	char **args;
	int arraylen; //for argument processing
	struct fileschanged_arguments_t fileschanged;
};

#ifdef __cplusplus
}
#endif
void parse_opts(int argc, char **argv, struct arguments_t *arguments);

#endif
