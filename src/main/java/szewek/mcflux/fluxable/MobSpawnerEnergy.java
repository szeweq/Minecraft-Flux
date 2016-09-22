package szewek.mcflux.fluxable;

import net.minecraft.tileentity.TileEntityMobSpawner;
import szewek.mcflux.api.ex.EnergyCapable;

import static szewek.mcflux.config.MCFluxConfig.MOB_SPAWNER_USE;

class MobSpawnerEnergy extends EnergyCapable {
	private final TileEntityMobSpawner spawner;

	MobSpawnerEnergy(TileEntityMobSpawner tems) {
		spawner = tems;
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
