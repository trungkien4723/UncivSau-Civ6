package com.unciv.civ6.barbarian

import com.unciv.civ6.domain.city.HexCoord
import com.unciv.civ6.map.TileMapV2

/**
 * Barbarians Civ6 - camps spawn scouts, convert Settler to Builder.
 */

data class BarbCamp(val coord: HexCoord, var turnsAlive: Int = 0)

class BarbarianManager(private val camps: MutableList<BarbCamp> = mutableListOf()) {
    fun spawnCamp(coord: HexCoord) { camps.add(BarbCamp(coord)) }
    fun tick(map: TileMapV2): List<HexCoord> {
        val spawned = mutableListOf<HexCoord>()
        for (camp in camps) {
            camp.turnsAlive++
            if (camp.turnsAlive % 5 == 0) {
                // Spawn unit near camp
                spawned.add(camp.coord)
            }
        }
        return spawned
    }
    fun captureSettler(): String = "Builder" // Barb converts Settler to Builder (Civ6 fix 4.26.4)
    fun allCamps(): List<BarbCamp> = camps
}
