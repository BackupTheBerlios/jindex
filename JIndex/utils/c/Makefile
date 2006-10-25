CC = gcc

JAVA_INCLUDE=/home/sorenm/jdk1.5.0_08/include
JAVA_INCLUDE_ARCH=/home/sorenm/jdk1.5.0_08/include/linux
CFLAGS=-fPIC -Wall -O2 `pkg-config --cflags gtk+-2.0 gnome-vfs-2.0 gnome-vfs-module-2.0 ` -I$(JAVA_INCLUDE) -I$(JAVA_INCLUDE_ARCH)

LDFLAGS=`pkg-config --libs gtk+-2.0 gnome-vfs-2.0 gnome-vfs-module-2.0 ` --shared

all: libJIndex

