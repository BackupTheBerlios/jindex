#!/bin/bash
#fileschanged -r --exec=./updateindex1.sh --show=created,changed,deleted /home/sorenm/workspace.stable/eclub2-srv
fileschanged -r --exec=./updateindex1.sh --show=created,changed,deleted $1

