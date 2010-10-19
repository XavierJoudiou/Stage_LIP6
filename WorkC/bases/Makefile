CC=gcc
CFLAGS=-Wall -ansi
bin=./bin
src=./src_C
obj=./obj
lib=./lib
inc=./include
EXEC=$(bin)/bases

all: $(EXEC)

$(bin)/bases: $(obj)/bases.o
	$(CC) -o $@ $^ $(CFLAGS)

$(obj)/bases.o: $(src)/bases.c
	$(CC) -o $@ -c $< $(CFLAGS)

clean: 
	rm -f $(bin)/*.o $(EXEC)
	rm -f $(obj)/*.o $(EXEC)
