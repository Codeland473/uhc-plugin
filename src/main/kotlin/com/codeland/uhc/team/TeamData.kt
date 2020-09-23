package com.codeland.uhc.team

import com.codeland.uhc.UHCPlugin
import com.codeland.uhc.core.GameRunner
import com.codeland.uhc.util.Util
import org.bukkit.ChatColor.*
import org.bukkit.ChatColor
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue

object TeamData {
	val teamColors = arrayOf(
		BLUE,
		RED,
		GREEN,
		AQUA,
		LIGHT_PURPLE,
		YELLOW,
		DARK_RED,
		DARK_AQUA,
		DARK_PURPLE,
		GRAY,
		DARK_BLUE,
		DARK_GREEN,
		DARK_GRAY
	)

	val teamColorIndices = Array(ChatColor.values().size) { i ->
		teamColors.indexOf(ChatColor.values()[i])
	}

	val MAX_TEAMS = 91

	val teams = ArrayList<Team>()

	fun colorPairFromIndex(index: Int): ColorPair? {
		val pair = Util.getCombination(index, teamColors.size)

		if (pair.first == -1) return null
		if (pair.first == pair.second) return ColorPair(teamColors[pair.first])

		return ColorPair(teamColors[pair.first], teamColors[pair.second])
	}

	fun teamExists(colorPair: ColorPair): Boolean {
		return teams.any { team -> team.colorPair == colorPair }
	}

	fun playersTeam(player: OfflinePlayer): Team? {
		for (team in teams)
			for (member in team.members)
				if (member.uniqueId == player.uniqueId) return team

		return null
	}

	fun addToTeam(colorPair: ColorPair, player: OfflinePlayer): Team {
		/* remove player from old team if they are on one */
		val oldTeam = playersTeam(player)
		if (oldTeam != null) removeFromTeam(oldTeam, player)

		/* find if the new team exists */
		var newTeam = teams.find { team -> team.colorPair == colorPair }

		/* create the team if it doesn't exist */
		if (newTeam == null) {
			newTeam = Team(colorPair)
			teams.add(newTeam)
		}

		if (GameRunner.uhc.usingBot) GameRunner.bot?.addPlayerToTeam(newTeam, player.uniqueId) {}

		newTeam.members.add(player)

		return newTeam
	}

	fun addToTeam(team: Team, player: OfflinePlayer): Team {
		/* remove player from old team if they are on one */
		val oldTeam = playersTeam(player)
		if (oldTeam != null) removeFromTeam(oldTeam, player)

		if (GameRunner.uhc.usingBot) GameRunner.bot?.addPlayerToTeam(team, player.uniqueId) {}

		team.members.add(player)

		return team
	}

	fun removeFromTeam(player: OfflinePlayer) {
		removeFromTeam(playersTeam(player), player)
	}

	fun removeFromTeam(oldTeam: Team?, player: OfflinePlayer): Boolean {
		oldTeam ?: return false

		oldTeam.members.removeIf { offlinePlayer -> offlinePlayer.uniqueId == player.uniqueId }

		/* remove the team if no one is left on it */
		if (oldTeam.members.isEmpty()) {
			if (GameRunner.uhc.usingBot) GameRunner.bot?.destroyTeam(oldTeam) {}
			teams.removeIf { team -> team === oldTeam }
		}

		return true
	}

	fun removeAllTeams() {
		teams.removeIf { team ->
			if (GameRunner.uhc.usingBot) GameRunner.bot?.destroyTeam(team) {}
			true
		}
	}
}