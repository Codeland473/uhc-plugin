package com.codeland.uhc.phase.phases.grace

import com.codeland.uhc.core.GameRunner
import com.codeland.uhc.core.Ledger
import com.codeland.uhc.core.UHC
import com.codeland.uhc.phase.Phase
import com.codeland.uhc.team.TeamData
import com.codeland.uhc.util.Util
import org.bukkit.*
import org.bukkit.attribute.Attribute
import org.bukkit.boss.BossBar
import org.bukkit.entity.Player
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.*

open class GraceDefault : Phase() {
	/* to be injected on phase creation */
	lateinit var teleportLocations: ArrayList<Location>
	lateinit var teleportGroups: Array<Array<UUID>>

	override fun customStart() {
		/* set border in overworld */
		val world = Bukkit.getWorlds()[0]

		world.time = 0
		world.worldBorder.setCenter(0.5, 0.5)
		world.worldBorder.size = uhc.startRadius * 2 + 1

		/* teleport and set players */
		teleportGroups.forEachIndexed { i, teleportGroup ->
			teleportGroup.forEach { uuid ->
				GameRunner.uhc.setAlive(uuid, true)

				GameRunner.teleportPlayer(uuid, teleportLocations[i])
				GameRunner.playerAction(uuid, ::startPlayer)
			}
		}

		/* non participants into spec */
		Bukkit.getOnlinePlayers().forEach { player ->
			if (!GameRunner.uhc.isParticipating(player.uniqueId)) {
				player.gameMode = GameMode.SPECTATOR
				player.teleport(GameRunner.uhc.spectatorSpawnLocation())
			}
		}

		/* reset the ledger */
		uhc.elapsedTime = 0
		uhc.ledger = Ledger()
	}

	fun startPlayer(player: Player) {
		/* absolutely nuke the inventory */
		player.inventory.clear()
		player.itemOnCursor.amount = 0
		player.setItemOnCursor(null)

		/* clear crafting slots */
		player.openInventory.topInventory.clear()
		player.openInventory.bottomInventory.clear()

		for (activePotionEffect in player.activePotionEffects)
			player.removePotionEffect(activePotionEffect.type)

		player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = 20.0
		player.health = 20.0
		player.absorptionAmount = 0.0
		player.exp = 0f
		player.level = 0
		player.foodLevel = 20
		player.saturation = 5f
		player.exhaustion = 0f
		player.fireTicks = 0
		player.setStatistic(Statistic.TIME_SINCE_REST, 0)

		/* remove all advancements */
		Bukkit.getServer().advancementIterator().forEach { advancement ->
			val progress = player.getAdvancementProgress(advancement)

			progress.awardedCriteria.forEach { criteria ->
				progress.revokeCriteria(criteria)
			}
		}

		player.gameMode = GameMode.SURVIVAL
	}

	override fun customEnd() {}
	override fun onTick(currentTick: Int) {}

	override fun perSecond(remainingSeconds: Int) {
		uhc.updateMobCaps()
	}

	override fun updateBarPerSecond(bossBar: BossBar, world: World, remainingSeconds: Int) {
		bossBar.setTitle("${ChatColor.RESET}Grace period ends in ${phaseType.chatColor}${ChatColor.BOLD}${Util.timeString(remainingSeconds)}")
	}

	override fun endPhrase(): String {
		return "Grace Period Ending"
	}

	companion object {
		fun spreadSinglePlayer(world: World, spreadRadius: Double): Location? {
			for (i in 0 until 16) {
				val location = findLocation(world, Math.random() * 2 * Math.PI, Math.PI * 0.9, spreadRadius)
				if (location != null) return location
			}

			return null
		}

		/**
		 * @return an empty arraylist if not all spaces could be filled
		 */
		fun spreadPlayers(world: World, numSpaces: Int, spreadRadius: Double): ArrayList<Location> {
			val ret = ArrayList<Location>(numSpaces)

			var angle = Math.random() * 2 * Math.PI

			val angleAdvance = 2 * Math.PI / numSpaces
			val angleDeviation = angleAdvance / 4

			for (i in 0 until numSpaces) {
				val location = findLocation(world, angle, angleDeviation, spreadRadius) ?: return ArrayList()
				ret.add(location)

				angle += angleAdvance
			}

			return ret
		}

		fun findLocation(world: World, angle: Double, angleDeviation: Double, spreadRadius: Double): Location? {
			val minRadius = spreadRadius / 2

			/* initial (i, j) within the 32 * 32 polar coordinate area is random */
			var position = (Math.random() * 32 * 32).toInt()
			for (iterator in 0 until 32 * 32) {
				val i = position % 32
				val j = position / 32

				/* convert to polar coordinates to search for valid spots */
				val iAngle = (angleDeviation * 2) * (i / 32.0) + (angle - angleDeviation)
				val jRadius = (spreadRadius - minRadius) * (j / 32.0) + minRadius

				val squircleRadius = getSquircleRadius(iAngle, jRadius)

				val x = round(cos(iAngle) * squircleRadius).toInt()
				val z = round(sin(iAngle) * squircleRadius).toInt()
				val (liquidY, solidY) = Util.topLiquidSolidY(world, x, z)

				/* if this there is no liquid or void in this block */
				if (solidY != -1) return Location(world, x + 0.5, solidY.toDouble() + 1, z + 0.5)

				position = (position + 1) % (32 * 32)
			}

			return null
		}

		fun positiveMod(a: Double, b: Double): Double {
			return (a % b + b) % b
		}

		fun getSquareRadius(angle: Double): Double {
			val angle = positiveMod(angle, Math.PI * 2)

			return when {
				angle < (Math.PI / 4) -> 1 / Math.cos(angle)
				angle < (3 * Math.PI / 4) -> 1 / Math.sin(angle)
				angle < (5 * Math.PI / 4) -> -1 / Math.cos(angle)
				angle < (7 * Math.PI / 4) -> -1 / Math.sin(angle)
				else -> 1 / Math.cos(angle)
			}
		}

		fun getSquircleRadius(angle: Double, radius: Double): Double {
			return (getSquareRadius(angle) * radius + radius) / 2
		}
	}
}
