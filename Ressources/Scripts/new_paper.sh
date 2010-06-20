#!/bin/sh

if [ $# -eq 1 ]
then
	echo On va verifier que cette fiche existe pas deja

	for file in `ls /home/xavier/Git_Lip6/Stage_LIP6/Vocabulaire`
	do
	    if [ $file = $1.tex ] 
	    then
		echo La fiche de vocabulaire existe deja.
		exit 1
	    fi
	done

	for file in `ls /home/xavier/Git_Lip6/Stage_LIP6/Fiches_Lectures`
	do
	    if [ $file = $1.tex ] 
	    then
		echo La fiche de lecture existe deja.
		exit 1
	    fi
	done

	echo On va creer les fichiers pour le vocabulaire et pour la fiche de lecture correspondant a $1
	cp /home/xavier/Git_Lip6/Stage_LIP6/Ressources/LaTex/Type_Vocabulaire.tex /home/xavier/Git_Lip6/Stage_LIP6/Vocabulaire/$1.tex
	cp /home/xavier/Git_Lip6/Stage_LIP6/Ressources/LaTex/Fiche_Type.tex /home/xavier/Git_Lip6/Stage_LIP6/Fiches_Lectures/$1.tex
	echo Créations OK
else 
	echo Donner le nom d\'un article 
	exit 1
fi 
