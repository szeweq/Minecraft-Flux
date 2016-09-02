package szewek.mcflux.wrapper.ic2;

import java.util.function.BiConsumer;

import ic2.core.block.TileEntityBlock;
import ic2.core.block.comp.Energy;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.mcflux.R;
import szewek.mcflux.util.EmptyCapabilityStorage;
import szewek.mcflux.util.IInjectRegistry;
import szewek.mcflux.util.InjectRegistry;
import szewek.mcflux.wrapper.InjectWrappers;

@InjectRegistry(detectMods = {"IC2", "IndustrialCraft 2"})
public class EUInjectRegistry implements IInjectRegistry {
	private static final ResourceLocation EU_TILE = new ResourceLocation(R.MF_NAME, "MFTileEU");

	@Override
	public void registerInjects() {
		MinecraftForge.EVENT_BUS.register(EUEnergyEvents.INSTANCE);
		CapabilityManager.INSTANCE.register(EUTileCapabilityProvider.class, new EmptyCapabilityStorage<>(), EUTileCapabilityProvider::new);
		InjectWrappers.INSTANCE.registerTileWrapperInject(EUInjectRegistry::wrapEUTile);
	}
	
	private static void wrapEUTile(TileEntity te, BiConsumer<ResourceLocation, ICapabilityProvider> add) {
		if(te instanceof TileEntityBlock) {
			Energy e = ((TileEntityBlock) te).getComponent(Energy.class);
			if (e != null)
				add.accept(EU_TILE, new EUTileCapabilityProvider(e));
		}	
	}
}
