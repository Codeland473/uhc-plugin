package com.codeland.uhc.quirk.quirks

import com.codeland.uhc.core.UHC
import com.codeland.uhc.quirk.Quirk
import com.codeland.uhc.quirk.QuirkType
import org.bukkit.Material.*
import org.bukkit.inventory.ItemStack

class Creative(type: QuirkType) : Quirk(type) {
	override fun onEnable() {}

	override fun onDisable() {}

	override val representation: ItemStack
		get() = ItemStack(SCAFFOLDING)

	companion object {
		val blocks = arrayOf(
			MOSSY_COBBLESTONE,
			MOSSY_COBBLESTONE_SLAB,
			MOSSY_COBBLESTONE_STAIRS,
			MOSSY_COBBLESTONE_WALL,

			SMOOTH_STONE_SLAB,

			MOSSY_STONE_BRICKS,

			BRICKS,
			BRICK_SLAB,
			BRICK_STAIRS,
			BRICK_WALL,

			RED_NETHER_BRICKS,
			RED_NETHER_BRICK_SLAB,
			RED_NETHER_BRICK_STAIRS,
			RED_NETHER_BRICK_WALL,
			CRACKED_NETHER_BRICKS,
			CHISELED_NETHER_BRICKS,
			NETHER_BRICK_FENCE,
			NETHER_BRICK_STAIRS,
			NETHER_BRICK_SLAB,
			NETHER_BRICK_WALL,

			CRACKED_POLISHED_BLACKSTONE_BRICKS,
			CHISELED_POLISHED_BLACKSTONE,
			POLISHED_BLACKSTONE_BRICK_SLAB,
			POLISHED_BLACKSTONE_BRICK_STAIRS,
			POLISHED_BLACKSTONE_BRICK_WALL,

			SOUL_LANTERN,
			CAMPFIRE,
			SOUL_TORCH,
			SOUL_CAMPFIRE,
			REDSTONE_LAMP,
			JACK_O_LANTERN,

			POLISHED_GRANITE_SLAB,
			POLISHED_GRANITE_STAIRS,

			POLISHED_ANDESITE_SLAB,
			POLISHED_ANDESITE_STAIRS,

			POLISHED_DIORITE_SLAB,
			POLISHED_DIORITE_STAIRS,

			STONECUTTER,
			SMITHING_TABLE,
			FLETCHING_TABLE,
			LOOM,
			ANVIL,
			LODESTONE,

			BLUE_ICE,
			JUKEBOX,

			RED_SAND,
			RED_SANDSTONE_SLAB,
			RED_SANDSTONE_STAIRS,
			RED_SANDSTONE_WALL,

			OAK_FENCE,
			SPRUCE_FENCE,
			DARK_OAK_FENCE,
			BIRCH_FENCE,
			ACACIA_FENCE,
			JUNGLE_FENCE,
			WARPED_FENCE,
			CRIMSON_FENCE,
			OAK_PRESSURE_PLATE,
			SPRUCE_PRESSURE_PLATE,
			DARK_OAK_PRESSURE_PLATE,
			BIRCH_PRESSURE_PLATE,
			ACACIA_PRESSURE_PLATE,
			JUNGLE_PRESSURE_PLATE,
			WARPED_PRESSURE_PLATE,
			CRIMSON_PRESSURE_PLATE,
			OAK_BUTTON,
			SPRUCE_BUTTON,
			DARK_OAK_BUTTON,
			BIRCH_BUTTON,
			ACACIA_BUTTON,
			JUNGLE_BUTTON,
			WARPED_BUTTON,
			CRIMSON_BUTTON,

			REDSTONE,
			PISTON,
			STICKY_PISTON,
			REPEATER,
			COMPARATOR,
			REDSTONE_BLOCK,
			REDSTONE_TORCH,
			OBSERVER,
			HOPPER,
			DISPENSER,
			TARGET,
			STONE_PRESSURE_PLATE,
			HEAVY_WEIGHTED_PRESSURE_PLATE,
			LIGHT_WEIGHTED_PRESSURE_PLATE,
			LEVER,
			STONE_BUTTON,

			OAK_DOOR,
			SPRUCE_DOOR,
			DARK_OAK_DOOR,
			BIRCH_DOOR,
			ACACIA_DOOR,
			JUNGLE_DOOR,
			WARPED_DOOR,
			CRIMSON_DOOR,

			OAK_TRAPDOOR,
			SPRUCE_TRAPDOOR,
			DARK_OAK_TRAPDOOR,
			BIRCH_TRAPDOOR,
			ACACIA_TRAPDOOR,
			JUNGLE_TRAPDOOR,
			WARPED_TRAPDOOR,
			CRIMSON_TRAPDOOR,

			CACTUS,

			FLOWER_POT,

			BLACK_CONCRETE,
			BLUE_CONCRETE,
			RED_CONCRETE,
			ORANGE_CONCRETE,
			YELLOW_CONCRETE,
			LIME_CONCRETE,
			GREEN_CONCRETE,
			LIGHT_BLUE_CONCRETE,
			PURPLE_CONCRETE,
			MAGENTA_CONCRETE,
			WHITE_CONCRETE,
			LIGHT_GRAY_CONCRETE,
			GRAY_CONCRETE,
			CYAN_CONCRETE,
			BROWN_CONCRETE,
			PINK_CONCRETE,

			BLACK_CONCRETE_POWDER,
			BLUE_CONCRETE_POWDER,
			RED_CONCRETE_POWDER,
			ORANGE_CONCRETE_POWDER,
			YELLOW_CONCRETE_POWDER,
			LIME_CONCRETE_POWDER,
			GREEN_CONCRETE_POWDER,
			LIGHT_BLUE_CONCRETE_POWDER,
			PURPLE_CONCRETE_POWDER,
			MAGENTA_CONCRETE_POWDER,
			WHITE_CONCRETE_POWDER,
			LIGHT_GRAY_CONCRETE_POWDER,
			GRAY_CONCRETE_POWDER,
			CYAN_CONCRETE_POWDER,
			BROWN_CONCRETE_POWDER,
			PINK_CONCRETE_POWDER,

			BLACK_CARPET,
			BLUE_CARPET,
			RED_CARPET,
			ORANGE_CARPET,
			YELLOW_CARPET,
			LIME_CARPET,
			GREEN_CARPET,
			LIGHT_BLUE_CARPET,
			PURPLE_CARPET,
			MAGENTA_CARPET,
			WHITE_CARPET,
			LIGHT_GRAY_CARPET,
			GRAY_CARPET,
			CYAN_CARPET,
			BROWN_CARPET,
			PINK_CARPET,

			SMOOTH_QUARTZ,
			SMOOTH_QUARTZ_SLAB,
			SMOOTH_QUARTZ_STAIRS,
			QUARTZ_BRICKS,
			QUARTZ_PILLAR,
			QUARTZ_SLAB,
			QUARTZ_STAIRS,

			LADDER,
			VINE,

			CHAIN,
			IRON_BARS,

			BLACK_STAINED_GLASS,
			BLUE_STAINED_GLASS,
			RED_STAINED_GLASS,
			ORANGE_STAINED_GLASS,
			YELLOW_STAINED_GLASS,
			LIME_STAINED_GLASS,
			GREEN_STAINED_GLASS,
			LIGHT_BLUE_STAINED_GLASS,
			PURPLE_STAINED_GLASS,
			MAGENTA_STAINED_GLASS,
			WHITE_STAINED_GLASS,
			LIGHT_GRAY_STAINED_GLASS,
			GRAY_STAINED_GLASS,
			CYAN_STAINED_GLASS,
			BROWN_STAINED_GLASS,
			PINK_STAINED_GLASS,

			BLACK_STAINED_GLASS_PANE,
			BLUE_STAINED_GLASS_PANE,
			RED_STAINED_GLASS_PANE,
			ORANGE_STAINED_GLASS_PANE,
			YELLOW_STAINED_GLASS_PANE,
			LIME_STAINED_GLASS_PANE,
			GREEN_STAINED_GLASS_PANE,
			LIGHT_BLUE_STAINED_GLASS_PANE,
			PURPLE_STAINED_GLASS_PANE,
			MAGENTA_STAINED_GLASS_PANE,
			WHITE_STAINED_GLASS_PANE,
			LIGHT_GRAY_STAINED_GLASS_PANE,
			GRAY_STAINED_GLASS_PANE,
			CYAN_STAINED_GLASS_PANE,
			BROWN_STAINED_GLASS_PANE,
			PINK_STAINED_GLASS_PANE
		)

		init {
			blocks.sort()
		}
	}
}
