package szewek.mcflux.blocks;

import java.util.List;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import szewek.mcflux.tileentities.TileEntityChunkCharger;
import szewek.mcflux.tileentities.TileEntityEnergyDistributor;
import szewek.mcflux.tileentities.TileEntityEnergyMachine;

public class BlockEnergyMachine extends BlockContainer {
	public static final AxisAlignedBB DEF_AABB = new AxisAlignedBB(0.25, 0.25, 0.25, 0.75, 0.75, 0.75);
	public static final PropertyEnum<Variant> VARIANT = PropertyEnum.create("variant", Variant.class);
	public static final PropertyInteger
			UP = PropertyInteger.create("up", 0, 2),
			DOWN = PropertyInteger.create("down", 0, 2),
			NORTH = PropertyInteger.create("north", 0, 2),
			SOUTH = PropertyInteger.create("south", 0, 2),
			EAST = PropertyInteger.create("east", 0, 2),
			WEST = PropertyInteger.create("west", 0, 2);
	
	public static PropertyInteger sideFromId(int id) {
		switch (id) {
		case 0: return DOWN;
		case 1: return UP;
		case 2: return NORTH;
		case 3: return SOUTH;
		case 4: return WEST;
		case 5: return EAST;
		default: return null;
		}
	}
	
	public BlockEnergyMachine() {
		super(Material.IRON);
	}

	@Override
	public TileEntity createNewTileEntity(World w, int m) {
		return m == 0 ? new TileEntityEnergyDistributor() : m == 1 ? new TileEntityChunkCharger() : null;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List<ItemStack> list) {
		for (int i = 0; i < 2; i++)
			list.add(new ItemStack(item, 1, i));
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(VARIANT, Variant.ALL_VARIANTS[meta % Variant.ALL_VARIANTS.length]);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return state.getValue(VARIANT).ordinal();
	}
	
	@Override
	protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, VARIANT, DOWN, UP, NORTH, SOUTH, WEST, EAST);
	}
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
		return DEF_AABB;
	}
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}
	
	@Override
	public boolean isFullCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}
	
	@Override
	public boolean onBlockActivated(World w, BlockPos bp, IBlockState ibs, EntityPlayer p, EnumHand h, ItemStack is, EnumFacing f, float x, float y, float z) {
		if (!w.isRemote && is == null) {
			TileEntity te = w.getTileEntity(bp);
			if (te != null && te instanceof TileEntityEnergyMachine)
				((TileEntityEnergyMachine) te).switchSideTransfer(f);
		}
		return is == null;
	}

	public static enum Variant implements IStringSerializable {
		ENERGY_DIST("energy_dist"), CHUNK_CHARGER("chunk_charger");

		public static final Variant[] ALL_VARIANTS;
		public final String name;

		Variant(String n) {
			name = n;
		}

		@Override
		public String getName() {
			return name;
		}
		
		public static String nameFromStack(ItemStack is) {
			return ALL_VARIANTS[is.getMetadata() % ALL_VARIANTS.length].name;
		}
		
		static {
			ALL_VARIANTS = Variant.values();
		}
	}
}
