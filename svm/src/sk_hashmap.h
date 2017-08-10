/***************************************************************************//**
 * @file hashmap.h
 * @author Dorian Weber
 * @brief Contains the interface specification of a hashmap.
 * @sa hashmap.c
 ******************************************************************************/
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <assert.h>

#ifndef HASHMAP_H_INCLUDED
#define HASHMAP_H_INCLUDED




/**@brief Structure of a hashmap entry.
 */
typedef struct {
	char* key; /**<@brief Data key. */
	void* data; /**<@brief Data pointer. */
} hashmapEntry;


/* forward declaration of opaque structure */
//typedef struct sHashmap hashmap;
/**@brief Structure of the hashmap.
 */
typedef struct {
	hashmapEntry* array; /**<@brief Array containing data/key tuples. */
	size_t size, /**<@brief Total size of the array (size-1 actually). */
	count; /**<@brief Number of items already saved. */
}hashmap;

#define HASHMAP_ILLEGAL 0   /**<@brief Flags an illegal access. */
#define HASHMAP_INSERT 1    /**<@brief New element inserted - flag. */
#define HASHMAP_UPDATE 2    /**<@brief Update of existing element. */

int get_count(const hashmap* map);
char** get_reverse_keys(hashmap* hashmap);

/**@brief Prototype of a function that processes an item.
 * @param[in] key    the key
 * @param[in] datum  the associated datum
 */
typedef void(*fHashmapProc)(const char* key, const void* datum);

/**@brief Creates a new hashmap.
 * @note The hashmap resizes itself automatically.
 * 
 * @param[in] hint  indicates the estimated number of entries
 * 
 * @return pointer to a hashmap
 */
extern hashmap* newHashmap(unsigned int hint);

/**@brief Destroys the hashmap without touching saved data.
 * 
 * @param[in] map   target hashmap
 */
extern void deleteHashmap(hashmap* map);

/**@brief Inserts a new element into the map or updates an existing one.
 * @pre \p key is not \c NULL or empty.
 * 
 * @param[in] map   target hashmap
 * @param[in] data  pointer to the data
 * @param[in] key   the key
 * 
 * @return \c HASHMAP_ILLEGAL, if the key is invalid (\c NULL or empty) \n
 *         \c HASHMAP_INSERT, if the element was inserted \n
 *         \c HASHMAP_UPDATE, if the element got updated (overwritten)
 */
extern int hashmapSet(hashmap* map, void* data, const char* key);

int hashmapGetInt(const hashmap* map, const char* key);

/**@brief Returns the element associated with a given key.
 * 
 * @param[in] map   target hashmap
 * @param[in] key   the key
 * 
 * @return the element associated with a \p key, if existing, \n
 *         \c NULL otherwise
 */
extern void* hashmapGet(const hashmap* map, const char* key);

/**@brief Removes the element associated with a given key from the map and
 * returns it.
 * 
 * @param[in] map   target hashmap
 * @param[in] key   the key
 * 
 * @return the element associated with a \p key, if existing, \n
 *         \c NULL otherwise
 */
extern void* hashmapRemove(hashmap* map, const char* key);

/**@brief Processes all elements of the map in lexicographical order of their
 * keys.
 * @pre \p proc is not \c NULL
 * 
 * @param[in] map   target hashmap
 * @param[in] proc  function that processes an item
 */
extern void hashmapProcess(const hashmap* map, fHashmapProc proc);

#endif
