package szewek.mcflux.fluxable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import szewek.mcflux.api.ex.EX;
import szewek.mcflux.api.ex.IEnergy;

public class PlayerEnergy implements IEnergy, ICapabilityProvider, INBTSerializable<NBTBase> {
	@CapabilityInject(PlayerEnergy.class)
	public static Capability<PlayerEnergy> SELF_CAP;

	private long energy = 0, maxEnergy = 0;
	private byte lvl = 0;
	private final EntityPlayer player;

	public PlayerEnergy() {
		this(null);
	}

	PlayerEnergy(EntityPlayer p) {
		player = p;
	}

	public byte updateLevel() {
		if (lvl == 30)
			return -1;
		++lvl;
		maxEnergy = 100000 * lvl;
		return lvl;
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing f) {
		return cap == SELF_CAP || (maxEnergy > 0 && cap == EX.CAP_ENERGY);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing f) {
		return cap == SELF_CAP || (maxEnergy > 0 && cap == EX.CAP_ENERGY) ? (T) this : null;
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
		NBTTagCompound nbt = new NBTTagCompound();
		nbt.setByte("lvl", lvl);
		nbt.setLong("e", energy);
		return nbt;
	}

	@Override public void deserializeNBT(NBTBase nbt) {
		if (nbt instanceof NBTTagCompound) {
			NBTTagCompound nbttc = (NBTTagCompound) nbt;
			lvl = nbttc.getByte("lvl");
			energy = nbttc.getLong("e");
		}
	}
}
