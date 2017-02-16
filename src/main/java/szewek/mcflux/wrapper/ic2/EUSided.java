package szewek.mcflux.wrapper.ic2;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import net.minecraft.util.EnumFacing;
import szewek.mcflux.wrapper.ForgeEnergyCapable;

import java.util.function.DoubleSupplier;

import static szewek.mcflux.config.MCFluxConfig.CFG_EU_VALUE;

final class EUSided extends ForgeEnergyCapable {
	private final EnumFacing face;
	private final DoubleSupplier capMethod, energyMethod;
	private final IEnergySink sink;
	private final IEnergySource source;

	EUSided(DoubleSupplier capMethod, DoubleSupplier energyMethod, IEnergySink sink, IEnergySource source, EnumFacing f) {
		face = f;
		this.capMethod = capMethod;
		this.energyMethod = energyMethod;
		this.sink = sink;
		this.source = source;
	}

	@Override
	public long getEnergy() {
		double dc = 0;
		if (energyMethod != null)
			dc = energyMethod.getAsDouble();
		return (long) (dc * CFG_EU_VALUE);
	}

	@Override
	public long getEnergyCapacity() {
		double dc = 0;
		if (capMethod != null)
			dc = capMethod.getAsDouble();
		return (long) (dc * CFG_EU_VALUE);
	}

	@Override public boolean canInputEnergy() {
		return sink != null && sink.acceptsEnergyFrom(null, face);
	}

	@Override public boolean canOutputEnergy() {
		return source != null && source.emitsEnergyTo(null, face);
	}

	@Override
	public long inputEnergy(long amount, boolean sim) {
		if (amount < CFG_EU_VALUE)
			return 0;
		if (sink != null) {
			long e = (long) sink.getDemandedEnergy() * CFG_EU_VALUE;
			long r = amount - (amount % CFG_EU_VALUE);
			if (r > e)
				r = e;
			if (!sim) {
				sink.injectEnergy(face, r / CFG_EU_VALUE, EnergyNet.instance.getPowerFromTier(sink.getSinkTier()));
			}
			return r;
		}
		return 0;
	}

	@Override
	public long outputEnergy(long amount, boolean sim) {
		if (amount < CFG_EU_VALUE)
			return 0;
		if (source != null) {
			long e = (long) source.getOfferedEnergy() * CFG_EU_VALUE;
			long r = amount - (amount % CFG_EU_VALUE);
			if (r > e)
				r = e;
			if (!sim) {
				source.drawEnergy(r / CFG_EU_VALUE);
			}
			return r;
		}
		return 0;
	}

	@Override public boolean hasNoEnergy() {
		return energyMethod != null && energyMethod.getAsDouble() == 0;
	}

	@Override public boolean hasFullEnergy() {
		return energyMethod != null && capMethod != null && energyMethod.getAsDouble() == capMethod.getAsDouble();
	}
}
