#! /bin/bash

PEERSIM_JARS="../.."
LIB_JARS=`find -L lib/ -name "*.jar" | tr [:space:] :`
TEMP=params_ANALYZER.cfg
RESULTS=results.txt

echo "Starting density validation"

for ZONENB in 2 3 4 5; do
    for OUTOFZONE in 3 5 7 9 10; do
	LOWBOUND=3
	HIGHBOUND=35
	while [ $(($HIGHBOUND - $LOWBOUND)) -gt 1 ]; do
	    MIDDLE=$((($HIGHBOUND + $LOWBOUND)/2));
	    echo "Starting iteration with parameters $OUTOFZONE $MIDDLE";
	    cat params_SCRIPT.cfg | sed "s/|x|/$MIDDLE/" | sed "s/|y|/$OUTOFZONE/" | sed "s/|z|/$ZONENB/" > $TEMP
	    java -cp $LIB_JARS:$PEERSIM_JARS:classes peersim.Simulator $TEMP
	    VARIANCE=$(java -cp classes traceAnalyzer/TraceAnalyzer defaultRecord.txt SYNTHETIC densityvariance)
	    
	    if [ $VARIANCE -gt 60 ]; then
		HIGHBOUND=$MIDDLE;
	    else
		LOWBOUND=$MIDDLE;
	    fi;
	    echo "zonenb=$ZONENB zonesize=$MIDDLE outofzonenb=$OUTOFZONE var=$VARIANCE" >> $RESULTS
	    echo "Variance is $VARIANCE";
	done
    done
done
