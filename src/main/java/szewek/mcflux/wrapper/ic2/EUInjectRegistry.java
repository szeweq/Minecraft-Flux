package szewek.mcflux.wrapper.ic2;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.mcflux.util.EmptyCapabilityStorage;
import szewek.mcflux.util.IInjectRegistry;
import szewek.mcflux.util.InjectRegistry;
import szewek.mcflux.util.MCFluxLocation;
import szewek.mcflux.wrapper.InjectWrappers;

import java.util.function.BiConsumer;

@InjectRegistry(detectMods = {"IC2", "IndustrialCraft 2"})
public class EUInjectRegistry implements IInjectRegistry {
	private static final MCFluxLocation EU_TILE = new MCFluxLocation("MFTileEU");

	@Override
	public void registerInjects() {
		MinecraftForge.EVENT_BUS.register(EUEnergyEvents.INSTANCE);
		CapabilityManager.INSTANCE.register(EUTileCapabilityProvider.class, new EmptyCapabilityStorage<>(), EUTileCapabilityProvider::new);
		InjectWrappers.INSTANCE.registerTileWrapperInject(EUInjectRegistry::wrapEUTile);
	}
	
	private static void wrapEUTile(TileEntity te, BiConsumer<ResourceLocation, ICapabilityProvider> add) {
		String xcn = te.getClass().getCanonicalName();
		if (xcn.startsWith("ic2.core") || xcn.startsWith("cpw.mods.compactsolars")) {
			add.accept(EU_TILE, new EUTileCapabilityProvider());
		}
	}
}
