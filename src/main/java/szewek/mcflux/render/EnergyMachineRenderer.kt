package szewek.mcflux.render

import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.BlockRendererDispatcher
import net.minecraft.client.renderer.BufferBuilder
import net.minecraftforge.client.model.animation.FastTESR
import szewek.mcflux.tileentities.TileEntityEnergyMachine

class EnergyMachineRenderer : FastTESR<TileEntityEnergyMachine>() {
	private var renderBlock: BlockRendererDispatcher? = null

	override fun renderTileEntityFast(te: TileEntityEnergyMachine, x: Double, y: Double, z: Double, partialTicks: Float, destroyStage: Int, partial: Float, bb: BufferBuilder) {
		if (renderBlock == null) renderBlock = Minecraft.getMinecraft().blockRendererDispatcher
		val bp = te.pos
		val ibs = te.cachedState
		bb.setTranslation(x - bp.x, y - bp.y, z - bp.z)
		renderBlock!!.blockModelRenderer.renderModel(te.world, renderBlock!!.getModelForState(ibs), ibs, bp, bb, false)
	}
}
