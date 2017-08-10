/*
 *  similarity.c
 *  
 *
 *  Created by Danilo Croce on 27/02/11.
 *  Copyright 2011 __MyCompanyName__. All rights reserved.
 *
 */

#include "similarity.h"

int sim_verbosity = 0;

similarity* init_similarity(int _cache_size, int _cache_max_element,
		double _cache_freshness, int _verbosity) {

	sim_verbosity = _verbosity;

	similarity* sim = (similarity*) malloc(sizeof(similarity));

	sim->cache_size = _cache_size;

	sim->cache_max_element = _cache_max_element;

	sim->cache_freshness = _cache_freshness;

	sim->matrix_is_loaded = 0;

	sim->cache_is_loaded = 0;

	if (_cache_size > 0)
		sim->cache = inithashtab(_cache_size, _cache_max_element,
				_cache_freshness);

	return sim;
}

similarity* init_similarity_no_chache(int _verbosity) {
	return init_similarity(0, 0, 0.0, _verbosity);
}

void cleanup_similarity(similarity* sim) {

	cleanup_hashtable(sim->dictionary);
	cleanup_matrix(sim->matrix);

	if (sim->cache_size > 0)
		cleanup_hashtable(sim->cache);

	return;
}

int check_pos(char* w1, char* w2) {

	char* ptr1 = w1;
	char* ptr2 = w2;
	while (*ptr1 != '\0') {
		if (*ptr1 == ':')
			break;
		ptr1++;
	}
	if (*ptr1 != '\0' && *(ptr1 + 1) != '\0' && *(ptr1 + 1) == ':' && *(ptr1
			+ 2) != '\0') {
		ptr1 = ptr1 + 2;
		//printf("%s %s\n", w1, ptr1);
	} else {
		return 0;
	}

	while (*ptr2 != '\0') {
		if (*ptr2 == ':')
			break;
		ptr2++;
	}
	if (*ptr2 != '\0' && *(ptr2 + 1) != '\0' && *(ptr2 + 1) == ':' && *(ptr2
			+ 2) != '\0') {
		ptr2 = ptr2 + 2;
		//	printf("%s %s\n", w2, ptr2);
	} else {
		return 0;
	}

	if (strcmp(ptr1, ptr2) == 0)
		return 1;

	return 0;
}

char key[1024];
double get_similarity(similarity * sim, char * w1, char *w2) {
	if (sim_verbosity >= 5) {
		printf("\n+++++++++++++++++++++++++++++++++++++\n");
		printf("Evaluate similarity between %s and %s\n", w1, w2);
	}

	double sim_score = 0;
	int c;

	//printf("HAD %s %s\n", w1, w2);

	c = strcmp(w1, w2);

	if (c == 0) {
		if (sim_verbosity >= 5) {
			printf("WORDS ARE THE SAME: SIM = 1.0\n");
			printf("+++++++++++++++++++++++++++++++++++++\n\n");
		}
		//printf("DONE %s %s\n\n", w1, w2);
		return 1.0;
	}

	//printf("DONE %s %s\n\n", w1, w2);

	if (sim->matrix_is_loaded || sim->cache_is_loaded)
		if (check_pos(w1, w2) != 1) {
			return 0;
		}

	if (sim->cache_is_loaded) {

		if (c < 0) {
			int i = 0, j = 0;
			for (; w1[i] != 0; i++) {
				key[i] = w1[i];
			}
			key[i++] = '#';
			for (; w2[j] != 0; j++, i++) {
				key[i] = w2[j];
			}
			key[i] = 0;
			//printf("%s\n", key);
			//strcpy(key, w1);
			//strcat(key, "#");
			//strcat(key, w2);
			//sprintf(key,"%s#%s",w1, w2);
		} else {
			int i = 0, j = 0;
			for (; w2[i] != 0; i++) {
				key[i] = w2[i];
			}
			key[i++] = '#';
			for (; w1[j] != 0; j++, i++) {
				key[i] = w1[j];
			}
			key[i] = 0;
			//printf("%s\n", key);
			//strcpy(key, w2);
			//strcat(key, "#");
			//strcat(key, w1);
			//sprintf(key,"%s#%s",w2, w1);
		}

		sim_score = get(sim->cache, key);
		if (!isnan(sim_score)) {
			if (sim_verbosity >= 5) {
				printf("PAIR FOUND IN CACHE (%s) %lf\n", key, sim_score);
			}
			return sim_score;
		} else if (sim_verbosity >= 5) {
			printf("PAIR NOT FOUND IN CACHE (%s)\n", key);
		}
		sim_score = 0;
	}

	if (sim->matrix_is_loaded) {
		int not_found_words = 0;
		double w_index1 = get(sim->dictionary, w1);
		if (isnan(w_index1)) {
			if (sim_verbosity >= 5) {
				printf("I CANNOT FOUND %s IN THE SPACE\n", w1);
			}
			not_found_words = 1;
		}

		double w_index2 = 0;
		if (not_found_words == 0) {
			w_index2 = get(sim->dictionary, w2);
			if (isnan(w_index2)) {
				if (sim_verbosity >= 5) {
					printf("I CANNOT FOUND %s IN THE SPACE\n", w2);
				}
				not_found_words = 1;
			}
		}

		if (not_found_words == 0) {
			sim_score = get_cosine_similarity(sim->matrix, w_index1, w_index2);
			if (sim_verbosity >= 5) {
				printf("ESTIMATING SIMILARITY OF %s:\t%lf\n", key, sim_score);
			}
		}

		if (not_found_words)
			sim_score = -1;

		if (sim->cache_size > 0) {
			put(sim->cache, key, sim_score);

			if (sim_verbosity >= 5) {
				printf("INSERTING %s %lf IN THE CACHE\n", key, sim_score);
			}
		}

		if (sim_verbosity >= 5) {
			printf("+++++++++++++++++++++++++++++++++++++\n\n");
		}
	}
	return sim_score;

}

void load_cache_from_file(similarity* sim, char* file_name) {
	sim->cache_is_loaded = 1;
	load_from_file(sim->cache, file_name);
	printf("Loaded %i pairs in cache\n", sim->cache->count);
}

void load_matrix_from_file(similarity* sim, char* file_name) {
	sim->matrix_is_loaded = 1;
	sim->dictionary = read_dictionary(file_name);
	sim->matrix = load_lsa_file(file_name);
}

void save_sim_cache(similarity* sim, char* file_name) {
	save_to_file(sim->cache, file_name);
}

/*int test() {

 char
 * file_name =
 "/Users/danilo/uni/stage/colorado/word_space/uwac_all_cut200_posFirstLetter_20kBesSplitted_win2_lsa250.txt";

 similarity* sim = init_similarity(file_name, 20, 200, 0.5, 0);

 load_cache(sim, "tmp.txt");
 displaytable(sim->cache);
 printf("seeusoon\n");
 return 0;

 char* word1 = "dog::n";
 char* word2 = "gerbil::n";

 float sim_score;

 int i = 0;
 for (i = 0; i < 1600; i++) {

 word1 = "dog::n";
 word2 = "gerbil::v";

 sim_score = get_similarity(sim, word1, word2);

 word2 = "cat::n";
 sim_score = get_similarity(sim, word1, word2);

 word1 = "run::v";
 word2 = "jump::v";
 sim_score = get_similarity(sim, word1, word2);

 sim_score = get_similarity(sim, word1, word2);
 }

 displaytable(sim->cache);
 save_to_file(sim->cache, "tmp.txt");

 //printf("sim between %s and %s:\t%f\n", word1, word2, sim_score);

 //cleanup_similarity(sim);
 return 0;
 }*/

