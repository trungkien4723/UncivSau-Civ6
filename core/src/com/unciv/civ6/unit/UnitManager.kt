package com.unciv.civ6.unit

/**
 * Unit management for Civ6v2 - upgrade chains per combat class (fix 4.26.3)
 */

enum class UnitCombatClass { Melee, Ranged, Cavalry, AntiCavalry, Siege, Naval, Air }

data class UnitInfo(
    val name: String,
    val combatClass: UnitCombatClass,
    val cost: Int,
    val strength: Int,
    val movement: Int,
    val requiredTech: String? = null,
    val upgradesTo: String? = null
)

class UnitManager(private val units: Map<String, UnitInfo> = mapOf(
    "Warrior" to UnitInfo("Warrior", UnitCombatClass.Melee, 40, 20, 2, upgradesTo = "Swordsman"),
    "Swordsman" to UnitInfo("Swordsman", UnitCombatClass.Melee, 90, 36, 2, requiredTech = "Iron Working", upgradesTo = "Musketman"),
    "Archer" to UnitInfo("Archer", UnitCombatClass.Ranged, 60, 15, 2, upgradesTo = "Crossbowman"),
    "Heavy Chariot" to UnitInfo("Heavy Chariot", UnitCombatClass.Cavalry, 65, 28, 2) // fixed 4.26.9 movement 2 cost 65
)) {
    fun canUpgrade(from: String): String? = units[from]?.upgradesTo
    fun upgradeCost(from: String, gold: Int): Int {
        val fromUnit = units[from] ?: return -1
        val toUnit = units[fromUnit.upgradesTo] ?: return -1
        return (toUnit.cost - fromUnit.cost) * 2 // simplified gold cost
    }
    fun all(): List<UnitInfo> = units.values.toList()
}
