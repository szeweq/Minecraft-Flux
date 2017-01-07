package szewek.mcflux.util.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import szewek.mcflux.U;
import szewek.mcflux.util.IX;

import javax.annotation.Nonnull;
import java.util.Arrays;

public class BuiltShapedRecipe implements IRecipe {
	private final IX[][] shapeCode;
	private final int width, height, size;
	private final ItemStack result;
	private final ItemStack[] stacks;
	private final String[] oreDicts;
	private final byte mirror;
	private final Object[] cached;

	BuiltShapedRecipe(IX[][] shape, int w, int h, ItemStack result, ItemStack[] stacks, String[] oreDicts, byte m) {
		shapeCode = shape;
		width = w;
		height = h;
		size = w * h;
		this.result = result;
		this.stacks = stacks;
		this.oreDicts = oreDicts;
		this.mirror = m;
		cached = new Object[size];
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				IX id = shapeCode[y][x];
				if (id == null) {
					cached[y * height + x] = null;
					continue;
				}
				Object r = null;
				String odict = this.oreDicts[id.ord];
				ItemStack is = this.stacks[id.ord];
				if (is != null && odict != null)
					r = Arrays.asList(is, odict);
				else if (is != null)
					r = is;
				else if (odict != null)
					r = odict;
				cached[y * height + x] = r;
			}
		}
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	@Override
	public boolean matches(@Nonnull InventoryCrafting inv, @Nonnull World w) {
		for (int x = 0; x < 4 - width; x++)
			for (int y = 0; y < 4 - height; y++) {
				if (matchOffset(inv, x, y, false, false) == size) {
					return true;
				}
				if (mirror == 0) continue;
				if ((mirror & 2) != 0 && matchOffset(inv, x, y, true, false) == size)
					return true;
				if ((mirror & 1) != 0 && matchOffset(inv, x, y, false, true) == size)
					return true;
				if ((mirror & 3) != 0 && matchOffset(inv, x, y, false, true) == size)
					return true;
			}
		return false;
	}

	private int matchOffset(InventoryCrafting inv, int ox, int oy, boolean mirrorX, boolean mirrorY) {
		int m = 0;
		LOOP_XY:
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				int zx = x + ox;
				int zy = y + oy;
				ItemStack slot = inv.getStackInRowAndColumn(zx, zy);
				IX id = shapeCode[mirrorY ? height - y - 1 : y][mirrorX ? width - x - 1 : x];
				if (id == null) {
					if (!U.isItemEmpty(slot))
						break LOOP_XY;
					else {
						m++;
						continue;
					}
				}
				NonNullList<ItemStack> oreDictItems = oreDicts[id.ord] != null ? OreDictionary.getOres(oreDicts[id.ord]) : null;
				boolean emptyList = oreDictItems == null || oreDictItems.isEmpty();
				if ((stacks[id.ord] == null && emptyList) != U.isItemEmpty(slot))
					break LOOP_XY;
				if (!U.isItemEmpty(slot)) {
					if (stacks[id.ord] == null || !OreDictionary.itemMatches(stacks[id.ord], slot, false))
						break LOOP_XY;
					if (emptyList || !OreDictionary.containsMatch(false, oreDictItems, slot))
						break LOOP_XY;
				}
				m++;
			}
		}
		return m;

	}

	public Object[] getCached() {
		return cached;
	}

	@Nonnull @Override
	public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
		return result.copy();
	}

	@Override
	public int getRecipeSize() {
		return size;
	}

	@Nonnull @Override
	public ItemStack getRecipeOutput() {
		return result;
	}

	@Nonnull @Override
	public NonNullList<ItemStack> getRemainingItems(@Nonnull InventoryCrafting inv) {
		return net.minecraftforge.common.ForgeHooks.defaultRecipeGetRemainingItems(inv);
	}
}
