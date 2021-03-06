package com.codeland.uhc.gui.guiItem

import com.codeland.uhc.core.KillReward
import com.codeland.uhc.core.UHC
import com.codeland.uhc.gui.GuiItem
import com.codeland.uhc.gui.GuiItemProperty
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class KillRewardCycler(index: Int) : GuiItemProperty <KillReward> (index, UHC.killReward) {
	override fun onClick(player: Player, shift: Boolean) {
		UHC.killReward.set(
			KillReward.values()[(UHC.killReward.get().ordinal + 1) % KillReward.values().size]
		)
	}

	override fun getStackProperty(value: KillReward): ItemStack {
		return name(lore(ItemStack(value.representation), value.lore), stateName("Kill Reward", value.prettyName))
	}
}
