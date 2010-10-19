CC=gcc
CFLAGS=-Wall -ansi
bin=./bin
src=./src_C
obj=./obj
lib=./lib
inc=./include

EXEC=$(bin)/signaux

all: $(EXEC)

$(bin)/signaux: $(obj)/signaux.o
	$(CC) -o $@ $^ $(CFLAGS)


$(obj)/signaux.o: $(src)/signaux.c
	$(CC) -o $@ -c $< $(FLAGS)


clean: 
	rm -f $(bin)/*.o $(EXEC)
	rm -f $(obj)/*.o $(EXEC)
