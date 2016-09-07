package szewek.mcflux.fluxable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTPrimitive;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.INBTSerializable;
import szewek.mcflux.api.ex.IEnergy;
import szewek.mcflux.wrapper.CompatEnergyWrapper;

class PlayerEnergy implements IEnergy, ICapabilityProvider, INBTSerializable<NBTBase> {
	private long energy = 0, maxEnergy = 0;
	private final EntityPlayer player;
	private final CompatEnergyWrapper cew;

	PlayerEnergy(EntityPlayer p) {
		player = p;
		NBTTagCompound nbtp = player.getEntityData();
		if (nbtp.hasKey("fluxLvl", NBT.TAG_BYTE)) {
			byte lvl = nbtp.getByte("fluxLvl");
			if (lvl > 30)
				lvl = 30;
			maxEnergy = 100000 * lvl;
		}
		cew = new CompatEnergyWrapper(this);
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing f) {
		return maxEnergy > 0 && (cap == IEnergy.CAP_ENERGY || cew.isCompatInputSuitable(cap) || cew.isCompatOutputSuitable(cap));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing f) {
		if (maxEnergy > 0) {
			if (cap == IEnergy.CAP_ENERGY)
				return (T) this;
			if (cew.isCompatInputSuitable(cap) || cew.isCompatOutputSuitable(cap))
				return (T) cew;
		}
		return null;
	}

	@Override
	public long outputEnergy(long amount, boolean sim) {
		if (amount == 0)
			return 0;
		long r = energy;
		if (amount < r)
			r = amount;
		if (!sim)
			energy -= r;
		return r;
	}

	@Override public boolean canInputEnergy() {
		return maxEnergy > 0;
	}

	@Override public boolean canOutputEnergy() {
		return maxEnergy > 0;
	}

	@Override
	public long inputEnergy(long amount, boolean sim) {
		if (amount == 0)
			return 0;
		if (maxEnergy == 0) {
			player.attackEntityFrom(DamageSource.generic, amount / 100);
			return 0;
		}
		long r = maxEnergy - energy;
		if (amount < r)
			r = amount;
		if (!sim)
			energy += r;
		return r;
	}

	@Override
	public long getEnergy() {
		return energy;
	}

	@Override
	public long getEnergyCapacity() {
		return maxEnergy;
	}

	@Override public NBTBase serializeNBT() {
		return new NBTTagLong(energy);
	}

	@Override public void deserializeNBT(NBTBase nbt) {
		if (nbt instanceof NBTPrimitive)
			energy = ((NBTPrimitive) nbt).getLong();
	}
}
