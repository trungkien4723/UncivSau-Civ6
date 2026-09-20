package com.unciv.civ6.notifications

/**
 * Turn notification for Civ6 multiplayer - replaces MultiplayerTurnCheckWorker.kt polling.
 * Worker pushes via FCM or WebSocket, client shows notification.
 */

data class TurnNotification(
    val gameId: String,
    val gameName: String,
    val nextPlayerId: String,
    val turn: Int,
    val timestamp: Long = System.currentTimeMillis()
)

class NotificationManager {
    private val pending = mutableMapOf<String, TurnNotification>()

    fun queue(notification: TurnNotification) {
        pending[notification.gameId] = notification
    }

    fun pop(gameId: String): TurnNotification? = pending.remove(gameId)

    fun hasPending(playerId: String): Boolean = pending.values.any { it.nextPlayerId == playerId }

    fun forPlayer(playerId: String): List<TurnNotification> = pending.values.filter { it.nextPlayerId == playerId }

    fun format(notification: TurnNotification): String {
        return "Your turn in ${notification.gameName} (Turn ${notification.turn})"
    }
}

/**
 * Client-side handler for FCM/WebSocket push.
 */
class PushHandler(
    private val manager: NotificationManager,
    private val onNotify: (TurnNotification) -> Unit
) {
    fun onPush(json: String) {
        // Parse json { gameId, gameName, nextPlayerId, turn }
        // Simplified: assume json is already parsed
        // In real impl: use json().fromJson
        // manager.queue(parsed); onNotify(parsed)
    }

    fun onTurnCompleted(gameId: String, nextPlayer: String, turn: Int, gameName: String) {
        val n = TurnNotification(gameId, gameName, nextPlayer, turn)
        manager.queue(n)
        onNotify(n)
    }
}
