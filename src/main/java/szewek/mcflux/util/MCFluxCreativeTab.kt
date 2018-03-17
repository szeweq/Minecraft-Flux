package szewek.mcflux.util

import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.ItemStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import szewek.mcflux.MCFlux
import szewek.mcflux.R

class MCFluxCreativeTab : CreativeTabs(R.MF_NAME) {

	@SideOnly(Side.CLIENT)
	override fun getTabIconItem() = ItemStack(MCFlux.Resources.MFTOOL)

	override fun hasSearchBar() = true
}
