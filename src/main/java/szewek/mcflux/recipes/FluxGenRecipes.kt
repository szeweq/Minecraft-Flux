package szewek.mcflux.recipes

import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import net.minecraftforge.fluids.FluidRegistry
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.oredict.OreDictionary
import szewek.fl.util.RecipeItem
import java.util.*

object FluxGenRecipes {

	private val ctlsts = HashMap<RecipeItem, RecipeFluxGen>()
	private val hfluids = HashMap<FluidStack, RecipeFluxGen>()
	private val cfluids = HashMap<FluidStack, RecipeFluxGen>()
	val DEFAULT = RecipeFluxGen(1, 0)

	init {
		ctlsts[RecipeItem(Items.FLINT, 0, null)] = RecipeFluxGen(2, 2)
		ctlsts[RecipeItem(Items.REDSTONE, 0, null)] = RecipeFluxGen(2, 1)
		ctlsts[RecipeItem(Blocks.REDSTONE_BLOCK, 0, null)] = RecipeFluxGen(10, 1)
		ctlsts[RecipeItem(Items.BLAZE_POWDER, 0, null)] = RecipeFluxGen(4, 1)
		ctlsts[RecipeItem(Items.DRAGON_BREATH, 0, null)] = RecipeFluxGen(60, 1)
		ctlsts[RecipeItem(Items.NETHER_STAR, 0, null)] = RecipeFluxGen(100, 1)
		ctlsts[RecipeItem(Items.TOTEM_OF_UNDYING, 0, null)] = RecipeFluxGen(200, 1)
		hfluids[FluidStack(FluidRegistry.LAVA, 0)] = RecipeFluxGen(2, 200)
		cfluids[FluidStack(FluidRegistry.WATER, 0)] = RecipeFluxGen(50, 200)
	}

	fun addCatalyst(s: String, factor: Int, usage: Int) {
		val nis = OreDictionary.getOres(s, false)
		if (nis.isEmpty())
			return
		val rfg = RecipeFluxGen(factor, usage)
		for (stk in nis) {
			ctlsts[RecipeItem(stk.item, stk.itemDamage, null)] = rfg
		}
	}

	fun addHotFluid(s: String, factor: Int, usage: Int) {
		val fs = FluidRegistry.getFluidStack(s, 0) ?: return
		hfluids[fs] = RecipeFluxGen(factor, usage)
	}

	fun addCleanFluid(s: String, factor: Int, usage: Int) {
		val fs = FluidRegistry.getFluidStack(s, 0) ?: return
		cfluids[fs] = RecipeFluxGen(factor, usage)
	}

	fun addCatalyst(ri: RecipeItem, factor: Int, usage: Int) {
		ctlsts[ri] = RecipeFluxGen(factor, usage)
	}

	fun isCatalyst(stk: ItemStack): Boolean {
		if (stk.isEmpty)
			return false
		val ri = RecipeItem(stk.item, stk.itemDamage, null)
		if (ctlsts.containsKey(ri))
			return true
		for (mri in ctlsts.keys) {
			if (mri.matchesStack(stk))
				return true
		}
		return false
	}

	fun getCatalyst(stk: ItemStack): RecipeFluxGen {
		if (stk.isEmpty)
			return DEFAULT
		val ri = RecipeItem(stk.item, stk.itemDamage, null)
		if (ri in ctlsts)
			return ctlsts[ri]!!
		for (mri in ctlsts.keys) {
			if (mri.matchesStack(stk))
				return ctlsts[mri]!!
		}
		return DEFAULT
	}

	fun isHotFluid(fs: FluidStack) = isFluid(fs, hfluids)

	fun isCleanFluid(fs: FluidStack) = isFluid(fs, cfluids)

	fun getHotFluid(fs: FluidStack?) = getFluid(fs, hfluids)

	fun getCleanFluid(fs: FluidStack?) = getFluid(fs, cfluids)

	private fun isFluid(fs: FluidStack?, m: Map<FluidStack, RecipeFluxGen>): Boolean {
		if (fs == null)
			return false
		if (m.containsKey(fs))
			return true
		val fl = fs.fluid
		for (mfs in m.keys) {
			if (mfs.fluid === fl)
				return true
		}
		return false
	}

	private fun getFluid(fs: FluidStack?, m: Map<FluidStack, RecipeFluxGen>): RecipeFluxGen {
		if (fs == null)
			return DEFAULT
		if (m.containsKey(fs))
			return m[fs] ?: DEFAULT
		val fl = fs.fluid
		for (mfs in m.keys) {
			if (mfs.fluid === fl)
				return m[mfs] ?: DEFAULT
		}
		return DEFAULT
	}

	val catalysts: Map<RecipeItem, RecipeFluxGen>
		get() = Collections.unmodifiableMap(ctlsts)

	val hotFluids: Map<FluidStack, RecipeFluxGen>
		get() = Collections.unmodifiableMap(hfluids)

	val cleanFluids: Map<FluidStack, RecipeFluxGen>
		get() = Collections.unmodifiableMap(cfluids)
}
