package szewek.mcflux.wrapper.mekanism;

import mekanism.api.energy.IStrictEnergyAcceptor;
import net.minecraft.util.EnumFacing;
import szewek.mcflux.wrapper.EnergyCapabilityProvider;

public class MKJTileCapabilityProvider extends EnergyCapabilityProvider {
	private final IStrictEnergyAcceptor acceptor;
	MKJTileCapabilityProvider(IStrictEnergyAcceptor isea) {
		acceptor = isea;
		for (int i = 0; i < 6; i++) {
			sides[i] = new MKJSided(isea, EnumFacing.VALUES[i]);
		}
		sides[6] = new MKJSided(isea, null);
		broken = false;
	}

	@Override protected boolean canConnect(EnumFacing f) {
		return acceptor.canReceiveEnergy(f);
	}
}
