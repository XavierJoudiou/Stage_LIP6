PDF_COMP=pdflatex
BIBL_COMP=bibtex

.PHONY: all clean 

all: Movements.pdf clean

cleanall: cleanpdf clean
        

Movements.pdf : Movements.tex Biblio.bib
	$(PDF_COMP) Movements.tex    
	$(BIBL_COMP) Movements
	$(PDF_COMP) Movements.tex    
	$(PDF_COMP) Movements.tex    
	

clean: 
	rm -f *.aux *.dvi *.log *~ *.blg *.bbl *.toc

cleanpdf:
	rm -f *.pdf *~


