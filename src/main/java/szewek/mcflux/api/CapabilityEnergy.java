package szewek.mcflux.api;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import szewek.mcflux.api.flavor.IFlavorEnergyNBT;

public final class CapabilityEnergy {
	private static boolean ONCE = true;

	/**
	 * Energy producer capability (for extracting energy).
	 */
	@CapabilityInject(IEnergyProducer.class)
	public static Capability<IEnergyProducer> ENERGY_PRODUCER = null;
	/**
	 * Energy consumer capability (for consuming energy).
	 */
	@CapabilityInject(IEnergyConsumer.class)
	public static Capability<IEnergyConsumer> ENERGY_CONSUMER = null;

	public static void register() {
		if (!ONCE)
			return;
		ONCE = false;
		CapabilityManager.INSTANCE.register(IEnergyProducer.class, new Storage<IEnergyProducer>(), EnergyBattery::new);
		CapabilityManager.INSTANCE.register(IEnergyConsumer.class, new Storage<IEnergyConsumer>(), EnergyBattery::new);
	}

	public static class Storage<T extends IEnergyHolder> implements Capability.IStorage<T> {
		@Override
		public NBTBase writeNBT(Capability<T> capability, T instance, EnumFacing side) {
			if (instance instanceof IFlavorEnergyNBT)
				return ((IEnergyNBT) instance).writeEnergyNBT();
			return new NBTTagInt(instance.getEnergy());
		}

		@Override
		public void readNBT(Capability<T> capability, T instance, EnumFacing side, NBTBase nbt) {
			if (instance instanceof IFlavorEnergyNBT)
				((IEnergyNBT) instance).readEnergyNBT(nbt);
			else if (instance instanceof NBTPrimitive)
				instance.setEnergy(((NBTPrimitive) nbt).getInt());
		}
	}
}
