package szewek.mcflux.compat.top;

import mcjty.theoneprobe.api.*;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import szewek.fl.FLU;
import szewek.fl.energy.IEnergy;
import szewek.mcflux.MCFluxResources;
import szewek.mcflux.R;
import szewek.mcflux.U;
import szewek.mcflux.blocks.BlockEnergyMachine;
import szewek.mcflux.blocks.BlockWET;
import szewek.mcflux.config.MCFluxConfig;
import szewek.mcflux.fluxable.WorldChunkEnergy;
import szewek.mcflux.fluxcompat.FluxCompat;

public final class MCFluxTOPProvider implements IProbeInfoProvider, IProbeInfoEntityProvider {
	private static final TextComponentTranslation
			compat = new TextComponentTranslation("mcflux.mfcompatible"),
			wetMode0 = new TextComponentTranslation("mcflux.wet.mode0"),
			wetMode1 = new TextComponentTranslation("mcflux.wet.mode1"),
			mfConvert = new TextComponentTranslation("mcflux.convert");
	private static final String ID = R.MF_NAME + ":top_info";
	private static IProgressStyle fStyle = null;

	private static IProgressStyle getFStyle(IProbeInfo info) {
		if (fStyle == null)
			fStyle = info.defaultProgressStyle().borderColor(0xFFC0C0C0).filledColor(0xFFDC181E).alternateFilledColor(0xFFDC181E).backgroundColor(0xFF4C080E).height(6).showText(false);
		return fStyle;
	}

	@Override
	public void addProbeEntityInfo(ProbeMode mode, IProbeInfo info, EntityPlayer p, World w, Entity e, IProbeHitEntityData data) {
		IEnergy ie = FLU.getEnergySafely(e, null);
		if (ie != null) {
			long en = ie.getEnergy(), ec = ie.getEnergyCapacity();
			if (ec == 1)
				info.text(compat.getUnformattedText());
			else
				info.text(U.formatMF(ie)).progress(en, ec);
		}
	}

	@Override public String getID() {
		return ID;
	}

	@Override
	public void addProbeInfo(ProbeMode mode, IProbeInfo info, EntityPlayer p, World w, IBlockState ibs, IProbeHitData data) {
		BlockPos bp = data.getPos();
		Block b = ibs.getBlock();
		if (b == MCFluxResources.WET) {
			int m = ibs.getValue(BlockWET.MODE);
			info.text((m == 0 ? wetMode0 : wetMode1).getUnformattedText());
		} else if (b == MCFluxResources.ENERGY_MACHINE) {
			BlockEnergyMachine.Variant var = ibs.getValue(BlockEnergyMachine.VARIANT);
			if (var == BlockEnergyMachine.Variant.ENERGY_DIST || var == BlockEnergyMachine.Variant.CHUNK_CHARGER) {
				WorldChunkEnergy wce = w.getCapability(WorldChunkEnergy.CAP_WCE, null);
				if (wce != null)
					displayMF(info, wce.getEnergyChunk(bp.getX(), bp.getY(), bp.getZ()), p.isSneaking());
			}
		} else {
			TileEntity te = w.getTileEntity(bp);
			if (te != null)
				displayMF(info, FLU.getEnergySafely(te, data.getSideHit()), p.isSneaking());
		}
	}

	private void displayMF(IProbeInfo info, IEnergy ie, boolean sneak) {
		if (ie == null)
			return;
		long en = ie.getEnergy(), ec = ie.getEnergyCapacity();
		boolean fcc = ie instanceof FluxCompat.Convert;
		if (MCFluxConfig.SHOW_FLUXCOMPAT || !fcc)
			info.text(U.formatMF(ie)).progress(en, ec, getFStyle(info));
		if (sneak && fcc)
			info.text(mfConvert.getUnformattedText() + ' ' + ((FluxCompat.Convert) ie).getEnergyType());
	}
}
