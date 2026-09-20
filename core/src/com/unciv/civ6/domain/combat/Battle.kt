package com.unciv.civ6.domain.combat

/**
 * High-level battle orchestration for Civ6.
 * Distinguishes melee vs ranged, wall handling, and retaliation.
 */

object Battle {

    data class BattleResult(
        val attackerHpAfter: Int,
        val defenderHpAfter: Int,
        val wallHpAfter: Int,
        val attackerKilled: Boolean,
        val defenderKilled: Boolean
    )

    fun meleeAttack(attacker: UnitCombatant, defender: Combatant, terrainModifier: Int = 0, support: Int = 0, flanking: Int = 0): BattleResult {
        val dmg = BattleDamage.calculateDamage(attacker, defender, terrainModifier, support, flanking)
        val defenderAfter = (defender.currentHp - dmg.damageToDefender).coerceAtLeast(0)
        val attackerAfter = (attacker.currentHp - dmg.damageToAttacker).coerceAtLeast(0)
        val wallAfter = if (defender is CityCombatant) (defender.wallHp - dmg.wallDamage).coerceAtLeast(0) else 0
        return BattleResult(attackerAfter, defenderAfter, wallAfter, dmg.attackerKilled, dmg.defenderKilled)
    }

    fun rangedAttack(attacker: UnitCombatant, defender: Combatant, terrainModifier: Int = 0): BattleResult {
        require(attacker.isRanged) { "Attacker must be ranged" }
        // Ranged has no retaliation (except city ranged counter? Simplified no)
        val dmg = BattleDamage.calculateDamage(attacker, defender, terrainModifier, 0, 0)
        // Override: ranged takes no retaliation
        val defenderAfter = (defender.currentHp - (dmg.damageToDefender + dmg.wallDamage)).coerceAtLeast(0) // for ranged vs walls, wall damage still applies but city damage 0
        // Actually for ranged vs walled city: wall absorbs, but need to separate
        val wallAfter = if (defender is CityCombatant) (defender.wallHp - dmg.wallDamage).coerceAtLeast(0) else 0
        // Ranged attacker HP unchanged
        return BattleResult(
            attackerHpAfter = attacker.currentHp,
            defenderHpAfter = if (defender is CityCombatant && defender.wallHp > 0) defender.currentHp else defenderAfter,
            wallHpAfter = wallAfter,
            attackerKilled = false,
            defenderKilled = defenderAfter <= 0
        )
    }

    fun siegeAttack(attacker: UnitCombatant, city: CityCombatant): BattleResult {
        require(attacker.isSiege) { "Attacker must be siege" }
        // Siege deals full damage to walls, no -17 penalty
        val dmg = BattleDamage.calculateDamage(attacker, city, 0, 0, 0)
        val wallAfter = (city.wallHp - dmg.wallDamage).coerceAtLeast(0)
        val cityAfter = (city.currentHp - dmg.damageToDefender).coerceAtLeast(0)
        return BattleResult(attacker.currentHp, cityAfter, wallAfter, false, cityAfter <= 0)
    }
}
