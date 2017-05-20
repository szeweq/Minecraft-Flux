package szewek.mcflux.wrapper.ic2;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import szewek.mcflux.L;
import szewek.mcflux.util.*;
import szewek.mcflux.wrapper.EnergyType;
import szewek.mcflux.wrapper.InjectCollector;
import szewek.mcflux.wrapper.InjectWrappers;
import szewek.mcflux.wrapper.WrapperRegistry;

@InjectRegistry(requires = InjectCond.MOD, args = {"IC2", "IndustrialCraft 2"})
public final class EUInjectRegistry implements IInjectRegistry {
	@Override
	public void registerInjects() {
		InjectCollector ic = InjectWrappers.getCollector();
		MinecraftForge.EVENT_BUS.register(EUEnergyEvents.INSTANCE);
		CapabilityManager.INSTANCE.register(EUTileCapabilityProvider.class, CapStorage.getEmpty(), EUTileCapabilityProvider::new);
		ic.addTileWrapperInject(EUInjectRegistry::wrapEUTile);
	}

	private static void wrapEUTile(TileEntity te, WrapperRegistry reg) {
		String xn = te.getClass().getName();
		if (xn == null) {
			L.warn("A tile entity doesn't have a class name: " + te);
			return;
		}
		if (xn.startsWith("ic2.core") || xn.startsWith("cpw.mods.compactsolars"))
			reg.add(EnergyType.EU, new EUTileCapabilityProvider());
	}
}
