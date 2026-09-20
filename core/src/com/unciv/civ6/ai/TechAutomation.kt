package com.unciv.civ6.ai

import com.unciv.civ6.domain.tech.Civic
import com.unciv.civ6.domain.tech.CivicManager
import com.unciv.civ6.domain.tech.Tech
import com.unciv.civ6.domain.tech.TechManager

/**
 * Research automation - picks cheapest available tech/civic that unlocks needed district.
 */

object TechAutomation {
    fun chooseTech(techManager: TechManager, techs: Map<String, Tech>, completed: Set<String>): String? {
        val available = techs.filter { (name, _) -> techManager.canResearch(name, completed) }
        // Prefer lowest cost that unlocks district
        return available.minByOrNull { it.value.cost }?.key
    }

    fun chooseCivic(civicManager: CivicManager, civics: Map<String, Civic>, completed: Set<String>): String? {
        val available = civics.filter { (name, _) -> civicManager.canResearch(name, completed) }
        return available.minByOrNull { it.value.cost }?.key
    }

    fun shouldTriggerEureka(techName: String, progress: Int, cost: Int): Boolean {
        // If boosted would complete faster, trigger when progress < 50%
        return progress < cost / 2
    }
}
