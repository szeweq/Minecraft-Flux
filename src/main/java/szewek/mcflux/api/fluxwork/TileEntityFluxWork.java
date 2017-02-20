package szewek.mcflux.api.fluxwork;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import szewek.mcflux.api.ex.EX;
import szewek.mcflux.api.ex.IEnergy;

import javax.annotation.Nonnull;

/**
 * TileEntity working cycle template.
 */
public abstract class TileEntityFluxWork extends TileEntity implements IEnergy, ITickable {
	protected WorkState workState = WorkState.LAZY;
	protected long energy, maxEnergy;
	protected int workNeeded, workDone;

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

	/**
	 * Tick update hapenning on gui side.
	 */
	protected abstract void tickClient();

	/**
	 * Checks if energy value can change.
	 *
	 * @return {@code true} if energy can be changed, otherwise {@code false}.
	 */
	protected abstract boolean canChangeEnergy();

	@Override
	public final void update() {
		if (world.isRemote) {
			tickClient();
			return;
		}
		if (!canWork()) {
			workState = WorkState.LAZY;
		} else if (!hasWork()) {
			workDone = 0;
			workNeeded = beginWork();
			workState = WorkState.WORKING;
		} else if (canChangeEnergy()) {
			work();
			++workDone;
			if (workNeeded <= workDone) {
				finishWork();
				workState = WorkState.FINISHED;
			}
		}
	}

	@Override
	public long getEnergy() {
		return energy;
	}

	@Override
	public long getEnergyCapacity() {
		return maxEnergy;
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> cap, EnumFacing f) {
		return cap == EX.CAP_ENERGY || super.hasCapability(cap, f);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(@Nonnull Capability<T> cap, EnumFacing f) {
		return cap == EX.CAP_ENERGY? (T) this : super.getCapability(cap, f);
	}
}
