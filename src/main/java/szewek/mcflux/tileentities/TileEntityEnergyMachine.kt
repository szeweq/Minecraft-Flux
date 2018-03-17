package szewek.mcflux.tileentities

import net.minecraft.block.state.IBlockState
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.network.NetworkManager
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import szewek.fl.FLU
import szewek.fl.annotations.NamedResource
import szewek.mcflux.MCFlux
import szewek.mcflux.blocks.BlockEnergyMachine
import szewek.mcflux.blocks.BlockSided
import szewek.mcflux.config.MCFluxConfig
import szewek.mcflux.network.MCFluxNetwork
import szewek.mcflux.network.Msg
import szewek.mcflux.util.TransferType

@NamedResource("mcflux:emachine")
class TileEntityEnergyMachine : TileEntityWCEAware(), ITickable {
	private var oddTick = true
	private var clientUpdate = true
	private var serverUpdate = false
	val allTransferSides = arrayOf(TransferType.NONE, TransferType.NONE, TransferType.NONE, TransferType.NONE, TransferType.NONE, TransferType.NONE)
	private val sideValues = longArrayOf(0, 0, 0, 0, 0, 0)
	var cachedState = MCFlux.Resources.SIDED.defaultState
		private set
	var moduleId: Int = 0

	override fun onLoad() {
		if (world.isRemote) {
			MCFluxNetwork.toServer(Msg.update(pos, emptyArray()))
			clientUpdate = false
		}
	}

	public override fun updateTile() {
		if (world.isRemote && clientUpdate) {
			MCFluxNetwork.toServer(Msg.update(pos, emptyArray()))
			clientUpdate = false
		} else if (!world.isRemote && serverUpdate) {
			MCFluxNetwork.toDimension(Msg.update(pos, allTransferSides), world.provider.dimension)
			serverUpdate = false
		}
		if (!world.isRemote && bat != null) {
			val i = if (oddTick) 0 else 3
			val m = i + 3
			for (j in i until m)
				sideValues[j] = 0
			when(moduleId) {
				0 -> moduleEnergyDistributor(i, m)
				1 -> moduleChunkCharger(i, m)
			}
		}
		oddTick = !oddTick
	}

	override fun readFromNBT(nbt: NBTTagCompound) {
		super.readFromNBT(nbt)
		moduleId = nbt.getInteger("module")
		val sides = nbt.getIntArray("sides")
		if (sides.size != 6) return
		val tt = TransferType.values()
		for (i in 0..5) {
			allTransferSides[i] = tt[sides[i]]
			cachedState = cachedState.withProperty(BlockSided.sideFromId(i), sides[i])
		}
		serverUpdate = true
	}

	override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
		super.writeToNBT(nbt)
		val sides = IntArray(6)
		for (i in 0..5) {
			sides[i] = allTransferSides[i].ord.toInt()
		}
		nbt.setIntArray("sides", sides)
		nbt.setInteger("module", moduleId)
		return nbt
	}

	override fun shouldRefresh(w: World, pos: BlockPos, obs: IBlockState, nbs: IBlockState): Boolean {
		return obs.block !== nbs.block || obs.getValue<BlockEnergyMachine.Variant>(BlockEnergyMachine.VARIANT) !== nbs.getValue<BlockEnergyMachine.Variant>(BlockEnergyMachine.VARIANT)
	}

	override fun getUpdatePacket(): SPacketUpdateTileEntity {
		return SPacketUpdateTileEntity(pos, blockMetadata, writeToNBT(NBTTagCompound()))
	}

	override fun onDataPacket(net: NetworkManager, pkt: SPacketUpdateTileEntity) {
		readFromNBT(pkt.nbtCompound)
	}

	override fun hasFastRenderer() = true

	fun switchSideTransfer(f: EnumFacing) {
		val s = f.index
		val v = (allTransferSides[s].ord + 1) % 3
		allTransferSides[s] = TransferType.values()[v]
		cachedState = cachedState.withProperty(BlockSided.sideFromId(s), v)
		MCFluxNetwork.toDimension(Msg.update(pos, allTransferSides), world.provider.dimension)
		markDirty()
	}

	fun getTransferSide(f: EnumFacing) = sideValues[f.index]

	fun updateTransferSides(tts: Array<TransferType>) {
		for (i in 0..5) {
			allTransferSides[i] = tts[i]
			cachedState = cachedState.withProperty(BlockSided.sideFromId(i), tts[i].ord.toInt())
		}
	}

	private fun moduleEnergyDistributor(i: Int, m: Int): Int {
		var i = i
		while (i < m) {
			val tt = allTransferSides[i]
			if (tt === TransferType.NONE) {
				i++
				continue
			}
			var f = EnumFacing.VALUES[i]
			val te = world.getTileEntity(pos.offset(f, 1))
			if (te == null) {
				i++
				continue
			}
			f = f.opposite
			val ea = FLU.getEnergySafely(te, f)
			if (ea == null) {
				i++
				continue
			}
			when (tt) {
				TransferType.INPUT -> sideValues[i] = ea.to(bat!!, (MCFluxConfig.ENERGY_DIST_TRANS * 2).toLong()) / 2
				TransferType.OUTPUT -> sideValues[i] = bat!!.to(ea, (MCFluxConfig.ENERGY_DIST_TRANS * 2).toLong()) / 2
				else -> sideValues[i] = 0
			}
			i++
		}
		return 0
	}

	private fun moduleChunkCharger(i: Int, m: Int): Int {
		var i = i
		while (i < m) {
			val tt = allTransferSides[i]
			if (tt === TransferType.NONE) {
				i++
				continue
			}
			val f = EnumFacing.VALUES[i]
			val bpc = pos.offset(f, 16)
			val ebc = wce!!.getEnergyChunk(bpc.x, bpc.y, bpc.z)
			when (tt) {
				TransferType.INPUT -> sideValues[i] = ebc.to(bat!!, (MCFluxConfig.CHUNK_CHARGER_TRANS * 2).toLong()) / 2
				TransferType.OUTPUT -> sideValues[i] = bat!!.to(ebc, (MCFluxConfig.CHUNK_CHARGER_TRANS * 2).toLong()) / 2
				else -> sideValues[i] = 0
			}
			i++
		}
		return 0
	}
}
