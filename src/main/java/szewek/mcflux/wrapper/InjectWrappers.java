package szewek.mcflux.wrapper;

import blusunrize.immersiveengineering.api.energy.immersiveflux.IFluxConnection;
import blusunrize.immersiveengineering.api.energy.immersiveflux.IFluxContainerItem;
import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.IEnergyHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import szewek.mcflux.R;

public enum InjectWrappers {
	INSTANCE;
	public static final ResourceLocation
		MF_TILE_ENERGY = new ResourceLocation(R.MF_NAME, "TileMF"),
		MF_ITEM_ENERGY = new ResourceLocation(R.MF_NAME, "ItemMF");

	@SubscribeEvent
	public void capabilityWrapperInject(AttachCapabilitiesEvent e) {
		if (e instanceof AttachCapabilitiesEvent.TileEntity) {
			AttachCapabilitiesEvent.TileEntity ete = (AttachCapabilitiesEvent.TileEntity) e;
			TileEntity te = ete.getTileEntity();
			if (te instanceof IFluxConnection)
				ete.addCapability(MF_TILE_ENERGY, new IFTileCapabilityProvider((IFluxConnection) te));
			if (te instanceof IEnergyHandler)
				ete.addCapability(MF_TILE_ENERGY, new RFTileCapabilityProvider((IEnergyHandler) te));
		} else if (e instanceof AttachCapabilitiesEvent.Item) {
			AttachCapabilitiesEvent.Item ei = (AttachCapabilitiesEvent.Item) e;
			ItemStack is = ei.getItemStack();
			Item it = is.getItem();
			if (it instanceof IFluxContainerItem)
				ei.addCapability(MF_ITEM_ENERGY, new IFItemContainerWrapper((IFluxContainerItem) it, is));
			else if (it instanceof IEnergyContainerItem)
				ei.addCapability(MF_ITEM_ENERGY, new RFItemContainerWrapper((IEnergyContainerItem) it, is));
		}
	}
}
