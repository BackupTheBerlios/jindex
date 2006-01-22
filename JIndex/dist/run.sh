#!/bin/bash
JAVA_HOME=`java-config -O` 
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


$JAVA_HOME/bin/java -Djava.library.path=/opt/sun-jdk-1.5.0.03/jre/lib/`uname -m`:libs/native/`uname -m` -DHOME=/home/sorenm -classpath $COMMON_CP:./JIndex.jar daemon.DirectoryMonitor .
