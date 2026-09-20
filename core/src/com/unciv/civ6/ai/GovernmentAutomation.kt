package com.unciv.civ6.ai

import com.unciv.civ6.domain.government.GovernmentManager

/**
 * Government automation - switches to best government and slots best cards.
 */
object GovernmentAutomation {
    fun autoSlotBestCards(manager: GovernmentManager, availableCards: List<String>) {
        // Simple: slot first available cards into available slots
        for (card in availableCards) {
            manager.slotPolicy(card)
        }
    }

    fun shouldSwitchGovernment(manager: GovernmentManager, completedCivics: Set<String>): String? {
        val available = manager.availableGovernments(completedCivics)
        val current = manager.currentGovernment?.let { govName -> available.find { it.name == govName } }
        // Switch if new gov has more slots
        val best = available.maxByOrNull { it.totalSlots() }
        if (best != null && best.name != manager.currentGovernment && (current == null || best.totalSlots() > current.totalSlots())) {
            return best.name
        }
        return null
    }
}
