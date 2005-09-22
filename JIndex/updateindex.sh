#!/bin/bash
java -DHOME=/home/sorenm -classpath libs/jmimemagic-0.0.4a.jar:libs/lucene-1.4.3.jar:binout:libs/jakarta-oro.jar:libs/log4j.jar:libs/xercesImpl.jar:libs/commons-collections.jar:libs/commons-lang.jar:.:libs/itext-1.3.jar IndexFiles updateindex $2
