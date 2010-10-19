CC=gcc
CFLAGS=-Wall -ansi -l pthread -Iinclude
bin=./bin
src=./src_C
obj=./obj
lib=./lib
inc=./include

EXEC=$(bin)/threads

all: $(EXEC)

$(bin)/threads: $(obj)/threads.o
	$(CC) -o $@ $^ $(CFLAGS)


$(obj)/threads.o: $(src)/threads.c
	$(CC) -o $@ -c $< $(FLAGS)


clean: 
	rm -f $(bin)/*.o $(EXEC)
	rm -f $(obj)/*.o $(EXEC)
