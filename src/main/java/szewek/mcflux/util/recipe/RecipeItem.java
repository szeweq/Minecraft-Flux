package szewek.mcflux.util.recipe;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public final class RecipeItem {
	private final Item item;
	private final int amount, meta;
	private final NBTTagCompound tags;

	public RecipeItem(Block b) {
		this(b, 1, 32767, null);
	}

	public RecipeItem(Item it) {
		this(it, 1, 32767, null);
	}

	public RecipeItem(Block b, int c, int m, NBTTagCompound nbt) {
		this(Item.getItemFromBlock(b), c, m, nbt);
	}

	public RecipeItem(Item it, int c, int m, NBTTagCompound nbt) {
		item = it;
		amount = c;
		meta = m;
		tags = nbt;
	}

	ItemStack makeItemStack() {
		ItemStack is = new ItemStack(item, amount, meta, null);
		if (tags != null)
			is.setTagCompound(tags.copy());
		return is;
	}

	boolean matchesStack(ItemStack is, boolean strict) {
		return is != null && is.getItem() == item && ((meta == 32767 && !strict) || is.getItemDamage() == meta);
	}
}
