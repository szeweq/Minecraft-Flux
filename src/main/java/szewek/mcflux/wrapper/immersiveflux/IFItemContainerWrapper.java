package szewek.mcflux.wrapper.immersiveflux;

import blusunrize.immersiveengineering.api.energy.immersiveflux.IFluxContainerItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.mcflux.api.CapabilityEnergy;
import szewek.mcflux.api.IEnergyConsumer;
import szewek.mcflux.api.IEnergyProducer;

public class IFItemContainerWrapper implements IEnergyProducer, IEnergyConsumer, ICapabilityProvider {
	private final IFluxContainerItem item;
	private final ItemStack stack;
	
	public IFItemContainerWrapper(IFluxContainerItem it, ItemStack is) {
		item = it;
		stack = is;
	}

	@Override
	public int getEnergy() {
		return item.getEnergyStored(stack);
	}

	@Override
	public int getEnergyCapacity() {
		return item.getMaxEnergyStored(stack);
	}

	@Override
	public int consumeEnergy(int amount, boolean simulate) {
		return item.receiveEnergy(stack, amount, simulate);
	}

	@Override
	public int extractEnergy(int amount, boolean simulate) {
		return item.extractEnergy(stack, amount, simulate);
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing f) {
		return cap == CapabilityEnergy.ENERGY_CONSUMER || cap == CapabilityEnergy.ENERGY_PRODUCER;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing f) {
		if (cap == CapabilityEnergy.ENERGY_CONSUMER || cap == CapabilityEnergy.ENERGY_PRODUCER)
			return (T) this;
		return null;
	}
}
