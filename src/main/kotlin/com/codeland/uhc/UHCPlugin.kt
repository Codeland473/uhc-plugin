package com.codeland.uhc

import co.aikar.commands.PaperCommandManager
import com.codeland.uhc.command.*
import com.codeland.uhc.core.GameRunner
import com.codeland.uhc.core.UHC
import com.codeland.uhc.discord.MixerBot
import com.codeland.uhc.event.*
import com.codeland.uhc.gui.GuiManager
import com.codeland.uhc.phase.VariantList
import com.codeland.uhc.util.GoogleDDNSUpdater
import com.codeland.uhc.util.Util
import com.codeland.uhc.util.WebAddress
import com.codeland.uhc.world.WorldManager
import com.codeland.uhc.world.gen.WorldGenManager
import org.bukkit.ChatColor
import org.bukkit.plugin.java.JavaPlugin

class UHCPlugin : JavaPlugin() {
	init {
		plugin = this
	}

	companion object {
		lateinit var plugin: JavaPlugin
	}

	override fun onEnable() {
		val commandManager = PaperCommandManager(this)

		Commands.registerCompletions(commandManager)

		commandManager.registerCommand(AdminCommands(), true)
		commandManager.registerCommand(TeamCommands(), true)
		commandManager.registerCommand(TestCommands(), true)
		commandManager.registerCommand(ParticipantCommands(), true)
		commandManager.registerCommand(ParticipantTeamCommands(), true)
		commandManager.registerCommand(ShareCoordsCommand(), true)
		commandManager.registerCommand(NicknameCommand(), true)

		/* register all events */
		server.pluginManager.registerEvents(ClassesEvents(), this)
		server.pluginManager.registerEvents(Chat(), this)
		server.pluginManager.registerEvents(EventListener(), this)
		server.pluginManager.registerEvents(Generation(), this)
		server.pluginManager.registerEvents(Portal(), this)
		server.pluginManager.registerEvents(PvpListener(), this)
		server.pluginManager.registerEvents(Brew(), this)
		server.pluginManager.registerEvents(Barter(), this)
		server.pluginManager.registerEvents(GuiManager(), this)
		server.pluginManager.registerEvents(Loot(), this)
		server.pluginManager.registerEvents(Enchant(), this)
		Packet.init()

		WorldGenManager.init(server)

		VariantList.create()

		val address = WebAddress.getLocalAddress()

		try {
			Util.log(GoogleDDNSUpdater.updateDomain(address))
		} catch (ex: Exception) {
			Util.log("$ex")
			Util.log("${ChatColor.RED}DDNS FAILED | STARTING SERVER AT $address")
		}

		MixerBot.createMixerBot(address, {
			GameRunner.bot = it
		}, {
			Util.log("${ChatColor.RED}$it")
			Util.log("${ChatColor.RED}BOT INIT FAILED | STARTING IN NO-BOT MODE")
		})

		server.scheduler.scheduleSyncDelayedTask(this) {
			WorldManager.init()

			GameRunner.registerHearts()

			UHC.startWaiting()
		}
	}
}
