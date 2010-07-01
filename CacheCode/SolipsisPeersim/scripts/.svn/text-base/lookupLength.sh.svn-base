#!/bin/bash

PEERSIM_JARS="../lib"
LIB_JARS=`find -L ../lib -name "*.jar" | tr [:space:] :`
#TEMP=params_CURRENT.cfg
REGEXP="\(.*\) \(.*\) \(.*\)"
RESULTS=../data/lookup.txt
ETALON=../config/params_SCRIPT.cfg
CONFIG=../config/params_LOOKUP.cfg
INTERMED=../config/params_INTERMED.cfg
ITER=5

> $RESULTS
for USERNB in 100 200 300 400 500 600 700 800 900 1000 2000 3000 4000 5000 10000 6000 7000 8000 9000 20000 30000 40000 50000 60000 70000 80000 90000 100000 ; do
    MAPSIZE=$(echo "7680*sqrt($USERNB/100)" | bc)
    cat $ETALON | sed "s/|userNb|/$USERNB/" | sed "s/|mapSize|/$MAPSIZE/" | sed "s/|random|/$RANDOM$RANDOM/" > $INTERMED

    LOOKUP=0
    SHORTEST=0
    LOOKUPSQ=0
    SHORTESTSQ=0
    echo "Iterations with userNb=$USERNB at $(date)"
    for ((i=1;$i<=$ITER;i++)); do
	        echo "$i/$ITER at $(date)";
		cat $INTERMED | sed "s/|random|/$RANDOM$RANDOM/" > $CONFIG
		RES=$(java -cp $LIB_JARS:$PEERSIM_JARS:../classes peersim.Simulator $CONFIG 2> /dev/null)
		TMP=$(echo $RES | sed "s/$REGEXP/\2/")
		TMPSQ=$(echo "$TMP*$TMP" | bc)
		LOOKUP=$(echo "$LOOKUP+$TMP" | bc)
		LOOKUPSQ=$(echo "$LOOKUPSQ+$TMPSQ" | bc)
		TMP=$(echo $RES | sed "s/$REGEXP/\3/")
		TMPSQ=$(echo "$TMP*$TMP" | bc)
		SHORTEST=$(echo "$SHORTEST+$TMP" | bc)
		SHORTESTSQ=$(echo "$SHORTESTSQ+$TMPSQ" | bc)
    done
    LOOKUP=$(echo "$LOOKUP/$ITER" | bc)
    LOOKUPSQ=$(echo "$LOOKUPSQ/$ITER" | bc)
    SHORTEST=$(echo "$SHORTEST/$ITER" | bc)
    SHORTESTSQ=$(echo "$SHORTESTSQ/$ITER" | bc)
    LV=$(echo "$LOOKUPSQ-$LOOKUP*$LOOKUP" | bc)
    SV=$(echo "$SHORTESTSQ-$SHORTEST*$SHORTEST" | bc)
    echo "$USERNB $LOOKUP $SHORTEST $LV $SV" >> $RESULTS
done