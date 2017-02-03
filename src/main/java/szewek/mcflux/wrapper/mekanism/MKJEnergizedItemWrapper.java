package szewek.mcflux.wrapper.mekanism;

import mekanism.api.energy.IEnergizedItem;
import net.minecraft.item.ItemStack;
import szewek.mcflux.api.ex.EnergyCapable;

import static szewek.mcflux.config.MCFluxConfig.CFG_MKJ_VALUE;

public class MKJEnergizedItemWrapper extends EnergyCapable {
	private final IEnergizedItem item;
	private final ItemStack stack;

	MKJEnergizedItemWrapper(IEnergizedItem it, ItemStack is) {
		item = it;
		stack = is;
	}

	@Override public boolean canInputEnergy() {
		return item.canReceive(stack);
	}

	@Override public boolean canOutputEnergy() {
		return item.canSend(stack);
	}

	@Override public long inputEnergy(long amount, boolean sim) {
		double dc = amount / CFG_MKJ_VALUE;
		double mt = item.getMaxTransfer(stack);
		double e = item.getEnergy(stack);
		if (dc > mt)
			dc = mt;
		if (dc > e)
			dc = e;
		if (!sim)
			item.setEnergy(stack, e - dc);
		return (long) (dc * CFG_MKJ_VALUE);
	}

	@Override public long outputEnergy(long amount, boolean sim) {
		double dc = amount / CFG_MKJ_VALUE;
		double mt = item.getMaxTransfer(stack);
		double e = item.getEnergy(stack);
		double ee = item.getMaxEnergy(stack) - e;
		if (dc > mt)
			dc = mt;
		if (dc > ee)
			dc = ee;
		if (!sim)
			item.setEnergy(stack, e + dc);
		return (long) (dc * CFG_MKJ_VALUE);
	}

	@Override public long getEnergy() {
		return (long) (item.getEnergy(stack) * CFG_MKJ_VALUE);
	}

	@Override public long getEnergyCapacity() {
		return (long) (item.getMaxEnergy(stack) * CFG_MKJ_VALUE);
	}
}
