#!/bin/bash
JAVA_HOME=`java-config -O`
#JAVA_HOME=~/jdk1.5.0_05
ARCH=amd64
LIBDIR=libs
COMMON_CP=.:binout

for i in `ls $LIBDIR/*.jar`
do
        COMMON_CP="$i:$COMMON_CP"
done

for i in `ls $LIBDIR/mail/*.jar`
do
        COMMON_CP="$i:$COMMON_CP"
done


$JAVA_HOME/bin/java -Djava.library.path=libs/native/$ARCH -DHOME=/home/sorenm -classpath $COMMON_CP daemon.DirectoryMonitor .
