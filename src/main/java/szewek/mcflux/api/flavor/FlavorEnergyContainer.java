package szewek.mcflux.api.flavor;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;

public class FlavorEnergyContainer implements IFlavorEnergyProducer, IFlavorEnergyConsumer {
	protected FlavorEnergyStorage[] storageArray;
	
	public FlavorEnergyContainer(FlavorEnergyStorage... storage) {
		storageArray = storage;
	}

	@Override
	public long consumeFlavorEnergy(FlavorEnergy fe, boolean simulate) {
		if (fe.getAmount() == 0) return 0;
		for (FlavorEnergyStorage fes : storageArray) {
			if (fe.flavor.equals(fes.flavor) && fe.customData.equals(fes.customData)) {
				long amount = fe.getAmount();
				long r = fes.getCapacity() - fes.getAmount();
				if (amount < r)
					r = amount;
				if (!simulate)
					fes.addAmount(r);
				return r;
			}
		}
		return 0;
	}

	@Override
	public long extractFlavorEnergy(FlavorEnergy fe, boolean simulate) {
		if (fe.getAmount() == 0) return 0;
		for (FlavorEnergyStorage fes : storageArray) {
			if (fe.flavor.equals(fes.flavor) && fe.customData.equals(fes.customData)) {
				long amount = fe.getAmount();
				long r = fes.getAmount();
				if (amount < r)
					r = amount;
				if (!simulate)
					fes.substractAmount(r);
				return r;
			}
		}
		return 0;
	}

	@Override
	public FlavorEnergy extractAnyFlavorEnergy(long amount, boolean simulate) {
		for (FlavorEnergyStorage fes : storageArray) {
			if (fes.amount > 0) {
				long r = fes.getAmount();
				if (amount < r)
					r = amount;
				if (!simulate)
					fes.substractAmount(r);
				FlavorEnergyModifiable fem = new FlavorEnergyModifiable(fes.flavor, fes.customData.copy());
				fem.setAmount(r);
				return fem;
			}
		}
		return null;
	}

	@Override
	public NBTBase serializeNBT() {
		NBTTagList nbt = new NBTTagList();
		for (FlavorEnergyStorage fes : storageArray) {
			nbt.appendTag(fes.serializeNBT());
		}
		return nbt;
	}

	@Override
	public void deserializeNBT(NBTBase nbt) {
		if (nbt instanceof NBTTagList) {
			NBTTagList nbtlist = (NBTTagList) nbt;
			if (nbtlist.tagCount() == storageArray.length) {
				for (int i = 0; i < storageArray.length; i++) {
					FlavorEnergyStorage fes = storageArray[i];
					fes.deserializeNBT((NBTTagLong) nbtlist.get(i));
				}
			}
		}
	}

}
