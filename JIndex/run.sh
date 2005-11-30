#!/bin/bash

LIBDIR=libs
COMMON_CP=.:binout

for i in `ls $LIBDIR/*.jar`
do
        COMMON_CP="$i:$COMMON_CP"
done

java -Djava.library.path=libs -DHOME=/home/sorenm -classpath $COMMON_CP daemon.DirectoryMonitor .
