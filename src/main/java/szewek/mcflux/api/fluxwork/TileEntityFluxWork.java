package szewek.mcflux.api.fluxwork;

import net.minecraft.tileentity.TileEntity;
import szewek.mcflux.api.IEnergyHolder;

/**
 * TileEntity working cycle template.
 */
public abstract class TileEntityFluxWork extends TileEntity implements IEnergyHolder {
	protected WorkState workState = WorkState.LAZY;
	protected int energy, maxEnergy, workNeeded, workDone, energyChange;

	/**
	 * Checks if TileEntity can work.
	 * 
	 * @return If {@code true} then work can be started or resumed.
	 */
	protected abstract boolean canWork();

	/**
	 * Checks if TileEntity has work.
	 * 
	 * @return If {@code true} then work can be resumed, otherwise new work will be started.
	 * @see #canWork()
	 */
	protected abstract boolean hasWork();

	/**
	 * This is called when work is about to begin (TileEntity can work but it haven't worked).
	 * 
	 * @return Amount of work ticks.
	 */
	protected abstract int beginWork();

	/**
	 * This is called only when TileEntity has work to do and has some energy left to use.
	 * 
	 * @see #hasWork()
	 * @see #canWork()
	 */
	protected abstract void work();

	/**
	 * Checks if work is ending.
	 * 
	 * @return {@code true} if work is ending, otherwise {@code false}.
	 */
	protected abstract boolean workEnding();

	/**
	 * This method is called when work is ending.
	 * 
	 * @see #workEnding()
	 */
	protected abstract void finishWork();

	/**
	 * Occurs at every tick update.
	 */
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
