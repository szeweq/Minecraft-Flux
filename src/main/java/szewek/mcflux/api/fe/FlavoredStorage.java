package szewek.mcflux.api.fe;

import net.minecraft.nbt.NBTTagCompound;

public class FlavoredStorage extends FlavoredMutable implements IFlavorEnergy {
	private final long capacity;
	private final Flavored accepted;

	public FlavoredStorage() {
		this("", null, 50000L);
	}

	public FlavoredStorage(String name, NBTTagCompound data, long cap) {
		super(name, 0, data);
		capacity = cap;
		accepted = new FlavoredImmutable(name, data);
	}

	@Override public boolean canInputFlavorEnergy(Flavored fl) {
		return areSameFlavor(fl);
	}

	@Override public boolean canOutputFlavorEnergy(Flavored fl) {
		return areSameFlavor(fl);
	}

	@Override public long inputFlavorEnergy(Flavored fl, boolean sim) {
		if (!areSameFlavor(fl)) return 0;
		long a = fl.getAmount();
		long r = capacity - amount;
		if (a < r)
			r = a;
		if (!sim)
			amount += r;
		return r;
	}

	@Override public long outputFlavorEnergy(Flavored fl, boolean sim) {
		if (!areSameFlavor(fl)) return 0;
		long a = fl.getAmount();
		long r = amount;
		if (a < r)
			r = a;
		if (!sim)
			amount -= r;
		return r;
	}

	@Override public Flavored outputAnyFlavorEnergy(long amount, boolean sim) {
		if (this.amount > 0) {
			long r = amount < this.amount ? amount : this.amount;
			if (!sim)
				this.amount -= r;
			return new FlavoredImmutable(name, r, data.copy());
		}
		return null;
	}

	@Override public long getFlavorEnergyAmount(Flavored fl) {
		return areSameFlavor(fl) ? amount : 0;
	}

	@Override public long getFlavorEnergyCapacity(Flavored fl) {
		return areSameFlavor(fl) ? capacity : 0;
	}

	@Override public Flavored[] allFlavorsContained() {
		return new Flavored[]{accepted};
	}

	@Override public Flavored[] allFlavorsAcceptable() {
		return new Flavored[]{accepted};
	}
}
