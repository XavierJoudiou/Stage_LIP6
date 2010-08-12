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

runnothing:
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_Base_2010.cfg #> trace
	CalculsBases Nothing 500_120

runnocache:
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_Prefetch_2010.cfg #> trace
	CalculsBases NoCachePrefetch 500_120

bureau:
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_Prefetch_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_Prefetch_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_Prefetch_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_Prefetch_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_Prefetch_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_Prefetch_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_Prefetch_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_Prefetch_2010.cfg #> trace
	CalculsBases NoCachePrefetchNormal 1k_600
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	CalculsBases CacheNoPrefetch 1k_600
	play -q ../../Sounds/Homer_Dooh.wav


rundebut:
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_Base_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_Base_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_Base_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_Base_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_Base_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_Base_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_Base_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_Base_2010.cfg #> trace
	CalculsBases Nothing 500_150
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_Prefetch_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_Prefetch_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_Prefetch_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_Prefetch_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_Prefetch_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_Prefetch_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_Prefetch_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_Prefetch_2010.cfg #> trace
	CalculsBases NoCachePrefetchNormal 500_150
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	CalculsBases CacheNoPrefetch 500_150
	play -q ../../Sounds/Homer_Dooh.wav

run:
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	CalculsBases NCachePrefetchAmeliore 500_150
	play -q ../../Sounds/Homer_Dooh.wav

runtest:
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	CalculsBases CacheNoPrefetchcf3 700_300
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	CalculsBases CacheNoPrefetchcf3 700_300
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_2010.cfg #> trace
	CalculsBases CacheNoPrefetchcf3 700_300
	play -q ../../Sounds/Homer_Dooh.wav

one:
	java -cp $(ALL) peersim.Simulator config/params_CURRENT_Base_2010.cfg #> trace
	CalculsBases Nothing 700_300	
	play -q ../../Sounds/Homer_Dooh.wav

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
