package szewek.mcflux.api.flavor;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagLong;
import net.minecraftforge.common.util.INBTSerializable;

public class FlavorEnergyStorage extends FlavorEnergyModifiable implements IFlavorEnergyProducer, IFlavorEnergyConsumer, IFlavorEnergyStorage {
	private final long capacity;

	public FlavorEnergyStorage(String flavor, NBTTagCompound data, long cap) {
		super(flavor, data, 0);
		capacity = cap;
	}

	@Override
	public long getAmount() {
		return amount;
	}

	@Override
	public long getCapacity() {
		return capacity;
	}

	@Override
	public NBTBase serializeNBT() {
		return new NBTTagLong(amount);
	}

	@Override
	public void deserializeNBT(NBTBase nbt) {
		if (nbt instanceof NBTTagLong)
			amount = ((NBTTagLong) nbt).getLong();
	}

	@Override
	public long consumeFlavorEnergy(FlavorEnergy fe, boolean simulate) {
		long a = fe.getAmount();
		long r = capacity - amount;
		if (a < r)
			r = a;
		if (!simulate)
			amount += r;
		return r;
	}

	@Override
	public long extractFlavorEnergy(FlavorEnergy fe, boolean simulate) {
		long a = fe.getAmount();
		long r = amount;
		if (a < r)
			r = a;
		if (!simulate)
			amount -= r;
		return r;
	}

	@Override
	public FlavorEnergy extractAnyFlavorEnergy(long amount, boolean simulate) {
		if (this.amount > 0) {
			long r = amount < this.amount ? amount : this.amount;
			if (!simulate)
				this.amount -= r;
			return new FlavorEnergyModifiable(flavor, customData, r);
		}
		return null;
	}

}
