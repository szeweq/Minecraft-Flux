package szewek.mcflux.wrapper.redstoneflux;

import cofh.api.energy.IEnergyContainerItem;
import net.minecraft.item.ItemStack;
import szewek.mcflux.api.ex.EnergyCapable;

class RFItemContainerWrapper extends EnergyCapable {
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
		return true;
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
}
