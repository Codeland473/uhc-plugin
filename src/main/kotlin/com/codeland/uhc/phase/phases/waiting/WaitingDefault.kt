package com.codeland.uhc.phase.phases.waiting

import com.codeland.uhc.UHCPlugin
import com.codeland.uhc.core.CustomSpawning
import com.codeland.uhc.core.GameRunner
import com.codeland.uhc.core.UHC
import com.codeland.uhc.gui.item.CommandItemType
import com.codeland.uhc.util.Util
import com.codeland.uhc.gui.item.ParkourCheckpoint
import com.codeland.uhc.phase.Phase
import com.codeland.uhc.quirk.quirks.Pests
import com.codeland.uhc.team.NameManager
import com.codeland.uhc.team.TeamData
import org.bukkit.*
import org.bukkit.attribute.Attribute
import org.bukkit.block.Biome
import org.bukkit.entity.Player

class WaitingDefault : Phase() {
	companion object {
		val oceans = arrayOf(
			Biome.OCEAN,
			Biome.DEEP_OCEAN,
			Biome.COLD_OCEAN,
			Biome.DEEP_COLD_OCEAN,
			Biome.FROZEN_OCEAN,
			Biome.DEEP_FROZEN_OCEAN,
			Biome.LUKEWARM_OCEAN,
			Biome.DEEP_LUKEWARM_OCEAN,
			Biome.WARM_OCEAN
		)

		fun validLobbySpot(world: World, x: Int, z: Int, radius: Int): Boolean {
			val halfRadius = radius / 2

			return Util.topLiquidSolidY(world, x, z).first == -1 &&
				Util.topLiquidSolidY(world, x + halfRadius, z + halfRadius).first == -1 &&
				Util.topLiquidSolidY(world, x - halfRadius, z + halfRadius).first == -1 &&
				Util.topLiquidSolidY(world, x + halfRadius, z - halfRadius).first == -1 &&
				Util.topLiquidSolidY(world, x - halfRadius, z - halfRadius).first == -1
		}

		fun teleportPlayerCenter(uhc: UHC, player: Player) {
			player.teleport(Location(Bukkit.getWorlds()[0], uhc.lobbyX + 0.5, Util.topBlockYTop(Bukkit.getWorlds()[0], 254, uhc.lobbyX, uhc.lobbyZ) + 1.0, uhc.lobbyZ + 0.5))
		}
	}

