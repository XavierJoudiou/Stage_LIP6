#!/bin/bash

#script de Sergey


> $LIST
for j in 114 112 118; do
    for i in `seq 254`; do
	if [ "132.227.114.42" != "132.227.$j.$i" ] && [ "132.227.118.21" != "132.227.$j.$i" ]; then
	    echo "Testing 132.227.$j.$i..."
	    scp -oCheckHostIP=no -oConnectTimeout=1 -oBatchMode=yes test.sh 132.227.$j.$i:. 
	    if [ $? == 0 ]; then
		ssh 132.227.$j.$i ./test.sh 
		if [ $? == 0 ]; then
		    echo "OK"
		    echo "132.227.$j.$i" >> $LIST
		else
		    echo "Failed"
		fi;
	    fi;
	fi;
    done
done;
