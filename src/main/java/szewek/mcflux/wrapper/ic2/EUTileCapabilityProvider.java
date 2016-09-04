package szewek.mcflux.wrapper.ic2;

import java.util.function.DoubleSupplier;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.prefab.BasicSink;
import ic2.api.energy.prefab.BasicSource;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;
import ic2.core.block.comp.Energy;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.mcflux.api.CapabilityEnergy;
import szewek.mcflux.api.IEnergyConsumer;
import szewek.mcflux.api.IEnergyProducer;

import static szewek.mcflux.config.MCFluxConfig.CFG_EU_VALUE;

public class EUTileCapabilityProvider implements IEnergyProducer, IEnergyConsumer, ICapabilityProvider {
	@CapabilityInject(EUTileCapabilityProvider.class)
	static Capability<EUTileCapabilityProvider> SELF_CAP = null;

	private IEnergySource source = null;
	private IEnergySink sink = null;
	
	private DoubleSupplier capMethod = null, energyMethod = null;
	
	EUTileCapabilityProvider() {
	}

	public EUTileCapabilityProvider(Energy e) {
		source = (IEnergySource) e.getDelegate();
		sink = (IEnergySink) e.getDelegate();
		capMethod = e::getCapacity;
		energyMethod = e::getEnergy;
	}

	void updateEnergyTile(IEnergyTile iet) {
		source = iet instanceof IEnergySource ? (IEnergySource) iet : null;
		sink = iet instanceof IEnergySink ? (IEnergySink) iet : null;
		if (capMethod == null)
			capMethod = iet instanceof BasicSource ? ((BasicSource) iet)::getCapacity : iet instanceof BasicSink ? ((BasicSink) iet)::getCapacity : null;
		if (energyMethod == null)
			energyMethod = iet instanceof BasicSource ? ((BasicSource) iet)::getEnergyStored : iet instanceof BasicSink ? ((BasicSink) iet)::getEnergyStored : null;
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing f) {
		if (cap == CapabilityEnergy.ENERGY_CONSUMER) {
			return sink != null && sink.acceptsEnergyFrom(null, f);
		}
		if (cap == CapabilityEnergy.ENERGY_PRODUCER) {
			return source != null && source.emitsEnergyTo(null, f);
		}
		return cap == SELF_CAP;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing f) {
		return hasCapability(cap, f) ? (T) this : null;
	}

	@Override
	public int getEnergy() {
		double dc = 0;
		if (energyMethod != null)
			dc = energyMethod.getAsDouble();
		return (int) (dc * CFG_EU_VALUE);
	}

	@Override
	public int getEnergyCapacity() {
		double dc = 0;
		if (capMethod != null)
			dc = capMethod.getAsDouble();
		return (int) (dc * CFG_EU_VALUE);
	}

	@Override
	public int consumeEnergy(int amount, boolean sim) {
		if (amount < CFG_EU_VALUE)
			return 0;
		if (sink != null) {
			int e = (int) sink.getDemandedEnergy() * CFG_EU_VALUE;
			int r = amount - (amount % CFG_EU_VALUE);
			if (r > e)
				r = e;
			if (!sim) {
				sink.injectEnergy(null, r / 4, EnergyNet.instance.getPowerFromTier(sink.getSinkTier()));
			}
			return (int) r;
		}
		return 0;
	}

	@Override
	public int extractEnergy(int amount, boolean sim) {
		if (amount < CFG_EU_VALUE)
			return 0;
		if (source != null) {
			int e = (int) source.getOfferedEnergy() * CFG_EU_VALUE;
			int r = amount - (amount % CFG_EU_VALUE);
			if (r > e)
				r = e;
			if (!sim) {
				source.drawEnergy(r / 4);
			}
			return r;
		}
		return 0;
	}
}
