.PHONY: all clean doc compile
KIND=popdensity
LIB_JARS=`find -L lib -name "*.jar" | tr [:space:] :`
ALL=$(LIB_JARS):classes

compile:
	mkdir -p classes
	javac  -sourcepath src -Xlint -classpath $(LIB_JARS) -d classes `find  -L . -name "*.java"` -nowarn

doc:
	mkdir -p doc
	javadoc -sourcepath src -classpath $(ALL) -d doc peersim.pastry

run:
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace

play:
	java -cp $(ALL) peersim/tracePlayer/TracePlayer defaultRecord.txt

analyze:
	java -cp $(ALL) traceAnalyzer/TraceAnalyzer defaultRecord.txt SYNTHETIC $(KIND)

analyzesl1:
	java -cp $(ALL) traceAnalyzer/TraceAnalyzer SecondLifeRecord.txt SECONDLIFE $(KIND)

analyzesl2:
	java -cp $(ALL) traceAnalyzer/TraceAnalyzer SecondLifeRecord2.txt SECONDLIFE $(KIND)


all: compile doc run 

clean: 
	rm -fr classes doc
