package szewek.mcflux.util.recipe;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.nbt.NBTTagCompound;
import szewek.mcflux.util.IX;

/**
 * Simple Recipe Builder class. Builds and automatically registers recipes without wasting resources.
 */
public class RecipeBuilder {
	private static RecipeBuilder builder = new RecipeBuilder();
	private IX[][] recipeShape;
	private int width, height, meta = 0, resultSize = 1;
	private byte mirror = 0;
	private Item result;
	private NBTTagCompound tags = null;
	private ItemStack[] stacks = new ItemStack[9];
	private String[] oreDicts = new String[9];

	public static RecipeBuilder buildRecipeFor(Item it, int size) {
		builder.clean();
		builder.result = it;
		builder.resultSize = size;
		return builder;
	}
	public static RecipeBuilder buildRecipeFor(Block b, int size) {
		builder.clean();
		builder.result = Item.getItemFromBlock(b);
		builder.resultSize = size;
		return builder;
	}

	private RecipeBuilder() {}

	private void clean() {
		recipeShape = null;
		width = 0;
		height = 0;
		tags = null;
		for (int i = 0; i < 9; i++) {
			stacks[i] = null;
			oreDicts[i] = null;
		}
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

	public RecipeBuilder mirror(boolean x, boolean y) {
		mirror = 0;
		if (x) mirror |= 2;
		if (y) mirror |= 1;
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

}
