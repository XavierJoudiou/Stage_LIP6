PDF_COMP=pdflatex
BIBL_COMP=bibtex

.PHONY: all clean 

all: Autres.pdf clean

cleanall: cleanpdf clean
        

Autres.pdf : Autres.tex Biblio.bib
	$(PDF_COMP) Autres.tex    
	$(BIBL_COMP) Autres
	$(PDF_COMP) Autres.tex    
	$(PDF_COMP) Autres.tex    
	

clean: 
	rm -f *.aux *.dvi *.log *~ *.blg *.bbl *.toc

cleanpdf:
	rm -f *.pdf *~


