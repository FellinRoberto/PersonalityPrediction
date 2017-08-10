/*
 *  hash_table.h
 *  
 *
 *  Created by Danilo Croce on 27/02/11.
 *  Copyright 2011 __MyCompanyName__. All rights reserved.
 *
 */
#ifndef HASHTABLE_H_
#define HASHTABLE_H_

#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <math.h>

typedef struct _node{
	char *name;
	double value;
	int id;
	struct _node *next;
}node;

typedef struct _hast_table{
	node** hashtab;
	int size;
	int count;
	double freshness;
	double max_element;
}hashtable;


hashtable* inithashtab(int _size, int _max_element, double _freshness);

double get(hashtable* map,char* name);

int put(hashtable* map,char* name,double value);

hashtable* read_dictionary(const char* file_name);

int get_element_number(hashtable* map);

void cleanup_hashtable(hashtable* map);

void displaytable(hashtable* map);

int save_to_file(hashtable* map, char* file_name);

void load_from_file(hashtable* map, char* file_name);

#endif
