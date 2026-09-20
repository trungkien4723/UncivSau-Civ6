package com.unciv.civ6.diplomacy

/**
 * Diplomacy Civ6 - warmonger to grievances, casus belli.
 */

enum class CasusBelli { SurpriseWar, FormalWar, HolyWar, Liberation, Reconquest, Colonial }

data class Grievance(val from: String, val to: String, var points: Int)

class DiplomacyManager(private val grievances: MutableList<Grievance> = mutableListOf()) {
    fun addGrievance(from: String, to: String, casus: CasusBelli, base: Int = 100) {
        val multiplier = when (casus) {
            CasusBelli.SurpriseWar -> 1.0
            CasusBelli.FormalWar -> 0.6
            CasusBelli.HolyWar -> 0.4
            CasusBelli.Liberation -> 0.0
            else -> 0.5
        }
        grievances.add(Grievance(from, to, (base * multiplier).toInt()))
    }

    fun totalGrievance(player: String): Int = grievances.filter { it.to == player }.sumOf { it.points }
    fun decay() { grievances.forEach { it.points = (it.points * 0.9).toInt() } }
}
