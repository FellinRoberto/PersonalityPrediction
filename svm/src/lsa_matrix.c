#include "lsa_matrix.h"

double get_cosine_similarity(matrix* matrix, int word_index1, int word_index2){

	double* a=matrix->data[word_index1];
	double* b=matrix->data[word_index2];
	int i=0;
	double res=0;
	for(i=0; i<matrix->column_size; i++){
		res+=a[i]*b[i];
	}
	return res/(matrix->norms[word_index1]*matrix->norms[word_index2]);
}


matrix* initialize_matrix(int _row_size, int _column_size){
	matrix* _matrix = (matrix *)malloc(sizeof(matrix));
	
	_matrix->row_size=_row_size;
	_matrix->column_size=_column_size;
	
	_matrix->data=(double**)malloc(sizeof(double*)*_row_size);
	int i=0;
	for(i=0; i<_row_size; i++)
		_matrix->data[i]=(double*)malloc(sizeof(double)*_column_size);
		
	_matrix->norms=(double*)malloc(sizeof(double)*_row_size);
		
	return _matrix;
}


void cleanup_matrix(matrix* matrix){
	int i=0;
	for(i=0; i<matrix->row_size; i++)
		free(matrix->data[i]);
	
	free(matrix->data);
	free(matrix->norms);
	free(matrix);
}

matrix* load_lsa_file(char* file_name){
		char *number=(char*)malloc(31);
		char separator=' ';
		char separator2=',';
		char separator3='\t';
		char c;
		int index=0;
		int i,j;
		
		matrix* matrix;
		
		FILE* file=fopen(file_name,"r");
	
		while((c=getc(file))!=separator){
			//printf("carattere letto: %c\n", c);
			number[index]=c;
			index++;
			
		}	
		number[index]='\0';
		int rows=atoi(number);
		index=0;
		while((c=getc(file))!='\n'){
			//printf("carattere letto: %c\n", c);
			number[index]=c;
			index++;
			
		}	
		number[index]='\0';
		int cols=atoi(number);
		index=0;

		printf("COSTRUISCO UNA MATRICE DENSA DI %d RIGHE E %d COLONNE\n", rows, cols);
		matrix=initialize_matrix(rows, cols);
		double f;
		for(i=0; i<rows; i++){
			
			for(j=0; j<3;j++){
				while((c=getc(file))!=separator && c!=separator3){
					//cout<<c;
				}
			}
			//cout<<endl;
			matrix->norms[i]=0;
			for(j=0; j<cols; j++){
				
				while((c=getc(file))!='\n' && c!=separator2 && c!=EOF){
					//printf("carattere letto: %c\n", c);
					number[index]=c;
					index++;
				}	
				number[index]='\0';
					f=atof(number);
				index=0;
				matrix->data[i][j]=f;
				matrix->norms[i]+=f*f;
			}
			matrix->norms[i]=sqrt(matrix->norms[i]);
		}
		free(number);
		return matrix;
	}


