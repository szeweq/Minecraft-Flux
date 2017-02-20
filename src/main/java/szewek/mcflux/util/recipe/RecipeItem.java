package szewek.mcflux.util.recipe;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.Nullable;

public final class RecipeItem {
	private final Item item;
	private final int meta, cachedHash;
	private final NBTTagCompound tags;

	public RecipeItem(Block b) {
		this(b, 32767, null);
	}

	public RecipeItem(Item it) {
		this(it, 32767, null);
	}

	public RecipeItem(Block b, int m, @Nullable NBTTagCompound nbt) {
		this(Item.getItemFromBlock(b), m, nbt);
	}

	public RecipeItem(Item it, int m, @Nullable NBTTagCompound nbt) {
		item = it;
		meta = m;
		tags = nbt;
		int h = (31 + item.hashCode()) * 31 + meta;
		if (tags != null)
			h = 31 * h + tags.hashCode();
		cachedHash = h;
	}

	public ItemStack makeItemStack() {
		ItemStack is = new ItemStack(item, 1, meta, null);
		if (tags != null)
			is.setTagCompound(tags.copy());
		return is;
	}

	public boolean matchesStack(ItemStack is, boolean strict) {
		return !is.isEmpty() && is.getItem() == item && ((meta == 32767 && !strict) || is.getItemDamage() == meta);
	}

	@Override public int hashCode() {
		return cachedHash;
	}

	@Override public boolean equals(Object obj) {
		return obj != null && obj instanceof RecipeItem && obj.hashCode() == cachedHash;

	}
}
