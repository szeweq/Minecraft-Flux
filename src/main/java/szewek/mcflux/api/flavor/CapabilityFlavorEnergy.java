package szewek.mcflux.api.flavor;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import szewek.mcflux.api.IEnergyConsumer;
import szewek.mcflux.api.IEnergyProducer;

public class CapabilityFlavorEnergy {
	private static boolean ONCE = true;

	@CapabilityInject(IFlavorEnergyProducer.class)
	public static Capability<IEnergyProducer> FLAVORENERGY_PRODUCER = null;
	@CapabilityInject(IFlavorEnergyConsumer.class)
	public static Capability<IEnergyConsumer> FLAVORENERGY_CONSUMER = null;

	public static void register() {
		if(!ONCE) return;
		ONCE = false;
		CapabilityManager.INSTANCE.register(IFlavorEnergyProducer.class, new Storage<IFlavorEnergyProducer>(), FlavorEnergyContainer::new);
		CapabilityManager.INSTANCE.register(IFlavorEnergyConsumer.class, new Storage<IFlavorEnergyConsumer>(), FlavorEnergyContainer::new);
	}
	
	public static class Storage<T> implements Capability.IStorage<T> {
		@Override
		public NBTBase writeNBT(Capability<T> capability, T instance, EnumFacing side) {
			if (instance instanceof IFlavorEnergyNBT)
				return ((IFlavorEnergyNBT) instance).writeFlavorEnergyNBT();
			return null;
		}

		@Override
		public void readNBT(Capability<T> capability, T instance, EnumFacing side, NBTBase nbt) {
			if (instance instanceof IFlavorEnergyNBT)
				((IFlavorEnergyNBT) instance).readFlavorEnergyNBT(nbt);
		}
	}
}
