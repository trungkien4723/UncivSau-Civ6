/**
 * Push notification helper for Worker - sends FCM to next player when turn advances.
 * Called from GameRoom DO after PUT /game/:id when currentPlayer changes.
 */

export async function notifyNextPlayer(env: any, gameId: string, nextPlayerId: string, turn: number) {
  // In production, lookup FCM token from KV/D1: playerTokens.get(nextPlayerId)
  const token = await env.GAMES.get(`playerToken:${nextPlayerId}`)
  if (!token) return

  // FCM v1 API - requires service account, simplified to fetch
  // const fcmUrl = "https://fcm.googleapis.com/v1/projects/your-project/messages:send"
  // await fetch(fcmUrl, { method: "POST", headers: { Authorization: `Bearer ${await getAccessToken()}` }, body: JSON.stringify({ message: { token, notification: { title: "Your turn!", body: `Turn ${turn} in game ${gameId}` } } }) })

  // Fallback: WebSocket push via DO broadcast already handles online players
  // This is for offline push
  console.log(`Notify ${nextPlayerId} for game ${gameId} turn ${turn} token ${token}`)
}
