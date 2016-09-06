package szewek.mcflux.fluxable;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.mcflux.api.CapabilityEnergy;
import szewek.mcflux.api.IEnergyConsumer;
import static szewek.mcflux.config.MCFluxConfig.FURNACE_CAP;

public class FurnaceEnergy implements IEnergyConsumer, ICapabilityProvider {
	
	private final TileEntityFurnace furnace;

	public FurnaceEnergy(TileEntityFurnace tef) {
		furnace = tef;
	}
	
	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing f) {
		return cap == CapabilityEnergy.ENERGY_CONSUMER;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing f) {
		return cap == CapabilityEnergy.ENERGY_CONSUMER ? (T) this : null;
	}

	@Override
	public int getEnergy() {
		int e = furnace.getField(0);
		return e > FURNACE_CAP ? FURNACE_CAP : e;
	}

	@Override
	public int getEnergyCapacity() {
		return canFurnaceSmelt() ? FURNACE_CAP : 0;
	}

	@Override
	public int consumeEnergy(int amount, boolean sim) {
		if (canFurnaceSmelt() && amount > 0) {
			int f = furnace.getField(0);
			if (f >= FURNACE_CAP) return 0;
			int fm = furnace.getField(1);
			if (fm < FURNACE_CAP)
				furnace.setField(1, FURNACE_CAP);
			int r = FURNACE_CAP - f;
			if (r > amount)
				r = amount;
			if (!sim)
				furnace.setField(0, f + r);
			return r;
		}
		return 0;
	}
	
	private boolean canFurnaceSmelt() {
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

}
