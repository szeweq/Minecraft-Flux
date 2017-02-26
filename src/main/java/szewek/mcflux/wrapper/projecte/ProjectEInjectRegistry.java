package szewek.mcflux.wrapper.projecte;

import moze_intel.projecte.api.item.IItemEmc;
import moze_intel.projecte.api.tile.IEmcStorage;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import szewek.mcflux.api.fe.Flavored;
import szewek.mcflux.api.fe.FlavoredImmutable;
import szewek.mcflux.util.IInjectRegistry;
import szewek.mcflux.util.InjectCond;
import szewek.mcflux.util.InjectRegistry;
import szewek.mcflux.wrapper.InjectCollector;
import szewek.mcflux.wrapper.InjectWrappers;
import szewek.mcflux.wrapper.WrapperRegistry;

@InjectRegistry(requires = InjectCond.MOD, args = "ProjectE")
public final class ProjectEInjectRegistry implements IInjectRegistry {
	static final String EMC = "projecte:emc", EMC_NAME = "emc";
	static final Flavored[] emcFill = new Flavored[]{new FlavoredImmutable(EMC, null)};

	@Override public void registerInjects() {
		InjectCollector ic = InjectWrappers.getCollector();
		if (ic == null)
			return;
		ic.addTileWrapperInject(ProjectEInjectRegistry::wrapEMCTile);
		ic.addItemWrapperInject(ProjectEInjectRegistry::wrapEMCItem);
	}

	private static void wrapEMCTile(TileEntity te, WrapperRegistry reg) {
		if (te instanceof IEmcStorage) {
			reg.register(EMC_NAME, new EMCFlavorWrapper((IEmcStorage) te));
		}
	}

	private static void wrapEMCItem(ItemStack is, WrapperRegistry reg) {
		if (is.getItem() instanceof IItemEmc) {
			reg.register(EMC_NAME, new EMCFlavorItemWrapper(is));
		}
	}
}
