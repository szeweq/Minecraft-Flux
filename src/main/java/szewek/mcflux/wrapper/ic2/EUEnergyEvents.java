package szewek.mcflux.wrapper.ic2;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.event.EnergyTileLoadEvent;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import szewek.mcflux.U;
import szewek.mcflux.util.MCFluxReport;

import java.lang.reflect.Method;

import static szewek.mcflux.MCFlux.L;

enum EUEnergyEvents {
	INSTANCE;

	private final Class<?> IC2_TEB, IC2_ENERGY;
	private final Method COMPONENT, CAPACITY, ENERGY;
	private final boolean broken;

	EUEnergyEvents() {
		IC2_TEB = U.getClassSafely("ic2.core.block.TileEntityBlock");
		IC2_ENERGY = U.getClassSafely("ic2.core.block.comp.Energy");
		if (IC2_TEB == null || IC2_ENERGY == null) {
			COMPONENT = CAPACITY = ENERGY = null;
		} else {
			COMPONENT = U.getMethodSafely(IC2_TEB, "getComponent", Class.class);
			CAPACITY = U.getMethodSafely(IC2_ENERGY, "getCapacity");
			ENERGY = U.getMethodSafely(IC2_ENERGY, "getEnergy");
		}
		broken = COMPONENT == null || CAPACITY == null || ENERGY == null;
		if (broken)
			L.warn("EUEnergyEvents is broken");
	}

	@SubscribeEvent
	public void loadEnergyTile(EnergyTileLoadEvent e) {
		if (broken)
			return;
		final TileEntity te = e.getWorld().getTileEntity(EnergyNet.instance.getPos(e.tile));
		if (te == null) return;
		final EUTileCapabilityProvider cap = te.getCapability(EUTileCapabilityProvider.SELF_CAP, null);
		if (cap != null) {
			cap.updateEnergyTile(e.tile);
			if (IC2_TEB.isInstance(te)) {
				Object o = null;
				try {
					o = COMPONENT.invoke(te, IC2_ENERGY);
				} catch (Exception e1) {
					MCFluxReport.sendException(e1, "[IC2] Getting Energy");
				}
				if (o == null)
					return;
				cap.updateEnergyMethods(o, CAPACITY, ENERGY);
			}
		}
	}
}
