package szewek.mcflux.proxy;

import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import szewek.mcflux.U;
import szewek.mcflux.blocks.BlockEnergyMachine;
import szewek.mcflux.client.EnergyMachineRenderer;
import szewek.mcflux.config.ConfigEvents;
import szewek.mcflux.tileentities.TileEntityEnergyMachine;

import static szewek.mcflux.MCFluxResources.*;

public final class ProxyClient extends ProxyCommon {
	@Override
	public void preInit() {
		MinecraftForge.EVENT_BUS.register(new ConfigEvents());
		U.registerItemMultiModels(Item.getItemFromBlock(ENERGY_MACHINE), BlockEnergyMachine.Variant.ALL_VARIANTS.length);
	}

	@Override
	public void init() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEnergyMachine.class, new EnergyMachineRenderer());
		U.registerItemModels(
				MFTOOL,
				FESNIFFER,
				UPCHIP,
				ASSISTANT,
				Item.getItemFromBlock(ECHARGER),
				Item.getItemFromBlock(SIDED),
				Item.getItemFromBlock(WET));
	}
}
