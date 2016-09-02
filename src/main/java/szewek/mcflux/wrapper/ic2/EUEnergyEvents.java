package szewek.mcflux.wrapper.ic2;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.event.EnergyTileLoadEvent;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import szewek.mcflux.L;

public enum EUEnergyEvents {
	INSTANCE;
	
	@SubscribeEvent
	public void loadEnergyTile(EnergyTileLoadEvent e) {
		L.info("EU TILE" + e.tile);
		TileEntity te = e.getWorld().getTileEntity(EnergyNet.instance.getPos(e.tile));
		if (te == null) return;
		EUTileCapabilityProvider cap = te.getCapability(EUTileCapabilityProvider.SELF_CAP, null);
		if (cap == null) {
			L.info("Tile " + te + " has no SELF_CAP");
			return;
		}
		cap.updateEnergyTile(e.tile);
	}
}
