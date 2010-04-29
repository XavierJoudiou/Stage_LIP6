PDF_COMP=pdflatex
BIBL_COMP=bibtex

.PHONY: all clean 

all: Rapport_Type.pdf clean

cleanall: cleanpdf clean
        

Rapport_Type.pdf : Rapport_Type.tex Biblio.bib
	$(PDF_COMP) Rapport_Type.tex    
	$(BIBL_COMP) Rapport_Type
	$(PDF_COMP) Rapport_Type.tex    
	$(PDF_COMP) Rapport_Type.tex    
	

clean: 
	rm -f *.aux *.dvi *.log *~ *.blg *.bbl *.toc

cleanpdf:
	rm -f *.pdf *~


