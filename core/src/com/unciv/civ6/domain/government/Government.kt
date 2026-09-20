package com.unciv.civ6.domain.government

/**
 * Civ6 Government + Policy Cards - replaces Civ5 PolicyBranch.
 * Each government has fixed policy slots: Military, Economic, Diplomatic, Wildcard.
 */

enum class PolicySlotType { Military, Economic, Diplomatic, Wildcard }

data class PolicyCard(
    val name: String,
    val slotType: PolicySlotType,
    val description: String = "",
    val bonus: Map<String, Float> = emptyMap(), // e.g. "GoldTradeRoute" -> 2f
    val requiredCivic: String? = null
)

data class Government(
    val name: String,
    val era: String,
    val slots: Map<PolicySlotType, Int>, // e.g. Chiefdom: 1 Economic, 1 Military
    val inherentBonus: Map<String, Float> = emptyMap(), // e.g. Merchant Republic +2 trade routes
    val requiredCivic: String? = null
) {
    fun totalSlots(): Int = slots.values.sum()
    fun canSlot(cardType: PolicySlotType): Boolean {
        if (slots[cardType] ?: 0 > 0) return true
        return (slots[PolicySlotType.Wildcard] ?: 0) > 0 // wildcard can hold any
    }
}

class GovernmentManager(
    private val governments: Map<String, Government>,
    private val policyCards: Map<String, PolicyCard>,
    var currentGovernment: String? = null,
    private val slottedCards: MutableMap<PolicySlotType, MutableList<String>> = mutableMapOf()
) {
    fun availableGovernments(completedCivics: Set<String>): List<Government> {
        return governments.values.filter { gov ->
            gov.requiredCivic == null || gov.requiredCivic in completedCivics
        }
    }

    fun switchGovernment(newGovName: String, completedCivics: Set<String>): Boolean {
        val gov = governments[newGovName] ?: return false
        if (gov.requiredCivic != null && gov.requiredCivic !in completedCivics) return false
        currentGovernment = newGovName
        slottedCards.clear() // free switch on government change (Civ6 rule)
        return true
    }

    fun slotPolicy(cardName: String): Boolean {
        val card = policyCards[cardName] ?: return false
        val govName = currentGovernment ?: return false
        val gov = governments[govName] ?: return false
        // Try exact slot first
        val exactCount = slottedCards[card.slotType]?.size ?: 0
        val exactCapacity = gov.slots[card.slotType] ?: 0
        if (exactCount < exactCapacity) {
            slottedCards.getOrPut(card.slotType) { mutableListOf() }.add(cardName)
            return true
        }
        // Try wildcard
        val wildCount = slottedCards[PolicySlotType.Wildcard]?.size ?: 0
        val wildCapacity = gov.slots[PolicySlotType.Wildcard] ?: 0
        if (wildCount < wildCapacity) {
            slottedCards.getOrPut(PolicySlotType.Wildcard) { mutableListOf() }.add(cardName)
            return true
        }
        return false
    }

    fun slottedPolicies(): List<String> = slottedCards.values.flatten()

    fun activeBonuses(): Map<String, Float> {
        val bonuses = mutableMapOf<String, Float>()
        for (cardName in slottedPolicies()) {
            val card = policyCards[cardName] ?: continue
            for ((k, v) in card.bonus) bonuses[k] = (bonuses[k] ?: 0f) + v
        }
        // Add government inherent
        val gov = currentGovernment?.let { governments[it] }
        gov?.inherentBonus?.forEach { (k, v) -> bonuses[k] = (bonuses[k] ?: 0f) + v }
        return bonuses
    }

    fun goldCostToSwitch(): Int {
        // Civ6: free on government change, otherwise increasing gold (simplified 50 + 50*switches)
        return if (slottedCards.isEmpty()) 0 else 50
    }
}
