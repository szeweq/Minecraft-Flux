package szewek.mcflux.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import szewek.mcflux.U;
import szewek.mcflux.blocks.BlockEnergyMachine;
import szewek.mcflux.render.EnergyMachineRenderer;
import szewek.mcflux.config.ConfigEvents;
import szewek.mcflux.special.SpecialEventHandler;
import szewek.mcflux.tileentities.TileEntityEnergyMachine;
import szewek.mcflux.util.MCFluxReport;

import static szewek.mcflux.MCFluxResources.*;

@SideOnly(Side.CLIENT)
public final class ProxyClient extends ProxyCommon {
	@Override
	public void preInit() {
		MCFluxReport.handleErrors();
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
				SPECIAL,
				Item.getItemFromBlock(ECHARGER),
				Item.getItemFromBlock(SIDED),
				Item.getItemFromBlock(WET),
				Item.getItemFromBlock(FLUXGEN));
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(SpecialEventHandler::getColors, SPECIAL);
	}

	@Override public EntityPlayer getSidedPlayer(EntityPlayer p) {
		return p == null ? Minecraft.getMinecraft().player : p;
	}
}
