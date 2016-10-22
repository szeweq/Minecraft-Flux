package szewek.mcflux.api.fe;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class FlavoredContainer implements IFlavorEnergy, INBTSerializable<NBTBase> {
	private final long capacity;
	private final Set<FlavoredStorage> flavors = new HashSet<>();

	public FlavoredContainer(long cap) {
		capacity = cap;
	}

	private FlavoredStorage findFlavor(Flavored fl) {
		for (FlavoredStorage fls : flavors)
			if (fl.areSameFlavor(fls))
				return fls;
		return null;
	}

	@Override public boolean canInputFlavorEnergy(Flavored fl) {
		return true;
	}

	@Override public boolean canOutputFlavorEnergy(Flavored fl) {
		return true;
	}

	@Override public long inputFlavorEnergy(Flavored fl, boolean sim) {
		FlavoredStorage fls = findFlavor(fl);
		if (fls == null) {
			fls = new FlavoredStorage(fl.name, fl.data == null ? null : fl.data.copy(), capacity);
			flavors.add(fls);
		}
		return fls.inputFlavorEnergy(fl, sim);
	}

	@Override public long outputFlavorEnergy(Flavored fl, boolean sim) {
		FlavoredStorage fls = findFlavor(fl);
		if (fls == null)
			return 0;
		return fls.outputFlavorEnergy(fl, sim);
	}

	@Override public Flavored outputAnyFlavorEnergy(long amount, boolean sim) {
		Iterator<FlavoredStorage> iterator = flavors.iterator();
		if (iterator.hasNext()) {
			FlavoredStorage fls = iterator.next();
			return fls.outputAnyFlavorEnergy(amount, sim);
		}
		return null;
	}

	@Override public long getFlavorEnergyAmount(Flavored fl) {
		FlavoredStorage fls = findFlavor(fl);
		if (fls == null)
			return 0;
		return fls.getFlavorEnergyAmount(fl);
	}

	@Override public long getFlavorEnergyCapacity(Flavored fl) {
		return capacity;
	}

	@Override public Flavored[] allFlavorsContained() {
		return flavors.toArray(new Flavored[flavors.size()]);
	}

	@Override public Flavored[] allFlavorsAcceptable() {
		return flavors.toArray(new Flavored[flavors.size()]);
	}

	@Override public NBTBase serializeNBT() {
		NBTTagList nbt = new NBTTagList();
		for (FlavoredStorage fls : flavors) {
			NBTTagCompound nbttc = new NBTTagCompound();
			nbttc.setString("name", fls.name);
			nbttc.setLong("amount", fls.getAmount());
			if (fls.data != null)
				nbttc.setTag("data", fls.data);
			nbt.appendTag(nbttc);
		}
		return nbt;
	}

	@Override public void deserializeNBT(NBTBase nbt) {
		if (nbt instanceof NBTTagList) {
			flavors.clear();
			NBTTagList nbtl = (NBTTagList) nbt;
			int m = nbtl.tagCount();
			for (int i = 0; i < m; i++) {
				NBTTagCompound nbttc = nbtl.getCompoundTagAt(i);
				long a = nbttc.getLong("amount");
				if (a <= 0)
					continue;
				String s = nbttc.getString("name");
				NBTTagCompound d = null;
				if (nbttc.hasKey("data", Constants.NBT.TAG_COMPOUND))
					d = nbttc.getCompoundTag("data");
				FlavoredStorage fls = new FlavoredStorage(s, d, capacity);
				fls.setAmount(a);
				flavors.add(fls);
			}
		}
	}
}
