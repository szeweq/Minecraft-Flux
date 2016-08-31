package szewek.mcflux.proxy;

import net.minecraft.item.Item;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import szewek.mcflux.MCFlux;
import szewek.mcflux.U;
import szewek.mcflux.client.EnergyDistributorRenderer;
import szewek.mcflux.tileentities.TileEntityEnergyDistributor;

public class ProxyClient extends ProxyCommon {
	@Override
	public void preInit() {
		U.registerItemMultiModels(Item.getItemFromBlock(MCFlux.ENERGY_MACHINE), 2);
	}
	
	@Override
	public void init() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEnergyDistributor.class, new EnergyDistributorRenderer());
		MCFlux.renders();
	}
}
