package szewek.mcflux.fluxable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants.NBT;
import szewek.mcflux.api.CapabilityEnergy;
import szewek.mcflux.api.IEnergyConsumer;
import szewek.mcflux.api.IEnergyHolder;
import szewek.mcflux.api.IEnergyProducer;

public class PlayerEnergy implements IEnergyHolder, IEnergyProducer, IEnergyConsumer, ICapabilityProvider {
	private int energy = 0, maxEnergy = 0;
	private final EntityPlayer player;
	public PlayerEnergy(EntityPlayer p) {
		player = p;
		NBTTagCompound nbtp = player.getEntityData();
		if (nbtp.hasKey("fluxLvl", NBT.TAG_BYTE)) {
			byte lvl = nbtp.getByte("fluxLvl");
			if (lvl > 10)
				lvl = 10;
			maxEnergy = 100000 * lvl;
		}
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing f) {
		return maxEnergy > 0 && (cap == CapabilityEnergy.ENERGY_CONSUMER || cap == CapabilityEnergy.ENERGY_PRODUCER);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing f) {
		return hasCapability(cap, f) ? (T) this : null;
	}
	
	@Override
	public int extractEnergy(int amount, boolean simulate) {
		if (amount == 0)
			return 0;
		int r = energy;
		if (amount < r)
			r = amount;
		if (!simulate)
			energy -= r;
		return r;
	}

	@Override
	public int consumeEnergy(int amount, boolean simulate) {
		if (amount == 0)
			return 0;
		int r = maxEnergy - energy;
		if (amount < r)
			r = amount;
		if (!simulate)
			energy += r;
		return r;
	}
	
	@Override
	public int getEnergy() {
		return energy;
	}

	@Override
	public int getEnergyCapacity() {
		return maxEnergy;
	}
	
	@Override
	public void setEnergy(int amount) {
		energy = amount > maxEnergy ? maxEnergy : amount;
	}
}
