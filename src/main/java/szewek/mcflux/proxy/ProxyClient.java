package szewek.mcflux.proxy;

import net.minecraftforge.fml.client.registry.ClientRegistry;
import szewek.mcflux.MCFlux;
import szewek.mcflux.client.EnergyDistributorRenderer;
import szewek.mcflux.tileentities.TileEntityEnergyDistributor;

public class ProxyClient extends ProxyCommon {
	@Override
	public void init() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEnergyDistributor.class, new EnergyDistributorRenderer());
		MCFlux.renders();
	}
}
