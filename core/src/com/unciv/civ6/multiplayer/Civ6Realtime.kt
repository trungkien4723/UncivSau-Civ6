package com.unciv.civ6.multiplayer

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

/**
 * Realtime WebSocket client for horse DO - connects to ws://worker/ws/:gameId?playerId=xxx
 * Replaces polling TurnCheckWorker for Civ6 v10 realtime.
 */
class Civ6Realtime(
    private val workerUrl: String,
    private val gameId: String,
    private val playerId: String,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private var session: DefaultClientWebSocketSession? = null
    val updates = MutableSharedFlow<String>(replay = 0)

    suspend fun connect(client: HttpClient) {
        val wsUrl = workerUrl.replace("https://", "wss://").replace("http://", "ws://") + "/ws/$gameId?playerId=$playerId"
        // In real impl: client.webSocket(wsUrl) { ... }
        // Placeholder for scaffold - actual Ktor WS in integration
    }

    fun onMessage(json: String) {
        scope.launch { updates.emit(json) }
    }

    suspend fun sendUpdate(json: String) {
        session?.send(Frame.Text(json))
    }

    suspend fun disconnect() {
        try { session?.close() } catch (_: Exception) {}
    }

    companion object {
        fun wsUrl(workerUrl: String, gameId: String, playerId: String): String {
            return workerUrl.replace("https://", "wss://") + "/ws/$gameId?playerId=$playerId"
        }
    }
}
