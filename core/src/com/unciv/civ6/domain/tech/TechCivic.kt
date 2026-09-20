package com.unciv.civ6.domain.tech

/**
 * Civ6: 2 cây song song - Technology (Science) và Civic (Culture).
 * Mỗi tech/civic có cost, prerequisites, eureka/inspiration boost 50%.
 */

data class Tech(
    val name: String,
    val cost: Int,
    val column: Int, // era column
    val era: String,
    val prerequisites: List<String> = emptyList(),
    val eurekaCondition: String? = null, // e.g. "Kill 3 Barbarians" for Bronze Working
    val unlocks: List<String> = emptyList() // units, districts, buildings
)

data class Civic(
    val name: String,
    val cost: Int,
    val column: Int,
    val era: String,
    val prerequisites: List<String> = emptyList(),
    val inspirationCondition: String? = null, // e.g. "Meet 3 City-States"
    val unlocksGovernment: String? = null,
    val unlocksPolicyCards: List<String> = emptyList(),
    val unlocksGovernor: String? = null
)

enum class ResearchState { NotStarted, InProgress, Boosted, Completed }

data class ResearchProgress(
    val techName: String,
    var progress: Int = 0,
    var isBoosted: Boolean = false,
    var state: ResearchState = ResearchState.NotStarted
)

class TechManager(
    private val techs: Map<String, Tech>,
    private val progressMap: MutableMap<String, ResearchProgress> = mutableMapOf()
) {
    fun canResearch(techName: String, completed: Set<String>): Boolean {
        val tech = techs[techName] ?: return false
        return tech.prerequisites.all { it in completed }
    }

    fun addProgress(techName: String, science: Int) {
        val p = progressMap.getOrPut(techName) { ResearchProgress(techName) }
        p.progress += science
        p.state = ResearchState.InProgress
        val tech = techs[techName] ?: return
        if (p.progress >= tech.cost) p.state = ResearchState.Completed
    }

    fun triggerEureka(techName: String) {
        val p = progressMap.getOrPut(techName) { ResearchProgress(techName) }
        if (p.isBoosted || p.state == ResearchState.Completed) return
        val tech = techs[techName] ?: return
        p.progress += tech.cost / 2 // Civ6 boost = 50% cost
        p.isBoosted = true
        if (p.state == ResearchState.NotStarted) p.state = ResearchState.Boosted
    }

    fun isCompleted(techName: String): Boolean = progressMap[techName]?.state == ResearchState.Completed
    fun progress(techName: String): Int = progressMap[techName]?.progress ?: 0
}

class CivicManager(
    private val civics: Map<String, Civic>,
    private val progressMap: MutableMap<String, ResearchProgress> = mutableMapOf()
) {
    fun canResearch(civicName: String, completed: Set<String>): Boolean {
        val civic = civics[civicName] ?: return false
        return civic.prerequisites.all { it in completed }
    }

    fun addProgress(civicName: String, culture: Int) {
        val p = progressMap.getOrPut(civicName) { ResearchProgress(civicName) }
        p.progress += culture
        p.state = ResearchState.InProgress
        val civic = civics[civicName] ?: return
        if (p.progress >= civic.cost) p.state = ResearchState.Completed
    }

    fun triggerInspiration(civicName: String) {
        val p = progressMap.getOrPut(civicName) { ResearchProgress(civicName) }
        if (p.isBoosted || p.state == ResearchState.Completed) return
        val civic = civics[civicName] ?: return
        p.progress += civic.cost / 2
        p.isBoosted = true
        if (p.state == ResearchState.NotStarted) p.state = ResearchState.Boosted
    }

    fun isCompleted(civicName: String): Boolean = progressMap[civicName]?.state == ResearchState.Completed
}
