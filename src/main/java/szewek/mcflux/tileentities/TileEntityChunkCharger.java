package szewek.mcflux.tileentities;

import net.minecraft.block.state.IBlockState;
import szewek.mcflux.MCFlux;
import szewek.mcflux.blocks.BlockEnergyMachine;

public class TileEntityChunkCharger extends TileEntityEnergyMachine {
	public TileEntityChunkCharger() {
		super(MCFlux.ENERGY_MACHINE.getDefaultState().withProperty(BlockEnergyMachine.VARIANT, BlockEnergyMachine.Variant.CHUNK_CHARGER));
	}
	
	public TileEntityChunkCharger(IBlockState ibs) {
		super(ibs);
	}

	@Override
	public void update() {
		
	}

}
