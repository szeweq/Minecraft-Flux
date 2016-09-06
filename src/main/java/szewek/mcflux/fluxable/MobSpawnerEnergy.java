package szewek.mcflux.fluxable;

import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.mcflux.api.CapabilityEnergy;
import szewek.mcflux.api.IEnergyConsumer;
import static szewek.mcflux.config.MCFluxConfig.MOB_SPAWNER_USE;

public class MobSpawnerEnergy implements IEnergyConsumer, ICapabilityProvider {
	private final TileEntityMobSpawner spawner;

	public MobSpawnerEnergy(TileEntityMobSpawner tems) {
		spawner = tems;
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing f) {
		return cap == CapabilityEnergy.ENERGY_CONSUMER;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing f) {
		return cap == CapabilityEnergy.ENERGY_CONSUMER ? (T) this : null;
	}

	@Override
	public int getEnergy() {
		return 0;
	}

	@Override
	public int getEnergyCapacity() {
		return MOB_SPAWNER_USE;
	}

	@Override
	public int consumeEnergy(int amount, boolean sim) {
		if (amount >= MOB_SPAWNER_USE) {
			spawner.update();
			return MOB_SPAWNER_USE;
		}
		return 0;
	}
}
