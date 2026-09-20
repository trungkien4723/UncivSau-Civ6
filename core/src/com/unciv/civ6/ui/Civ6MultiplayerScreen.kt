package com.unciv.civ6.ui

import com.unciv.civ6.engine.GameInfoV2
import com.unciv.civ6.multiplayer.Civ6Multiplayer

/**
 * Civ6 MultiplayerScreen placeholder (like MultiplayerScreen.kt).
 * Shows listGames, upload/download via Cloudflare Worker.
 */
class Civ6MultiplayerScreen(private val multiplayer: Civ6Multiplayer) {

    suspend fun refresh(playerId: String): List<String> {
        return multiplayer.listGames(playerId)
    }

    suspend fun uploadCurrentGame(gameInfo: GameInfoV2): String {
        val ok = multiplayer.uploadGame(gameInfo)
        return if (ok) "Uploaded ${gameInfo.gameId}" else "Upload failed: ${multiplayer.getErrorMessage(Exception("save failed"))}"
    }

    suspend fun downloadAndLoad(gameId: String): GameInfoV2? {
        return try {
            multiplayer.downloadGame(gameId)
        } catch (ex: Exception) {
            null
        }
    }

    suspend fun getGamePreview(gameId: String): String {
        val game = downloadAndLoad(gameId) ?: return "Not found"
        return "${game.cities.size} cities, Turn ${game.turns}, Player ${game.currentPlayer}"
    }
}
