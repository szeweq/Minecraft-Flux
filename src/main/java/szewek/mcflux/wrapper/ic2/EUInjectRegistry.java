package szewek.mcflux.wrapper.ic2;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import szewek.mcflux.util.*;
import szewek.mcflux.wrapper.InjectWrappers;

@InjectRegistry(requires = InjectCond.MOD, args = {"IC2", "IndustrialCraft 2"})
public class EUInjectRegistry implements IInjectRegistry {
	private static final MCFluxLocation EU_TILE = new MCFluxLocation("MFTileEU");

	@Override
	public void registerInjects() {
		MinecraftForge.EVENT_BUS.register(EUEnergyEvents.INSTANCE);
		CapabilityManager.INSTANCE.register(EUTileCapabilityProvider.class, new EmptyCapabilityStorage<>(), EUTileCapabilityProvider::new);
		InjectWrappers.registerTileWrapperInject(EUInjectRegistry::wrapEUTile);
	}
	
	private static void wrapEUTile(TileEntity te, InjectWrappers.Registry reg) {
		String xcn = te.getClass().getCanonicalName();
		if (xcn.startsWith("ic2.core") || xcn.startsWith("cpw.mods.compactsolars")) {
			reg.add(EU_TILE, new EUTileCapabilityProvider());
		}
	}
}
