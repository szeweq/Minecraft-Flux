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
		RFTILE_ENERGY = new ResourceLocation(R.MCFLUX_NAME, "RFTileEnergy"),
		RFITEM_ENERGY = new ResourceLocation(R.MCFLUX_NAME, "RFItemEnergy"),
		IFTILE_ENERGY = new ResourceLocation(R.MCFLUX_NAME, "IFTileEnergy"),
		IFITEM_ENERGY = new ResourceLocation(R.MCFLUX_NAME, "IFItemEnergy");

	@SubscribeEvent
	public void capabilityWrapperInject(AttachCapabilitiesEvent e) {
		if (e instanceof AttachCapabilitiesEvent.TileEntity) {
			AttachCapabilitiesEvent.TileEntity ete = (AttachCapabilitiesEvent.TileEntity) e;
			TileEntity te = ete.getTileEntity();
			if (te instanceof IFluxConnection)
				ete.addCapability(IFTILE_ENERGY, new IFTileCapabilityProvider((IFluxConnection) te));
			if (te instanceof IEnergyHandler)
				ete.addCapability(RFTILE_ENERGY, new RFTileCapabilityProvider((IEnergyHandler) te));
		} else if (e instanceof AttachCapabilitiesEvent.Item) {
			AttachCapabilitiesEvent.Item ei = (AttachCapabilitiesEvent.Item) e;
			ItemStack is = ei.getItemStack();
			Item it = is.getItem();
			if (it instanceof IFluxContainerItem)
				ei.addCapability(IFITEM_ENERGY, new IFItemContainerWrapper((IFluxContainerItem) it, is));
			else if (it instanceof IEnergyContainerItem)
				ei.addCapability(RFITEM_ENERGY, new RFItemContainerWrapper((IEnergyContainerItem) it, is));
		}
	}
}
