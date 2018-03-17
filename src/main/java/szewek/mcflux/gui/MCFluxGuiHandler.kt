package szewek.mcflux.gui

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler
import szewek.mcflux.R
import szewek.mcflux.containers.ContainerFluxGen
import szewek.mcflux.tileentities.TileEntityFluxGen

class MCFluxGuiHandler : IGuiHandler {
	override fun getServerGuiElement(ID: Int, p: EntityPlayer, w: World, x: Int, y: Int, z: Int): Any? {
		if (ID == R.MF_GUI_FLUXGEN) {
			val tefg = w.getTileEntity(BlockPos(x, y, z)) as TileEntityFluxGen?
			if (tefg != null)
				return ContainerFluxGen(p, tefg)
		}
		return null
	}

	override fun getClientGuiElement(ID: Int, p: EntityPlayer, w: World, x: Int, y: Int, z: Int): Any? {
		if (ID == R.MF_GUI_FLUXGEN) {
			val tefg = w.getTileEntity(BlockPos(x, y, z)) as TileEntityFluxGen?
			if (tefg != null)
				return GuiFluxGen(p, tefg)
		}
		return null
	}
}
