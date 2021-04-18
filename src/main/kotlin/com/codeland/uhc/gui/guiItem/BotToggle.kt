package com.codeland.uhc.gui.guiItem

import com.codeland.uhc.core.GameRunner
import com.codeland.uhc.core.UHC
import com.codeland.uhc.gui.Gui
import com.codeland.uhc.gui.GuiInventory
import com.codeland.uhc.gui.GuiItem
import com.codeland.uhc.quirk.QuirkType
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class BotToggle(index: Int) : GuiItem(index, true) {
	override fun onClick(player: Player, shift: Boolean) {
		UHC.updateUsingBot(!UHC.usingBot)
	}

	override fun getStack(): ItemStack {
		val stack = if (GameRunner.bot == null)
			setName(ItemStack(Material.GUNPOWDER), "${ChatColor.RED}${ChatColor.BOLD}Bot is not running")
		else
			setName(ItemStack(if (UHC.usingBot) Material.NAUTILUS_SHELL else Material.HONEYCOMB), enabledName("Bot VCs", UHC.usingBot))

		setLore(stack, listOf("Separate teams into separate discord vcs?"))

		return stack
	}
}