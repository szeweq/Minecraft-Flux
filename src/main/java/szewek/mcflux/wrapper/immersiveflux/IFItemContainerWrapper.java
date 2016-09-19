package szewek.mcflux.wrapper.immersiveflux;

import blusunrize.immersiveengineering.api.energy.immersiveflux.IFluxContainerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.mcflux.api.ex.EX;
import szewek.mcflux.api.ex.IEnergy;
import szewek.mcflux.wrapper.CompatEnergyWrapper;

class IFItemContainerWrapper implements IEnergy, ICapabilityProvider {
	private final IFluxContainerItem item;
	private final ItemStack stack;
	private final CompatEnergyWrapper cew;

	IFItemContainerWrapper(IFluxContainerItem it, ItemStack is) {
		item = it;
		stack = is;
		cew = new CompatEnergyWrapper(this);
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

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing f) {
		return cap == EX.CAP_ENERGY || cew.isCompatInputSuitable(cap) || cew.isCompatOutputSuitable(cap);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing f) {
		if (cap == EX.CAP_ENERGY)
			return (T) this;
		if (cew.isCompatInputSuitable(cap) || cew.isCompatOutputSuitable(cap))
			return (T) cew;
		return null;
	}
}
