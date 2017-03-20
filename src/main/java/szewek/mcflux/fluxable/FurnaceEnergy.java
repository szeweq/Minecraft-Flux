package szewek.mcflux.fluxable;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import szewek.fl.FL;
import szewek.mcflux.api.ex.EX;
import szewek.mcflux.api.ex.EnergyCapable;
import szewek.mcflux.util.ForgeEnergyCompat;

import javax.annotation.Nullable;

import static szewek.mcflux.config.MCFluxConfig.FURNACE_CAP;

public final class FurnaceEnergy extends EnergyCapable {
	private final TileEntityFurnace furnace;
	private final ForgeEnergyCompat fec = new ForgeEnergyCompat(this);

	public FurnaceEnergy(TileEntityFurnace tef) {
		furnace = tef;
	}

	@Override public boolean hasCapability(Capability<?> cap, @Nullable EnumFacing f) {
		return cap == EX.CAP_ENERGY || cap == CapabilityEnergy.ENERGY;
	}

	@SuppressWarnings("unchecked")
	@Override public <T> T getCapability(Capability<T> cap, @Nullable EnumFacing f) {
		if (cap == CapabilityEnergy.ENERGY)
			return (T) fec;
		return super.getCapability(cap, f);
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
		if (FL.isItemEmpty(is0))
			return false;
		ItemStack is = FurnaceRecipes.instance().getSmeltingResult(is0);
		if (FL.isItemEmpty(is))
			return false;
		ItemStack is2 = furnace.getStackInSlot(2);
		if (FL.isItemEmpty(is2))
			return true;
		if (!is2.isItemEqual(is))
			return false;
		int r = is2.getCount() + is.getCount();
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
