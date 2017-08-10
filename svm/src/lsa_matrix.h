#ifndef MATRIX_H_
#define MATRIX_H_

#include <stdlib.h>
#include <stdio.h>
#include <math.h>



struct matrix_struct{
	int row_size;
	int column_size;
	double** data;
	double* norms;

};

typedef struct matrix_struct matrix;

double get_cosine_similarity(matrix* matrix, int word_index1, int word_index2);

matrix* load_lsa_file(char* file_name);

void cleanup_matrix(matrix* matrix);

#endif /* MATRIX_H_ */
