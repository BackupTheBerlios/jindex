Summary: displays altered files
Name: fileschanged
Version: 0.6.4
Release: 0
Copyright: GPL
Group: Utilities/File
Source: fileschanged-0.6.4.tar.gz
BuildRoot: %{_tmppath}/%{name}-%{version}-%{release}-root-%(id -un)
Vendor: Ben Asselstine
Packager: Ben Asselstine

%description
fileschanged is a GNU/Linux command-line utility that reports when files have been altered. 
This software is a client to the FAM (File Alteration Monitor) server that is now available in some distributions.  Here's how the fileschanged FAM client works: you give it some filenames on the command line and then it monitors those files for changes.  When it discovers that a file has changed (or has been altered), it displays the filename on the standard-output.

%prep
%setup
%configure

%build
make

%install
[ "$RPM_BUILD_ROOT" != "/" ] && rm -rf $RPM_BUILD_ROOT
%makeinstall

%files
%defattr(-,root,root)
%{_bindir}/*
%{_mandir}/*/*
%{_infodir}/*.info*
%{_datadir}/%{name}

%changelog
* Fri May 7 2004 Dick Marinus <dick.marinus@etos.nl>
- version 0.6.0
- tidied specfile

