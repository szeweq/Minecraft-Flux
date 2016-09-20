package szewek.mcflux.api.fe;

import net.minecraft.nbt.NBTTagCompound;

public abstract class Flavored {
	public final String name;
	public final NBTTagCompound data;

	public Flavored(String name, NBTTagCompound data) {
		this.name = name;
		this.data = data;
	}

	public boolean areSameFlavor(Flavored fl) {
		return ((name == null && fl.name == null) || name.equals(fl.name)) && ((data == null && fl.data == null) || data.equals(fl.data));
	}

	public abstract long getAmount();
}
