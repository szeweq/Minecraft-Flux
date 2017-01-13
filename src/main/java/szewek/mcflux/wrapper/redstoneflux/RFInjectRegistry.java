package szewek.mcflux.wrapper.redstoneflux;

import cofh.api.energy.IEnergyContainerItem;
import cofh.api.energy.IEnergyHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import szewek.mcflux.util.ErrorReport;
import szewek.mcflux.util.IInjectRegistry;
import szewek.mcflux.util.InjectCond;
import szewek.mcflux.util.InjectRegistry;
import szewek.mcflux.util.error.ErrMsgOldAPI;
import szewek.mcflux.wrapper.EnergyType;
import szewek.mcflux.wrapper.InjectWrappers;

@InjectRegistry(requires = InjectCond.CLASS, args = {"cofh.api.energy.IEnergyHandler"})
public class RFInjectRegistry implements IInjectRegistry {
	private static final String RF_API_NAME = "CoFHAPI|energy";
	@Override
	public void registerInjects() {
		InjectWrappers.addTileWrapperInject(RFInjectRegistry::wrapRFTile);
		InjectWrappers.addItemWrapperInject(RFInjectRegistry::wrapRFItem);
	}
	private static void wrapRFTile(TileEntity te, InjectWrappers.Registry reg) {
		if (te instanceof IEnergyHandler) {
			ErrorReport.addErrMsg(new ErrMsgOldAPI(RF_API_NAME, te.getClass()));
			reg.add(EnergyType.RF, new RFTileCapabilityProvider((IEnergyHandler) te));
		}

	}
	
	private static void wrapRFItem(ItemStack is, InjectWrappers.Registry reg) {
		Item it = is.getItem();
		if (it instanceof IEnergyContainerItem) {
			ErrorReport.addErrMsg(new ErrMsgOldAPI(RF_API_NAME, it.getClass()));
			reg.add(EnergyType.RF, new RFItemContainerWrapper((IEnergyContainerItem) it, is));
		}
	}
}
