package szewek.mcflux.wrapper.ic2;

import ic2.api.energy.EnergyNet;
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

public class EUTileCapabilityProvider implements IEnergyProducer, IEnergyConsumer, ICapabilityProvider {
	private static final int EU_VALUE = 4;
	@CapabilityInject(EUTileCapabilityProvider.class)
	static Capability<EUTileCapabilityProvider> SELF_CAP = null;

	private Energy energy = null;
	private IEnergySource source = null;
	private IEnergySink sink = null;
	
	EUTileCapabilityProvider() {
	}

	public EUTileCapabilityProvider(Energy e) {
		energy = e;
		source = (IEnergySource) e.getDelegate();
		sink = (IEnergySink) e.getDelegate();
	}

	void updateEnergyTile(IEnergyTile iet) {
		source = iet instanceof IEnergySource ? (IEnergySource) iet : null;
		sink = iet instanceof IEnergySink ? (IEnergySink) iet : null;
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
		if (hasCapability(cap, f))
			return (T) this;
		return null;
	}

	@Override
	public int getEnergy() {
		return (int) (energy.getEnergy() * EU_VALUE);
	}

	@Override
	public int getEnergyCapacity() {
		return (int) (energy.getCapacity() * EU_VALUE);
	}

	@Override
	public int consumeEnergy(int amount, boolean sim) {
		if (amount < EU_VALUE)
			return 0;
		if (sink != null) {
			int e = (int) sink.getDemandedEnergy() * EU_VALUE;
			int r = amount - (amount % EU_VALUE);
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
		if (amount < EU_VALUE)
			return 0;
		if (source != null) {
			int e = (int) source.getOfferedEnergy() * EU_VALUE;
			int r = amount - (amount % EU_VALUE);
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
