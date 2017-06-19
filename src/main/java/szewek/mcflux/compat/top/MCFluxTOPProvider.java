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
import szewek.mcflux.MCFluxResources;
import szewek.mcflux.R;
import szewek.mcflux.U;
import szewek.mcflux.api.MCFluxAPI;
import szewek.mcflux.api.ex.IEnergy;
import szewek.mcflux.blocks.BlockEnergyMachine;
import szewek.mcflux.blocks.BlockWET;
import szewek.mcflux.fluxable.WorldChunkEnergy;
import szewek.mcflux.wrapper.EnergyType;

public final class MCFluxTOPProvider implements IProbeInfoProvider, IProbeInfoEntityProvider {
	private static final TextComponentTranslation
			compat = new TextComponentTranslation("mcflux.mfcompatible"),
			wetMode0 = new TextComponentTranslation("mcflux.wet.mode0"),
			wetMode1 = new TextComponentTranslation("mcflux.wet.mode1"),
			mfConvert = new TextComponentTranslation("mcflux.convert");
	private static final String ID = R.MF_NAME + ":top_info";
	@Override
	public void addProbeEntityInfo(ProbeMode mode, IProbeInfo info, EntityPlayer p, World w, Entity e, IProbeHitEntityData data) {
		IEnergy ie = MCFluxAPI.getEnergySafely(e, null);
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
				displayMF(info, MCFluxAPI.getEnergySafely(te, data.getSideHit()), p.isSneaking());
		}
	}

	private void displayMF(IProbeInfo info, IEnergy ie, boolean sneak) {
		if (ie == null)
			return;
		long en = ie.getEnergy(), ec = ie.getEnergyCapacity();
		info.text(U.formatMF(ie)).progress(en, ec, info.defaultProgressStyle().filledColor(0xFFCC181E).alternateFilledColor(0xFFCC181E));
		if (sneak && ie instanceof EnergyType.Converter) {
			info.text(mfConvert.getUnformattedText() + ' ' + ((EnergyType.Converter) ie).getEnergyType());
		}
	}
}
