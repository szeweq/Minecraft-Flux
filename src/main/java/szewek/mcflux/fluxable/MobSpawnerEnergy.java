package szewek.mcflux.fluxable;

import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import szewek.fl.FL;
import szewek.fl.energy.ForgeEnergyCompat;
import szewek.mcflux.util.EnergyCapable;

import javax.annotation.Nullable;

import static szewek.mcflux.config.MCFluxConfig.MOB_SPAWNER_USE;

public final class MobSpawnerEnergy extends EnergyCapable {
	private final TileEntityMobSpawner spawner;
	private final ForgeEnergyCompat fec = new ForgeEnergyCompat(this);

	public MobSpawnerEnergy(TileEntityMobSpawner tems) {
		spawner = tems;
	}

	@Override public boolean hasCapability(Capability<?> cap, @Nullable EnumFacing f) {
		return cap == FL.ENERGY_CAP || cap == CapabilityEnergy.ENERGY;
	}

	@SuppressWarnings("unchecked")
	@Override public <T> T getCapability(Capability<T> cap, @Nullable EnumFacing f) {
		if (cap == CapabilityEnergy.ENERGY)
			return (T) fec;
		return super.getCapability(cap, f);
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
