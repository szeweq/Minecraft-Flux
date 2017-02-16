package szewek.mcflux.wrapper.immersiveflux;

import blusunrize.immersiveengineering.api.energy.immersiveflux.IFluxProvider;
import blusunrize.immersiveengineering.api.energy.immersiveflux.IFluxReceiver;
import net.minecraft.util.EnumFacing;
import szewek.mcflux.api.ex.IEnergy;

final class IFSided implements IEnergy {
	private final EnumFacing face;
	private final IFluxProvider provider;
	private final IFluxReceiver receiver;

	IFSided(IFluxProvider p, IFluxReceiver r, EnumFacing f) {
		face = f;
		provider = p;
		receiver = r;
	}

	@Override
	public long getEnergy() {
		return provider != null ? provider.getEnergyStored(face) : receiver != null ? receiver.getEnergyStored(face) : 0;
	}

	@Override
	public long getEnergyCapacity() {
		return provider != null ? provider.getMaxEnergyStored(face) : receiver != null ? receiver.getMaxEnergyStored(face) : 0;
	}

	@Override public boolean canInputEnergy() {
		return receiver != null;
	}

	@Override public boolean canOutputEnergy() {
		return provider != null;
	}

	@Override
	public long inputEnergy(long amount, boolean sim) {
		return receiver != null ? receiver.receiveEnergy(face, amount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) amount, sim) : 0;
	}

	@Override
	public long outputEnergy(long amount, boolean sim) {
		return provider != null ? provider.extractEnergy(face, amount > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) amount, sim) : 0;
	}

	@Override public boolean hasNoEnergy() {
		return (provider != null && provider.getEnergyStored(face) == 0) || (receiver != null && receiver.getEnergyStored(face) == 0);
	}

	@Override public boolean hasFullEnergy() {
		return (provider != null && provider.getEnergyStored(face) == provider.getMaxEnergyStored(face)) || (receiver != null && receiver.getEnergyStored(face) == receiver.getMaxEnergyStored(face));
	}
}
