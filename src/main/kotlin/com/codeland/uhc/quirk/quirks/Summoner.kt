package com.codeland.uhc.quirk.quirks

import com.codeland.uhc.UHCPlugin
import com.codeland.uhc.core.GameRunner
import com.codeland.uhc.core.UHC
import com.codeland.uhc.gui.GuiItem
import com.codeland.uhc.quirk.Quirk
import com.codeland.uhc.quirk.QuirkType
import com.codeland.uhc.util.Util
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.Material.*
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.EntityType.*
import org.bukkit.entity.Player
import org.bukkit.event.block.Action
import org.bukkit.event.entity.CreatureSpawnEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.metadata.FixedMetadataValue

class Summoner(uhc: UHC, type: QuirkType) : Quirk(uhc, type) {
	override fun onEnable() {}

	override fun onDisable() {
		/* remove commanded tag from all commanded mobs */
		Bukkit.getWorlds().forEach { world ->
			world.entities.forEach { entity ->
				if (isCommanded(entity)) setCommandedByNone(entity)
			}
		}
	}

	var allowAggro = true
	var allowPassive = true
	var commander = true

	init {
		inventory.addItem(object : GuiItem(inventory, uhc, 11, true) {
			override fun onClick(player: Player, shift: Boolean) {
				allowAggro = !allowAggro
			}
			override fun getStack(): ItemStack {
				return if (allowAggro)
					setName(ItemStack(CREEPER_SPAWN_EGG), "${ChatColor.RESET}Aggro ${ChatColor.GRAY}- ${ChatColor.GREEN}Allowed")
				else
					setName(ItemStack(GUNPOWDER), "${ChatColor.RESET}Aggro ${ChatColor.GRAY}- ${ChatColor.RED}Disallowed")
			}
		})

		inventory.addItem(object : GuiItem(inventory, uhc, 15, true) {
			override fun onClick(player: Player, shift: Boolean) {
				allowPassive = !allowPassive
			}
			override fun getStack(): ItemStack {
				return if (allowPassive)
					setName(ItemStack(CHICKEN_SPAWN_EGG), "${ChatColor.RESET}Passive ${ChatColor.GRAY}- ${ChatColor.GREEN}Allowed")
				else
					setName(ItemStack(FEATHER), "${ChatColor.RESET}Passive ${ChatColor.GRAY}- ${ChatColor.RED}Disallowed")
			}
		})

		inventory.addItem(object : GuiItem(inventory, uhc, 22, true) {
			override fun onClick(player: Player, shift: Boolean) {
				commander = !commander
			}
			override fun getStack(): ItemStack {
				return if (commander)
					setName(ItemStack(NETHERITE_HELMET), "${ChatColor.RESET}Summoner ${ChatColor.GRAY}- ${ChatColor.GREEN}Allowed")
				else
					setName(ItemStack(LEATHER_HELMET), "${ChatColor.RESET}Summoner ${ChatColor.GRAY}- ${ChatColor.RED}Disallowed")
			}
		})
	}

	fun getSpawnEgg(entity: EntityType): Material? {
		return getSpawnEgg(entity, allowAggro, allowPassive)
	}

	fun onSummon(event: PlayerInteractEvent): Boolean {
		if (event.action != Action.RIGHT_CLICK_BLOCK) return false

		val item = event.item ?: return false

		val block = event.clickedBlock ?: return false

		val type = getSpawnEntity(item.type, true, true) ?: return false

		val location = block.location.add(event.blockFace.direction).toCenterLocation()
		val entity = event.player.world.spawnEntity(location, type, CreatureSpawnEvent.SpawnReason.SPAWNER_EGG)

		val team = GameRunner.playersTeam(event.player.name)
		if (team != null) {
			setCommandedBy(entity, team.color)

			if (commander) entity.customName = "${team.color}${team.displayName}${net.md_5.bungee.api.ChatColor.RESET} ${entity.name}"
		}

		--item.amount

		return true
	}

