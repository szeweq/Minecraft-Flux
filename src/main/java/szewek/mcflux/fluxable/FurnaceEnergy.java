package szewek.mcflux.fluxable;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.mcflux.api.ex.IEnergy;
import szewek.mcflux.wrapper.CompatEnergyWrapper;

import static szewek.mcflux.config.MCFluxConfig.FURNACE_CAP;

class FurnaceEnergy implements IEnergy, ICapabilityProvider {
	private final TileEntityFurnace furnace;
	private final CompatEnergyWrapper cew;

	FurnaceEnergy(TileEntityFurnace tef) {
		furnace = tef;
		cew = new CompatEnergyWrapper(this);
	}
	
	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing f) {
		return cap == IEnergy.CAP_ENERGY || cew.isCompatInputSuitable(cap);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing f) {
		return (T) (cap == IEnergy.CAP_ENERGY ?  this : cew.isCompatInputSuitable(cap) ? cew : null);
	}

	@Override
	public long getEnergy() {
		int e = furnace.getField(0);
		return e > FURNACE_CAP ? FURNACE_CAP : e;
	}

	@Override
	public long getEnergyCapacity() {
		return canInputEnergy() ? FURNACE_CAP : 0;
	}

	@Override public boolean canInputEnergy() {
		ItemStack is0 = furnace.getStackInSlot(0);
		if (is0 == null)
			return false;
		ItemStack is = FurnaceRecipes.instance().getSmeltingResult(is0);
		if (is == null)
			return false;
		ItemStack is2 = furnace.getStackInSlot(2);
		if (is2 == null)
			return true;
		if (!is2.isItemEqual(is))
			return false;
		int r = is2.stackSize + is.stackSize;
		return r <= furnace.getInventoryStackLimit() && r <= is2.getMaxStackSize();
	}

	@Override public boolean canOutputEnergy() {
		return false;
	}

	@Override
	public long inputEnergy(long amount, boolean sim) {
		if (canInputEnergy() && amount > 0) {
			int f = furnace.getField(0);
			if (f >= FURNACE_CAP) return 0;
			int fm = furnace.getField(1);
			if (fm < FURNACE_CAP)
				furnace.setField(1, FURNACE_CAP);
			int r = FURNACE_CAP - f;
			if (r > amount)
				r = (int) amount;
			if (!sim)
				furnace.setField(0, f + r);
			return r;
		}
		return 0;
	}

	@Override public long outputEnergy(long amount, boolean sim) {
		return 0;
	}
}
