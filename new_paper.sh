#!/bin/sh

if [ $# -eq 1 ]
then
	echo On va verifier que cette fiche existe pas deja

	for file in `ls ./Vocabulaire`
	do
	    if [ $file = $1.tex ] 
	    then
		echo Le fichier existe deja.
		exit 1
	    fi
	done

	for file in `ls ./Fiches_Lectures`
	do
	    if [ $file = $1.tex ] 
	    then
		echo Le fichier existe deja.
		exit 1
	    fi
	done

	echo On va creer les fichiers pour le vocabulaire et pour la fiche de lecture correspondant a $1
	cp ./Ressources/LaTex/Type_Vocabulaire.tex ./Vocabulaire/$1.tex
	cp ./Ressources/LaTex/Fiche_Type.tex ./Fiches_Lectures/$1.tex
else 
	echo Donner le nom d\'un article 
	exit 1
fi 
