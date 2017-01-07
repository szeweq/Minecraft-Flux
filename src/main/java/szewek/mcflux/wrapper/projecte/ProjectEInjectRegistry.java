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
import szewek.mcflux.util.MCFluxLocation;
import szewek.mcflux.wrapper.InjectWrappers;

@InjectRegistry(requires = InjectCond.MOD, args = "ProjectE")
public class ProjectEInjectRegistry implements IInjectRegistry {
	static final String EMC = "projecte:emc";
	static final Flavored[] emcFill = new Flavored[]{new FlavoredImmutable(EMC, null)};
	private static final MCFluxLocation EMC_RL = new MCFluxLocation("emc");
	@Override public void registerInjects() {
		InjectWrappers.addTileWrapperInject(ProjectEInjectRegistry::wrapEMCTile);
		InjectWrappers.addItemWrapperInject(ProjectEInjectRegistry::wrapEMCItem);
	}

	private static void wrapEMCTile(TileEntity te, InjectWrappers.Registry reg) {
		if (te instanceof IEmcStorage) {
			reg.register(EMC_RL, new EMCFlavorWrapper((IEmcStorage) te));
		}
	}

	private static void wrapEMCItem(ItemStack is, InjectWrappers.Registry reg) {
		if (is.getItem() instanceof IItemEmc) {
			reg.register(EMC_RL, new EMCFlavorItemWrapper(is));
		}
	}
}
