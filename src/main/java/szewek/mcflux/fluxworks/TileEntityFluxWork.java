package szewek.mcflux.fluxworks;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import szewek.mcflux.api.IEnergyHolder;

public abstract class TileEntityFluxWork extends TileEntity implements ITickable, IEnergyHolder {
	protected WorkState workState = WorkState.LAZY;
	protected int energy, maxEnergy, workNeeded, workDone, energyChange;
	
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
			work();
			if (workNeeded == workDone) {
				finishWork();
				workState = WorkState.FINISHED;
			}
		}
	}

	protected abstract boolean canWork();
	protected abstract boolean hasWork();
	protected abstract int beginWork();
	protected abstract void work();
	protected abstract boolean workEnding();
	protected abstract void finishWork();

	@Override
	public int getEnergy() {
		return energy;
	}

	@Override
	public int getEnergyCapacity() {
		return maxEnergy;
	}

}
