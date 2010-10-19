CC=gcc
CFLAGS=-Wall -ansi
bin=./bin
src=./src_C
obj=./obj
lib=./lib
inc=./include

EXEC=$(bin)/forks

all: $(EXEC)

$(bin)/forks: $(obj)/forks.o
	$(CC) -o $@ $^ $(CFLAGS)


$(obj)/forks.o: $(src)/forks.c
	$(CC) -o $@ -c $< $(CFLAGS)


clean: 
	rm -f $(bin)/*.o $(EXEC)
	rm -f $(obj)/*.o $(EXEC)
