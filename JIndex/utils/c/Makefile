JAVA_INCLUDE=/opt/sun-jdk-1.5.0.07/include/
#CFLAGS=-fPIC -g -Wall -I/opt/sun-jdk-1.5.0.07/include -I/usr/include/glib-1.2 -I/usr/lib/glib/include -I/usr/include/libgnomeui-2.0 -DNEED_GNOMESUPPORT_H -I/usr/lib/gnome-libs/include -I/usr/include/gtk-1.2 -I/usr/include/gmime-2.0 -I/usr/include/gdk-pixbuf-1.0 -I/usr/include -I/usr/include/orbit-1.0 -I/usr/include/gnome-vfs-1.0 -I/usr/lib/gnome-vfs-1.0/include -I/usr/include/gnome-xml -I/usr/include/gconf/1 -D_REENTRANT
CFLAGS=`pkg-config --cflags gtk+-2.0 libgnomemm-2.6 libgnome-2.0 gnome-vfs-2.0` -I/opt/sun-jdk-1.5.0.07/include -fPIC  -D_REENTRANT 
#LDFLAGS=-rdynamic -L/usr/lib -lgnomeui -lgdk_imlib -lSM -lICE -lgnome -lgnomesupport -lesd -lasound -laudiofile -ldb1 -lgmime-2.0 -lnsl -lgdk_pixbuf -lart_lgpl -lgnomevfs-2 -lxml2 -lz -lgconf-gtk-2 -lgconf-1 -loaf -lORBitCosNaming -lORBit -lIIOP -lORBitutil -lgtk -lgdk -lXi -lXext -lX11 -lm -lgmodule -lgthread -lglib -lpthread -shared
#LDFLAGS=`gnome-config --libs gnome gnomeui` --shared

LDFLAGS=`pkg-config --libs  gtk+-2.0 libgnomemm-2.6 libgnome-2.0 gnome-vfs-2.0` -shared -Wl,-soname 
#LDFLAGS=`pkg-config --libs  gtk+-2.0 libgnomemm-2.6 libgnome-2.0 gnome-vfs-2.0`
all: libJIndex test
