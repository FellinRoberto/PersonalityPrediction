#include "hashtable.h"

float NaN = 0.0 / 0.0;

char* m_strdup(char *o) {
	int l = strlen(o) + 1;
	char *ns = (char*) malloc(l * sizeof(char));
	strcpy(ns,o);
	if (ns == NULL)
		return NULL;
	else
		return ns;
}

unsigned int hash(hashtable* map, char *s) {
	unsigned int h=0;
	 for(;*s;s++)
	 h=*s+h*31;
	 return h%map->size;
	 /*
	unsigned int h = 0;
		for(;*s;s++)
		h ^= (h << 5) + (h >> 2) + *s;
	return h%map->size;*/
}

hashtable* inithashtab(int _size, int _max_element, double _freshness) {
	if (_freshness < 0.0 || _freshness > 1.0) {
		printf("Freshness factor MUST be between 0 and 1.\n");
		return NULL;
	}

	if (_size < 0) {
		printf("Size MUST be more than 0.\n");
		return NULL;
	}

	if (_max_element < 0) {
		printf("Max Element MUST be more than 0.\n");
		return NULL;
	}

	if (_size < 10) {
		_size = 10;
	}

	if (_max_element < _size) {
		_max_element = _size;
	}

	int i;
	hashtable* map = (hashtable*) malloc(sizeof(hashtable));
	map->size = _size;
	map->hashtab = (node**) malloc(sizeof(node) * map->size);
	map->count = 0;
	map->freshness = _freshness;
	map->max_element = _max_element;
	for (i = 0; i < map->size; i++)
		map->hashtab[i] = NULL;
	return map;
}

node* lookup(hashtable* map, char *n) {
	unsigned int hi = hash(map, n);
	node* np = map->hashtab[hi];
	for (; np != NULL; np = np->next) {
		if (!strcmp(np->name, n))
			return np;
	}
	return NULL;
}

double get(hashtable* map, char* name) {
	node* n = lookup(map, name);
	if (n == NULL)
		return NaN;
	else
		return n->value;
}

/* A pretty useless but good debugging function,
 which simply displays the hashtable in (key.value) pairs
 */
void displaytable(hashtable* map) {
	int i;
	node *t;
	for (i = 0; i < map->size; i++) {
		if (map->hashtab[i] == NULL)
			printf("%i\t[]\n", i);
		else {
			t = map->hashtab[i];
			printf("%i\t[", i);
			for (; t != NULL; t = t->next)
				printf("(%s - %f - %i) ", t->name, t->value, t->id);
			printf("]\n");
		}
	}
	printf("\n");
}

void remove_old_elements(hashtable* map) {
	int new_size = map->max_element * map->freshness;
	int last_index_to_delete = map->max_element - new_size;
	int i;

	//printf("%i %i\n",  new_size, last_index_to_delete);
	printf("refreshing cache\n");
	//return;
	node *np, *prev;

	for (i = 0; i < map->size; i++) {
		np = map->hashtab[i];
		prev = NULL;
		while (np != NULL) {
			//update count 
			if (np->id >= last_index_to_delete) {
				np->id -= last_index_to_delete;
				prev = np;
				np = np->next;
			}//remove element
			else {
				if (np == map->hashtab[i]) {
					map->hashtab[i] = np->next;
					free(np->name);
					free(np);
					np = map->hashtab[i];
				} else {
					prev->next = np->next;
					free(np->name);
					free(np);
					np = prev->next;
				}
				map->count--;
			}
		}
	}
}

int put(hashtable* map, char* name, double value) {
	unsigned int hi;
	node* np;

	if (map->count >= map->max_element) {
		//printf("before\n");
		//displaytable(map);
		remove_old_elements(map);
		//printf("after\n");
		//displaytable(map);
		//printf("*******\n");
	}

	if ((np = lookup(map, name)) == NULL) {
		hi = hash(map, name);
		np = (node*) malloc(sizeof(node));
		np->id = map->count;
		map->count++;
		if (np == NULL)
			return 0;
		np->name = m_strdup(name);
		if (np->name == NULL)
			return 0;
		np->next = map->hashtab[hi];
		map->hashtab[hi] = np;
	}
	np->value = value;

	return 1;
}

int get_element_number(hashtable* map) {
	return map->count;
}

void cleanup_hashtable(hashtable* map) {
	int i;
	node *np, *t;
	for (i = 0; i < map->size; i++) {
		if (map->hashtab[i] != NULL) {
			np = map->hashtab[i];
			while (np != NULL) {
				t = np->next;
				free(np->name);
				//free(np->value);
				free(np);
				np = t;
			}
		}
	}
	map->count = 0;
}

hashtable* read_dictionary(const char* file_name) {
	//printf("inizio read_dictionary\n");
	FILE* file;

	file = fopen(file_name, "r");
	if (!file) {
		perror(file_name);
		exit(-1);
	}
	char *number = (char*) malloc(31);
	char *word = (char*) malloc(1024);
	char separator = '\t';
	char separator2 = ' ';
	char separator3 = '\n';
	char c;
	int index = 0;

	while ((c = getc(file)) != separator2) {
		//printf("carattere letto: %c\n", c);
		number[index] = c;
		index++;

	}
	number[index] = '\0';
	int rows = atoi(number);
	printf("the dictionary has rows:%d\n", rows);
	index = 0;
	hashtable* map = inithashtab(rows*2, rows *2, 0.5);

	int i;

	for (i = 0; i < rows; i++) {
		//printf("inizio for \t");
		while ((c = getc(file)) != separator3) {
			//printf("c=%c\t", c);
		}
		while ((c = getc(file)) != separator) {
			word[index] = c;
			index++;
		}
		word[index] = '\0';
		index = 0;
		double d = (double) i;
		put(map, word, d);

	}
	fclose(file);
	free(word);
	free(number);
	return map;

}

int save_to_file(hashtable* map, char* file_name) {

	node *np;
	FILE * pFile;
	if ((pFile = fopen(file_name, "w")) == NULL) {
		printf("Cannot save cache: error opening file %s", file_name);
		exit(-1);
	}

	int i = 0;
	for (i = 0; i < map->size; i++) {
		if (map->hashtab[i] != NULL) {
			np = map->hashtab[i];
			while (np != NULL) {
				fprintf(pFile, "%s\t%lf\n", np->name, np->value);
				np = np->next;
			}
		}
	}
	fclose(pFile);
	return 0;
}

void load_from_file(hashtable* map, char* file_name) {
	char buf[1024];
	double value;
	FILE *input = fopen(file_name, "r+");
	if (input == NULL) {
		printf("Cannot load cache: error opening file %s", file_name);
		exit(-1);
	} else {
		while (feof(input) == 0) {
			fscanf(input, "%s\t%lf", buf, &value);
			put(map, buf, value);
		}
		fclose(input);
	}
}

//test
/*main(){

 hashtable* map=inithashtab(5000000, 10000000, 0.5);

 char buf[30];

 int i=0;
 int t=0;
 for (i=0; i<1000000; i++) {
 double d=rand();
 sprintf(buf,"word-%f",d	);
 put(map,buf,d);
 //t=getInt(map, "distillation::n");
 //printf("%i\n", t);
 }

 //displaytable(map);

 printf("size:\t%i\n", get_element_number(map));
 //displaytable(map);

 //cleanup(map);
 //printf("size:\t%i\n", get_element_number(map));

 return 0;
 }
 */

