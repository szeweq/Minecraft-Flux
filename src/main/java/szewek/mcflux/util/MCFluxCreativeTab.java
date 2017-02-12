package szewek.mcflux.util;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import szewek.mcflux.MCFluxResources;
import szewek.mcflux.R;

import javax.annotation.Nonnull;

public final class MCFluxCreativeTab extends CreativeTabs {
	public MCFluxCreativeTab() {
		super(R.MF_NAME);
	}
	@Nonnull @Override public Item getTabIconItem() {
		return MCFluxResources.MFTOOL;
	}
}
