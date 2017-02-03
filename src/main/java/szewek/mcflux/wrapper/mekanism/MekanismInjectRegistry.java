package szewek.mcflux.wrapper.mekanism;

import mekanism.api.energy.IEnergizedItem;
import mekanism.api.energy.IStrictEnergyAcceptor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import szewek.mcflux.util.IInjectRegistry;
import szewek.mcflux.util.InjectCond;
import szewek.mcflux.util.InjectRegistry;
import szewek.mcflux.wrapper.EnergyType;
import szewek.mcflux.wrapper.InjectWrappers;

@InjectRegistry(requires = InjectCond.MOD, args = {"Mekanism", "mekanism"})
public class MekanismInjectRegistry implements IInjectRegistry {
	@Override public void registerInjects() {
		InjectWrappers.addTileWrapperInject(MekanismInjectRegistry::wrapTile);
		InjectWrappers.addItemWrapperInject(MekanismInjectRegistry::wrapItem);
	}

	private static void wrapTile(TileEntity te, InjectWrappers.Registry reg) {
		if (te instanceof IStrictEnergyAcceptor) {
			reg.add(EnergyType.MEKANISM, new MKJTileCapabilityProvider((IStrictEnergyAcceptor) te));
		}
	}

	private static void wrapItem(ItemStack is, InjectWrappers.Registry reg) {
		if (is.getItem() instanceof IEnergizedItem) {
			reg.add(EnergyType.MEKANISM, new MKJEnergizedItemWrapper((IEnergizedItem) is.getItem(), is));
		}
	}
}
