package com.codeland.uhc.phase.phases.endgame

import com.codeland.uhc.core.GameRunner
import com.codeland.uhc.phase.Phase
import com.codeland.uhc.team.TeamData
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.World
import org.bukkit.boss.BossBar
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class EndgameGlowingTopTwo : Phase() {
	override fun customStart() {
		EndgameNone.closeNether()

		for (player in Bukkit.getServer().onlinePlayers) {
			GameRunner.sendGameMessage(player, "Glowing will be applied to the top two teams")
		}
	}

	override fun customEnd() {}
	override fun onTick(currentTick: Int) {}

	override fun perSecond(second: Int) {
		val sortedTeams = TeamData.teams.sortedByDescending { team ->
			team.members.fold(0.0) { health, member ->
				val player = member.player
				if (player != null) player.health + player.absorptionAmount else 0.0
			}
		}

		sortedTeams.forEachIndexed { i, team ->
			if (i < 2) team.members.forEach { member ->
				member.player?.addPotionEffect(PotionEffect(PotionEffectType.GLOWING, Int.MAX_VALUE, 0, false, false, false))
			}
			else team.members.forEach { member ->
				member.player?.removePotionEffect(PotionEffectType.GLOWING)
			}
		}
	}

	override fun updateBarPerSecond(bossBar: BossBar, world: World, remainingSeconds: Int) {
		barStatic(bossBar)
	}

	override fun endPhrase() = ""
}
