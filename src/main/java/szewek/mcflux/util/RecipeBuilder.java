package szewek.mcflux.util;

import net.minecraft.block.Block;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import szewek.mcflux.U;

import java.util.Collections;
import java.util.List;

/**
 * Simple Recipe Builder class. Builds and automatically register recipes without wasting resources.
 */
public class RecipeBuilder {
	private IX[][] recipeShape;
	private int width, height, meta = 0, resultSize = 1;
	private Item result;
	private NBTTagCompound tags = null;
	private ItemStack[] stacks = new ItemStack[9];
	private String[] oreDicts = new String[9];
	private EnumRecipeMirror mirror = EnumRecipeMirror.NO_MIRROR;

	public static void deployAll(RecipeBuilder... rbs) {
		for (RecipeBuilder rb : rbs)
			net.minecraftforge.fml.common.registry.GameRegistry.addRecipe(rb.build());
	}

	public RecipeBuilder() {}

	public RecipeBuilder(Item it) {
		result = it;
	}

	public RecipeBuilder(Block b) {
		result = Item.getItemFromBlock(b);
	}

	public RecipeBuilder shape(IX[][] shape, int w, int h) {
		if (shape.length != h)
			throw new RuntimeException("Height of shape bytes (" + shape.length + ") is not equal to actual height (" + h + ").");
		for (IX[] sw : shape) {
			if (sw.length != w)
				throw new RuntimeException("Width of shape bytes (" + sw.length + ") is not equal to actual width (" + w + ").");
		}
		recipeShape = shape;
		width = w;
		height = h;
		return this;
	}

	public RecipeBuilder clear(IX... ix) {
		for (IX id : ix) {
			if (id == null)
				continue;
			stacks[id.ord] = null;
			oreDicts[id.ord] = null;
		}
		return this;
	}

	public RecipeBuilder mirror(EnumRecipeMirror mirror) {
		this.mirror = mirror;
		return this;
	}

	public RecipeBuilder result(Item it) {
		result = it;
		return this;
	}

	public RecipeBuilder result(Block b) {
		result = Item.getItemFromBlock(b);
		return this;
	}

	public RecipeBuilder resultSize(int size) {
		resultSize = size;
		return this;
	}

	public RecipeBuilder resultMeta(int m) {
		meta = m;
		return this;
	}

	public RecipeBuilder resultNBT(NBTTagCompound nbt) {
		tags = nbt;
		return this;
	}

	public RecipeBuilder with(IX id, ItemStack is) {
		if (id != null)
			stacks[id.ord] = is;
		return this;
	}

	public RecipeBuilder with(IX id, String s) {
		if (id != null)
			oreDicts[id.ord] = s;
		return this;
	}

	public IRecipe build() {
		return result == null? null : new BuiltShapedRecipe(recipeShape.clone(), width, height, new ItemStack(result, resultSize, meta, tags), stacks.clone(), oreDicts.clone(), mirror);
	}

	public RecipeBuilder deploy() {
		net.minecraftforge.fml.common.registry.GameRegistry.addRecipe(build());
		return this;
	}

	public static class BuiltShapedRecipe implements IRecipe {
		private final IX[][] shapeCode;
		private final int width, height;
		private final ItemStack result;
		private final ItemStack[] stacks;
		private final String[] oreDicts;
		private final EnumRecipeMirror mirror;
		private final List<List<ItemStack>> cached;

		private BuiltShapedRecipe(IX[][] shape, int w, int h, ItemStack result, ItemStack[] stacks, String[] oreDicts, EnumRecipeMirror m) {
			shapeCode = shape;
			width = w;
			height = h;
			this.result = result;
			this.stacks = stacks;
			this.oreDicts = oreDicts;
			this.mirror = m;
			NonNullList<List<ItemStack>> pc = NonNullList.withSize(width * height, Collections.emptyList());
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					IX id = shapeCode[y][x];
					if (id == null)
						continue;
					List<ItemStack> lis = this.oreDicts[id.ord] != null ? OreDictionary.getOres(this.oreDicts[id.ord]) : null;
					if (lis != null) {
						pc.set(y * width + x, lis);
						continue;
					}
					ItemStack is = this.stacks[id.ord];
					pc.set(y * width + x, Collections.singletonList(is));
				}
			}
			cached = Collections.unmodifiableList(pc);
		}

		public int getWidth() {
			return width;
		}

		public int getHeight() {
			return height;
		}

		@Override
		public boolean matches(InventoryCrafting inv, World w) {
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
					IX id = shapeCode[mirrorY ? height - y - 1 : y][mirrorX ? width - x - 1 : x];
					if (id == null) {
						if (!U.isItemEmpty(slot))
							break;
						else {
							m++;
							continue;
						}
					}
					NonNullList<ItemStack> oreDictItems = oreDicts[id.ord] != null ? OreDictionary.getOres(oreDicts[id.ord]) : null;
					boolean emptyList = oreDictItems == null || oreDictItems.isEmpty();
					if ((stacks[id.ord] == null && emptyList) != U.isItemEmpty(slot))
						break;
					if (!U.isItemEmpty(slot)) {
						if (stacks[id.ord] == null || !OreDictionary.itemMatches(stacks[id.ord], slot, false))
							break;
						if (emptyList || !OreDictionary.containsMatch(false, oreDictItems, slot))
							break;
					}
					m++;
				}
			}
			return m;

		}

		public List<List<ItemStack>> getCached() {
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
		public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
			return net.minecraftforge.common.ForgeHooks.defaultRecipeGetRemainingItems(inv);
		}
	}
}
