package szewek.mcflux.blocks.itemblocks

import net.minecraft.block.Block
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.world.World

class ItemMCFluxBlock(block: Block) : ItemBlock(block) {

	override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
		tooltip += I18n.format(getUnlocalizedName(stack) + ".desc")
		super.addInformation(stack, worldIn, tooltip, flagIn)
	}
}
