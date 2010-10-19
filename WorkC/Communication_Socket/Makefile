CC=gcc
CFLAGS=-Wall -ansi -O -lrt -l pthread -Iinclude
bin=./bin
src=./src_C
obj=./obj
lib=./lib
inc=./include


all: com_sock_cli com_sock_ser

com_sock_cli: $(bin)/com_sock_cli

com_sock_ser: $(bin)/com_sock_ser

$(obj)/%.o: $(src)/%.c
	$(CC) -o $@ -c $< $(FLAGS)


$(bin)/com_sock_cli : $(src)/com_sock_cli.c
	$(CC) -o $@ $^ $(CFLAGS)

$(bin)/com_sock_ser : $(src)/com_sock_ser.c
	$(CC) -o $@ $^ $(CFLAGS)



$(bin)/% : $(src)/%.c
	$(CC) -o $@ $^ $(CFLAGS)

clean: 
	rm -f $(bin)/*.o $(EXEC)
	rm -f $(obj)/*.o $(EXEC)
