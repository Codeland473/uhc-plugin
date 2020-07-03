package com.codeland.uhc.phases

import com.codeland.uhc.core.GameRunner
import com.codeland.uhc.core.UHC
import com.codeland.uhc.phaseType.UHCPhase
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable

abstract class Phase {

	protected var runnable : BukkitRunnable? = null

	open fun start(uhc : UHC, length : Long) {
		runnable = object : BukkitRunnable() {
			var remainingSeconds = length
			override fun run() {
				if (remainingSeconds == 0L) {
					cancel()
					for (player in Bukkit.getServer().onlinePlayers) {
						player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent(""))
					}
					endPhase()
					uhc.startNextPhase()
					return
				}
				perSecond(remainingSeconds)
				--remainingSeconds
			}
		}
		runnable!!.runTaskTimer(GameRunner.plugin!!, 0, 20)
		countdownToEvent(length, endPhrase())
	}

	protected open fun endPhase() {

	}

	protected open fun perSecond(second : Long) {
		updateActionBar(second)
	}

	protected open fun updateActionBar(remainingSeconds : Long) {
		val countdownComponent = TextComponent(getCountdownString())
		val remainingTimeComponent = TextComponent(getRemainingTimeString(remainingSeconds))
		remainingTimeComponent.color = ChatColor.GOLD
		remainingTimeComponent.isBold = true
		for (player in Bukkit.getServer().onlinePlayers) {
			player.spigot().sendMessage(ChatMessageType.ACTION_BAR, countdownComponent, remainingTimeComponent)
		}
	}

	protected open fun getRemainingTimeString(remainingSeconds : Long) : String {
		var timeRemaining = remainingSeconds
		var units : String =
				if (remainingSeconds >= 60) {
					timeRemaining = timeRemaining / 60 + 1
					" minute"
				} else {
					" second"
				}
		if (timeRemaining > 1) {
			units += "s"
		}
		return timeRemaining.toString() + units
	}

	protected open fun interrupt() {
		runnable?.cancel()
	}

	abstract fun getCountdownString() : String

	abstract fun getPhaseType() : UHCPhase

	abstract fun endPhrase() : String

	private fun countdownToEvent(totalDelay : Long, subtitle : String) {
		val countdownRunnable = object  : BukkitRunnable() {
			var num = 3
			override fun run() {
				if (num != 0) {
					for (onlinePlayer in Bukkit.getServer().onlinePlayers) {
						onlinePlayer.sendTitle("" + num, subtitle, 0, 21, 0)
					}
				} else {
					cancel()
				}
				--num
			}
		}

		countdownRunnable.runTaskTimer(GameRunner.plugin!!, totalDelay * 20 - 60, 20)
	}
}