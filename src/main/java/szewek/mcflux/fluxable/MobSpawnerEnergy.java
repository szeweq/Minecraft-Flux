package szewek.mcflux.fluxable;

import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.mcflux.api.ex.EX;
import szewek.mcflux.api.ex.IEnergy;

import static szewek.mcflux.config.MCFluxConfig.MOB_SPAWNER_USE;

class MobSpawnerEnergy implements IEnergy, ICapabilityProvider {
	private final TileEntityMobSpawner spawner;

	MobSpawnerEnergy(TileEntityMobSpawner tems) {
		spawner = tems;
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing f) {
		return cap == EX.CAP_ENERGY;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing f) {
		return cap == EX.CAP_ENERGY ? (T) this : null;
	}

	@Override
	public long getEnergy() {
		return 0;
	}

	@Override
	public long getEnergyCapacity() {
		return MOB_SPAWNER_USE;
	}

	@Override public boolean canInputEnergy() {
		return true;
	}

	@Override public boolean canOutputEnergy() {
		return false;
	}

	@Override
	public long inputEnergy(long amount, boolean sim) {
		if (amount >= MOB_SPAWNER_USE) {
			spawner.update();
			return MOB_SPAWNER_USE;
		}
		return 0;
	}

	@Override public long outputEnergy(long amount, boolean sim) {
		return 0;
	}
}
