package com.unciv.civ6.multiplayer

import com.unciv.civ6.engine.Civ6Files
import com.unciv.civ6.engine.GameInfoV2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Civ6 Multiplayer helper - bridges GameInfoV2 + CloudflareWorkerStorage.
 * Replaces MultiplayerHelpers.kt + OnlineMultiplayer for Civ6 v10.
 */
class Civ6Multiplayer(
    private val files: Civ6Files = Civ6Files(),
    private val storage: Civ6FileStorage
) {
    suspend fun uploadGame(gameInfo: GameInfoV2): Boolean = withContext(Dispatchers.IO) {
        val json = files.gameInfoToString(gameInfo)
        storage.saveGame(gameInfo.gameId, json)
    }

    suspend fun downloadGame(gameId: String): GameInfoV2? = withContext(Dispatchers.IO) {
        val json = storage.loadGame(gameId) ?: return@withContext null
        files.gameInfoFromString(json)
    }

    suspend fun listGames(playerId: String): List<String> = withContext(Dispatchers.IO) {
        storage.listGames(playerId)
    }

    suspend fun deleteGame(gameId: String): Boolean = withContext(Dispatchers.IO) {
        storage.deleteGame(gameId)
    }

    // For UI: shows error via LoadGameScreen.getLoadExceptionMessage pattern but for v10
    fun getErrorMessage(ex: Throwable): String {
        return ex.message ?: "Unknown multiplayer error"
    }
}

/**
 * Factory to create storage based on settings - like UncivGame.Current.settings.multiplayerServer
 * Options: "civ6-worker" -> CloudflareWorkerStorage, "custom" -> custom URL
 */
object Civ6MultiplayerFactory {
    fun create(workerUrl: String?, authToken: String?): Civ6FileStorage {
        return if (workerUrl != null) CloudflareWorkerStorage(workerUrl, authToken)
        else object : Civ6FileStorage {
            override suspend fun saveGame(gameId: String, json: String, previewJson: String?) = false
            override suspend fun loadGame(gameId: String) = null
            override suspend fun listGames(playerId: String) = emptyList<String>()
            override suspend fun deleteGame(gameId: String) = false
        }
    }
}
