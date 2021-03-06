package com.codeland.uhc.blockfix

import com.codeland.uhc.util.Util
import com.codeland.uhc.util.Util.binarySearch
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

class LeavesFix : BlockFix("Leaves", arrayOf(
	Range("apple", "appleCount", "appleIndex", 200, { leaves, _ -> ItemStack(Material.APPLE) }, { leaves, drops ->
		val drop = drops.firstOrNull() ?: return@Range null
		if (isLeaves(drop.type)) drop else null
	}),
	Range("stick", "stickCount", "stickIndex", 50, { leaves, _ -> ItemStack(Material.STICK, Util.randRange(1, 2)) }),
	Range("sapling", "saplingCount", "saplingIndex", 20, { leaves, _ ->
		ItemStack(Util.binaryFind(leaves, leavesInfo) { info -> info.leaves }?.sapling ?: Material.OAK_SAPLING)
	})
)) {
	init {
		leavesInfo.sortBy { info -> info.leaves }
	}

	override fun isBlock(material: Material): Boolean {
		return isLeaves(material)
	}

	override fun reject(tool: ItemStack, drops: List<ItemStack>): Boolean {
		return isSilkTouch(tool)
	}

	override fun allowTool(tool: ItemStack): Boolean {
		return true
	}

	companion object {
		class LeafInfo(var leaves: Material, var sapling: Material)

		fun isLeaves(material: Material): Boolean {
			return binarySearch(material, leavesInfo) { leafInfo -> leafInfo.leaves }
		}

		val leavesInfo = arrayOf(
			LeafInfo(Material.OAK_LEAVES, Material.OAK_SAPLING),
			LeafInfo(Material.SPRUCE_LEAVES, Material.SPRUCE_SAPLING),
			LeafInfo(Material.BIRCH_LEAVES, Material.BIRCH_SAPLING),
			LeafInfo(Material.JUNGLE_LEAVES, Material.JUNGLE_SAPLING),
			LeafInfo(Material.ACACIA_LEAVES, Material.ACACIA_SAPLING),
			LeafInfo(Material.DARK_OAK_LEAVES, Material.DARK_OAK_SAPLING)
		)
	}
}
