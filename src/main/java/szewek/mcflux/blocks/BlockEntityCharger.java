package szewek.mcflux.blocks;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import szewek.mcflux.api.MCFluxAPI;
import szewek.mcflux.api.ex.IEnergy;
import szewek.mcflux.tileentities.TileEntityECharger;

import javax.annotation.Nullable;

public final class BlockEntityCharger extends BlockMCFluxContainer {
	private static final AxisAlignedBB DEF_AABB = new AxisAlignedBB(0.125, 0, 0.125, 0.875, 0.1875, 0.875);

	public BlockEntityCharger() {
		super();
		setHardness(0.5F);
	}

	@Nullable @Override public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileEntityECharger();
	}

	@Override public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return DEF_AABB;
	}

	@Override public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
		return false;
	}

	@Override public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return false;
	}

	@Override
	public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
		return false;
	}

	@Override public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
		return 15;
	}

	@Override public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
		return 255;
	}

	@Override public void onEntityCollidedWithBlock(World w, BlockPos pos, IBlockState state, Entity e) {
		double edx = e.posX - pos.getX(), edy = e.posY - pos.getY(), edz = e.posZ - pos.getZ();
		double pdx = e.prevPosX - pos.getX(), pdy = e.prevPosY - pos.getY(), pdz = e.prevPosZ - pos.getZ();
		boolean inX = edx >= 0.125 && edx <= 0.875;
		boolean inZ = edz >= 0.125 && edz <= 0.875;
		boolean pinX = pdx >= 0.125 && pdx <= 0.875;
		boolean pinZ = pdz >= 0.125 && pdz <= 0.875;
		boolean crossX = inX != pinX;
		boolean crossZ = inZ != pinZ;
		TileEntityECharger teec = (TileEntityECharger) w.getTileEntity(pos);
		IEnergy ie = MCFluxAPI.getEnergySafely(e, null);
		if (teec == null || ie == null)
			return;
		if (inX && inZ) {
			if (crossX || crossZ) {
				// Entity is standing on a block
				teec.addEntityEnergy(ie);
			}
		}
		if (pinX && pinZ) {
			if (crossX || crossZ) {
				// Entity moved away from a block
				teec.removeEntityEnergy(ie);
			}
		}
	}

	@Override public void onFallenUpon(World w, BlockPos pos, Entity e, float fell) {
		e.fall(fell, 0.5f);
		if (fell > 3.5f)
			w.newExplosion(e, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1.5F * fell, false, false);
	}
}
