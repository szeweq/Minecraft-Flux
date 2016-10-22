package szewek.mcflux.api.fe;

import net.minecraft.nbt.NBTTagCompound;

public class FlavoredMutable extends Flavored {
	protected long amount;

	public FlavoredMutable(String name, long amount, NBTTagCompound data) {
		super(name, data);
		this.amount = amount;
	}

	public FlavoredMutable(Flavored fl, long amount) {
		super(fl.name, fl.data);
		this.amount = amount;
	}

	public void setAmount(long amount) {
		this.amount = amount;
	}

	@Override public long getAmount() {
		return amount;
	}

	public Flavored toImmutable() {
		return new FlavoredImmutable(name, amount, data);
	}
}
