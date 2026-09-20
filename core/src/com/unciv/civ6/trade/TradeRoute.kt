package com.unciv.civ6.trade

/**
 * Trade Routes Civ6 - Trader builds roads, yields depend on districts.
 */

enum class TradeRouteType { Domestic, International }

data class TradeRoute(
    val fromCity: String,
    val toCity: String,
    val type: TradeRouteType,
    var turnsRemaining: Int = 15,
    var yields: Map<String, Int> = emptyMap()
) {
    fun calculateYields(toCityDistricts: List<String>): Map<String, Int> {
        return if (type == TradeRouteType.Domestic) {
            // Domestic: Food+Production based on districts at destination
            mapOf("Food" to 2 + toCityDistricts.size, "Production" to 1 + toCityDistricts.size)
        } else {
            // International: Gold + Science
            mapOf("Gold" to 5, "Science" to 1)
        }
    }

    fun tick(): Boolean {
        turnsRemaining--
        return turnsRemaining <= 0
    }
}

class TradeManager(private val routes: MutableList<TradeRoute> = mutableListOf()) {
    fun addRoute(route: TradeRoute) { routes.add(route) }
    fun active(): List<TradeRoute> = routes.filter { it.turnsRemaining > 0 }
    fun tickAll(): List<TradeRoute> {
        val completed = routes.filter { it.tick() }
        routes.removeAll(completed)
        return completed
    }
    fun maxRoutes(commercialHubs: Int, harbors: Int): Int = 1 + commercialHubs + harbors
}
