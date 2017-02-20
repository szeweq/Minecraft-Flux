package szewek.mcflux.util.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import szewek.mcflux.U;
import szewek.mcflux.util.IX;

import javax.annotation.Nonnull;
import java.util.Arrays;

import static net.minecraftforge.oredict.OreDictionary.WILDCARD_VALUE;

public final class BuiltShapedRecipe implements IRecipe {
	private final IX[][] shapeCode;
	private final int width, height, size;
	private final ItemStack result;
	private final RecipeItem[] items;
	private final String[] oreDicts;
	private final byte mirror;
	private final Object[] cached;

	BuiltShapedRecipe(IX[][] shape, int w, int h, ItemStack result, RecipeItem[] ris, String[] oreDicts, byte m) {
		shapeCode = shape;
		width = w;
		height = h;
		size = w * h;
		this.result = result;
		items = ris;
		this.oreDicts = oreDicts;
		this.mirror = m;
		ItemStack[] stacks = new ItemStack[ris.length];
		for (int i = 0; i < ris.length; i++) {
			stacks[i] = ris[i] == null ? null : ris[i].makeItemStack();
		}
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
				ItemStack is = stacks[id.ord];
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
	public boolean matches(InventoryCrafting inv, World w) {
		for (int x = 0; x < 4 - width; x++)
			for (int y = 0; y < 4 - height; y++) {
				if (matchOffset(inv, x, y, false, false)) {
					return true;
				}
				if (mirror == 0) continue;
				if ((mirror & 2) != 0 && matchOffset(inv, x, y, true, false))
					return true;
				if ((mirror & 1) != 0 && matchOffset(inv, x, y, false, true))
					return true;
				if ((mirror & 3) != 0 && matchOffset(inv, x, y, false, true))
					return true;
			}
		return false;
	}

	private boolean matchOffset(InventoryCrafting inv, int ox, int oy, boolean mirrorX, boolean mirrorY) {
		int x, y = 0;
		LOOP_XY:
		for (x = 0; x < width; x++) {
			int zx = x + ox;
			int mx = mirrorX ? width - x - 1 : x;
			for (y = 0; y < height; y++) {
				int zy = y + oy;
				int my = mirrorY ? height - y - 1 : y;
				ItemStack slot = inv.getStackInRowAndColumn(zx, zy);
				boolean slotEmpty = U.isItemEmpty(slot);
				IX id = shapeCode[my][mx];
				boolean matchEmpty = id == null;
				if (matchEmpty) {
					if (!slotEmpty)
						break LOOP_XY;
					else
						continue;
				}
				boolean stackEmpty = items[id.ord] == null;
				NonNullList<ItemStack> oreDictItems = oreDicts[id.ord] != null ? OreDictionary.getOres(oreDicts[id.ord]) : null;
				boolean oredictEmpty = oreDictItems == null || oreDictItems.isEmpty();
				if ((stackEmpty && oredictEmpty) == slotEmpty) {
					if (!slotEmpty) {
						boolean notStack = stackEmpty || !items[id.ord].matchesStack(slot, false);
						boolean notOredict = oredictEmpty || !allMatch(slot, oreDictItems, false);
						if (notStack && notOredict)
							break LOOP_XY;
					}
				} else
					break LOOP_XY;
			}
		}
		return x == width && y == height;
	}

	public Object[] getCached() {
		return cached;
	}

	@Nonnull @Override
	public ItemStack getCraftingResult(InventoryCrafting inv) {
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
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		return net.minecraftforge.common.ForgeHooks.defaultRecipeGetRemainingItems(inv);
	}

	private static boolean allMatch(ItemStack target, NonNullList<ItemStack> inputs, boolean strict) {
		boolean empty = target.isEmpty();
		Item it = target.getItem();
		int m = target.getItemDamage();
		for (ItemStack is : inputs)
			if (empty == is.isEmpty() && it == is.getItem() && ((is.getItemDamage() == WILDCARD_VALUE && !strict) || is.getItemDamage() == m))
				return true;
		return false;
	}
}
