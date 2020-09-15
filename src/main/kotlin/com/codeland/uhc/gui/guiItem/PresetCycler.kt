package com.codeland.uhc.gui.guiItem

import com.codeland.uhc.core.Preset
import com.codeland.uhc.core.Preset.Companion.NO_PRESET_REPRESENTATION
import com.codeland.uhc.core.UHC
import com.codeland.uhc.gui.GuiItem
import com.codeland.uhc.phase.PhaseType
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class PresetCycler(uhc: UHC, index: Int) : GuiItem(uhc, index, true) {
	override fun onClick(player: Player, shift: Boolean) {
		val oldPreset = uhc.preset

		uhc.updatePreset(Preset.values()[
			if (oldPreset == null) 0
			else (oldPreset.ordinal + 1) % Preset.values().size
		])
	}

	override fun getStack(): ItemStack {
		val preset = uhc.preset
		val stack = ItemStack(preset?.representation ?: NO_PRESET_REPRESENTATION)

		setLore(stack, preset?.createLore() ?: Preset.createLore(
			uhc.startRadius,
			uhc.endRadius,
			uhc.getTime(PhaseType.GRACE),
			uhc.getTime(PhaseType.SHRINK),
			uhc.getTime(PhaseType.FINAL)
		))

		setName(stack, "${ChatColor.WHITE}Preset ${ChatColor.GRAY}- ${ChatColor.GOLD}${ChatColor.BOLD}${preset?.prettyName ?: "Custom"}")

		return stack
	}
}
