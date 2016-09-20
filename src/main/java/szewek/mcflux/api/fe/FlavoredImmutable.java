package szewek.mcflux.api.fe;

import net.minecraft.nbt.NBTTagCompound;

public final class FlavoredImmutable extends Flavored {
	private final long amount;

	public FlavoredImmutable(String name, NBTTagCompound data) {
		super(name, data);
		amount = 0;
	}

	public FlavoredImmutable(String name, long amount, NBTTagCompound data) {
		super(name, data);
		this.amount = amount;
	}

	@Override public long getAmount() {
		return amount;
	}
}
