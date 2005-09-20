#!/bin/bash
java -DHOME=~/ -classpath libs/jmimemagic-0.0.4a.jar:libs/lucene-1.4.3.jar:binout:libs/jakarta-oro.jar:libs/log4j.jar:libs/xercesImpl.jar:. IndexFiles $2
