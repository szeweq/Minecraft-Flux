package szewek.mcflux.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraftforge.fml.common.registry.GameRegistry;
import szewek.mcflux.util.MCFluxLocation;

import javax.annotation.Nonnull;

public class BlockSided extends Block {
	private static final PropertyInteger
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
			default: return DOWN;
		}
	}

	public BlockSided(String name) {
		super(Material.ROCK);
		MCFluxLocation rs = new MCFluxLocation(name);
		setUnlocalizedName(name);
		GameRegistry.register(this, rs);
		GameRegistry.register(new ItemBlock(this), rs);
	}

	@Override public int getMetaFromState(IBlockState state) {
		return 0;
	}

	@Nonnull @Override protected BlockStateContainer createBlockState() {
		return new BlockStateContainer(this, DOWN, UP, NORTH, SOUTH, WEST, EAST);
	}

	@Nonnull @Override public EnumBlockRenderType getRenderType(IBlockState state) {
		return EnumBlockRenderType.MODEL;
	}
}
