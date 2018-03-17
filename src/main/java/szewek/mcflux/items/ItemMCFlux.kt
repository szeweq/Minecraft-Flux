package szewek.mcflux.items

import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.world.World

open class ItemMCFlux : Item() {
	override fun addInformation(stack: ItemStack, w: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
		val key = getUnlocalizedName(stack) + ".desc"
		if (I18n.hasKey(key))
			tooltip.add(I18n.format(key))
		super.addInformation(stack, w, tooltip, flagIn)
	}
}