	override fun customStart() {
		val world = Bukkit.getWorlds()[0]
		
		fun findSpot(signX: Int, signZ: Int): Pair<Int, Int> {
			var x: Int
			var z: Int
			var tries = 0

			do {
				x = Util.randRange(10000, 100000) * signX
				z = Util.randRange(10000, 100000) * signZ
				++tries
			} while (!validLobbySpot(world, x, z, uhc.lobbyRadius) && tries < 100)

			return Pair(x, z)
		}

		if (uhc.lobbyX == -1) {
			val (x, z) = findSpot(1, 1)
			uhc.lobbyX = x
			uhc.lobbyZ = z

			LobbyPvp.createArena(world, x, z, uhc.lobbyRadius)
		}

		if (uhc.lobbyPvpX == -1) {
			val (x, z) = findSpot(-1, -1)
			uhc.lobbyPvpX = x
			uhc.lobbyPvpZ = z

			LobbyPvp.createArena(world, x, z, uhc.lobbyRadius)
			LobbyPvp.determineHeight(uhc, world, x, z, uhc.lobbyRadius)
		}

		world.setSpawnLocation(uhc.lobbyX, Util.topBlockYTop(world, 254, uhc.lobbyX, uhc.lobbyZ) + 1, uhc.lobbyZ)
		world.worldBorder.reset()

		Bukkit.getWorlds().forEach { otherWorld ->
			otherWorld.isThundering = false
			otherWorld.setStorm(false)
			otherWorld.setGameRule(GameRule.SPECTATORS_GENERATE_CHUNKS, false)
			otherWorld.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true)
			otherWorld.setGameRule(GameRule.DO_MOB_SPAWNING, false)

			otherWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false)
			otherWorld.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false)
			otherWorld.setGameRule(GameRule.SHOW_DEATH_MESSAGES, false)

			otherWorld.time = 6000
			otherWorld.difficulty = Difficulty.NORMAL
		}

		TeamData.removeAllTeams { player ->
			uhc.setParticipating(player, false)
		}

		Bukkit.getServer().onlinePlayers.forEach { player ->
			player.inventory.clear()
			onPlayerJoin(player)
		}

		CustomSpawning.stopSpawning()
	}

	override fun customEnd() {
		Bukkit.getWorlds().forEach { world ->
			world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true)
			world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, true)
			world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, true)
		}

		LobbyPvp.allInPvp { player, pvpData ->
			pvpData.inPvp = false
			NameManager.updateName(player)
		}
	}

	override fun updateBarLength(remainingSeconds: Int, currentTick: Int): Double {
		return 1.0
	}

	override fun updateBarTitle(world: World, remainingSeconds: Int, currentTick: Int): String {
		return barStatic()
	}

	override fun perTick(currentTick: Int) {
		Bukkit.getOnlinePlayers().forEach { player ->
			ParkourCheckpoint.updateCheckpoint(player)
		}

		LobbyPvp.onTick()
	}

	override fun perSecond(remainingSeconds: Int) {
		val numSlides = 4
		val perSlide = 6

		fun slideN(n: Int): Boolean {
			return remainingSeconds % (numSlides * perSlide) < perSlide * (n + 1)
		}

		Bukkit.getOnlinePlayers().forEach { player ->
			val pvpData = LobbyPvp.getPvpData(player)

			if (!pvpData.inPvp) {
				when {
					slideN(0) -> {
						if (uhc.usingBot) {
							val linked = GameRunner.bot?.isLinked(player.uniqueId)

							if (linked != null) player.sendActionBar(if (linked) "${ChatColor.GOLD}Link status: ${ChatColor.GREEN}${ChatColor.BOLD}Linked"
							else "${ChatColor.RED}${ChatColor.BOLD}You are not linked! ${ChatColor.GOLD}Use the ${ChatColor.WHITE}${ChatColor.BOLD}\"%link [your minecraft username]\" ${ChatColor.GOLD}command in discord")

						} else {
							player.sendActionBar("${ChatColor.GOLD}This UHC is running in no-bot mode")
						}
					}
					slideN(1) -> {
						val team = TeamData.playersTeam(player.uniqueId)

						if (team == null) player.sendActionBar("${ChatColor.GOLD}You are not on a team")
						else player.sendActionBar("${ChatColor.GOLD}Team name: ${team.colorPair.colorStringModified(team.displayName, ChatColor.BOLD)}")
					}
					slideN(2) -> {
						player.sendActionBar("${ChatColor.GOLD}Use ${ChatColor.WHITE}${ChatColor.BOLD}/uhc color [color] ${ChatColor.GOLD}to set your team's color")
					}
					else -> {
						player.sendActionBar("${ChatColor.GOLD}Use ${ChatColor.WHITE}${ChatColor.BOLD}/uhc name [name] ${ChatColor.GOLD}to set your team's name")
					}
				}
			}
		}
	}

	override fun endPhrase() = "Game starts in"

	fun onPlayerJoin(player: Player) {
		player.exp = 0.0F
		player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = 20.0
		player.health = 20.0
		player.foodLevel = 20
		player.fallDistance = 0f
		teleportPlayerCenter(uhc, player)
		player.gameMode = GameMode.CREATIVE

		Pests.makeNotPest(player)

		/* get them on the health scoreboard */
		player.damage(0.05)

		val inventory = player.inventory

		CommandItemType.giveItem(CommandItemType.GUI_OPENER, inventory)
		CommandItemType.giveItem(CommandItemType.JOIN_PVP, inventory)
		CommandItemType.giveItem(CommandItemType.PARKOUR_CHECKPOINT, inventory)
	}
}
