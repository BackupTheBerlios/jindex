#!/bin/bash
JAVA_HOME=`java-config -O` 
LIBDIR=libs
COMMON_CP=.:binout

for i in `ls $LIBDIR/*.jar`
do
        COMMON_CP="$i:$COMMON_CP"
done


LIBDIR=/usr/share/java-gnome/lib
for i in `ls $LIBDIR/*.jar`
do
        COMMON_CP="$i:$COMMON_CP"
done

LIBDIR=libs/mail
for i in `ls $LIBDIR/*.jar`
do
        COMMON_CP="$i:$COMMON_CP"
done

$JAVA_HOME/bin/java -Djava.library.path=/usr/lib:libs/native/`uname -m` -DHOME=/home/sorenm -classpath $COMMON_CP:./JIndex.jar client.JIndexClient .
