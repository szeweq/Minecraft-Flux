package szewek.mcflux.wrapper.immersiveflux;

import blusunrize.immersiveengineering.api.energy.immersiveflux.IFluxConnection;
import blusunrize.immersiveengineering.api.energy.immersiveflux.IFluxContainerItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import szewek.mcflux.util.IInjectRegistry;
import szewek.mcflux.util.InjectRegistry;
import szewek.mcflux.util.MCFluxLocation;
import szewek.mcflux.wrapper.InjectWrappers;

@InjectRegistry(detectMods = {"immersiveengineering", "Immersive Engineering"})
public class IFInjectRegistry implements IInjectRegistry {
	private static final MCFluxLocation IF_TILE = new MCFluxLocation("MFTileIF"), IF_ITEM = new MCFluxLocation("MFItemIF");

	@Override
	public void registerInjects() {
		InjectWrappers.registerTileWrapperInject(IFInjectRegistry::wrapIFTile);
		InjectWrappers.registerItemWrapperInject(IFInjectRegistry::wrapIFItem);
	}
	
	private static void wrapIFTile(TileEntity te, InjectWrappers.Registry reg) {
		if (te instanceof IFluxConnection)
			reg.add(IF_TILE, new IFTileCapabilityProvider((IFluxConnection) te));
	}
	
	private static void wrapIFItem(ItemStack is, InjectWrappers.Registry reg) {
		Item it = is.getItem();
		if (it instanceof IFluxContainerItem)
			reg.add(IF_ITEM, new IFItemContainerWrapper((IFluxContainerItem) it, is));
	}

}
