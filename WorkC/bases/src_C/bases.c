#include<stdio.h>


/* Fonction qui trouve le maximum entre deux Int*/
int max2int(int a, int b){
	int max = 0; 

	if(a<b){
		max = b;
	}else{
		max = a;
	}

	return max;
} 

/* Fonction qui trouve le minimum entre deux Int*/
int min2int(int a, int b){
	int min = 0;

	if(a<b){
		min = a;
	}else{
		min = b;
	}

	return min;
}

/* Fonction de recherche de maximum dans un tableau de Int*/
int maxInTabint(int tab[], int size){
	int max;
	int i=0;

	max = tab[0];
	for(i=0;i<size;i++){
		if(max<tab[i]){
			max = tab[i];
		}
	}

	return max;
}

/* Fonction de recherche de minimum dans un tableau de Int*/
int minInTabint(int tab[], int size){
	int min;
	int i=0;

	min = tab[0];
	for(i=0;i<size;i++){
		if(min>tab[i]){
			min = tab[i];
		}
	}

	return min;
}

/* Fonction pour échanger la position de deux éléments d'un tableau*/
void switchInTab(int tab[], int size, int a, int b){
	int tmp;
	
	tmp = tab[a];
	tab[a] = tab[b];
	tab[b] = tmp; 


}

/* Fonction qui affiche les éléments d'un tableau de Int*/
void printTab(int tab[], int size){

	int i=0;

	printf("Taille du tableau: %d\n", size);
        for(i=0;i<size;i++){
		printf("%d, ",tab[i]);
	}
	printf("\n");

}




/*************************/
/*   TESTS des fonctions */
/*************************/


int main(){
	int a = 10;
	int b = 14;

	int c = 21;
	int d = 19;
	
	int resMax = 0;
	int resMin = 0;
	int resMaxTab = 0;
	int resMinTab = 0;

	int tabA[5] = {2,3,5,1,7};
	int tabB[5] = {8,3,5,1,7};
	int tabC[5] = {1,3,5,6,7};

	printf("-----------------------------\n");
	printf("Tests Différentes Fonctions \n");
	printf("-----------------------------\n\n");

	/* On compare a et b, et on cherche le max */
	printf("Test de maximum entre %d et %d :\n",a,b);
	resMax = max2int(a,b);
	printf("Le maximum trouvé est: %d\n",resMax);
	printf("-----------------------------\n\n");
	
	/* On compare c et d, et on cherche le min */
	printf("Test de minimum entre %d et %d :\n",c,d);
	resMin = min2int(c,d);
	printf("Le minimum trouvé est: %d\n",resMin);
	printf("-----------------------------\n\n");

	/* On cherche le maximum dans un tableau de int */
	printf("Test de maximum dans le tableau suivant:\n");
	printTab(tabA,(sizeof(tabA)/sizeof(int)));
	resMaxTab = maxInTabint(tabA,(sizeof(tabA)/sizeof(int)));
	printf("Le maximum trouvé est: %d\n",resMaxTab);
	printf("-----------------------------\n\n");
	
	/* On cherche le minimum dans un tableau de int */
	printf("Test de minimum dans le tableau suivant:\n");
	printTab(tabB,(sizeof(tabB)/sizeof(int)));
	resMinTab = minInTabint(tabB,(sizeof(tabB)/sizeof(int)));
	printf("Le minimum trouvé est: %d\n",resMinTab);
	printf("-----------------------------\n\n");

	/* On veut inverser deux éléments dans un tableau de Int*/
	printf("Test d'échange d'éléments dans le tableau suivant:\n");
	printTab(tabC,(sizeof(tabC)/sizeof(int)));
	switchInTab(tabC,(sizeof(tabC)/sizeof(int)),2,4);
	printTab(tabC,(sizeof(tabC)/sizeof(int)));



	return 1; 

}
