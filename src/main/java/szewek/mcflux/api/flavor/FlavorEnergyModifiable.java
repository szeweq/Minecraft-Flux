package szewek.mcflux.api.flavor;

import net.minecraft.nbt.NBTTagCompound;

/**
 * Flavor energy with a modifiable amount.
 */
public class FlavorEnergyModifiable extends FlavorEnergy {
	protected long amount;
	public FlavorEnergyModifiable(String flavor, NBTTagCompound data) {
		super(flavor, data);
	}

	public void addAmount(long a) {
		amount += a;
	}

	public void substractAmount(long a) {
		amount -= a;
	}
	
	public void setAmount(long a) {
		amount = a;
	}

	@Override
	public long getAmount() {
		return amount;
	}
}
