package szewek.mcflux.wrapper.immersiveflux;

import blusunrize.immersiveengineering.api.energy.immersiveflux.IFluxConnection;
import blusunrize.immersiveengineering.api.energy.immersiveflux.IFluxContainerItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import szewek.mcflux.util.IInjectRegistry;
import szewek.mcflux.util.InjectCond;
import szewek.mcflux.util.InjectRegistry;
import szewek.mcflux.wrapper.EnergyType;
import szewek.mcflux.wrapper.InjectCollector;
import szewek.mcflux.wrapper.InjectWrappers;
import szewek.mcflux.wrapper.WrapperRegistry;

@InjectRegistry(requires = InjectCond.MOD, args = {"immersiveengineering", "Immersive Engineering"})
public final class IFInjectRegistry implements IInjectRegistry {
	@Override
	public void registerInjects() {
		InjectCollector ic = InjectWrappers.getCollector();
		if (ic == null)
			return;
		ic.addTileWrapperInject(IFInjectRegistry::wrapIFTile);
		ic.addItemWrapperInject(IFInjectRegistry::wrapIFItem);
	}
	
	private static void wrapIFTile(TileEntity te, WrapperRegistry reg) {
		if (te instanceof IFluxConnection)
			reg.add(EnergyType.IF, new IFTileCapabilityProvider((IFluxConnection) te));
	}
	
	private static void wrapIFItem(ItemStack is, WrapperRegistry reg) {
		Item it = is.getItem();
		if (it instanceof IFluxContainerItem)
			reg.add(EnergyType.IF, new IFItemContainerWrapper((IFluxContainerItem) it, is));
	}

}
