
#
# makefile for svm_light with Tree Kernel
#
# Thorsten Joachims, 2002
#
# Thanks to Ralf Herbrich for the initial version.
#
# Modified by Alessandro Moschitti, 2004 for the tree-kernel compilation
#


CC=gcc                          # used C-compiler
CFLAGS= -g -O3 -Wall                 # release C-Compiler flags
# CFLAGS=   -g -Wall -pedantic  # debugging C-Compiler flags
LD=gcc                          # used linker
LFLAGS=-O3                      # linker flags
LIBS=-L. -lm                    # used libraries


all: svm_learn_hideo svm_classify

tidy: 
	rm -f *.o 
	rm -f donlp2/*.o
	rm -f donlp2_c/*.o
	rm -f pr_loqo/*.o

clean:	tidy
	rm -f svm_learn
	rm -f svm_classify

help:   info

info:
	@echo
	@echo "make for SVM-light with Tree Kernel              prepared by Alessandro Moschitti, 2003 on the version of Thorsten Joachims, 1998"
	@echo
	@echo "Thanks to Ralf Herbrich for the initial version."
	@echo 
	@echo "USAGE: make [svm_learn | svm_learn_loqo | svm_learn_hideo | "
	@echo "             svm_classify | all | clean | tidy]"
	@echo 
	@echo "    svm_learn           builds the learning module (prefers HIDEO)"
	@echo "    svm_learn_hideo     builds the learning module using HIDEO"
	@echo "    svm_learn_loqo      builds the learning module using PR_LOQO"
	@echo "    svm_classify        builds the classfication module"
	@echo "    all (default)       builds svm_learn + svm_classify"
	@echo "    clean               removes .o and target files"
	@echo "    tidy                removes .o files"
	@echo



svm_learn_hideo: svm_learn_main.o similarity.o lsa_matrix.o hashtable.o svm_learn.o svm_common.o tree_manager.o tree_kernel.o svm_hideo.o vec_kernel.o my_ini.o
	$(LD) $(LFLAGS) svm_learn_main.o similarity.o lsa_matrix.o hashtable.o svm_learn.o svm_common.o tree_manager.o tree_kernel.o svm_hideo.o vec_kernel.o my_ini.o -o svm_learn $(LIBS)

svm_learn_loqo: svm_learn_main.o svm_learn.o svm_common.o tree_manager.o tree_kernel.o svm_loqo.o loqo
	$(LD) $(LFLAGS) svm_learn_main.o svm_learn.o svm_common.o tree_manager.o tree_kernel.o svm_loqo.o pr_loqo/pr_loqo.o -o svm_learn $(LIBS)

svm_classify: svm_classify.o similarity.o lsa_matrix.o hashtable.o svm_common.o tree_manager.o tree_kernel.o vec_kernel.o my_ini.o
	$(LD)  $(LFLAGS) svm_classify.o svm_common.o similarity.o lsa_matrix.o hashtable.o tree_kernel.o tree_manager.o vec_kernel.o my_ini.o -o svm_classify $(LIBS)

svm_classify.o: svm_classify.c svm_common.h kernel.h 
	$(CC) -c $(CFLAGS) svm_classify.c -o svm_classify.o

tree_kernel.o: tree_kernel.c similarity.h lsa_matrix.h hashtable.h similarity.c lsa_matrix.c hashtable.c
	$(CC) -c $(CFLAGS) tree_kernel.c -o tree_kernel.o 

similarity.o: similarity.c lsa_matrix.c hashtable.c
	$(CC) -c $(CFLAGS) similarity.c -o similarity.o
	
lsa_matrix.o: lsa_matrix.c 
	$(CC) -c $(CFLAGS) lsa_matrix.c -o lsa_matrix.o

hashtable.o: hashtable.c 
	$(CC) -c $(CFLAGS) hashtable.c -o hashtable.o
	
svm_hideo.o: svm_hideo.c
	$(CC) -c $(CFLAGS) svm_hideo.c -o svm_hideo.o 

svm_loqo.o: svm_loqo.c 
	$(CC) -c $(CFLAGS) svm_loqo.c -o svm_loqo.o 

svm_common.o: svm_common.c svm_common.h kernel.h
	$(CC) -c $(CFLAGS) svm_common.c -o svm_common.o 

tree_manager.o: tree_manager.c svm_common.h kernel.h
	$(CC) -c $(CFLAGS) tree_manager.c -o tree_manager.o 

svm_learn.o: svm_learn.c svm_common.h
	$(CC) -c $(CFLAGS) svm_learn.c -o svm_learn.o 

svm_learn_main.o: svm_learn_main.c svm_learn.h svm_common.h 
	$(CC) -c $(CFLAGS) svm_learn_main.c -o svm_learn_main.o 

vec_kernel.o: vec_kernel.c svm_common.h
	$(CC) -c $(CFLAGS) vec_kernel.c -o vec_kernel.o 

my_ini.o: my_ini.c
	$(CC) -c $(CFLAGS) my_ini.c -o my_ini.o


loqo: pr_loqo/pr_loqo.o

pr_loqo/pr_loqo.o: pr_loqo/pr_loqo.c
	$(CC) -c $(CFLAGS) pr_loqo/pr_loqo.c -o pr_loqo/pr_loqo.o

