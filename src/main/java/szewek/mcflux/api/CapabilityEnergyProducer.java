package szewek.mcflux.api;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityEnergyProducer {
	@CapabilityInject(IEnergyProducer.class)
	public static Capability<IEnergyProducer> ENERGY_PRODUCER_CAPABILITY = null;
	
	public static void register() {
		CapabilityManager.INSTANCE.register(IEnergyProducer.class, new Capability.IStorage<IEnergyProducer>() {
			@Override
			public NBTBase writeNBT(Capability<IEnergyProducer> capability, IEnergyProducer instance, EnumFacing side) {
				return instance.saveEnergyNBT();
			}

			@Override
			public void readNBT(Capability<IEnergyProducer> capability, IEnergyProducer instance, EnumFacing side,
					NBTBase nbt) {
				instance.loadEnergyNBT(nbt);
			}
		}, EnergyProducerStorage::createDefault);
	}
}
