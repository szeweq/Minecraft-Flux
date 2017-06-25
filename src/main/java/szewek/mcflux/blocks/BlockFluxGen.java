package szewek.mcflux.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryHelper;
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

	@Override
	public void onBlockPlacedBy(World w, BlockPos bp, IBlockState ibs, EntityLivingBase placer, ItemStack stack) {
		if (!w.isRemote)
			updateRedstoneState(w, bp);
	}

	@Override
	public void neighborChanged(IBlockState ibs, World w, BlockPos bp, Block b, BlockPos fromPos) {
		if (!w.isRemote)
			updateRedstoneState(w, bp);
	}

	@Override
	public void breakBlock(World w, BlockPos pos, IBlockState state) {
		TileEntity te = w.getTileEntity(pos);
		if (te instanceof TileEntityFluxGen) {
			InventoryHelper.dropInventoryItems(w, pos, (TileEntityFluxGen) te);
			w.updateComparatorOutputLevel(pos, this);
		}
		super.breakBlock(w, pos, state);
	}

	private void updateRedstoneState(World w, BlockPos bp) {
		TileEntityFluxGen tefg = (TileEntityFluxGen) w.getTileEntity(bp);
		if (tefg != null) {
			boolean b = tefg.getReceivedRedstone(), nb = false;
			for (EnumFacing f : EnumFacing.VALUES)
				if (w.getRedstonePower(bp.offset(f, 1), f) > 0) {
					nb = true;
					break;
				}
			if (b != nb)
				tefg.setReceivedRedstone(nb);
		}
	}
}
