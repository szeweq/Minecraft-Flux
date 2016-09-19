package szewek.mcflux.wrapper.immersiveflux;

import blusunrize.immersiveengineering.api.energy.immersiveflux.IFluxConnection;
import blusunrize.immersiveengineering.api.energy.immersiveflux.IFluxProvider;
import blusunrize.immersiveengineering.api.energy.immersiveflux.IFluxReceiver;
import net.minecraft.util.EnumFacing;
import szewek.mcflux.wrapper.CompatEnergyWrapper;
import szewek.mcflux.wrapper.EnergyCapabilityProvider;

class IFTileCapabilityProvider extends EnergyCapabilityProvider {
	private final IFluxConnection conn;

	IFTileCapabilityProvider(IFluxConnection ifc) {
		conn = ifc;
		IFluxProvider provider = ifc instanceof IFluxProvider ? (IFluxProvider) ifc : null;
		IFluxReceiver receiver = ifc instanceof IFluxReceiver ? (IFluxReceiver) ifc : null;
		broken = provider == null && receiver == null;
		for (int i = 0; i < 6; i++) {
			sides[i] = new IFSided(provider, receiver, EnumFacing.VALUES[i]);
			compatSides[i] = new CompatEnergyWrapper(sides[i]);
		}
		sides[6] = new IFSided(provider, receiver, null);
		compatSides[6] = new CompatEnergyWrapper(sides[6]);
	}

	@Override protected boolean canConnect(EnumFacing f) {
		return conn.canConnectEnergy(f);
	}
}
