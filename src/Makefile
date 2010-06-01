PDF_COMP=pdflatex
BIBL_COMP=bibtex

.PHONY: all clean 

all: Etude_BB.pdf clean

cleanall: cleanpdf clean
        

Etude_BB.pdf : Etude_BB.tex Biblio.bib
	$(PDF_COMP) Etude_BB.tex    
	$(BIBL_COMP) Etude_BB
	$(PDF_COMP) Etude_BB.tex    
	$(PDF_COMP) Etude_BB.tex    
	

clean: 
	rm -f *.aux *.dvi *.log *~ *.blg *.bbl *.toc

cleanpdf:
	rm -f *.pdf *~


