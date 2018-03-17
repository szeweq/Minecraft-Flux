package szewek.mcflux.compat.top

import mcjty.theoneprobe.api.*
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumFacing
import net.minecraft.util.text.TextComponentTranslation
import net.minecraft.world.World
import szewek.fl.FLU
import szewek.fl.energy.IEnergy
import szewek.mcflux.MCFlux
import szewek.mcflux.R
import szewek.mcflux.U
import szewek.mcflux.blocks.BlockEnergyMachine
import szewek.mcflux.blocks.BlockWET
import szewek.mcflux.config.MCFluxConfig
import szewek.mcflux.fluxable.FluxableCapabilities
import szewek.mcflux.fluxcompat.FluxCompat

class MCFluxTOPProvider : IProbeInfoProvider, IProbeInfoEntityProvider {

	override fun addProbeEntityInfo(mode: ProbeMode, info: IProbeInfo, p: EntityPlayer, w: World, e: Entity, data: IProbeHitEntityData) {
		val ie = FLU.getEnergySafely(e, EnumFacing.UP) // <- SET TO null
		if (ie != null) {
			val en = ie.energy
			val ec = ie.energyCapacity
			if (ec == 1L)
				info.text(compat.unformattedText)
			else
				info.text(U.formatMF(ie)).progress(en, ec)
		}
	}

	override fun getID(): String {
		return ID
	}

	override fun addProbeInfo(mode: ProbeMode, info: IProbeInfo, p: EntityPlayer, w: World, ibs: IBlockState, data: IProbeHitData) {
		val bp = data.pos
		val b = ibs.block
		if (b === MCFlux.Resources.WET) {
			val m = ibs.getValue(BlockWET.MODE)
			info.text((if (m == 0) wetMode0 else wetMode1).unformattedText)
		} else if (b === MCFlux.Resources.ENERGY_MACHINE) {
			val `var` = ibs.getValue<BlockEnergyMachine.Variant>(BlockEnergyMachine.VARIANT)
			if (`var` == BlockEnergyMachine.Variant.ENERGY_DIST || `var` == BlockEnergyMachine.Variant.CHUNK_CHARGER) {
				val wce = w.getCapability(FluxableCapabilities.CAP_WCE, null)
				if (wce != null)
					displayMF(info, wce.getEnergyChunk(bp.x, bp.y, bp.z), p.isSneaking)
			}
		} else {
			val te = w.getTileEntity(bp)
			if (te != null)
				displayMF(info, FLU.getEnergySafely(te, data.sideHit), p.isSneaking)
		}
	}

	private fun displayMF(info: IProbeInfo, ie: IEnergy?, sneak: Boolean) {
		if (ie == null)
			return
		val en = ie.energy
		val ec = ie.energyCapacity
		val fcc = ie is FluxCompat.Convert
		if (MCFluxConfig.SHOW_FLUXCOMPAT || !fcc)
			info.text(U.formatMF(ie)).progress(en, ec, getFStyle(info))
		if (sneak && fcc)
			info.text(mfConvert.unformattedText + ' '.toString() + (ie as FluxCompat.Convert).energyType)
	}

	companion object {
		private val compat = TextComponentTranslation("mcflux.mfcompatible")
		private val wetMode0 = TextComponentTranslation("mcflux.wet.mode0")
		private val wetMode1 = TextComponentTranslation("mcflux.wet.mode1")
		private val mfConvert = TextComponentTranslation("mcflux.convert")
		private val ID = R.MF_NAME + ":top_info"
		private var fStyle: IProgressStyle? = null

		private fun getFStyle(info: IProbeInfo): IProgressStyle {
			if (fStyle == null)
				fStyle = info.defaultProgressStyle().borderColor(-0x3f3f40).filledColor(-0x23e7e2).alternateFilledColor(-0x23e7e2).backgroundColor(-0xb3f7f2).height(6).showText(false)
			return fStyle!!
		}
	}
}
