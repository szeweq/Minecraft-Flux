package szewek.mcflux.wrapper.projecte;

import moze_intel.projecte.api.item.IItemEmc;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.mcflux.api.fe.FE;
import szewek.mcflux.api.fe.Flavored;
import szewek.mcflux.api.fe.FlavoredImmutable;
import szewek.mcflux.api.fe.IFlavorEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static szewek.mcflux.wrapper.projecte.ProjectEInjectRegistry.EMC;

final class EMCFlavorItemWrapper implements IFlavorEnergy, ICapabilityProvider {
	private final IItemEmc emcItem;
	private final ItemStack stack;

	EMCFlavorItemWrapper(ItemStack is) {
		emcItem = (IItemEmc) is.getItem();
		stack = is;
	}

	@Override public boolean hasCapability(@Nonnull Capability<?> cap, @Nullable EnumFacing f) {
		return emcItem != null && cap == FE.CAP_FLAVOR_ENERGY;
	}

	@SuppressWarnings("unchecked")
	@Nullable @Override public <T> T getCapability(@Nonnull Capability<T> cap, @Nullable EnumFacing f) {
		return emcItem != null && cap == FE.CAP_FLAVOR_ENERGY ? (T) this : null;
	}

	@Override public boolean canInputFlavorEnergy(Flavored fl) {
		return EMC.equals(fl.name) && emcItem.getStoredEmc(stack) < emcItem.getMaximumEmc(stack);
	}

	@Override public boolean canOutputFlavorEnergy(Flavored fl) {
		return EMC.equals(fl.name) && emcItem.getStoredEmc(stack) > 0;
	}

	@Override public long inputFlavorEnergy(Flavored fl, boolean sim) {
		if (!EMC.equals(fl.name))
			return 0;
		double c = emcItem.getMaximumEmc(stack) - emcItem.getStoredEmc(stack);
		long r = fl.getAmount();
		if (r > c)
			r = (long) c;
		if (!sim)
			r = (long) emcItem.addEmc(stack, r);
		return r;
	}

	@Override public long outputFlavorEnergy(Flavored fl, boolean sim) {
		if (!EMC.equals(fl.name))
			return 0;
		double c = emcItem.getStoredEmc(stack);
		long r = fl.getAmount();
		if (r > c)
			r = (long) c;
		if (!sim)
			r = (long) emcItem.extractEmc(stack, r);
		return r;
	}

	@Override public Flavored outputAnyFlavorEnergy(long amount, boolean sim) {
		double c = emcItem.getStoredEmc(stack);
		if (c == 0)
			return null;
		long r = amount > c ? (long) c : amount;
		if (!sim)
			r = (long) emcItem.extractEmc(stack, r);
		return new FlavoredImmutable(EMC, r, null);
	}

	@Override public long getFlavorEnergyAmount(Flavored fl) {
		return EMC.equals(fl.name) ? (long) emcItem.getStoredEmc(stack) : 0;
	}

	@Override public long getFlavorEnergyCapacity(Flavored fl) {
		return EMC.equals(fl.name) ? (long) emcItem.getMaximumEmc(stack) : 0;
	}

	@Override public Flavored[] allFlavorsContained() {
		return ProjectEInjectRegistry.emcFill;
	}

	@Override public Flavored[] allFlavorsAcceptable() {
		return ProjectEInjectRegistry.emcFill;
	}
}
