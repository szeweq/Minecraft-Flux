package szewek.mcflux.tileentities

import net.minecraft.tileentity.TileEntity
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import szewek.fl.energy.Battery
import szewek.mcflux.fluxable.FluxableCapabilities
import szewek.mcflux.fluxable.WorldChunkEnergy

open class TileEntityWCEAware : TileEntity(), ITickable {
	protected var wce: WorldChunkEnergy? = null
	protected var bat: Battery? = null
	protected var updateMode = 0

	override fun setWorld(w: World) {
		if (world == null || world != w)
			updateMode = updateMode or 1
		super.setWorld(w)
	}

	override fun setPos(bp: BlockPos) {
		if (pos == null || pos != bp)
			updateMode = updateMode or 2
		super.setPos(bp)
	}

	override fun update() {
		if (wce == null)
			updateMode = updateMode or 1
		if (bat == null)
			updateMode = updateMode or 2
		if (updateMode != 0) {
			if (updateMode and 1 != 0)
				wce = world.getCapability(FluxableCapabilities.CAP_WCE, null)
			if (wce == null)
				return
			if (updateMode and 2 != 0)
				bat = wce!!.getEnergyChunk(pos.x, pos.y, pos.z)
			if (updateVariables())
				updateMode = 0
		}
		updateTile()
	}

	protected open fun updateVariables() = true

	protected open fun updateTile() {}
}
