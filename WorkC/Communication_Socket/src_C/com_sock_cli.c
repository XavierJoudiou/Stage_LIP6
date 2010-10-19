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

	struct sockaddr_in dest; /* nom du serveur */
	struct hostent *hp;
	
	int sock;
	int fromlen=sizeof(dest);
	int msg;
	int response;

	if (argc != 2){
		fprintf(stderr,"Usage : %s machine\n",argv[0]);
		exit(1);
	}

	if((sock = socket(AF_INET,SOCK_STREAM,0)) == -1){
		perror("socket");
		exit(1);
	}

	/* Remplir la strucutre Dest */
	if((hp=gethostbyname(argv[1])) == NULL){
		perror("gethostbyname");
		exit(1);
	}

	bzero((char*)&dest,sizeof(dest));
	bcopy(hp->h_addr_list,(char*)&dest.sin_addr,hp->h_length);
	dest.sin_family = AF_INET;
	dest.sin_port = htons(PORTSERV);

	/* Etablir la connexion */
	if(connect(sock,(struct sockaddr *)&dest, sizeof(dest)) == -1){
		perror("connect");
		exit(1);
	}
	msg=10;
	/* Envoyer un message */
	if(write(sock,&msg,sizeof(msg)) == -1){
		perror("write");
		exit(1);
	}	

	/* Reception Reponse */
	if(read(sock,&response,sizeof(response)) == -1){
		perror("read");
		exit(1);
	}

	printf("Reponse est : %d\n",response);
	
	/* Fermer la connexion */
	shutdown(sock,2);
	close(sock);
	


	return EXIT_SUCCESS;
}
