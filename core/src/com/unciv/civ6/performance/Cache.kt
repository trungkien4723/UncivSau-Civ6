package com.unciv.civ6.performance

import java.util.LinkedHashMap

/**
 * Performance caches for Civ6 clean - replaces scattered Civ5 caches.
 * LRU for adjacency, tile, etc.
 */

class LRUCache<K, V>(private val maxSize: Int) : LinkedHashMap<K, V>(maxSize, 0.75f, true) {
    override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>?): Boolean = size > maxSize
}

object Civ6Caches {
    // Adjacency cache: (districtName, coord) -> yield
    val adjacencyCache = LRUCache<String, Float>(1000)
    // City stats cache: cityName -> housing/amenity
    val cityStatsCache = LRUCache<String, Pair<Int, Int>>(500)
    // Pathing cache
    val pathCache = LRUCache<String, List<String>>(2000)

    fun clearAll() {
        adjacencyCache.clear()
        cityStatsCache.clear()
        pathCache.clear()
    }

    fun stats(): String = "Adj:${adjacencyCache.size} City:${cityStatsCache.size} Path:${pathCache.size}"
}

object Pooling {
    // Simple object pooling for HexCoord to reduce GC
    private val pool = ArrayDeque<com.unciv.civ6.domain.city.HexCoord>()

    fun obtain(q: Int, r: Int): com.unciv.civ6.domain.city.HexCoord {
        return if (pool.isNotEmpty()) pool.removeFirst().copy(q = q, r = r) else com.unciv.civ6.domain.city.HexCoord(q, r)
    }

    fun free(coord: com.unciv.civ6.domain.city.HexCoord) {
        if (pool.size < 1000) pool.addLast(coord)
    }
}
