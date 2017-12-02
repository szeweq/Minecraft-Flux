package szewek.mcflux.proxy;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import szewek.mcflux.config.ConfigEvents;
import szewek.mcflux.render.EnergyMachineRenderer;
import szewek.mcflux.special.SpecialEventHandler;
import szewek.mcflux.tileentities.TileEntityEnergyMachine;
import szewek.mcflux.util.MCFluxReport;

import static szewek.mcflux.MCFluxResources.SPECIAL;

@SideOnly(Side.CLIENT)
public final class ProxyClient extends ProxyCommon {
	@Override
	public void preInit() {
		MCFluxReport.handleErrors();
		MinecraftForge.EVENT_BUS.register(new ConfigEvents());
	}

	@Override
	public void init() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEnergyMachine.class, new EnergyMachineRenderer());
		Minecraft.getMinecraft().getItemColors().registerItemColorHandler(SpecialEventHandler::getColors, SPECIAL);
	}

	@Override public String side() {
		return "CLIENT";
	}
}
