package com.codeland.uhc.event

import com.codeland.uhc.UHCPlugin
import com.codeland.uhc.core.*
import com.codeland.uhc.phase.PhaseType
import com.codeland.uhc.world.*
import org.bukkit.Bukkit
import org.bukkit.Chunk
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.world.ChunkPopulateEvent
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import kotlin.math.abs
import kotlin.math.ceil

class Generation : Listener {
	@EventHandler
	fun onChunkLoad(event: ChunkPopulateEvent) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(UHCPlugin.plugin) {
			val world = event.world
			val chunk = event.chunk

			/* prevent animal spawns in the waiting area */
			if (GameRunner.uhc.isPhase(PhaseType.WAITING) && world.environment == World.Environment.NORMAL && (abs(chunk.x) > 10 || abs(chunk.z) > 10)) {
				chunk.entities.forEach { entity ->
					if (entity !is Player) entity.remove()
				}
			}

			if (GameRunner.netherWorldFix && world.environment == World.Environment.NETHER) {
				NetherFix.wartPlacer.onGenerate(chunk, world.seed.toInt(), -1)
			}

			if (GameRunner.oreWorldFix && world.environment == World.Environment.NORMAL) {
				OreFix.removeMinerals(chunk)
				OreFix.removeOres(chunk)
				OreFix.reduceLava(chunk)

				OreFix.diamondPlacer.onGenerate(chunk, world.seed.toInt(), GameRunner.uhc.startRadius)
				OreFix.goldPlacer.onGenerate(chunk, world.seed.toInt(), GameRunner.uhc.startRadius)
				OreFix.lapisPlacer.onGenerate(chunk, world.seed.toInt(), GameRunner.uhc.startRadius)
				OreFix.mineralPlacer.onGenerate(chunk, world.seed.toInt(), GameRunner.uhc.startRadius)
			}

			if (GameRunner.mushroomWorldFix && world.environment == World.Environment.NORMAL) {
				StewFix.removeOxeye(chunk)

				StewFix.redMushroomPlacer.onGenerate(chunk, world.seed.toInt(), GameRunner.uhc.startRadius)
				StewFix.brownMushroomPlacer.onGenerate(chunk, world.seed.toInt(), GameRunner.uhc.startRadius)
			}

			if (GameRunner.melonWorldFix && world.environment == World.Environment.NORMAL) {
				MelonFix.melonPlacer.onGenerate(chunk, world.seed.toInt(), GameRunner.uhc.startRadius)
			}

			if (GameRunner.halloweenGeneration) {
				if (world.environment == World.Environment.NORMAL) {
					HalloweenWorld.pumpkinPlacer.onGenerate(chunk, world.seed.toInt(), GameRunner.uhc.startRadius)
					HalloweenWorld.deadBushPlacer.onGenerate(chunk, world.seed.toInt(), GameRunner.uhc.startRadius)
				}

				HalloweenWorld.lanternPlacer.onGenerate(chunk, world.seed.toInt(), GameRunner.uhc.startRadius)
				HalloweenWorld.cobwebPlacer.onGenerate(chunk, world.seed.toInt(), GameRunner.uhc.startRadius)
				HalloweenWorld.bannerPlacer.onGenerate(chunk, world.seed.toInt(), GameRunner.uhc.startRadius)
				HalloweenWorld.bricksPlacer.onGenerate(chunk, world.seed.toInt(), GameRunner.uhc.startRadius)
			}

			//diamondPictureChunk(chunk)
		}
	}

	val numChunks = ceil(1001 / 16.0).toInt()
	val offset = numChunks / 2
	val img = BufferedImage(numChunks, numChunks, BufferedImage.TYPE_INT_ARGB)

	fun diamondPictureChunk(chunk: Chunk) {
		val x = chunk.x + offset
		val z = chunk.z + offset

		if (x >= 0 && z >= 0 && x < numChunks && z < numChunks) {
			var caveCount = 0

			for (x in 0..15) for (z in 0..15) for (y in 11..15)
				if (chunk.getBlock(x, y, x).isPassable) ++caveCount


			var caveValue = caveCount / (16 * 16 * 2f)
			if (caveValue > 1f) caveValue = 1f

			img.setRGB(x, z, (caveValue * 0xff).toInt().shl(16).or(0xff000000.toInt()))
			ImageIO.write(img, "png", File("diamondCaves.png"))
		}
	}
}
