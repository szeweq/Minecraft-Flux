package szewek.mcflux.recipes;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import szewek.fl.recipes.RecipeItem;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum FluxGenRecipes {
	INSTANCE;
	public static final RecipeFluxGen DEFAULT = new RecipeFluxGen(1, 0);
	private final Map<RecipeItem, RecipeFluxGen> catalysts = new HashMap<>();
	private final Map<FluidStack, RecipeFluxGen> hotFluids = new HashMap<>(), cleanFluids = new HashMap<>();

	FluxGenRecipes() {
		catalysts.put(new RecipeItem(Items.FLINT, 0, null), new RecipeFluxGen(2, 2));
		catalysts.put(new RecipeItem(Items.REDSTONE, 0, null), new RecipeFluxGen(2, 1));
		catalysts.put(new RecipeItem(Blocks.REDSTONE_BLOCK, 0, null), new RecipeFluxGen(10, 1));
		catalysts.put(new RecipeItem(Items.BLAZE_POWDER, 0, null), new RecipeFluxGen(4, 1));
		catalysts.put(new RecipeItem(Items.DRAGON_BREATH, 0, null), new RecipeFluxGen(60, 1));
		catalysts.put(new RecipeItem(Items.NETHER_STAR, 0, null), new RecipeFluxGen(100, 1));
		catalysts.put(new RecipeItem(Items.TOTEM, 0, null), new RecipeFluxGen(200, 1));
		hotFluids.put(new FluidStack(FluidRegistry.LAVA, 0), new RecipeFluxGen(2, 200));
		cleanFluids.put(new FluidStack(FluidRegistry.WATER, 0), new RecipeFluxGen(50, 200));
	}

	public static void addCatalyst(RecipeItem ri, int factor, int usage) {
		INSTANCE.catalysts.put(ri, new RecipeFluxGen(factor, usage));
	}

	public void addCatalyst(String s, int factor, int usage) {
		NonNullList<ItemStack> nis = OreDictionary.getOres(s, false);
		if (nis.isEmpty())
			return;
		RecipeFluxGen rfg = new RecipeFluxGen(factor, usage);
		for (ItemStack is : nis) {
			catalysts.put(new RecipeItem(is.getItem(), is.getItemDamage(), null), rfg);
		}
	}

	public void addHotFluid(String s, int factor, int usage) {
		FluidStack fs = FluidRegistry.getFluidStack(s, 0);
		if (fs == null)
			return;
		hotFluids.put(fs, new RecipeFluxGen(factor, usage));
	}

	public void addCleanFluid(String s, int factor, int usage) {
		FluidStack fs = FluidRegistry.getFluidStack(s, 0);
		if (fs == null)
			return;
		cleanFluids.put(fs, new RecipeFluxGen(factor, usage));
	}

	public static boolean isCatalyst(ItemStack is) {
		if (is.isEmpty())
			return false;
		RecipeItem ri = new RecipeItem(is.getItem(), is.getItemDamage(), null);
		if (INSTANCE.catalysts.containsKey(ri))
			return true;
		for (RecipeItem mri : INSTANCE.catalysts.keySet()) {
			if (mri.matchesStack(is, false))
				return true;
		}
		return false;
	}

	public static RecipeFluxGen getCatalyst(ItemStack is) {
		if (is.isEmpty())
			return DEFAULT;
		RecipeItem ri = new RecipeItem(is.getItem(), is.getItemDamage(), null);
		if (INSTANCE.catalysts.containsKey(ri))
			return INSTANCE.catalysts.get(ri);
		for (RecipeItem mri : INSTANCE.catalysts.keySet()) {
			if (mri.matchesStack(is, false))
				return INSTANCE.catalysts.get(mri);
		}
		return DEFAULT;
	}

	public static boolean isHotFluid(FluidStack fs) {
		return isFluid(fs, INSTANCE.hotFluids);
	}

	public static boolean isCleanFluid(FluidStack fs) {
		return isFluid(fs, INSTANCE.cleanFluids);
	}

	public static RecipeFluxGen getHotFluid(FluidStack fs) {
		return getFluid(fs, INSTANCE.hotFluids);
	}

	public static RecipeFluxGen getCleanFluid(FluidStack fs) {
		return getFluid(fs, INSTANCE.cleanFluids);
	}

	private static boolean isFluid(@Nullable FluidStack fs, final Map<FluidStack, RecipeFluxGen> m) {
		if (fs == null)
			return false;
		if (m.containsKey(fs))
			return true;
		Fluid fl = fs.getFluid();
		for (FluidStack mfs : m.keySet()) {
			if (mfs.getFluid() == fl)
				return true;
		}
		return false;
	}

	@Nonnull
	private static RecipeFluxGen getFluid(@Nullable FluidStack fs, final Map<FluidStack, RecipeFluxGen> m) {
		if (fs == null)
			return DEFAULT;
		if (m.containsKey(fs))
			return m.get(fs);
		Fluid fl = fs.getFluid();
		for (FluidStack mfs : m.keySet()) {
			if (mfs.getFluid() == fl)
				return m.get(mfs);
		}
		return DEFAULT;
	}

	public static Map<RecipeItem, RecipeFluxGen> getCatalysts() {
		return Collections.unmodifiableMap(INSTANCE.catalysts);
	}

	public static Map<FluidStack, RecipeFluxGen> getHotFluids() {
		return Collections.unmodifiableMap(INSTANCE.hotFluids);
	}

	public static Map<FluidStack, RecipeFluxGen> getCleanFluids() {
		return Collections.unmodifiableMap(INSTANCE.cleanFluids);
	}
}
