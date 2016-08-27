package szewek.mcflux.api.flavor;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import szewek.mcflux.api.IEnergyConsumer;
import szewek.mcflux.api.IEnergyProducer;

public class CapabilityFlavorEnergy {
	@CapabilityInject(IFlavorEnergyProducer.class)
	public static Capability<IEnergyProducer> FLAVORENERGY_PRODUCER = null;
	@CapabilityInject(IFlavorEnergyConsumer.class)
	public static Capability<IEnergyConsumer> FLAVORENERGY_CONSUMER = null;

	public static class Storage<T> implements Capability.IStorage<T> {
		@Override
		public NBTBase writeNBT(Capability<T> cap, T t, EnumFacing side) {
			return t instanceof IFlavorEnergyNBT ? ((IFlavorEnergyNBT) t).writeFlavorEnergyNBT() : null;
		}

		@Override
		public void readNBT(Capability<T> cap, T t, EnumFacing side, NBTBase nbt) {
			if (t instanceof IFlavorEnergyNBT)
				((IFlavorEnergyNBT) t).readFlavorEnergyNBT(nbt);
		}
	}
}
