#define POSIX_SOURCE 1
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <signal.h>

#define nbArg 2

int main (int argc, char* argv[]){

	int i=0;
	int nbargs = nbArg -1;
	int nbfils=0;
	if(argc == nbargs){
		printf("Nombre d'argument incohérent: %d\n",argc);
		printf("--------------------------------\n");
		printf("Le nombre d'argument doit être de %d\n",nbargs);
		exit(1);
	}

	printf("+++++++++++++++++++\n");
	printf("le Nombre de fils a créer est de: %d\n",atoi(argv[1]));
	nbfils=atoi(argv[1]);
	printf("+++++++++++++++++++\n");
	if(argc > (nbargs + 1)){
		printf("Certains des arguments ne servent pas\n");
		int arginutiles = argc - nbargs;
		for(i=0;i<arginutiles-1;i++){
			printf("***************\n");
			printf("argument inutile: %s\n", argv[2+i]);
		}
	
	}
	printf("--------------------------------\n");
	printf("	Début programme   	\n");
	printf("--------------------------------\n\n");

	int fils=0;
	int p;
	int perepid=getpid();
	printf("Le pid du père est: %d et je vais avoir %d fils\n",perepid,nbfils);
	while(fils < nbfils){
		if(getpid() == perepid){
			if((p=fork()) == -1){
				exit(1);
			}
			if(p != 0){
				printf("J'ai créé un fils \n");
			}
		fils++;
		}
		if(getpid() == (perepid+1)){
			printf("Je suis le premier fils de %d, mon pid est: %d\n",getppid(),getpid());
			printf("Je me met en pause !!\n");
			pause();
			exit(2);
		}else{
			if(p == 0){
				printf("Je suis un fils de %d, mon pid est: %d\n",getppid(),getpid());
				exit(3);
			}
		}
	}
	int status;
	int pid_fils;
	int waitfils=0;
	
	if(getpid() == perepid){
		kill(getpid()+1,SIGKILL);
		while(waitfils < nbfils){
			pid_fils = wait(&status);
			printf("Fin du fils %d, status: %d\n",pid_fils,WEXITSTATUS(status));
			waitfils ++;
		}
	}

	printf("\n");



	return EXIT_SUCCESS;
}
