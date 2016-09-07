package szewek.mcflux.fluxable;

import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.mcflux.api.ex.IEnergy;
import szewek.mcflux.wrapper.CompatEnergyWrapper;

import static szewek.mcflux.config.MCFluxConfig.MOB_SPAWNER_USE;

class MobSpawnerEnergy implements IEnergy, ICapabilityProvider {
	private final TileEntityMobSpawner spawner;
	private final CompatEnergyWrapper cew;

	MobSpawnerEnergy(TileEntityMobSpawner tems) {
		spawner = tems;
		cew = new CompatEnergyWrapper(this);
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing f) {
		return cap == IEnergy.CAP_ENERGY || cew.isCompatInputSuitable(cap);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing f) {
		return (T) (cap == IEnergy.CAP_ENERGY ?  this : cew.isCompatInputSuitable(cap) ? cew : null);
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
