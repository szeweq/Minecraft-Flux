package szewek.mcflux.api;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import szewek.mcflux.api.util.CapabilityNBTStorage;

public final class CapabilityEnergy {
	private static boolean ONCE = true;

	@CapabilityInject(IEnergyProducer.class)
	public static Capability<IEnergyProducer> ENERGY_PRODUCER = null;
	@CapabilityInject(IEnergyConsumer.class)
	public static Capability<IEnergyConsumer> ENERGY_CONSUMER = null;

	public static void register() {
		if(!ONCE) return;
		ONCE = false;
		CapabilityManager.INSTANCE.register(IEnergyProducer.class, new CapabilityNBTStorage<IEnergyProducer>(), EnergyBattery::new);
		CapabilityManager.INSTANCE.register(IEnergyConsumer.class, new CapabilityNBTStorage<IEnergyConsumer>(), EnergyBattery::new);
	}
}
