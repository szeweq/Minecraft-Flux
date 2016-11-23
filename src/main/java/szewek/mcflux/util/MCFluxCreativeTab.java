package szewek.mcflux.util;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import szewek.mcflux.MCFlux;
import szewek.mcflux.R;

public class MCFluxCreativeTab extends CreativeTabs {
	private ItemStack iconStack;
	public MCFluxCreativeTab() {
		super(R.MF_NAME);
	}
	public final void init() {
		iconStack = new ItemStack(MCFlux.MFTOOL);
	}
	@Override public ItemStack getTabIconItem() {
		return iconStack;
	}
}
