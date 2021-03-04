package com.codeland.uhc.quirk.quirks

import com.codeland.uhc.UHCPlugin
import com.codeland.uhc.core.GameRunner
import com.codeland.uhc.core.PlayerData
import com.codeland.uhc.core.UHC
import com.codeland.uhc.phase.PhaseType
import com.codeland.uhc.phase.PhaseVariant
import com.codeland.uhc.quirk.Quirk
import com.codeland.uhc.quirk.QuirkType
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import java.util.*

class RandomEffects(uhc: UHC, type: QuirkType) : Quirk(uhc, type) {
	override fun onEnable() {
		timer = time

		taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(UHCPlugin.plugin, {
			if (!GameRunner.uhc.isGameGoing()) return@scheduleSyncRepeatingTask

			if (--timer == 0) {
				timer = time
				giveEffects()
			}
		}, 0, 20)

		if (GameRunner.uhc.isGameGoing())
			giveEffects()
	}

	override fun onPhaseSwitch(phase: PhaseVariant) {
		if (phase.type == PhaseType.GRACE)
			giveEffects()

		else if (phase.type == PhaseType.POSTGAME || phase.type == PhaseType.WAITING)
			Bukkit.getScheduler().cancelTask(taskID)
	}

	override fun onDisable() {
		Bukkit.getScheduler().cancelTask(taskID)
	}

	var taskID = 0

	companion object {
		var time = 180
		var timer = 0

		var effects = arrayOf(
			PotionEffectType.SPEED,
			PotionEffectType.FAST_DIGGING,
			PotionEffectType.INCREASE_DAMAGE,
			PotionEffectType.JUMP,
			PotionEffectType.DAMAGE_RESISTANCE,
			PotionEffectType.FIRE_RESISTANCE,
			PotionEffectType.INVISIBILITY,
			PotionEffectType.NIGHT_VISION,
			PotionEffectType.ABSORPTION,
			PotionEffectType.SATURATION,
			PotionEffectType.CONDUIT_POWER,
			PotionEffectType.SLOW_FALLING
		)

		var usingEffects = emptyArray<PotionEffectType>()
		var effectIndex = -1

		fun giveEffects() {
			if (effectIndex == -1) {
				generateUsingEffects()
				effectIndex = usingEffects.lastIndex
			}

			PlayerData.playerDataList.forEach { (uuid, playerData) ->
				if (playerData.participating) GameRunner.playerAction(uuid) { player ->
					player.addPotionEffect(PotionEffect(usingEffects[effectIndex], 3600, 0, false, true, true))
				}
			}

			--effectIndex
		}

		private fun generateUsingEffects() {
			effectIndex = 0

			usingEffects = effects.copyOf()

			/* shuffle array */
			for (index in usingEffects.indices) {
				val swapIndex = (Math.random() * effects.size).toInt()

				val temp = usingEffects[index]
				usingEffects[index] = usingEffects[swapIndex]
				usingEffects[swapIndex] = temp
			}
		}
	}
}