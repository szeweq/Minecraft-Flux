package szewek.mcflux.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.items.wrapper.InvWrapper;
import szewek.mcflux.MCFlux;
import szewek.mcflux.R;
import szewek.mcflux.tileentities.TileEntityFluxGen;

import javax.annotation.Nullable;

public class BlockFluxGen extends BlockMCFluxContainer {

	public BlockFluxGen() {
		super();
		setHardness(1);
	}

	@Nullable @Override public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityFluxGen();
	}

	@Override
	public boolean onBlockActivated(World w, BlockPos pos, IBlockState state, EntityPlayer p, EnumHand h, EnumFacing f, float x, float y, float z) {
		ItemStack is = p.getHeldItem(h);
		if (!w.isRemote) {
			TileEntity te = w.getTileEntity(pos);
			if (te instanceof TileEntityFluxGen) {
				FluidActionResult far = FluidUtil.tryEmptyContainerAndStow(is, (TileEntityFluxGen) te, new InvWrapper(p.inventory), TileEntityFluxGen.fluidCap, p);
				if (far.success)
					p.setHeldItem(h, far.result);
				else
					p.openGui(MCFlux.MF, R.MF_GUI_FLUXGEN, w, pos.getX(), pos.getY(), pos.getZ());
			}
		}
		return true;
	}
}
