#define POSIX_SOURCE 1
#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <unistd.h>


pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER;
pthread_mutex_t mutex_fin = PTHREAD_MUTEX_INITIALIZER;
pthread_cond_t cond_fin = PTHREAD_COND_INITIALIZER;
int nbCons=0;



void* test_pair(void* arg){

	printf("+Pair: Argument reçu %s, tid: %d\n", (char*)arg,(int)pthread_self());
	pthread_mutex_lock(&mutex);
	printf("++Pair: Je consulte, tid: %d\n", (int)pthread_self());
	sleep(4);	
	pthread_mutex_unlock(&mutex);
	printf("+Fin de sieste pour le thread pair pid: %d\n",(int)pthread_self());
	return NULL;
}

void* test_impair(void* arg){

	printf("-Impair: Argument reçu %s, tid: %d\n", (char*)arg,(int)pthread_self());
	sleep(1);
	pthread_mutex_lock(&mutex);
	printf("--Impair: Je consulte, tid: %d\n", (int)pthread_self());
	pthread_mutex_unlock(&mutex);
	sleep(8);
	pthread_mutex_lock(&mutex_fin);
	printf("-- J'envoie un signal, pid: %d\n",(int)pthread_self());
	pthread_cond_signal(&cond_fin);
	pthread_mutex_unlock(&mutex_fin);
	pthread_exit((void*)0);	
	return NULL;
}

void* tempo(void* arg){

	int i,* arg_tps = ((int*)arg);
	printf("...... Recupération arg_tps: %d\n",*arg_tps);
	
	for(i=0;i<15;){
		printf("..... %d\n",i);
		sleep(*arg_tps);
		i=i + *arg_tps;
	}


}


int main(int argc,char* argv[]){

	pthread_t tid[argc],tid_tps;
	pthread_attr_t attr;

	int i,status;
	int nbthreads=0;

	if(argc != 2){
		printf("Mauvais Nombre de Paramètre\n");
		exit(1);
	}
	nbthreads=atoi(argv[1]);

	int temp = 1; 
	if(pthread_create(&tid_tps,NULL,tempo,(void*)&temp) != 0){
		perror("pthread_create\n");
		exit(1);
	}

	
	for(i=0;i<nbthreads;i++){
		if( (i % 2) == 0){ 
			if(pthread_create(&tid[i],NULL,test_pair,"BONJOUR") != 0){
				perror("pthread_create\n");
				exit(1);
			}
		}else{
			if(pthread_create(&tid[i],NULL,test_impair,"HELLO") != 0){
				perror("pthread_create\n");
				exit(1);
			}
		}
	}

	sleep(2);
	printf("Fin de sieste pour le processus principal, pid: %d\n",(int)pthread_self());
	
	pthread_mutex_lock(&mutex_fin);
	printf("En Attente du signal pour continuer, pid: %d\n",(int)pthread_self());
	pthread_cond_wait(&cond_fin,&mutex_fin);
	printf("On a recul le signal d'un des thread impair, pid: %d\n",(int)pthread_self());
	pthread_mutex_unlock(&mutex_fin);

	
	if(pthread_join(tid[0],(void**)&status) != 0){
		perror("pthread_join error\n");
		exit(1);
	}
	printf("fin thread main %d\n",(int)pthread_self());
	return EXIT_SUCCESS;
}
