package szewek.mcflux.fluxwork;

import net.minecraft.tileentity.TileEntity;
import szewek.mcflux.api.IEnergyHolder;

public abstract class TileEntityFluxWork extends TileEntity implements IEnergyHolder {
	protected WorkState workState = WorkState.LAZY;
	protected int energy, maxEnergy, workNeeded, workDone, energyChange;

	protected abstract boolean canWork();
	protected abstract boolean hasWork();
	protected abstract int beginWork();
	protected abstract void work();
	protected abstract boolean workEnding();
	protected abstract void finishWork();
	protected abstract void tick();

	@Override
	public int getEnergy() {
		return energy;
	}

	@Override
	public int getEnergyCapacity() {
		return maxEnergy;
	}
}
