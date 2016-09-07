package szewek.mcflux.api;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

@Deprecated
public final class CapabilityEnergy {
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

	public static class Storage<T extends IEnergyHolder> implements Capability.IStorage<T> {
		@Override
		public NBTBase writeNBT(Capability<T> cap, T t, EnumFacing side) {
			return t instanceof IEnergyNBT ? ((IEnergyNBT) t).writeEnergyNBT() : new NBTTagInt(t.getEnergy());
		}

		@Override
		public void readNBT(Capability<T> cap, T t, EnumFacing side, NBTBase nbt) {
			if (t instanceof IEnergyNBT)
				((IEnergyNBT) t).readEnergyNBT(nbt);
			else if (t instanceof NBTPrimitive)
				t.setEnergy(((NBTPrimitive) nbt).getInt());
		}
	}
}
