/*
 *  similarity.h
 *  
 *
 *  Created by Danilo Croce on 27/02/11.
 *  Copyright 2011 __MyCompanyName__. All rights reserved.
 *
 */
#ifndef SIMILARITY_H_
#define SIMILARITY_H_


#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include "lsa_matrix.h"
#include "hashtable.h"


typedef struct _similarity_struct{
	hashtable* dictionary;
	matrix* matrix;
	hashtable* cache;
	int cache_size;
	int cache_is_loaded;
	int matrix_is_loaded;
	int cache_max_element;
	double cache_freshness;
}similarity;


similarity* init_similarity(int _cache_size, int _cache_max_element, double _cache_freshness, int _verbosity);

double get_similarity(similarity * sim, char * w1, char *w2);

void load_cache_from_file(similarity* sim, char* file_name);

void load_matrix_from_file(similarity* sim, char* file_name);

void save_sim_cache(similarity* sim, char* file_name);

#endif

