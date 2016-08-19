package szewek.mcflux.api.flavor;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagLong;
import net.minecraftforge.common.util.INBTSerializable;

public class FlavorEnergyStorage extends FlavorEnergyModifiable implements IFlavorEnergyStorage, INBTSerializable<NBTTagLong> {
	private final long capacity;

	public FlavorEnergyStorage(String flavor, NBTTagCompound data, long cap) {
		super(flavor, data);
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
	public NBTTagLong serializeNBT() {
		return new NBTTagLong(amount);
	}

	@Override
	public void deserializeNBT(NBTTagLong nbt) {
		amount = nbt.getLong();
	}

}
