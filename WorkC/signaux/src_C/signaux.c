#define POSIX_SOURCE 1
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <signal.h>


void handler(int sig){
	
	printf("Traitement du signal SIGINT\n");

}

int main(int argc, char* argv[]){

	sigset_t sig_proc;
	struct sigaction action;

	sigemptyset(&sig_proc);

	action.sa_mask=sig_proc;
	action.sa_flags=0;
	action.sa_handler=handler;

	sigaction(SIGINT,&action,NULL);

	/* MASQUER SIGINT*/
	sigaddset(&sig_proc,SIGINT);
	sigprocmask(SIG_SETMASK,&sig_proc,NULL);

	sigfillset(&sig_proc);
	sigdelset(&sig_proc,SIGINT);
	sigsuspend(&sig_proc);


	return EXIT_SUCCESS;
}
