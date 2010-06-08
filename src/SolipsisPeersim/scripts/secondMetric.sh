#!/bin/bash

PEERSIM_JARS="../.."
LIB_JARS=`find -L lib/ -name "*.jar" | tr [:space:] :`
#TEMP=params_CURRENT.cfg
REGEXP="\(.*\) \(.*\) \(.*\) \(.*\) \(.*\)"
RESULTS=results.txt

for size in 1000; do
    for kind in "BASIC"; do
	if [ $kind = "ENHANCED" ]; then
	    for algo in "SHARP"; do
		echo "----SECOND METRIC ENHANCED $algo (size=$size)----" >> $RESULTS
		for i in 2 5 10 15 20 25 30 40 50 60 70 80; do
		    TEMP=$(cat params_SCRIPT.cfg | sed "s/|algo|/$algo/")
		    echo $TEMP > params_CURRENT.cfg
		    j=$((100 - i))
		    TIME=0;
		    PEERNUM=0;
		    MOVING=0;
		    CONNECTNUM=0;
		    CONNECTDURATION=0;
		    TEMP=$(cat params_CURRENT.cfg | sed "s/|wth|/$j/" | sed "s/|wtt|/$i/" | sed "s/|type|/$kind/" | sed "s/|size|/$size/") 
		    echo $TEMP > params_CURRENT.cfg
		    for k in 1 2 3 4 5; do
			false;
			while [ $? -ne 0 ]; do 
			    echo "Iteration $k with parameter $i and type $kind (size=$size)"
			    RES=$(java -cp $LIB_JARS:$PEERSIM_JARS:classes peersim.Simulator params_CURRENT.cfg)
			done
			TMP=$(echo $RES | sed "s/$REGEXP/\1/")
			TIME=$(echo "$TIME+$TMP" | bc)
			TMP=$(echo $RES | sed "s/$REGEXP/\2/")
			PEERNUM=$(echo "$PEERNUM+$TMP" | bc)
			TMP=$(echo $RES | sed "s/$REGEXP/\3/")
			MOVING=$(echo "$MOVING+$TMP" | bc)
			TMP=$(echo $RES | sed "s/$REGEXP/\4/")
			CONNECTDURATION=$(echo "$CONNECTDURATION+$TMP" | bc)
			TMP=$(echo $RES | sed "s/$REGEXP/\5/")
			CONNECTNUM=$(echo "$CONNECTNUM+$TMP" | bc)
		    done
		    TIME=$(echo "$TIME/5" | bc)
		    PEERNUM=$(echo "$PEERNUM/5" | bc)
		    MOVING=$(echo "$MOVING/5" | bc)
		    CONNECTDURATION=$(echo "$CONNECTDURATION/5" | bc)
		    CONNECTNUM=$(echo "$CONNECTNUM/5" | bc)
		    
		    echo "$i $TIME $PEERNUM $MOVING $CONNECTDURATION $CONNECTNUM" >> $RESULTS
		done
	    done
	else
	    echo "----SECOND METRIC BASIC (size=$size)----" >> $RESULTS
	    for i in 80; do
		j=$((100 - i))
		TIME=0;
		PEERNUM=0;
		MOVING=0;
		CONNECTNUM=0;
		CONNECTDURATION=0;
		TEMP=$(cat params_SCRIPT.cfg | sed "s/|wth|/$j/" | sed "s/|wtt|/$i/" | sed "s/|type|/$kind/" | sed "s/|algo|/$algo/" | sed "s/|size|/$size/")
		echo $TEMP > params_CURRENT.cfg
		for k in 1 2 3 4 5; do
		    false;
		    while [ $? -ne 0 ]; do
			echo "Iteration $k with parameter $i and type $kind (size=$size)"
			RES=$(java -cp $LIB_JARS:$PEERSIM_JARS:classes peersim.Simulator params_CURRENT.cfg)
		    done
		    TMP=$(echo $RES | sed "s/$REGEXP/\1/")
		    TIME=$(echo "$TIME+$TMP" | bc)
		    TMP=$(echo $RES | sed "s/$REGEXP/\2/")
		    PEERNUM=$(echo "$PEERNUM+$TMP" | bc)
		    TMP=$(echo $RES | sed "s/$REGEXP/\3/")
		    MOVING=$(echo "$MOVING+$TMP" | bc)
		    TMP=$(echo $RES | sed "s/$REGEXP/\4/")
		    CONNECTDURATION=$(echo "$CONNECTDURATION+$TMP" | bc)
		    TMP=$(echo $RES | sed "s/$REGEXP/\5/")
		    CONNECTNUM=$(echo "$CONNECTNUM+$TMP" | bc)
		done
		TIME=$(echo "$TIME/5" | bc)
		PEERNUM=$(echo "$PEERNUM/5" | bc)
		MOVING=$(echo "$MOVING/5" | bc)
		CONNECTDURATION=$(echo "$CONNECTDURATION/5" | bc)
		CONNECTNUM=$(echo "$CONNECTNUM/5" | bc)
		echo "$i $TIME $PEERNUM $MOVING $CONNECTDURATION $CONNECTNUM" >> $RESULTS
	    done 
	fi
    done
done