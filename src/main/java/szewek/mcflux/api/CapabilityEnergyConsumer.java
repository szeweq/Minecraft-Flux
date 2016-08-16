package szewek.mcflux.api;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilityEnergyConsumer {
	@CapabilityInject(IEnergyConsumer.class)
	public static Capability<IEnergyConsumer> ENERGY_CONSUMER_CAPABILITY = null;
	
	public static void register() {
		CapabilityManager.INSTANCE.register(IEnergyConsumer.class, new Capability.IStorage<IEnergyConsumer>() {
			@Override
			public NBTBase writeNBT(Capability<IEnergyConsumer> capability, IEnergyConsumer instance, EnumFacing side) {
				return instance.saveEnergyNBT();
			}

			@Override
			public void readNBT(Capability<IEnergyConsumer> capability, IEnergyConsumer instance, EnumFacing side,
					NBTBase nbt) {
				instance.loadEnergyNBT(nbt);
			}
		}, EnergyConsumerStorage::createDefault);
	}
}
