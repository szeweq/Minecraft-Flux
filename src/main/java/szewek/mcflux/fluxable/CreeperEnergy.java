package szewek.mcflux.fluxable;

import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.mcflux.api.CapabilityEnergy;
import szewek.mcflux.api.IEnergyConsumer;

public class CreeperEnergy implements IEnergyConsumer, ICapabilityProvider {
	private boolean charged = false;
	private final EntityCreeper creeper;
	
	public CreeperEnergy(EntityCreeper ec) {
		creeper = ec;
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing f) {
		return cap == CapabilityEnergy.ENERGY_CONSUMER;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing f) {
		if (cap == CapabilityEnergy.ENERGY_CONSUMER)
			return (T) this;
		return null;
	}

	@Override
	public int getEnergy() {
		return 0;
	}

	@Override
	public int getEnergyCapacity() {
		return 1;
	}

	@Override
	public int consumeEnergy(int amount, boolean simulate) {
		if (!simulate) {
			creeper.onStruckByLightning(null);
			charged = true;
			return 1;
		}
		return charged ? 0 : 1;
	}

}
