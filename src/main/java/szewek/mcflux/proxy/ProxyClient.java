package szewek.mcflux.proxy;

import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import szewek.mcflux.MCFlux;
import szewek.mcflux.U;
import szewek.mcflux.client.EnergyDistributorRenderer;
import szewek.mcflux.config.ConfigEvents;
import szewek.mcflux.network.MessageHandlerClient;
import szewek.mcflux.network.UpdateMessageServer;
import szewek.mcflux.tileentities.TileEntityEnergyDistributor;

public class ProxyClient extends ProxyCommon {
	private static final MessageHandlerClient MSG_CLI = new MessageHandlerClient();
	@Override
	public void preInit() {
		MinecraftForge.EVENT_BUS.register(new ConfigEvents());
		MCFlux.SNW.registerMessage(MSG_CLI, UpdateMessageServer.class, MCFlux.UPDATE_SRV, Side.CLIENT);
		U.registerItemMultiModels(Item.getItemFromBlock(MCFlux.ENERGY_MACHINE), 2);
	}

	@Override
	public void init() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEnergyDistributor.class, new EnergyDistributorRenderer());
		U.registerItemModels(MCFlux.MFTOOL, MCFlux.UPCHIP);
	}
}
