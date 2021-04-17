package com.codeland.uhc.lobbyPvp

import com.codeland.uhc.command.Commands
import com.codeland.uhc.core.AbstractLobby
import com.codeland.uhc.core.GameRunner
import com.codeland.uhc.core.PlayerData
import com.codeland.uhc.core.WorldManager
import com.codeland.uhc.util.Util
import net.kyori.adventure.text.Component
import org.bukkit.*
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

object PvpGameManager {
	const val ARENA_STRIDE = 160
	const val BEACH = 128
	const val LARGE_BORDER = 96
	const val SMALL_BORDER = 64

	class PvpGame(val players: Array<UUID>, position: Int, val borderSize: Int) {
		val x = xFromPosition(position)
		val z = zFromPosition(position)

		var winner: String? = null
		var time = -4

		fun centerLocation(): Pair<Int, Int> {
			return Pair(x * ARENA_STRIDE + (ARENA_STRIDE / 2), z * ARENA_STRIDE + (ARENA_STRIDE / 2))
		}

		fun alive(): List<Player> {
			return players.mapNotNull { Bukkit.getPlayer(it) }
				.filter { it.location.world.name == WorldManager.PVP_WORLD_NAME && it.gameMode != GameMode.SPECTATOR }
		}

		fun endNaturally(winner: String) {
			this.winner = winner
			time = -10

			players.mapNotNull { Bukkit.getPlayer(it) }.forEach { it.sendTitle("${ChatColor.RED}$winner wins!", "", 0, 160, 40) }
		}

		fun checkEnd(): Boolean {
			val alive = alive()

			return if (alive.size == 1) {
				endNaturally(alive.first().name)
				false

			/* if somehow both players disconnect then end it immediately */
			} else alive.isEmpty()
		}
	}

	val ongoingGames = ArrayList<PvpGame>()
	var nextGamePosition = 0

	fun addGame(players: Array<UUID>) {
		val game = PvpGame(players, nextGamePosition, SMALL_BORDER)

		val playerData0 = PlayerData.getPlayerData(game.players[0])
		playerData0.lastPlayed = game.players[1]
		val playerData1 = PlayerData.getPlayerData(game.players[1])
		playerData1.lastPlayed = game.players[0]

		++nextGamePosition
		ongoingGames.add(game)
	}

	fun playersGame(uuid: UUID): PvpGame? {
		return ongoingGames.find { game -> game.players.contains(uuid) }
	}

	fun perTick(currentTick: Int) {
		if (currentTick % 20 == 0) {
			PvpQueue.perSecond()

			ongoingGames.removeIf { game ->
				++game.time
				val winner = game.winner

				/* postgame length until game is destroyed */
				val removeResult = if (winner != null) {
					game.time >= 0

				/* before and during the game */
				} else when {
					/* countdown before match starts */
					game.time < 0 -> {
						game.players.mapNotNull { Bukkit.getPlayer(it) }.forEach { player ->
							player.sendTitle("${ChatColor.RED}${-game.time}", "${ChatColor.RED}PVP Match Starting", 0, 21, 0)
							player.sendActionBar(Component.text(""))
						}

						false
					}
					/* start match */
					game.time == 0 -> {
						val world = WorldManager.getPVPWorld()

						val positions = playerPositions(game).map { position ->
							val (liquidY, solidY) = Util.topLiquidSolidY(world, position.first, position.second)
							Location(world, position.first + 0.5, (if (liquidY == -1) solidY else liquidY) + 1.0, position.second + 0.5)
						}

						val players = game.players.mapNotNull { Bukkit.getPlayer(it) }

						val data = players.zip(positions)
						if (data.size < game.players.size) {
							players.forEach { player -> Commands.errorMessage(player, "Game cancelled! A player left") }
							true

						} else {
							data.forEach { (player, position) ->
								enablePvp(player, true, position)
								player.sendTitle("${ChatColor.GOLD}FIGHT", "", 0, 20, 10)
							}
							false
						}
					}
					/* during match */
					else -> {
						game.checkEnd()
					}
				}

				/* teleport all players back */
				if (removeResult) {
					game.players.mapNotNull { Bukkit.getPlayer(it) }.forEach { disablePvp(it) }
				}

				removeResult
			}
		}
	}

	fun playerPositions(game: PvpGame): List<Pair<Int, Int>> {
		val (centerX, centerZ) = game.centerLocation()

		val radius = SMALL_BORDER / 2 - 4

		val startAngle = Math.random() * PI * 2
		val angleStride = PI * 2 / game.players.size

		return (game.players.indices).map { i ->
			val angle = startAngle + angleStride * i
			Pair(centerX + (cos(angle) * radius).roundToInt(), centerZ + (sin(angle) * radius).roundToInt())
		}
	}

	private fun xFromPosition(position: Int): Int {
		return position % 16
	}

	private fun zFromPosition(position: Int): Int {
		return position / 16
	}

	fun enablePvp(player: Player, save: Boolean, location: Location) {
		val playerData = PlayerData.getPlayerData(player.uniqueId)

		/* save before pvp state */
		if (save) playerData.lobbyInventory = player.inventory.contents.clone()

		AbstractLobby.resetPlayerStats(player)
		player.gameMode = GameMode.SURVIVAL

		/* give items */
		player.inventory.setArmorContents(arrayOf(
			LobbyPvpItems.genBoots(),
			LobbyPvpItems.genLeggings(),
			LobbyPvpItems.genChestplate(),
			LobbyPvpItems.genHelmet()
		))

		player.inventory.setItemInOffHand(LobbyPvpItems.genShield())

		LobbyPvpItems.itemsList.forEach { gen -> player.inventory.addItem(gen()) }

		player.teleport(location)
	}

	fun disablePvp(player: Player) {
		val playerData = PlayerData.getPlayerData(player.uniqueId)

		AbstractLobby.onSpawnLobby(player)

		player.inventory.contents = playerData.lobbyInventory
	}
}
