/***************************************************************************//**
 * @file hashmap.c
 * @author Dorian Weber
 * @brief Implementation of the hashmap.
 ******************************************************************************/

#include "sk_hashmap.h"

/* *************************************************************** structures */


/**@brief Structure of the hashmap.
 */
//struct sHashmap {
	//hashmapEntry* array; /**<@brief Array containing data/key tuples. */
	//size_t size, /**<@brief Total size of the array (size-1 actually). */
	//count; /**<@brief Number of items already saved. */
//};

/* ******************************************************** private functions */

/**@brief Doubles the size of the hashmap and re-inserts all old elements.
 */
static void rehash(hashmap* map);

/**@brief Ensures that a given element is present in the hashmap without copying
 * the key.
 * 
 * @return \c HASHMAP_INSERT, if the hashmaps count changed \n
 *         \c HASHMAP_UPDATE, if the element got updated
 */
static int insert(hashmap* map, void* data, char* key);

/**@brief Searches for the hashmap entry using the key and returns the first
 * free index if the entry could not be found.
 */
static hashmapEntry* find(const hashmap* map, const char* key);

/**@brief Compares two hashmap entries lexicographically according to their
 * keys, if they exist.
 */
static int compare(const hashmapEntry* lhs, const hashmapEntry* rhs);

/**@brief Computes the SDBM 32-Bit hashcode. (Sleepycats Berkeley DataBase)
 */
static unsigned long hash1(const char* rawKey) {
	const unsigned char* s = (const unsigned char*) rawKey;
	unsigned long hash = 0;

	do {
		hash = *s + (hash << 6) + (hash << 16) - hash;
	} while (*++s);

	return hash;
}

/**@brief Computes the FNV 32-Bit hashcode. (Fowler/Noll/Vo)
 */
static unsigned long hash2(const char* rawKey) {
	const unsigned char* s = (const unsigned char*) rawKey;
	unsigned long hash = 0;

	do {
		hash ^= (unsigned long) *s;
		hash += (hash << 1) + (hash << 4) + (hash << 7) + (hash << 8) + (hash
				<< 24);
	} while (*++s);

	return hash;
}

static void rehash(hashmap* map) {
	size_t size;
	hashmapEntry* array = map->array;

	/* double the size of the array */
	size = ++map->size;
	map->size <<= 1;
	map->array = (hashmapEntry*) calloc(sizeof(hashmapEntry), map->size);
	--map->size;
	map->count = 0;

	/* re-insert all elements */
	do {
		--size;
		if (array[size].key)
			insert(map, array[size].data, array[size].key);
	} while (size);

	/* return unused memory */
	free(array);
}

static hashmapEntry* find(const hashmap* map, const char* key) {
	unsigned long index, step, initialIndex;
	hashmapEntry* freeEntry = 0;

	initialIndex = index = hash1(key) & map->size;

	/* first try */
	if (map->array[index].key) {
		if (!strcmp(map->array[index].key, key))
			return &map->array[index];
	} else if (!map->array[index].data) {
		return &map->array[index];
	} else {
		freeEntry = &map->array[index];
	}

	/* collision */
	step = (hash2(key) % map->size) + 1;

	do {
		index = (index + step) & map->size;

		if (map->array[index].key) {
			if (!strcmp(map->array[index].key, key))
				return &map->array[index];
		} else if (!map->array[index].data) {
			return (freeEntry) ? freeEntry : &map->array[index];
		} else if (!freeEntry) {
			freeEntry = &map->array[index];
		}
	} while (index != initialIndex);

	return freeEntry;
}

static int insert(hashmap* map, void* data, char* key) {
	hashmapEntry* entry;

	if (map->size == map->count)
		rehash(map);

	do {
		entry = find(map, key);

		if (entry) {
			entry->data = data;

			if (entry->key) {
				/* updated the entry */
				free(key);
				return HASHMAP_UPDATE;
			} else {
				/* inserted the entry */
				++map->count;
				entry->key = key;
				return HASHMAP_INSERT;
			}
		}

		rehash(map);
	} while (1);
}

static int compare(const hashmapEntry* lhs, const hashmapEntry* rhs) {
	return (lhs->key) ? (rhs->key) ? strcmp(lhs->key, rhs->key) : -1
			: (rhs->key) ? 1 : 0;
}

/* ******************************************************* exported functions */

int get_count(const hashmap* map) {
	return map->count;
}

hashmap* newHashmap(unsigned int hint) {
	hashmap* map = (hashmap*) malloc(sizeof(hashmap));

	if (hint < 4) {
		hint = 4;
	} else if (hint & (hint - 1)) {
		unsigned int i = 1;

		do {
			hint |= (hint >> i);
			i <<= 1;
		} while (i <= (sizeof(hint) << 2));
		++hint;
	}

	map->array = (hashmapEntry*) calloc(sizeof(hashmapEntry), hint);
	map->size = hint - 1;
	map->count = 0;

	return map;
}

void deleteHashmap(hashmap* map) {
	unsigned long index = 0;

	assert(map);

	do {
		if (map->array[index].key)
			free(map->array[index].key);
	} while (++index <= map->size);

	free(map->array);
	free(map);
}

int hashmapSet(hashmap* map, void* data, const char* key) {
	return (map && key && *key) ? insert(map, data, strdup(key))
			: HASHMAP_ILLEGAL;
}

int hashmapGetInt(const hashmap* map, const char* key) {
	void* res = hashmapGet(map, key);
	if (res)
		return *((int*) res);
	else
		return -1;

}

void* hashmapGet(const hashmap* map, const char* key) {
	hashmapEntry* entry;
	assert(map && key && *key);

	entry = find(map, key);

	if (entry && entry->key)
		return entry->data;

	return 0;
}

void* hashmapRemove(hashmap* map, const char* key) {
	void* res = 0;
	hashmapEntry* entry;

	assert(map && key && *key);

	entry = find(map, key);

	if (entry && entry->key) {
		--map->count;

		free(entry->key);
		entry->key = 0;
		res = entry->data;

		/* setting exist to one indicates that this entry was already in use */
		entry->data = (void*) 1;
	}

	return res;
}

void hashmapProcess(const hashmap* map, fHashmapProc proc) {
	hashmapEntry* array;
	size_t size;
	int i;

	assert(map);

	size = map->size + 1;
	array = (hashmapEntry*) malloc(sizeof(hashmapEntry) * size);

	memcpy(array, map->array, sizeof(hashmapEntry) * size);
	qsort(array, size, sizeof(hashmapEntry),
			(int(*)(const void*, const void*)) compare);

	for (i = 0; i < map->count; ++i)
		proc(array[i].key, array[i].data);

	free(array);
}

char** get_reverse_keys(hashmap* hashmap) {

	//printf("aaa\n");

	int size = get_count(hashmap);

	//printf("count %i\n", size);

	hashmapEntry* array_tmp = hashmap->array;


	char** reverse_keys = (char**) malloc(sizeof(char*) * size);
	int i = 0;
	int* temp_int_ptr;
	char* temp_char_ptr;
	int temp_size;

	for (i = 0; i < hashmap->size; i++) {


		temp_int_ptr=(int*)array_tmp[i].data;
		//printf("%i\n", temp_int_ptr);

		if(!temp_int_ptr)
			continue;
		temp_char_ptr=array_tmp[i].key;
		temp_size=strlen(temp_char_ptr);


		reverse_keys[*temp_int_ptr] = (char*) malloc(sizeof(char) * (temp_size + 1));
		strcpy(reverse_keys[*temp_int_ptr], temp_char_ptr);
	}

	return reverse_keys;
}

