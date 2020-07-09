package com.codeland.uhc.command;

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.CommandAlias
import co.aikar.commands.annotation.Description
import co.aikar.commands.annotation.HelpCommand
import com.codeland.uhc.core.GameRunner
import com.codeland.uhc.phaseType.PhaseType
import net.md_5.bungee.api.ChatColor as ChatColorBungee
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("uhc")
class ParticipantCommands : BaseCommand() {

	@CommandAlias("setup")
	@Description("get the current setup")
	fun getCurrentSetup(sender: CommandSender) {
		sender as Player

		GameRunner.sendPlayer(sender, "Starting radius : " + GameRunner.uhc.startRadius.toInt() + " blocks")
		GameRunner.sendPlayer(sender, "Ending radius : " + GameRunner.uhc.endRadius.toInt() + " blocks")
		GameRunner.sendPlayer(sender, "Nether closes after shrinking : " + GameRunner.uhc.netherToZero)
		GameRunner.sendPlayer(sender, "Spawn cap coefficient : " + GameRunner.uhc.mobCapCoefficient)
		GameRunner.sendPlayer(sender, "Team Kill Bounty : " + GameRunner.uhc.killReward)
		GameRunner.sendPlayer(sender, "----------------PHASES----------------")
		GameRunner.sendPlayer(sender, "Grace period variant : " + GameRunner.uhc.getVariant(PhaseType.GRACE) + " " + GameRunner.uhc.phaseDurations[0] + " seconds")
		GameRunner.sendPlayer(sender, "Shrinking period variant : " + GameRunner.uhc.getVariant(PhaseType.SHRINK) + " " + GameRunner.uhc.phaseDurations[1] + " seconds")
		GameRunner.sendPlayer(sender, "Final zone variant : " + GameRunner.uhc.getVariant(PhaseType.FINAL) + " " + GameRunner.uhc.phaseDurations[2] + " seconds")
		GameRunner.sendPlayer(sender, "Glowing period variant : " + GameRunner.uhc.getVariant(PhaseType.GLOWING) + " " + GameRunner.uhc.phaseDurations[3] + " seconds")
		GameRunner.sendPlayer(sender, "Endgame variant : " + GameRunner.uhc.getVariant(PhaseType.ENDGAME))
	}

	@CommandAlias("name")
	@Description("change the name of your team")
	fun teamName(sender: CommandSender, newName: String) {
		val team = GameRunner.playersTeam(sender.name)
			?: return Commands.errorMessage(sender, "You are not on a team!")

		var realNewName = newName
		if (realNewName.startsWith('"') && realNewName.endsWith('"') && realNewName.length > 1) {
			realNewName = realNewName.substring(1, realNewName.length - 1)
		}

		GameRunner.discordBot?.renameTeam(team, realNewName)

		team.displayName = realNewName

		val message = TextComponent("Your team name has been changed to \"$realNewName\"")
		message.color = team.color.asBungee()
		message.isBold = true

		/* broadcast change to all teammates */
		team.entries.forEach { entry ->
			Bukkit.getServer().getPlayer(entry)?.sendMessage(message)
		}
	}

	@CommandAlias("color")
	@Description("change your team color")
	fun teamColor(sender: CommandSender, color: ChatColor) {
		val team = GameRunner.playersTeam(sender.name)
			?: return Commands.errorMessage(sender, "You are not on a team!")

		if (!TeamData.isValidColor(color))
			return Commands.errorMessage(sender, "That color is not allowed!")

		if (Bukkit.getServer().scoreboardManager.mainScoreboard.teams.any { team ->
				return@any team.color == color
		})
			return Commands.errorMessage(sender, "That color is already being used by another team!")

		/* now finally change color */
		team.color = color

		val message = TextComponent("Your team color has been changed to ${TeamData.colorPrettyNames[color.ordinal].toLowerCase()}")
		message.color = color.asBungee()
		message.isBold = true

		/* broadcast change to all teammates */
		team.entries.forEach { entry ->
			Bukkit.getServer().getPlayer(entry)?.sendMessage(message)
		}
	}

	@CommandAlias("mobcaps")
	@Description("query the current spawn limit coefficient")
	fun getMobCaps(sender: CommandSender) {
		sender as Player

		GameRunner.sendPlayer(sender, "Monster spawn limit: " + sender.world.monsterSpawnLimit)
		GameRunner.sendPlayer(sender, "Animal spawn limit: " + sender.world.animalSpawnLimit)
		GameRunner.sendPlayer(sender, "Ambient spawn limit: " + sender.world.ambientSpawnLimit)
		GameRunner.sendPlayer(sender, "Water animal spawn limit: " + sender.world.waterAnimalSpawnLimit)
	}

	@CommandAlias("help")
	@Description("display a list of commands and how to use them")
	@HelpCommand("bruh what?")
	fun help(sender: CommandSender) {
		sender as Player

		GameRunner.sendPlayer(sender, "Commands:")

		this.registeredCommands.forEach { command ->
			GameRunner.sendPlayer(sender, "${command.getSyntaxText()} > ${command.getHelpText()}")
		}
	}
}
