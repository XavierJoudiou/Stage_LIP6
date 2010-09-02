PDF_COMP=pdflatex
BIBL_COMP=bibtex

.PHONY: all clean 

all:  Slides_08092010.pdf clean

cleanall: cleanpdf clean
        

Slides_08092010.pdf : Slides_08092010.tex 
	$(PDF_COMP)  Slides_08092010.tex
	$(PDF_COMP)  Slides_08092010.tex
	

clean: 
	rm -f *.aux *.dvi *.log *~ *.blg *.bbl *.toc *.nav *.out *.snm

cleanpdf:
	rm -f *.pdf *~


