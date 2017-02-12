package szewek.mcflux.wrapper.redstoneflux;

import cofh.api.energy.IEnergyContainerItem;
import net.minecraft.item.ItemStack;
import szewek.mcflux.api.ex.EnergyCapable;

final class RFItemContainerWrapper extends EnergyCapable implements net.minecraftforge.energy.IEnergyStorage {
	private final IEnergyContainerItem item;
	private final ItemStack stack;

	RFItemContainerWrapper(IEnergyContainerItem it, ItemStack is) {
		item = it;
		stack = is;
	}

	@Override
	public long getEnergy() {
		return item.getEnergyStored(stack);
	}

	@Override
	public long getEnergyCapacity() {
		return item.getMaxEnergyStored(stack);
	}

	@Override public boolean canInputEnergy() {
		return item.getEnergyStored(stack) < item.getMaxEnergyStored(stack);
	}

	@Override public boolean canOutputEnergy() {
		return item.getEnergyStored(stack) > 0;
	}

	@Override
	public long inputEnergy(long amount, boolean sim) {
		return item.receiveEnergy(stack, amount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) amount, sim);
	}

	@Override
	public long outputEnergy(long amount, boolean sim) {
		return item.extractEnergy(stack, amount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) amount, sim);
	}

	@Override public int receiveEnergy(int maxReceive, boolean simulate) {
		return item.receiveEnergy(stack, maxReceive, simulate);
	}

	@Override public int extractEnergy(int maxExtract, boolean simulate) {
		return item.extractEnergy(stack, maxExtract, simulate);
	}

	@Override public int getEnergyStored() {
		return item.getEnergyStored(stack);
	}

	@Override public int getMaxEnergyStored() {
		return item.getMaxEnergyStored(stack);
	}

	@Override public boolean canExtract() {
		return canOutputEnergy();
	}

	@Override public boolean canReceive() {
		return canInputEnergy();
	}
}
