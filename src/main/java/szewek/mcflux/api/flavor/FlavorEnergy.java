package szewek.mcflux.api.flavor;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Basic implementation of flavored energy.
 */
public abstract class FlavorEnergy {
	public final String flavor;
	public final NBTTagCompound customData;
	
	/**
	 * Creates a basic flavor energy.
	 * @param flavor Unique flavor name.
	 * @param data Custom NBT-encoded data.
	 */
	public FlavorEnergy(String flavor, NBTTagCompound data) {
		this.flavor = flavor;
		customData = data;
	}
	
	/**
	 * Getter for flavor energy amount. It must be implemented in FlavorEnergy subclasses.
	 * @return Amount of contained flavor energy.
	 */
	public abstract long getAmount();
}
