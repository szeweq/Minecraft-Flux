package szewek.mcflux.wrapper.ic2;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import szewek.mcflux.L;
import szewek.mcflux.util.EmptyCapabilityStorage;
import szewek.mcflux.util.IInjectRegistry;
import szewek.mcflux.util.InjectCond;
import szewek.mcflux.util.InjectRegistry;
import szewek.mcflux.wrapper.EnergyType;
import szewek.mcflux.wrapper.InjectWrappers;

@InjectRegistry(requires = InjectCond.MOD, args = {"IC2", "IndustrialCraft 2"})
public class EUInjectRegistry implements IInjectRegistry {
	@Override
	public void registerInjects() {
		MinecraftForge.EVENT_BUS.register(EUEnergyEvents.INSTANCE);
		CapabilityManager.INSTANCE.register(EUTileCapabilityProvider.class, new EmptyCapabilityStorage<>(), EUTileCapabilityProvider::new);
		InjectWrappers.registerTileWrapperInject(EUInjectRegistry::wrapEUTile);
	}

	private static void wrapEUTile(TileEntity te, InjectWrappers.Registry reg) {
		String xn = te.getClass().getName();
		if (xn == null) {
			L.warn("A tile entity doesn't have a class name: " + te);
			return;
		}
		if (xn.startsWith("ic2.core") || xn.startsWith("cpw.mods.compactsolars"))
			reg.add(EnergyType.EU, new EUTileCapabilityProvider());
	}
}
