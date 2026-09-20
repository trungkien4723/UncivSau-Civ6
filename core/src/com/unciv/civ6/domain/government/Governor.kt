package com.unciv.civ6.domain.government

/**
 * Governor - must use ArrayList<ArrayList<String>> per fix e50079e, not List<List<String>>.
 * Gdx Json deserializes Array into ArrayList, causing CCE on getPromotionObjects.
 */

data class Governor(
    val name: String, // Magnus, Pingala, Victor, Liang, Reyna, Moksha, Amani
    val description: String = "",
    // promotions[0] = level1, promotions[1]=level2, etc. Must be ArrayList
    var promotions: ArrayList<ArrayList<String>> = ArrayList(),
    var assignedCity: String? = null,
    var turnsToEstablish: Int = 5 // needs 5 turns to stabilize after move
) {
    val promotionObjects: List<List<String>> get() = promotions.map { it.toList() }

    fun assignTo(cityName: String) {
        assignedCity = cityName
        turnsToEstablish = 5
    }

    fun tickTurn() {
        if (turnsToEstablish > 0) turnsToEstablish--
    }

    fun isEstablished(): Boolean = turnsToEstablish == 0
    fun level(): Int = promotions.size
}

class GovernorManager(
    private val governors: MutableMap<String, Governor> = mutableMapOf()
) {
    fun addGovernor(governor: Governor) { governors[governor.name] = governor }
    fun available(): List<Governor> = governors.values.filter { it.assignedCity == null }
    fun assign(name: String, city: String): Boolean {
        val g = governors[name] ?: return false
        if (g.assignedCity != null) return false
        g.assignTo(city)
        return true
    }
    fun move(name: String, newCity: String): Boolean {
        val g = governors[name] ?: return false
        g.assignTo(newCity)
        return true
    }
    fun tickAll() { governors.values.forEach { it.tickTurn() } }
}
