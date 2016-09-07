package szewek.mcflux.api.ex;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagLong;
import net.minecraftforge.common.util.INBTSerializable;

public class Battery implements IEnergy, INBTSerializable<NBTBase> {
	protected long energy = 0;
	protected final long maxEnergy;

	public Battery() {
		this(50000);
	}

	public Battery(long max) {
		maxEnergy = max;
	}

	@Override
	public boolean canInputEnergy() {
		return true;
	}

	@Override
	public boolean canOutputEnergy() {
		return true;
	}

	@Override
	public long inputEnergy(long amount, boolean sim) {
		if (amount == 0)
			return 0;
		long r = maxEnergy - energy;
		if (amount < r)
			r = amount;
		if (!sim)
			energy += r;
		return r;
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

	@Override
	public long getEnergy() {
		return energy;
	}

	@Override
	public long getEnergyCapacity() {
		return maxEnergy;
	}

	@Override
	public void setEnergy(long amount) {
		energy = amount > maxEnergy ? maxEnergy : amount;
	}

	@Override public NBTBase serializeNBT() {
		return new NBTTagLong(energy);
	}

	@Override public void deserializeNBT(NBTBase nbt) {
		if (!(nbt instanceof NBTTagLong))
			return;
		energy = ((NBTTagLong) nbt).getLong();
	}
}
