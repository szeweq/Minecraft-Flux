package szewek.mcflux.wrapper.roots;

import elucent.roots.capability.mana.IManaCapability;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.mcflux.api.fe.FE;
import szewek.mcflux.api.fe.Flavored;
import szewek.mcflux.api.fe.FlavoredImmutable;
import szewek.mcflux.api.fe.IFlavorEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static szewek.mcflux.wrapper.roots.RootsInjectRegistry.*;

public final class RootsPlayerWrapper implements IFlavorEnergy, ICapabilityProvider {
	private final EntityPlayer player;
	private final IManaCapability manaCap;
	RootsPlayerWrapper(EntityPlayer p) {
		player = p;
		manaCap = p.getCapability(MANA_CAP, null);
	}

	@Override public boolean hasCapability(@Nonnull Capability<?> cap, @Nullable EnumFacing f) {
		return manaCap != null && cap == FE.CAP_FLAVOR_ENERGY;
	}

	@SuppressWarnings("unchecked")
	@Nullable @Override public <T> T getCapability(@Nonnull Capability<T> cap, @Nullable EnumFacing f) {
		return manaCap != null && cap == FE.CAP_FLAVOR_ENERGY ? (T) this : null;
	}

	@Override public boolean canInputFlavorEnergy(Flavored fl) {
		return MANA.equals(fl.name) && manaCap.getMana() < manaCap.getMaxMana();
	}

	@Override public boolean canOutputFlavorEnergy(Flavored fl) {
		return MANA.equals(fl.name) && manaCap.getMana() > 0;
	}

	@Override public long inputFlavorEnergy(Flavored fl, boolean sim) {
		if (!MANA.equals(fl.name))
			return 0;
		float cc = manaCap.getMana();
		float c = manaCap.getMaxMana() - cc;
		long r = fl.getAmount();
		if (r > c)
			r = (long) c;
		if (!sim)
			manaCap.setMana(player, cc + r);
		return r;
	}

	@Override public long outputFlavorEnergy(Flavored fl, boolean sim) {
		if (!MANA.equals(fl.name))
			return 0;
		float c = manaCap.getMana();
		long r = fl.getAmount();
		if (r > c)
			r = (long) c;
		if (!sim)
			manaCap.setMana(player, c - r);
		return r;
	}

	@Override public Flavored outputAnyFlavorEnergy(long amount, boolean sim) {
		float c = manaCap.getMana();
		if (c == 0)
			return null;
		long r = amount > c ? (long) c : amount;
		if (!sim)
			manaCap.setMana(player, c - r);
		return new FlavoredImmutable(MANA, r, null);
	}

	@Override public long getFlavorEnergyAmount(Flavored fl) {
		return MANA.equals(fl.name) ? (long) manaCap.getMana() : 0;
	}

	@Override public long getFlavorEnergyCapacity(Flavored fl) {
		return MANA.equals(fl.name) ? (long) manaCap.getMaxMana() : 0;
	}

	@Override public Flavored[] allFlavorsContained() {
		return manaFill;
	}

	@Override public Flavored[] allFlavorsAcceptable() {
		return manaFill;
	}
}
