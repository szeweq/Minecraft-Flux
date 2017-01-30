package szewek.mcflux.blocks;

import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import szewek.mcflux.U;
import szewek.mcflux.tileentities.TileEntityWET;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockWET extends BlockMCFluxContainer {
	public static final PropertyDirection FACING = PropertyDirection.create("f");
	public static final PropertyInteger MODE = PropertyInteger.create("m", 0, 1);

	public BlockWET() {
		super();
		setHardness(1);
	}

	@Nullable @Override public TileEntity createNewTileEntity(World w, int meta) {
		return new TileEntityWET();
	}

	@Nonnull @Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return getDefaultState().withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer)).withProperty(MODE, 0);
	}

	@Override
	public void onBlockPlacedBy(World w, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		w.setBlockState(pos, state.withProperty(FACING, EnumFacing.getDirectionFromEntityLiving(pos, placer)), 2);
	}

	@Override
	public boolean onBlockActivated(World w, BlockPos bp, IBlockState ibs, EntityPlayer p, EnumHand h, EnumFacing f, float x, float y, float z) {
		boolean b = h == EnumHand.MAIN_HAND && U.isItemEmpty(p.getHeldItem(h));
		if (b && !w.isRemote)
			w.setBlockState(bp, ibs.cycleProperty(MODE), 3);
		return b;
	}

	@Override public IBlockState getStateFromMeta(int meta) {
		return getDefaultState().withProperty(MODE, meta % 2).withProperty(FACING, EnumFacing.VALUES[(meta / 2) % 6]);
	}

	@Override public int getMetaFromState(IBlockState state) {
		return state.getValue(MODE) + (2 * state.getValue(FACING).getIndex());
	}

	@Nonnull @Override public IBlockState withRotation(@Nonnull IBlockState state, Rotation rot) {
		return state.withProperty(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Nonnull @Override public IBlockState withMirror(@Nonnull IBlockState state, Mirror mir) {
		return state.withRotation(mir.toRotation(state.getValue(FACING)));
	}

	@Nonnull @Override protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, FACING, MODE);
	}
}
