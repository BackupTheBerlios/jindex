#!/bin/bash
JAVA_HOME=/opt/sun-jdk-1.5.0.03
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


$JAVA_HOME/bin/java -Djava.library.path=/opt/sun-jdk-1.5.0.03/jre/lib/i386/:libs/native/i386 -DHOME=/home/sorenm -classpath $COMMON_CP:../JIndex.jar daemon.DirectoryMonitor .