	companion object {
		class Summon(var type: EntityType, var egg: Material)

		private val aggroSummons = arrayOf(
			Summon(ELDER_GUARDIAN, ELDER_GUARDIAN_SPAWN_EGG),
			Summon(WITHER_SKELETON, WITHER_SKELETON_SPAWN_EGG),
			Summon(STRAY, STRAY_SPAWN_EGG),
			Summon(HUSK, HUSK_SPAWN_EGG),
			Summon(ZOMBIE_VILLAGER, ZOMBIE_VILLAGER_SPAWN_EGG),
			Summon(SKELETON_HORSE, SKELETON_HORSE_SPAWN_EGG),
			Summon(ZOMBIE_HORSE, ZOMBIE_HORSE_SPAWN_EGG),
			Summon(DONKEY, DONKEY_SPAWN_EGG),
			Summon(EVOKER, EVOKER_SPAWN_EGG),
			Summon(VINDICATOR, VINDICATOR_SPAWN_EGG),
			Summon(CREEPER, CREEPER_SPAWN_EGG),
			Summon(SKELETON, SKELETON_SPAWN_EGG),
			Summon(SPIDER, SPIDER_SPAWN_EGG),
			Summon(ZOMBIE, ZOMBIE_SPAWN_EGG),
			Summon(SLIME, SLIME_SPAWN_EGG),
			Summon(GHAST, GHAST_SPAWN_EGG),
			Summon(ZOMBIFIED_PIGLIN, ZOMBIFIED_PIGLIN_SPAWN_EGG),
			Summon(ENDERMAN, ENDERMAN_SPAWN_EGG),
			Summon(CAVE_SPIDER, CAVE_SPIDER_SPAWN_EGG),
			Summon(SILVERFISH, SILVERFISH_SPAWN_EGG),
			Summon(BLAZE, BLAZE_SPAWN_EGG),
			Summon(MAGMA_CUBE, MAGMA_CUBE_SPAWN_EGG),
			Summon(WITCH, WITCH_SPAWN_EGG),
			Summon(ENDERMITE, ENDERMITE_SPAWN_EGG),
			Summon(GUARDIAN, GUARDIAN_SPAWN_EGG),
			Summon(SHULKER, SHULKER_SPAWN_EGG),
			Summon(WOLF, WOLF_SPAWN_EGG),
			Summon(POLAR_BEAR, POLAR_BEAR_SPAWN_EGG),
			Summon(LLAMA, LLAMA_SPAWN_EGG),
			Summon(PHANTOM, PHANTOM_SPAWN_EGG),
			Summon(DROWNED, DROWNED_SPAWN_EGG),
			Summon(DOLPHIN, DOLPHIN_SPAWN_EGG),
			Summon(PILLAGER, PILLAGER_SPAWN_EGG),
			Summon(RAVAGER, RAVAGER_SPAWN_EGG),
			Summon(HOGLIN, HOGLIN_SPAWN_EGG),
			Summon(PIGLIN, PIGLIN_SPAWN_EGG),
			Summon(ZOGLIN, ZOGLIN_SPAWN_EGG),
			Summon(VEX, VEX_SPAWN_EGG)
		)

		private val inverseAggroSummons = aggroSummons.copyOf()

		private val passiveSummons = arrayOf(
			Summon(MULE, MULE_SPAWN_EGG),
			Summon(BAT, BAT_SPAWN_EGG),
			Summon(PIG, PIG_SPAWN_EGG),
			Summon(SHEEP, SHEEP_SPAWN_EGG),
			Summon(COW, COW_SPAWN_EGG),
			Summon(EntityType.CHICKEN, CHICKEN_SPAWN_EGG),
			Summon(SQUID, SQUID_SPAWN_EGG),
			Summon(MUSHROOM_COW, MOOSHROOM_SPAWN_EGG),
			Summon(OCELOT, OCELOT_SPAWN_EGG),
			Summon(HORSE, HORSE_SPAWN_EGG),
			Summon(EntityType.RABBIT, RABBIT_SPAWN_EGG),
			Summon(PARROT, PARROT_SPAWN_EGG),
			Summon(VILLAGER, VILLAGER_SPAWN_EGG),
			Summon(TURTLE, TURTLE_SPAWN_EGG),
			Summon(EntityType.COD, COD_SPAWN_EGG),
			Summon(EntityType.SALMON, SALMON_SPAWN_EGG),
			Summon(EntityType.PUFFERFISH, PUFFERFISH_SPAWN_EGG),
			Summon(EntityType.TROPICAL_FISH, TROPICAL_FISH_SPAWN_EGG),
			Summon(CAT, CAT_SPAWN_EGG),
			Summon(PANDA, PANDA_SPAWN_EGG),
			Summon(TRADER_LLAMA, TRADER_LLAMA_SPAWN_EGG),
			Summon(WANDERING_TRADER, WANDERING_TRADER_SPAWN_EGG),
			Summon(FOX, FOX_SPAWN_EGG),
			Summon(BEE, BEE_SPAWN_EGG),
			Summon(STRIDER, STRIDER_SPAWN_EGG)
		)

		private val inversePassiveSummons = passiveSummons.copyOf()

		init {
			aggroSummons.sortBy { summon -> summon.type }
			passiveSummons.sortBy { summon -> summon.type }
			inverseAggroSummons.sortBy { summon -> summon.egg }
			inversePassiveSummons.sortBy { summon -> summon.egg }
		}

		fun getSpawnEgg(entity: EntityType, allowAggro: Boolean, allowPassive: Boolean): Material? {
			var ret = null as Summon?

			if (allowAggro)
				ret = Util.binaryFind(entity, aggroSummons) { summon -> summon.type }

			if (ret == null && allowPassive)
				ret = Util.binaryFind(entity, passiveSummons) { summon -> summon.type }

			return ret?.egg
		}

		fun getSpawnEntity(egg: Material, allowAggro: Boolean, allowPassive: Boolean): EntityType? {
			var ret = null as Summon?

			if (allowAggro)
				ret = Util.binaryFind(egg, inverseAggroSummons) { summon -> summon.egg }

			if (ret == null && allowPassive)
				ret = Util.binaryFind(egg, inversePassiveSummons) { summon -> summon.egg }

			return ret?.type
		}

		fun randomPassiveEgg(amount: Int): ItemStack {
			return ItemStack(Util.randFromArray(passiveSummons).egg, amount)
		}

		fun randomAggroEgg(amount: Int): ItemStack {
			return ItemStack(Util.randFromArray(aggroSummons).egg, amount)
		}

		/* COMMADNER */

		const val META_TAG = "commandedBy"

		fun setCommandedBy(entity: Entity, color: ChatColor) {
			entity.setMetadata(META_TAG, FixedMetadataValue(UHCPlugin.plugin, color))
		}

		fun setCommandedByNone(entity: Entity) {
			entity.removeMetadata(META_TAG, UHCPlugin.plugin)

			entity.customName = null
		}

		fun isCommandedBy(entity: Entity, color: ChatColor): Boolean {
			val meta = entity.getMetadata(META_TAG)

			return (meta.size > 0 && (meta[0].value() as ChatColor) == color)
		}

		fun isCommanded(entity: Entity): Boolean {
			val meta = entity.getMetadata(META_TAG)

			return meta.size > 0
		}
	}
}