PDF_COMP=pdflatex

LATEX_SOURCES=$(shell grep -l '^\\begin{document}' *.tex)
TARGETS=$(LATEX_SOURCES:%.tex=%.pdf)

.PHONY: all clean view 

all: $(TARGETS) clean

cleanall: cleanpdf clean
	

%.pdf : 
	$(PDF_COMP) "$*.tex"	

clean: 
	rm -f *.aux *.dvi *.log *~

cleanpdf:
	rm -f *.pdf *~

