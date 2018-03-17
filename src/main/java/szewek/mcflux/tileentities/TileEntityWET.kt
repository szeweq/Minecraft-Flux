package szewek.mcflux.tileentities

import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos
import szewek.fl.FLU
import szewek.fl.annotations.NamedResource
import szewek.fl.energy.IEnergy
import szewek.mcflux.config.MCFluxConfig
import java.util.*

@NamedResource("mcflux:wet")
class TileEntityWET : TileEntityWCEAware(), ITickable {
	private var face: EnumFacing? = null
	private var faceOpposite: EnumFacing? = null
	private var out = false
	private var ix = -1
	private var iy = -1
	private var iz = -1
	private var nx = 1
	private var ny = 1
	private var nz = 1
	private var upos: BlockPos? = null
	private var poss: Iterable<BlockPos>? = null
	private val elist = ArrayList<IEnergy>(27)

	public override fun updateTile() {
		if (world.isRemote)
			return
		elist.clear()
		var se: IEnergy? = null
		var te: TileEntity?
		for (cbp in poss!!) {
			te = world.getTileEntity(cbp)
			if (te == null)
				continue
			val ie = FLU.getEnergySafely(te, faceOpposite!!)
			if (ie != null)
				elist.add(ie)
		}
		if (elist.isEmpty())
			return
		te = world.getTileEntity(upos!!)
		if (te != null)
			se = FLU.getEnergySafely(te, face!!)
		if (se == null) {
			if (bat != null)
				se = bat
			else
				return
		}
		for (ue in elist) {
			if (out) {
				ue.to(se!!, MCFluxConfig.WET_TRANS.toLong())
				if (se.hasFullEnergy())
					break
			} else {
				se!!.to(ue, MCFluxConfig.WET_TRANS.toLong())
				if (se.hasNoEnergy())
					break
			}
		}
	}

	override fun updateVariables(): Boolean {
		if (updateMode and 3 == 0)
			return true
		if (face == null) {
			val m = blockMetadata
			face = EnumFacing.VALUES[m / 2]
			out = m % 2 == 1
			faceOpposite = face!!.opposite
			when (face) {
				EnumFacing.DOWN -> {
					iy = -3
					ny = -1
				}
				EnumFacing.UP -> {
					iy = 1
					ny = 3
				}
				EnumFacing.NORTH -> {
					iz = -3
					nz = -1
				}
				EnumFacing.SOUTH -> {
					iz = 1
					nz = 3
				}
				EnumFacing.WEST -> {
					ix = -3
					nx = -1
				}
				EnumFacing.EAST -> {
					ix = 1
					nx = 3
				}
			}
		}
		poss = BlockPos.getAllInBox(pos.add(ix, iy, iz), pos.add(nx, ny, nz))
		upos = pos.offset(faceOpposite!!, 1)
		return true
	}
}
