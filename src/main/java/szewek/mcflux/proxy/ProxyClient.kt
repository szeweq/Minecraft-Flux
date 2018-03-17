package szewek.mcflux.proxy

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.color.IItemColor
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.client.registry.ClientRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import szewek.mcflux.MCFlux
import szewek.mcflux.config.ConfigEvents
import szewek.mcflux.render.EnergyMachineRenderer
import szewek.mcflux.special.SpecialEventHandler
import szewek.mcflux.tileentities.TileEntityEnergyMachine
import szewek.mcflux.util.MCFluxReport

@SideOnly(Side.CLIENT)
class ProxyClient : ProxyCommon() {
	override fun preInit() {
		MCFluxReport.handleErrors()
		MinecraftForge.EVENT_BUS.register(ConfigEvents())
	}

	override fun init() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityEnergyMachine::class.java, EnergyMachineRenderer())
		Minecraft.getMinecraft().itemColors.registerItemColorHandler(IItemColor(SpecialEventHandler::getColors), MCFlux.Companion.Resources.SPECIAL)
	}

	override fun side() = "CLIENT"
}
