package szewek.mcflux.util;

import java.util.List;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;

public class RecipeBuilder {
	private byte[][] shapeBytes;
	private int width, height, resultSize = 1;
	private ItemStack result;
	private ItemStack[] stacks = new ItemStack[9];
	private String[] oreDicts = new String[9];
	private EnumRecipeMirror mirror = EnumRecipeMirror.NO_MIRROR;

	public RecipeBuilder() {
	}

	public RecipeBuilder shapeCode(byte[][] bs, int w, int h) {
		if (bs.length != h)
			throw new RuntimeException("Height of shape bytes (" + bs.length + ") is not equal to actual height (" + h + ").");
		for (byte[] bw : bs) {
			if (bw.length != w)
				throw new RuntimeException("Width of shape bytes (" + bw.length + ") is not equal to actual width (" + w + ").");
			for (byte b : bw) {
				if (b < 0 || b > 9)
					throw new RuntimeException("Only items with numbers 1-9 and 0 (as empty space) are allowed.");
			}
		}
		shapeBytes = bs;
		width = w;
		height = h;
		return this;
	}

	public RecipeBuilder clearNumbers(int... bs) {
		for (int b : bs) {
			if (b < 1 || b > 9)
				continue;
			stacks[b - 1] = null;
			oreDicts[b - 1] = null;
		}
		return this;
	}

	public RecipeBuilder mirror(EnumRecipeMirror mirror) {
		this.mirror = mirror;
		return this;
	}

	public RecipeBuilder result(ItemStack is) {
		result = is;
		return this;
	}

	public RecipeBuilder resultSize(int size) {
		resultSize = size;
		return this;
	}

	public RecipeBuilder stackWithNumber(int i, ItemStack is) {
		if (i > 0 && i < 10) {
			stacks[i - 1] = is;
		}
		return this;
	}

	public RecipeBuilder oreDictWithNumber(int i, String s) {
		if (i > 0 && i < 10) {
			oreDicts[i - 1] = s;
		}
		return this;
	}

	public IRecipe build() {
		if (result != null)
			result.stackSize = resultSize;
		return new BuiltShapedRecipe(shapeBytes.clone(), width, height, result.copy(), stacks.clone(), oreDicts.clone(), mirror);
	}

	public void deploy() {
		net.minecraftforge.fml.common.registry.GameRegistry.addRecipe(build());
	}

	public static class BuiltShapedRecipe implements IRecipe {
		private final byte[][] shapeCode;
		private final int width, height;
		private final ItemStack result;
		private final ItemStack[] stacks;
		private final String[] oreDicts;
		private final EnumRecipeMirror mirror;
		private final Object[] cached;

		private BuiltShapedRecipe(byte[][] bytes, int w, int h, ItemStack result, ItemStack[] stacks, String[] oreDicts, EnumRecipeMirror m) {
			shapeCode = bytes;
			width = w;
			height = h;
			this.result = result;
			this.stacks = stacks;
			this.oreDicts = oreDicts;
			this.mirror = m;
			Object[] pc = new Object[width * height];
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					int code = shapeCode[y][x] - 1;
					if (code < 0)
						continue;
					List<ItemStack> lis = this.oreDicts[code] != null ? OreDictionary.getOres(this.oreDicts[code]) : null;
					if (lis != null) {
						pc[y * width + x] = lis;
						continue;
					}
					ItemStack is = this.stacks[code];
					pc[y * width + x] = is;
				}
			}
			cached = pc;
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		@Override
		public boolean matches(InventoryCrafting inv, World worldIn) {
			int full = width * height;
			for (int x = 0; x < 4 - width; x++)
				for (int y = 0; y < 4 - height; y++) {
					if (matchOffset(inv, x, y, false, false) == full) {
						return true;
					}
					if ((mirror == EnumRecipeMirror.MIRROR_X || mirror == EnumRecipeMirror.MIRROR_XY) && matchOffset(inv, x, y, true, false) == full) {
						return true;
					}
					if ((mirror == EnumRecipeMirror.MIRROR_Y || mirror == EnumRecipeMirror.MIRROR_XY) && matchOffset(inv, x, y, false, true) == full) {
						return true;
					}
					if (mirror == EnumRecipeMirror.MIRROR_XY && matchOffset(inv, x, y, true, true) == full) {
						return true;
					}
				}
			return false;
		}

		private int matchOffset(InventoryCrafting inv, int ox, int oy, boolean mirrorX, boolean mirrorY) {
			int m = 0;
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					int zx = x + ox;
					int zy = y + oy;
					ItemStack slot = inv.getStackInRowAndColumn(zx, zy);
					int code = shapeCode[mirrorY ? height - y - 1 : y][mirrorX ? width - x - 1 : x] - 1;
					if (code < 0) {
						if (slot != null)
							break;
						else {
							m++;
							continue;
						}
					}
					ItemStack recipeItem = stacks[code] != null ? stacks[code] : null;
					List<ItemStack> oreDictItems = oreDicts[code] != null ? OreDictionary.getOres(oreDicts[code]) : null;
					boolean emptyList = oreDictItems == null || oreDictItems.isEmpty();
					if (recipeItem == null && emptyList && slot != null)
						break;
					if (recipeItem != null) {
						if (slot == null)
							break;
						if (!OreDictionary.itemMatches(recipeItem, slot, false))
							break;
					}
					if (!emptyList) {
						if (slot == null)
							break;
						if (!OreDictionary.containsMatch(false, oreDictItems, slot))
							break;
					}
					m++;
				}
			}
			return m;

		}

		public Object[] getCached() {
			return cached;
		}

		@Override
		public ItemStack getCraftingResult(InventoryCrafting inv) {
			return result.copy();
		}

		@Override
		public int getRecipeSize() {
			return width * height;
		}

		@Override
		public ItemStack getRecipeOutput() {
			return result;
		}

		@Override
		public ItemStack[] getRemainingItems(InventoryCrafting inv) {
			return net.minecraftforge.common.ForgeHooks.defaultRecipeGetRemainingItems(inv);
		}
	}
}
