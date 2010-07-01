#!/bin/bash

PEERSIM_JARS="../.."
LIB_JARS=`find -L lib/ -name "*.jar" | tr [:space:] :`
TEMP=params_CURRENT.cfg
RESULTS=results.txt

for i in 2 5 10 20 50 90 
do
j=$((100 - i))
cat params_SCRIPT.cfg | sed "s/|wth|/$j/" | sed "s/|wtt|/$i/" > $TEMP
echo $i $(java -cp $LIB_JARS:$PEERSIM_JARS:classes peersim.Simulator $TEMP) >> $RESULTS
done
rm -f params_CURRENT.cfg