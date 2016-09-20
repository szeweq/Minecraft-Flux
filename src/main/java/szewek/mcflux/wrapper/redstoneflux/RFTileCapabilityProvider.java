package szewek.mcflux.wrapper.redstoneflux;

import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.util.EnumFacing;
import szewek.mcflux.wrapper.EnergyCapabilityProvider;

class RFTileCapabilityProvider extends EnergyCapabilityProvider {
	private final IEnergyHandler handler;

	RFTileCapabilityProvider(IEnergyHandler ieh) {
		handler = ieh;
		IEnergyProvider provider = ieh instanceof IEnergyProvider ? (IEnergyProvider) ieh : null;
		IEnergyReceiver receiver = ieh instanceof IEnergyReceiver ? (IEnergyReceiver) ieh : null;
		broken = provider == null && receiver == null;
		for (int i = 0; i < 6; i++) {
			sides[i] = new RFSided(handler, provider, receiver, EnumFacing.VALUES[i]);
		}
		sides[6] = new RFSided(handler, provider, receiver, null);
	}

	@Override protected boolean canConnect(EnumFacing f) {
		return handler.canConnectEnergy(f);
	}
}
