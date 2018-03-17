package szewek.mcflux.blocks.itemblocks

import net.minecraft.block.Block
import net.minecraft.client.resources.I18n
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.fml.common.registry.GameRegistry
import szewek.mcflux.blocks.BlockEnergyMachine

class ItemBlockEnergyMachine(block: Block, ct: CreativeTabs) : ItemBlock(block) {

	init {
		setHasSubtypes(true)
		creativeTab = ct
		unlocalizedName = "mcflux:energy_machine"
		setRegistryName("mcflux:energy_machine")
		GameRegistry.findRegistry(Item::class.java).register(this)
	}

	override fun getMetadata(damage: Int): Int {
		return damage % BlockEnergyMachine.Variant.ALL_VARIANTS.size
	}

	override fun getUnlocalizedName(stack: ItemStack): String {
		return "tile.mcflux:" + BlockEnergyMachine.Variant.nameFromStack(stack)
	}

	override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
		tooltip += I18n.format(getUnlocalizedName(stack) + ".desc")
		super.addInformation(stack, worldIn, tooltip, flagIn)
	}
}
