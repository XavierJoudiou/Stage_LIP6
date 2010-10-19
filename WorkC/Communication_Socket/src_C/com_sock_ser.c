#define POSIX_SOURCE 1
#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <sys/select.h>
#include <netinet/in.h>
#include <netdb.h>
#include <string.h>

#define PORTSERV 7100


extern void bzero (void *, size_t);
extern void bcopy (const void *, void *, size_t);


int main(int argc,char* argv[]){

	struct sockaddr_in sin; /* Nom de la socket de connexion */
	struct sockaddr_in exp;  /* Nom de la socket du client */
	int sc;                 /* Socket de connexion */
	int scom;               /* Socket de communication */
	struct hostent *hp;
	int fromlen = sizeof exp;
	int cpt;

	/* Creation de la socket */
	if((sc = socket(AF_INET,SOCK_STREAM,0)) < 0){
		perror("socket");
		exit(1);
	}

	bzero((char*)&sin,sizeof(sin));
	sin.sin_addr.s_addr = htonl(INADDR_ANY);
	sin.sin_port = htons(PORTSERV);
	sin.sin_family = AF_INET;
	
	/* Nommage */
	if(bind(sc,(struct sockaddr *)&sin,sizeof(sin)) < 0){
		perror("bind");
		exit(1);
	}
	listen(sc,5);

	/* Boucle principale */
	for(;;){
		if((scom = accept(sc,(struct sockaddr *)&exp, &fromlen)) == -1){
			perror("accept");
			exit(3);
		}
		
		/* Creation d'un fils qui traire la requete */
		if(fork() == 0){
			/*processus fils */
			if(read(scom,&cpt,sizeof(cpt)) < 0){
				perror("read");
				exit(4);
			}
			/* Traitement du message */
			cpt++;
			if(write(scom,&cpt,sizeof(cpt)) == -1){
				perror("write");
				exit(2);
			}
			/* Fermer la communication */
			shutdown(scom,2);
			close(scom);
			exit(0);
		}
	}
	close(sc);

	return EXIT_SUCCESS;
}
