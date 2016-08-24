package szewek.mcflux.fluxwork;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import szewek.mcflux.api.CapabilityEnergy;
import szewek.mcflux.api.IEnergyConsumer;

public abstract class TileEntityFluxWorkConsumer extends TileEntityFluxWork implements ITickable, IEnergyConsumer {

	@Override
	public final void update() {
		if (worldObj.isRemote) return;
		if (!canWork()) {
			workState = WorkState.LAZY;
		} else if (!hasWork()) {
			workDone = 0;
			workNeeded = beginWork();
			workState = WorkState.WORKING;
		} else {
			if (energy >= energyChange) {
				work();
				workDone++;
				energy -= energyChange;
				if (workNeeded <= workDone) {
					finishWork();
					workState = WorkState.FINISHED;
				}
			}
			workState = WorkState.PAUSED;
		}
		tick();
	}
	
	@Override
	public int consumeEnergy(int amount, boolean simulate) {
		if (amount == 0)
			return 0;
		int r = maxEnergy - energy;
		if (amount < r)
			r = amount;
		if (!simulate)
			energy += r;
		return r;
	}
	
	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing f) {
		return cap == CapabilityEnergy.ENERGY_CONSUMER || super.hasCapability(cap, f);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing f) {
		if (cap == CapabilityEnergy.ENERGY_CONSUMER)
			return (T) this;
		return super.getCapability(cap, f);
	}
}
