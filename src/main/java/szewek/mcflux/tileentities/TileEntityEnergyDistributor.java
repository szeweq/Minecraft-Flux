package szewek.mcflux.tileentities;

import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import szewek.mcflux.MCFlux;
import szewek.mcflux.U;
import szewek.mcflux.api.CapabilityEnergy;
import szewek.mcflux.api.EnergyBattery;
import szewek.mcflux.api.IEnergyConsumer;
import szewek.mcflux.api.IEnergyProducer;
import szewek.mcflux.blocks.BlockEnergyMachine;
import szewek.mcflux.config.MCFluxConfig;
import szewek.mcflux.fluxable.CapabilityFluxable;
import szewek.mcflux.fluxable.WorldChunkEnergy;
import szewek.mcflux.util.TransferType;

public class TileEntityEnergyDistributor extends TileEntityEnergyMachine {
	public TileEntityEnergyDistributor() {
		super(MCFlux.ENERGY_MACHINE.getDefaultState().withProperty(BlockEnergyMachine.VARIANT, BlockEnergyMachine.Variant.ENERGY_DIST));
	}

	public TileEntityEnergyDistributor(IBlockState ibs) {
		super(ibs);
	}

	@Override
	public void update() {
		super.update();
		if (worldObj.isRemote)
			return;
		WorldChunkEnergy wce = worldObj.getCapability(CapabilityFluxable.FLUXABLE_WORLD_CHUNK, null);
		EnergyBattery eb = wce.getEnergyChunk(pos.getX(), pos.getY(), pos.getZ());
		for (int i = 0; i < 6; i++) {
			TransferType tt = sideTransfer[i];
			if (tt == TransferType.NONE)
				continue;
			EnumFacing f = EnumFacing.VALUES[i];
			TileEntity te = worldObj.getTileEntity(pos.offset(f));
			if (te == null)
				continue;
			f = f.getOpposite();
			IEnergyProducer from = null;
			IEnergyConsumer to = null;
			switch (tt) {
			case INPUT:
				from = te.getCapability(CapabilityEnergy.ENERGY_PRODUCER, f);
				if (from == null)
					continue;
				to = eb;
				break;
			case OUTPUT:
				to = te.getCapability(CapabilityEnergy.ENERGY_CONSUMER, f);
				if (to == null)
					continue;
				from = eb;
				break;
			default:
			}
			U.transferEnergy(from, to, MCFluxConfig.ENERGY_DIST_TRANS);
		}
	}

}
