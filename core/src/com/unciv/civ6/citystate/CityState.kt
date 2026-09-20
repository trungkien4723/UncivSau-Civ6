package com.unciv.civ6.citystate

/**
 * City-States & Envoys Civ6 - replaces Civ5 city-state logic.
 */

enum class CityStateType { Scientific, Industrial, Trade, Cultural, Religious, Militaristic }

data class CityState(
    val name: String,
    val type: CityStateType,
    val bonus: String,
    var envoys: MutableMap<String, Int> = mutableMapOf(), // playerId -> count
    var suzerain: String? = null
) {
    fun addEnvoy(playerId: String) {
        envoys[playerId] = (envoys[playerId] ?: 0) + 1
        updateSuzerain()
    }

    private fun updateSuzerain() {
        val max = envoys.maxByOrNull { it.value }?.value ?: 0
        val leaders = envoys.filter { it.value == max }.keys
        suzerain = if (leaders.size == 1) leaders.first() else suzerain // tie keeps current
    }

    fun isSuzerain(playerId: String): Boolean = suzerain == playerId
    fun bonusLevel(playerId: String): Int {
        val count = envoys[playerId] ?: 0
        return when {
            count >= 6 -> 3
            count >= 3 -> 2
            count >= 1 -> 1
            else -> 0
        }
    }
}

class CityStateManager(private val states: MutableList<CityState> = mutableListOf()) {
    fun add(state: CityState) { states.add(state) }
    fun all(): List<CityState> = states
    fun suzerainsOf(playerId: String): List<CityState> = states.filter { it.suzerain == playerId }
}
