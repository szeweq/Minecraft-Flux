package szewek.mcflux.gui

import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.resources.I18n
import net.minecraft.entity.player.EntityPlayer
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.IFluidTankProperties
import szewek.fl.gui.FLGui
import szewek.fl.gui.GuiContainerFL
import szewek.fl.gui.GuiRect
import szewek.mcflux.U
import szewek.mcflux.containers.ContainerFluxGen
import szewek.mcflux.tileentities.TileEntityFluxGen
import szewek.mcflux.util.MCFluxLocation
import java.util.*

class GuiFluxGen(p: EntityPlayer, private val fluxGen: TileEntityFluxGen) : GuiContainerFL(ContainerFluxGen(p, fluxGen)) {
	private val fgName: String
	private val ipName: String
	private var nameL = -1
	private var nameX = -1
	private val tanks: Array<IFluidTankProperties>
	private var fsHot: FluidStack? = null
	private var fsClean: FluidStack? = null

	init {
		fgName = fluxGen.displayName.unformattedText
		ipName = p.inventory.displayName.unformattedText
		tanks = fluxGen.tankProperties
	}

	override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
		drawWorldBackground(0)
		super.drawScreen(mouseX, mouseY, partialTicks)
		renderHoveredToolTip(mouseX, mouseY)
	}

	override fun drawGuiContainerBackgroundLayer(partialTicks: Float, mouseX: Int, mouseY: Int) {
		GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
		mc.renderEngine.bindTexture(tex)
		drawTexturedModalRect(guiX, guiY, 0, 0, xSize, ySize)
		fsHot = tanks[0].contents
		fsClean = tanks[1].contents
		GlStateManager.pushMatrix()
		GlStateManager.translate(guiX.toFloat(), guiY.toFloat(), 0f)
		drawGuiBar(workRect, fluxGen.workFill, -0x575758, -0x101011, false, false)
		drawGuiBar(energyRect, fluxGen.energyFill, -0x57dedf, -0x10bdbe, true, true)
		FLGui.drawFluidStack(mc, hotFluidRect, zLevel, fsHot, TileEntityFluxGen.fluidCap)
		FLGui.drawFluidStack(mc, cleanFluidRect, zLevel, fsClean, TileEntityFluxGen.fluidCap)
		mc.renderEngine.bindTexture(tex)
		drawTexturedModalRect(47, 21, 176, 0, 16, 43)
		drawTexturedModalRect(113, 21, 176, 0, 16, 43)
		GlStateManager.popMatrix()
	}

	override fun drawGuiContainerForegroundLayer(mx: Int, my: Int) {
		var mx = mx
		var my = my
		mx -= guiX
		my -= guiY
		if (nameL == -1) {
			nameL = fontRenderer.getStringWidth(fgName)
			nameX = (xSize - nameL) / 2
		}
		fontRenderer.drawString(fgName, nameX, 4, 0x404040)
		fontRenderer.drawString(ipName, 8, ySize - 92, 0x404040)
		if (energyRect.contains(mx, my)) {
			drawHoveringText(Arrays.asList(U.formatMF(fluxGen), I18n.format("mcflux.gen", fluxGen.getField(3))), mx, my, fontRenderer)
		}
		displayFluidInfo(hotFluidRect, fsHot, TileEntityFluxGen.fluidCap, mx, my)
		displayFluidInfo(cleanFluidRect, fsClean, TileEntityFluxGen.fluidCap, mx, my)
	}

	companion object {
		private val tex = MCFluxLocation("textures/gui/fluxgen.png")
		private val workRect = GuiRect(86, 34, 4, 18)
		private val energyRect = GuiRect(68, 63, 40, 8)
		private val hotFluidRect = GuiRect(47, 15, 16, 56)
		private val cleanFluidRect = GuiRect(113, 15, 16, 56)
	}
}
