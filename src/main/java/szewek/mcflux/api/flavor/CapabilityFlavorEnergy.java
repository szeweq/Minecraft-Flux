package szewek.mcflux.api.flavor;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import szewek.mcflux.api.IEnergyConsumer;
import szewek.mcflux.api.IEnergyProducer;
import szewek.mcflux.api.util.CapabilityNBTStorage;

public class CapabilityFlavorEnergy {
	private static boolean ONCE = true;

	@CapabilityInject(IFlavorEnergyProducer.class)
	public static Capability<IEnergyProducer> FLAVORENERGY_PRODUCER = null;
	@CapabilityInject(IFlavorEnergyConsumer.class)
	public static Capability<IEnergyConsumer> FLAVORENERGY_CONSUMER = null;

	public static void register() {
		if(!ONCE) return;
		ONCE = false;
		CapabilityManager.INSTANCE.register(IFlavorEnergyProducer.class, new CapabilityNBTStorage<IFlavorEnergyProducer>(), FlavorEnergyContainer::new);
		CapabilityManager.INSTANCE.register(IFlavorEnergyConsumer.class, new CapabilityNBTStorage<IFlavorEnergyConsumer>(), FlavorEnergyContainer::new);
	}
}
