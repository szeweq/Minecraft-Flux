package szewek.mcflux.network

import net.minecraft.client.Minecraft
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.network.PacketBuffer
import net.minecraft.util.math.BlockPos
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.util.text.event.ClickEvent
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import szewek.fl.network.FLNetMsg
import szewek.mcflux.R
import szewek.mcflux.tileentities.TileEntityEnergyMachine
import szewek.mcflux.tileentities.TileEntityFluxGen
import szewek.mcflux.util.MCFluxReport
import szewek.mcflux.util.TransferType
import java.io.IOException

abstract class Msg : FLNetMsg() {

	override fun exception(x: Exception) {
		MCFluxReport.sendException(x, "Msg Exception from: " + this.javaClass.name)
	}

	internal fun unbreak() {
		broken = false
	}

	open class Update : Msg() {
		internal var pos: BlockPos? = null
		internal var sides: Array<TransferType>? = null

		@Throws(IOException::class)
		override fun decode(pb: PacketBuffer) {
			val rb = pb.readableBytes()
			if (rb != 8 && rb != 14)
				throw IOException("Msg.Update incomplete - too few readable bytes")
			pos = BlockPos.fromLong(pb.readLong())
			@Suppress("UNCHECKED_CAST")
			if (rb == 14) {
				val sd: Array<TransferType?> = arrayOfNulls(6)
				val ttv = TransferType.values()
				for (i in 0..5)
					sd[i] = ttv[pb.readByte().toInt()]
				sides = sd as Array<TransferType>
			}
			broken = false
		}

		@Throws(IOException::class)
		override fun encode(pb: PacketBuffer) {
			pb.writeLong(pos!!.toLong())
			if (sides != null)
				for (i in 0..5)
					pb.writeByte(sides!![i].ord.toInt())
		}

		override fun srvmsg(p: EntityPlayer) {
			val mp = p as EntityPlayerMP?
			if (mp != null) {
				val te = mp.world.getTileEntity(pos!!)
				if (te != null && te is TileEntityEnergyMachine)
					MCFluxNetwork.to(update(pos!!, te.allTransferSides), mp)
			}
		}

		@SideOnly(Side.CLIENT)
		override fun climsg(p: EntityPlayer) {
			if (sides == null || sides!!.size != 6) {
				return
			}
			val te = Minecraft.getMinecraft().world.getTileEntity(pos!!)
			if (te != null && te is TileEntityEnergyMachine)
				te.updateTransferSides(sides!!)
		}
	}

	open class NewVersion : Msg() {
		internal var version: String? = null

		@Throws(IOException::class)
		override fun decode(pb: PacketBuffer) {
			if (pb.readableBytes() < 2)
				throw IOException("Msg.NewVersion incomplete - too few readable bytes")
			version = pb.readString(32)
			broken = false
		}

		@Throws(IOException::class)
		override fun encode(pb: PacketBuffer) {
			pb.writeString(version!!)
		}

		@SideOnly(Side.CLIENT)
		override fun climsg(p: EntityPlayer) {
			val tt = TextComponentTranslation("mcflux.update.newversion", version!!)
			tt.style.clickEvent = ClickEvent(ClickEvent.Action.OPEN_URL, R.MF_URL)
			p.sendMessage(tt)
		}
	}

	open class FluidAmount : Msg() {
		internal var pos: BlockPos? = null
		internal var fluid: Fluid? = null
		internal var id: Int = 0
		internal var amount: Int = 0

		@Throws(IOException::class)
		override fun decode(pb: PacketBuffer) {
			pos = BlockPos.fromLong(pb.readLong())
			id = pb.readInt()
			amount = pb.readInt()
			if (amount > 0)
				fluid = FluidRegistry.getFluid(pb.readString(32767))
			broken = false
		}

		@Throws(IOException::class)
		override fun encode(pb: PacketBuffer) {
			pb.writeLong(pos!!.toLong())
			pb.writeInt(id)
			pb.writeInt(amount)
			if (amount > 0)
				pb.writeString(fluid!!.name)
		}

		override fun climsg(p: EntityPlayer) {
			val te = p.world.getTileEntity(pos!!)
			if (te is TileEntityFluxGen)
				te.updateFluid(id, fluid, amount)
		}
	}

	companion object {

		@JvmStatic
		fun update(bp: BlockPos, tts: Array<TransferType>): Msg {
			val m = Update()
			m.pos = bp
			m.sides = tts
			m.unbreak()
			return m
		}

		@JvmStatic
		fun newVersion(v: String): Msg {
			val m = NewVersion()
			m.version = v
			m.unbreak()
			return m
		}

		@JvmStatic
		fun fluidAmount(bp: BlockPos, id: Int, fs: FluidStack?): Msg {
			val m = FluidAmount()
			m.pos = bp
			m.id = id
			if (fs == null)
				m.amount = 0
			else {
				m.fluid = fs.fluid
				m.amount = fs.amount
			}
			m.unbreak()
			return m
		}
	}
}
