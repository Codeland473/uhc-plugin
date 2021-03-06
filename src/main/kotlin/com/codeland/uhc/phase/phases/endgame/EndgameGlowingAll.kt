package com.codeland.uhc.phase.phases.endgame

import com.codeland.uhc.core.GameRunner
import com.codeland.uhc.core.PlayerData
import com.codeland.uhc.phase.Phase
import org.bukkit.Bukkit
import org.bukkit.World
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class EndgameGlowingAll : Endgame() {
	override fun customStart() {
		super.customStart()

		for (player in Bukkit.getServer().onlinePlayers) {
			GameRunner.sendGameMessage(player, "Glowing has been applied")
		}
	}

	override fun updateBarLength(remainingSeconds: Int, currentTick: Int): Float {
		return 1.0f
	}

	override fun updateBarTitle(world: World, remainingSeconds: Int, currentTick: Int): String {
		return barStatic()
	}

	override fun perTick(currentTick: Int) {}

	override fun perSecond(remainingSeconds: Int) {
		PlayerData.playerDataList.forEach { (uuid, playerData) ->
			if (playerData.alive) GameRunner.potionEffectPlayer(uuid, PotionEffect(PotionEffectType.GLOWING, Int.MAX_VALUE, 1, false, false, false))
		}
	}

	override fun endPhrase() = ""
}
