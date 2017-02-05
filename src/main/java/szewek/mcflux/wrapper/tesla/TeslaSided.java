package szewek.mcflux.wrapper.tesla;

import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.darkhax.tesla.lib.TeslaUtils;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.mcflux.wrapper.ForgeEnergyCapable;

public class TeslaSided extends ForgeEnergyCapable {
	private final EnumFacing face;
	private final ITeslaHolder holder;
	private final ITeslaConsumer consumer;
	private final ITeslaProducer producer;
	public TeslaSided(ICapabilityProvider provider, EnumFacing f) {
		face = f;
		holder = TeslaUtils.getTeslaHolder(provider, f);
		consumer = TeslaUtils.getTeslaConsumer(provider, f);
		producer = TeslaUtils.getTeslaProducer(provider, f);
	}

	@Override public boolean canInputEnergy() {
		return consumer != null;
	}

	@Override public boolean canOutputEnergy() {
		return producer != null;
	}

	@Override public long inputEnergy(long amount, boolean sim) {
		return consumer != null ? consumer.givePower(amount, sim) : 0;
	}

	@Override public long outputEnergy(long amount, boolean sim) {
		return producer != null ? producer.takePower(amount, sim) : 0;
	}

	@Override public long getEnergy() {
		return holder != null ? holder.getStoredPower() : 0;
	}

	@Override public long getEnergyCapacity() {
		return holder != null ? holder.getCapacity() : 0;
	}
}
