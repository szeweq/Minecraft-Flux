package szewek.mcflux.wrapper.ic2;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.event.EnergyTileLoadEvent;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import szewek.mcflux.L;
import szewek.mcflux.U;

import java.lang.reflect.Method;

enum EUEnergyEvents {
	INSTANCE;

	private Class<?> IC2_TEB_CLASS, IC2_ENERGY_CLASS;
	private Method GET_COMPONENT, GET_CAPACITY, GET_ENERGY;
	private final boolean broken;

	EUEnergyEvents() {
		IC2_TEB_CLASS = U.getClassSafely("ic2.core.block.TileEntityBlock");
		IC2_ENERGY_CLASS = U.getClassSafely("ic2.core.block.comp.Energy");
		if (IC2_TEB_CLASS == null || IC2_ENERGY_CLASS == null) {
			GET_COMPONENT = GET_CAPACITY = GET_ENERGY = null;
		} else {
			GET_COMPONENT = U.getMethodSafely(IC2_TEB_CLASS, "getComponent", Class.class);
			GET_CAPACITY = U.getMethodSafely(IC2_ENERGY_CLASS, "getCapacity");
			GET_ENERGY = U.getMethodSafely(IC2_ENERGY_CLASS, "getEnergy");
		}
		broken = GET_COMPONENT == null || GET_CAPACITY == null || GET_ENERGY == null;
		if (broken)
			L.warn("EUEnergyEvents is broken");
	}
	
	@SubscribeEvent
	public void loadEnergyTile(EnergyTileLoadEvent e) {
		TileEntity te = e.getWorld().getTileEntity(EnergyNet.instance.getPos(e.tile));
		if (te == null) return;
		EUTileCapabilityProvider cap = te.getCapability(EUTileCapabilityProvider.SELF_CAP, null);
		if (cap == null) {
			L.warn("Tile class " + te.getClass().getCanonicalName() + " has no SELF_CAP");
			return;
		}
		cap.updateEnergyTile(e.tile);
		if (!broken && IC2_TEB_CLASS.isInstance(te)) {
			Object o = null;
			try {
				o = GET_COMPONENT.invoke(te, IC2_ENERGY_CLASS);
			} catch (Exception e1) {
				L.warn(e1);
			}
			if (o == null)
				return;
			cap.updateEnergyMethods(o, GET_CAPACITY, GET_ENERGY);
		}
	}
}
