#include <stdio.h>
#include <stdlib.h>
#include <ctype.h>
#include <string.h>

#define DIM_BUF_MAX 256
#define DIM_BUF_MIN 32

//imposta il buffer con il valore di una variabile della sezione del file
void getValue(FILE * , char * , char * , char * );

//restituisce il numero di sezioni 'nameVec' nel file
int getNumVector(FILE * , char * );
