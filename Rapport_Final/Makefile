PDF_COMP=pdflatex
BIBL_COMP=bibtex

.PHONY: all clean 

all: Rapport_Final.pdf clean

cleanall: cleanpdf clean
        

Rapport_Final.pdf : Rapport_Final.tex Biblio.bib
	$(PDF_COMP) Rapport_Final.tex    
	$(BIBL_COMP) Rapport_Final
	$(PDF_COMP) Rapport_Final.tex    
	$(PDF_COMP) Rapport_Final.tex    
	

clean: 
	rm -f *.aux *.dvi *.log *~ *.blg *.bbl *.toc

cleanpdf:
	rm -f *.pdf *~


