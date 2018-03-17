package szewek.mcflux

import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing.*
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import szewek.fl.energy.IEnergy

object U {
	val FANCY_FACING = arrayOf(DOWN, UP, NORTH, SOUTH, WEST, EAST, null)

	fun formatMF(ie: IEnergy) = "${ie.energy} / ${ie.energyCapacity} F"

	@SideOnly(Side.CLIENT)
	@JvmStatic
	fun registerItemMultiModels(item: Item, m: Int) {
		val rl = item.registryName
		MCFlux.L!!.info("MULTI MODELS for $rl")
		for (i in 0 until m) {
			val stk = ItemStack(item, 1, i)
			val mrl = ModelResourceLocation(item.getUnlocalizedName(stk).substring(5), "inventory")
			net.minecraftforge.client.model.ModelLoader.setCustomModelResourceLocation(item, i, mrl)
		}
	}
}
