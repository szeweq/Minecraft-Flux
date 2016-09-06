package szewek.mcflux.wrapper.redstoneflux;

import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.IEnergyHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.mcflux.util.IInjectRegistry;
import szewek.mcflux.util.InjectRegistry;
import szewek.mcflux.util.MCFluxLocation;
import szewek.mcflux.wrapper.InjectWrappers;

import java.util.function.BiConsumer;

@InjectRegistry(included = true, detectMods = {})
public class RFInjectRegistry implements IInjectRegistry {
	private static final MCFluxLocation RF_TILE = new MCFluxLocation("MFTileRF"), RF_ITEM = new MCFluxLocation("MFItemRF");

	@Override
	public void registerInjects() {
		InjectWrappers.INSTANCE.registerTileWrapperInject(RFInjectRegistry::wrapRFTile);
		InjectWrappers.INSTANCE.registerItemWrapperInject(RFInjectRegistry::wrapRFItem);
	}
	private static void wrapRFTile(TileEntity te, BiConsumer<ResourceLocation, ICapabilityProvider> add) {
		if (te instanceof IEnergyHandler)
			add.accept(RF_TILE, new RFTileCapabilityProvider((IEnergyHandler) te));
	}
	
	private static void wrapRFItem(ItemStack is, BiConsumer<ResourceLocation, ICapabilityProvider> add) {
		Item it = is.getItem();
		if (it instanceof IEnergyContainerItem)
			add.accept(RF_ITEM, new RFItemContainerWrapper((IEnergyContainerItem) it, is));
	}
}
