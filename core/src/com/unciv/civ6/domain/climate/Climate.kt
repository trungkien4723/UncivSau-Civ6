package com.unciv.civ6.domain.climate

/**
 * Gathering Storm climate - CO2, disasters, sea level.
 */

enum class DisasterType { Volcano, Flood, Hurricane, Drought, Blizzard }
enum class PowerType { Coal, Oil, Uranium, Hydro, Wind, Solar, Geothermal }

data class PowerPlant(
    val cityName: String,
    val type: PowerType,
    val co2PerTurn: Float = when (type) {
        PowerType.Coal -> 3.28f
        PowerType.Oil -> 1.96f
        PowerType.Uranium -> 0.768f
        else -> 0f
    }
)

class ClimateManager(
    var co2: Float = 0f,
    var seaLevelStage: Int = 0, // 0-7
    private val plants: MutableList<PowerPlant> = mutableListOf()
) {
    fun addPlant(plant: PowerPlant) { plants.add(plant) }
    fun tickTurn() {
        val turnCO2 = plants.sumOf { it.co2PerTurn.toDouble() }.toFloat()
        co2 += turnCO2
        seaLevelStage = (co2 / 1000).toInt().coerceIn(0, 7)
    }

    fun disasterChance(disaster: DisasterType): Float {
        return when (disaster) {
            DisasterType.Flood -> 0.05f + co2 / 5000
            DisasterType.Volcano -> 0.03f
            DisasterType.Hurricane -> 0.04f + co2 / 4000
            else -> 0.02f
        }
    }

    fun canBuildFloodBarrier(cityCoastal: Boolean): Boolean = seaLevelStage >= 1 && cityCoastal

    fun carbonRecapture(): Float {
        val removed = 50000f
        co2 = (co2 - removed).coerceAtLeast(0f)
        return removed
    }
}
