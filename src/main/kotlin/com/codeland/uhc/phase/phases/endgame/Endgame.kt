
package com.codeland.uhc.phase.phases.endgame

import com.codeland.uhc.command.Commands
import com.codeland.uhc.core.GameRunner
import com.codeland.uhc.core.PlayerData
import com.codeland.uhc.core.UHC
import com.codeland.uhc.phase.Phase
import com.codeland.uhc.util.SchedulerUtil
import org.bukkit.World
import kotlin.math.round

abstract class Endgame : Phase() {
	override fun customStart() {
		SchedulerUtil.nextTick {
			val defaultWorld = UHC.getDefaultWorldGame()

			PlayerData.playerDataList.forEach { (uuid, playerData) ->
				val location = GameRunner.getPlayerLocation(uuid)

				if (location != null && location.world !== defaultWorld) {
					GameRunner.playerAction(uuid) { player -> Commands.errorMessage(player, "Failed to return to home dimension!") }
					GameRunner.damagePlayer(uuid, 100000000000.0)
				}
			}
		}

		val world = UHC.getDefaultWorldGame()
		world.worldBorder.size = (UHC.endRadius() * 2 + 1).toDouble()
	}

	companion object {
		fun determineMinMax(world: World, radius: Int, maxHeight: Int): Pair<Int, Int> {
			/* store every recorded height of every x z coordinate within the radius */
			val heightList = ArrayList<Int>((radius * 2 + 1) * (radius * 2 + 1))

			for (x in -radius..radius) {
				for (z in -radius..radius) {
					var solidCount = 0
					var topLevel = 0
					var foundLevel = false

					/* start looking for solid blocks down from maxHeight */
					for (y in maxHeight downTo 0) {
						val solid = !world.getBlockAt(x, y, z).isPassable

						/* the first solid block hit is recorded as the top level */
						if (solidCount == 0) {
							if (solid) {
								topLevel = y
								solidCount = 1
							}
							/* need a chain of 6 solid blocks below top level to count as ground */
						} else {
							if (solid) {
								++solidCount
								if (solidCount == 6) {
									foundLevel = true
									break
								}
							} else {
								solidCount = 0
							}
						}
					}

					/* only record heights if ground was found at this coordinate */
					if (foundLevel) heightList.add(topLevel)
				}
			}

			/* order the height list to find percentiles */
			heightList.sort()

			/* data not enough to describe the zone */
			return if (heightList.size < 32) {
				Pair(58, 66)

			} else {
				/* range is from 10th percentile to 90th percentile */
				var min = heightList[round(heightList.size * 0.10).toInt().coerceAtMost(heightList.lastIndex)]
				var max = heightList[round(heightList.size * 0.90).toInt().coerceAtMost(heightList.lastIndex)]

				val rangeSize = max - min + 1

				/* endgame range must be at least 9 blocks */
				if (rangeSize < 9) {
					val addedDistance = 9 - rangeSize
					val topAdded = addedDistance / 2
					val bottomAdded = addedDistance - topAdded

					max += topAdded
					min -= bottomAdded
				}

				Pair(min, max)
			}
		}
	}
}
