#!/bin/bash
JAVA_HOME=~/jdk1.5.0_08/
LIBDIR=libs
COMMON_CP=.:binout

for i in `ls $LIBDIR/*.jar`
do
        COMMON_CP="$i:$COMMON_CP"
done


#LIBDIR=/usr/share/java/
#for i in `ls $LIBDIR/*.jar`
#do
#        COMMON_CP="$i:$COMMON_CP"
#done

LIBDIR=libs/mail
for i in `ls $LIBDIR/*.jar`
do
        COMMON_CP="$i:$COMMON_CP"
done

export JAVALIB_PATH=~/jdk1.5.0_08/jre/lib/amd64/:libs/native/amd64

$JAVA_HOME/bin/java -Djava.library.path=$JAVALIB_PATH -DHOME=/home/sorenm -classpath $COMMON_CP client.JIndexClient .
