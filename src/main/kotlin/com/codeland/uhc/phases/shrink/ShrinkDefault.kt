package com.codeland.uhc.phases.shrink

import com.codeland.uhc.core.GameRunner
import com.codeland.uhc.core.UHC
import com.codeland.uhc.phaseType.UHCPhase
import com.codeland.uhc.phases.Phase
import com.destroystokyo.paper.utils.PaperPluginLogger
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.World
import java.util.logging.Level
import kotlin.math.max
import kotlin.math.min

class ShrinkDefault : Phase() {

	var minRadius : Double? = null

	override fun start(uhc: UHC, length: Long) {
		minRadius = uhc.endRadius
		for (w in Bukkit.getServer().worlds) {
			if (w.environment == World.Environment.NETHER && uhc.netherToZero) {
				w.worldBorder.setSize(0.0, length)
			} else {
				w.worldBorder.setSize(minRadius!! * 2.0, length)
			}
		}
		for (player in Bukkit.getServer().onlinePlayers) {
			GameRunner.sendPlayer(player, "The border is now shrinking")
		}
		super.start(uhc, length)
	}

	override fun perSecond(second: Long) {
		GameRunner.uhc.updateMobCaps()
		super.perSecond(second)
	}

	override fun endPhase() {
		for (player in Bukkit.getServer().onlinePlayers) {
			GameRunner.sendPlayer(player, "The border has stopped shrinking")
		}
		super.endPhase()
	}

	override fun updateActionBar(remainingSeconds: Long) {
		val countdownComponent = TextComponent(getCountdownString())
		val messageComponent = TextComponent(" reaching ")
		val minRadComponent = TextComponent(minRadius!!.toLong().toString())
		minRadComponent.color = ChatColor.GOLD
		minRadComponent.isBold = true
		val inComponent = TextComponent(" in ")
		val remainingTimeComponent = TextComponent(getRemainingTimeString(remainingSeconds))
		remainingTimeComponent.color = ChatColor.GOLD
		remainingTimeComponent.isBold = true
		for (player in Bukkit.getServer().onlinePlayers) {
			val radiusComponent = TextComponent((player.world.worldBorder.size.toLong() / 2).toString())
			radiusComponent.color = ChatColor.GOLD
			remainingTimeComponent.isBold = true
			player.spigot().sendMessage(ChatMessageType.ACTION_BAR, countdownComponent, radiusComponent, messageComponent, minRadComponent, inComponent, remainingTimeComponent)
		}
	}

	override fun interrupt() {
		for (w in Bukkit.getServer().worlds) {
			w.worldBorder.setSize(w.worldBorder.size, 0)
		}
		super.interrupt()
	}

	override fun getCountdownString(): String {
		return "Border radius: "
	}

	override fun getPhaseType(): UHCPhase {
		return UHCPhase.SHRINKING
	}

	override fun endPhrase(): String {
		return "BORDER STOPPING"
	}
}