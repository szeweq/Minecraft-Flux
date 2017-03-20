package szewek.mcflux.util;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import szewek.mcflux.MCFluxResources;
import szewek.mcflux.R;

import javax.annotation.Nonnull;

public final class MCFluxCreativeTab extends CreativeTabs {
	public MCFluxCreativeTab() {super(R.MF_NAME);}

	@SideOnly(Side.CLIENT) @Nonnull @Override public ItemStack getTabIconItem() {return new ItemStack(MCFluxResources.MFTOOL);}

	@Override public boolean hasSearchBar() {return true;}
}
