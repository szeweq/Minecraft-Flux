package szewek.mcflux.api.flavor;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;

/**
 * Simple flavored energy container. It allows multiple kinds of flavors.
 * You should depend on FlavorEnergyStorage internally.
 */
public class FlavorEnergyContainer implements IFlavorEnergyProducer, IFlavorEnergyConsumer, IFlavorEnergyHolder, IFlavorEnergyNBT {
	protected FlavorEnergyStorage[] storageArray;
	
	public FlavorEnergyContainer(FlavorEnergyStorage... storage) {
		storageArray = storage;
	}

	@Override
	public long consumeFlavorEnergy(FlavorEnergy fe, boolean simulate) {
		if (fe.getAmount() == 0) return 0;
		for (FlavorEnergyStorage fes : storageArray)
			if (fe.flavor.equals(fes.flavor) && fe.customData.equals(fes.customData))
				return fes.consumeFlavorEnergy(fe, simulate);
		return 0;
	}

	@Override
	public long extractFlavorEnergy(FlavorEnergy fe, boolean simulate) {
		if (fe.getAmount() == 0) return 0;
		for (FlavorEnergyStorage fes : storageArray)
			if (fe.flavor.equals(fes.flavor) && fe.customData.equals(fes.customData))
				return fes.extractFlavorEnergy(fe, simulate);
		return 0;
	}

	@Override
	public FlavorEnergy extractAnyFlavorEnergy(long amount, boolean simulate) {
		for (FlavorEnergyStorage fes : storageArray)
			if (fes.amount > 0)
				return fes.extractAnyFlavorEnergy(amount, simulate);
		return null;
	}

	@Override
	public NBTBase writeFlavorEnergyNBT() {
		NBTTagList nbt = new NBTTagList();
		for (FlavorEnergyStorage fes : storageArray)
			nbt.appendTag(fes.writeFlavorEnergyNBT());
		return nbt;
	}

	@Override
	public void readFlavorEnergyNBT(NBTBase nbt) {
		if (nbt instanceof NBTTagList) {
			NBTTagList nbtlist = (NBTTagList) nbt;
			if (nbtlist.tagCount() == storageArray.length) {
				for (int i = 0; i < storageArray.length; i++) {
					FlavorEnergyStorage fes = storageArray[i];
					fes.readFlavorEnergyNBT((NBTTagLong) nbtlist.get(i));
				}
			}
		}
	}

	@Override
	public IFlavorEnergyStorage[] getAllFlavors() {
		return storageArray;
	}

	@Override
	public IFlavorEnergyStorage getFlavor(FlavorEnergy fe) {
		for (FlavorEnergyStorage fes : storageArray)
			if (fe.flavor.equals(fes.flavor) && fe.customData.equals(fes.customData))
				return fes;
		return null;
	}

}
