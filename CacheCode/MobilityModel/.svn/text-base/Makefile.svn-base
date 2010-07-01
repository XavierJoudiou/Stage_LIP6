.PHONY: all clean doc compile
ALL=bin

compile:
	javac  -sourcepath src -Xlint -d bin `find -L -name "*.java"` -nowarn

doc:
	mkdir -p doc
	javadoc -sourcepath src -classpath $(ALL) -d doc peersim.pastry

run:
	java -cp $(ALL) mobilityModel.Main
	
all: compile doc run 

clean: 
	rm -fr classes doc
