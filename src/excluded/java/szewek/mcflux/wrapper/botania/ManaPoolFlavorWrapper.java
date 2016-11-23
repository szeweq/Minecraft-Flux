package szewek.mcflux.wrapper.botania;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.mcflux.api.fe.FE;
import szewek.mcflux.api.fe.Flavored;
import szewek.mcflux.api.fe.FlavoredImmutable;
import szewek.mcflux.api.fe.IFlavorEnergy;
import vazkii.botania.common.block.tile.mana.TilePool;

import javax.annotation.Nullable;

import static szewek.mcflux.wrapper.botania.BotaniaInjectRegistry.BOTANIA_MANA;

public class ManaPoolFlavorWrapper implements IFlavorEnergy, ICapabilityProvider {
	private static final Flavored[] poolFill = new Flavored[]{new FlavoredImmutable(BOTANIA_MANA, null)};
	private final TilePool pool;

	ManaPoolFlavorWrapper(TilePool tp) {
		pool = tp;
	}

	@Override public boolean hasCapability(Capability<?> cap, @Nullable EnumFacing f) {
		return cap == FE.CAP_FLAVOR_ENERGY;
	}

	@SuppressWarnings("unchecked")
	@Override public <T> T getCapability(Capability<T> cap, @Nullable EnumFacing f) {
		return cap == FE.CAP_FLAVOR_ENERGY ? (T) this : null;
	}

	@Override public boolean canInputFlavorEnergy(Flavored fl) {
		return BOTANIA_MANA.equals(fl.name) && !pool.isFull();
	}

	@Override public boolean canOutputFlavorEnergy(Flavored fl) {
		return BOTANIA_MANA.equals(fl.name) && pool.getCurrentMana() > 0;
	}

	@Override public long inputFlavorEnergy(Flavored fl, boolean sim) {
		if (!BOTANIA_MANA.equals(fl.name))
			return 0;
		int c = pool.getAvailableSpaceForMana();
		long r = fl.getAmount();
		if (r > c)
			r = c;
		if (!sim)
			pool.recieveMana((int) r);
		return r;
	}

	@Override public long outputFlavorEnergy(Flavored fl, boolean sim) {
		if (!BOTANIA_MANA.equals(fl.name))
			return 0;
		int c = pool.getCurrentMana();
		long r = fl.getAmount();
		if (r > c)
			r = c;
		if (!sim)
			pool.recieveMana((int) -r);
		return r;
	}

	@Override public Flavored outputAnyFlavorEnergy(long amount, boolean sim) {
		int c = pool.getCurrentMana();
		if (c == 0)
			return null;
		long r = amount > c ? c : amount;
		if (!sim)
			pool.recieveMana((int) -r);
		return new FlavoredImmutable(BOTANIA_MANA, r, null);
	}

	@Override public long getFlavorEnergyAmount(Flavored fl) {
		return BOTANIA_MANA.equals(fl.name) ? pool.getCurrentMana() : 0;
	}

	@Override public long getFlavorEnergyCapacity(Flavored fl) {
		return BOTANIA_MANA.equals(fl.name) ? pool.manaCap : 0;
	}

	@Override public Flavored[] allFlavorsContained() {
		return poolFill;
	}

	@Override public Flavored[] allFlavorsAcceptable() {
		return poolFill;
	}
}
