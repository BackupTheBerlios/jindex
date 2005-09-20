AC_SUBST(PACKAGE_MAINTAINER)
AC_SUBST(PACKAGE_SUMMARY)
AC_SUBST(PACKAGE_WEBSITE)
AC_SUBST(PACKAGE_TEX_BUGREPORT)

AUTOMAKE_OPTIONS=no-dependencies
AC_INIT(fileschanged, 0.6.4, benasselstine@users.sf.net)
AM_INIT_AUTOMAKE
AC_CONFIG_SRCDIR([src/fileschanged.c])
AM_CONFIG_HEADER([config.h])
AM_GNU_GETTEXT([external])
AM_GNU_GETTEXT_VERSION(0.12.1)

AC_SUBST(VERSION)

ISODATE=`date +%Y-%m-%d`
AC_SUBST(ISODATE)

AC_CANONICAL_HOST

AC_PROG_INSTALL
AC_PROG_CC


dnl Checks for libraries.
AC_CHECK_LIB(fam,FAMOpen,,[echo "To build this software, you must install the FAM development package"; exit 1])

AC_CHECK_LIB(fam,FAMNoExists,[AC_DEFINE(HAVE_FAMNOEXISTS, 1, [Define if you have gamin instead of fam])],)

dnl Checks for header files.
AC_HEADER_STDC
AC_CHECK_HEADER(fam.h,,[echo "To build this software, you must install the FAM development package"; exit 1])
AC_CHECK_HEADERS(unistd.h time.h string.h dirent.h argp.h)

PACKAGE_MAINTAINER="Ben Asselstine"
PACKAGE_SUMMARY="displays altered files"
PACKAGE_WEBSITE="http://fileschanged.sf.net/"
PACKAGE_TEX_BUGREPORT="benasselstine\@sf.net"

AC_OUTPUT( Makefile info/Makefile man/Makefile info/package.texinfo src/Makefile redhat/fileschanged.spec debian/Makefile  po/Makefile.in m4/Makefile )
