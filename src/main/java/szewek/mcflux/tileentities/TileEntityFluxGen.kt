package szewek.mcflux.tileentities

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.inventory.IInventory
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagIntArray
import net.minecraft.nbt.NBTTagList
import net.minecraft.network.play.server.SPacketUpdateTileEntity
import net.minecraft.tileentity.TileEntity
import net.minecraft.tileentity.TileEntityFurnace
import net.minecraft.util.EnumFacing
import net.minecraft.util.ITickable
import net.minecraft.util.text.TextComponentTranslation
import net.minecraftforge.common.capabilities.Capability
import net.minecraftforge.common.util.Constants
import net.minecraftforge.energy.CapabilityEnergy
import net.minecraftforge.fluids.Fluid
import net.minecraftforge.fluids.FluidStack
import net.minecraftforge.fluids.capability.CapabilityFluidHandler
import net.minecraftforge.fluids.capability.IFluidHandler
import net.minecraftforge.fluids.capability.IFluidTankProperties
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.minecraftforge.items.CapabilityItemHandler
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.ItemHandlerHelper
import szewek.fl.FL
import szewek.fl.FLU
import szewek.fl.annotations.NamedResource
import szewek.fl.energy.ForgeEnergyCompat
import szewek.fl.energy.IEnergy
import szewek.fl.fluxwork.WorkState
import szewek.fl.kotlin.*
import szewek.mcflux.network.MCFluxNetwork
import szewek.mcflux.network.Msg
import szewek.mcflux.recipes.FluxGenRecipes

@NamedResource("mcflux:fluxgen")
class TileEntityFluxGen : TileEntity(), IEnergy, IInventory, IItemHandler, IFluidHandler, ITickable {
	private var workState = WorkState.LAZY
	private val vals = IntArray(5)
	private var nrg: Long = 0
	private var tickCount = 0
	private var isDirty = false
	private var isReady = false
	var receivedRedstone = false
	private val items = szewek.fl.util.JavaUtils.makeFilledArray(arrayOfNulls(2), ItemStack.EMPTY)
	private val tanks = arrayOf(FluidStorage(fluidCap, true, false), FluidStorage(fluidCap, true, false))
	private val fec = ForgeEnergyCompat(this)

	private val workTicks: Int
		get() {
			var f = TileEntityFurnace.getItemBurnTime(items[0])
			if (f == 0)
				return 0
			val rfgCat = FluxGenRecipes.getCatalyst(items[1])
			if (rfgCat.usage > items[1].count)
				return 0
			val rfgHot = FluxGenRecipes.getHotFluid(tanks[0].fluid)
			if (rfgHot.usage > tanks[0].amount)
				return 0
			val rfgClean = FluxGenRecipes.getCleanFluid(tanks[1].fluid)
			if (rfgClean.usage > tanks[1].amount)
				return 0
			items[0].grow(-1)
			if (rfgCat.usage > 0)
				items[1].grow(-rfgCat.usage)
			if (rfgHot.usage > 0) {
				tanks[0].substract(rfgHot.usage.toInt())
				MCFluxNetwork.toDimension(Msg.fluidAmount(pos, 0, tanks[0].fluid), world.provider.dimension)
			}
			if (rfgClean.usage > 0) {
				tanks[1].substract(rfgClean.usage.toInt())
				MCFluxNetwork.toDimension(Msg.fluidAmount(pos, 1, tanks[1].fluid), world.provider.dimension)
			}
			isDirty = true
			vals[3] = 40 * rfgCat.factor
			vals[4] = if (rfgClean.factor < rfgCat.factor) rfgCat.factor - rfgClean.factor else 1
			f *= rfgHot.factor.toInt()
			return f
		}

	val workFill: Float
		get() = if (vals[2] == 0) 0F else vals[1].toFloat() / vals[2].toFloat()

	val energyFill: Float
		get() = vals[0].toFloat() / maxEnergy.toFloat()

	override fun update() {
		if (world.isRemote)
			return
		if (!isReady) {
			for (f in EnumFacing.VALUES) {
				if (world.getRedstonePower(pos.offset(f, 1), f) > 0) {
					receivedRedstone = true
					break
				}
			}
			isReady = true
		}
		if (!receivedRedstone) {
			if (workState != WorkState.WORKING && TileEntityFurnace.getItemBurnTime(items[0]) <= 0)
				workState = WorkState.LAZY
			else if (workState != WorkState.WORKING || vals[1] >= vals[2]) {
				vals[1] = 0
				vals[2] = workTicks
				workState = WorkState.WORKING
			} else if (nrg + vals[3] <= maxEnergy) {
				nrg += vals[3].toLong()
				vals[0] = nrg.toInt()
				vals[1] += vals[4]
				if (vals[2] <= vals[1]) {
					vals[2] = 0
					vals[3] = 0
					workState = WorkState.FINISHED
				}
			}
		}
		tickCount++
		if (tickCount > 3 && energy > 0) {
			tickCount = 0
			for (f in EnumFacing.VALUES) {
				val bp = pos.offset(f, 1)
				val te = world.getTileEntity(bp) ?: continue
				val ie = FLU.getEnergySafely(te, f.opposite) ?: continue
				to(ie, 40000)
			}
		}
		if (isDirty)
			markDirty()
	}

	override fun hasCapability(cap: Capability<*>, f: EnumFacing?): Boolean {
		return cap === FL.ENERGY_CAP || cap === CapabilityEnergy.ENERGY || cap === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || cap === CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(cap, f)
	}

	@Suppress("UNCHECKED_CAST")
	override fun <T> getCapability(cap: Capability<T>, f: EnumFacing?): T? {
		if (cap === FL.ENERGY_CAP || cap === CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || cap === CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return this as T
		return if (cap === CapabilityEnergy.ENERGY) fec as T else super.getCapability(cap, f)
	}

	override fun getEnergy() = nrg
	override fun getEnergyCapacity() = maxEnergy
	override fun canInputEnergy() = false
	override fun canOutputEnergy() = true
	override fun inputEnergy(amount: Long, sim: Boolean) = 0L

	override fun outputEnergy(amount: Long, sim: Boolean): Long {
		var r = amount
		if (r > energy)
			r = energy
		if (!sim) {
			nrg -= r
			vals[0] = nrg.toInt()
			isDirty = true
		}
		return r
	}

	override fun hasNoEnergy(): Boolean = nrg == 0L
	override fun hasFullEnergy(): Boolean = nrg == maxEnergy

	override fun to(ie: IEnergy, amount: Long): Long {
		if (amount > 0 && ie.canInputEnergy()) {
			var r = amount
			if (r > energy)
				r = energy
			r = ie.inputEnergy(r, true)
			if (r > 0) {
				nrg -= r
				vals[0] = nrg.toInt()
				isDirty = true
				return ie.inputEnergy(r, false)
			}
		}
		return 0
	}

	override fun getSlots() = 2
	override fun getSizeInventory() = 2
	override fun isEmpty() = items[0].isEmpty && items[1].isEmpty

	override fun getStackInSlot(slot: Int): ItemStack {
		checkSlot(slot)
		return items[slot]
	}

	override fun decrStackSize(index: Int, count: Int): ItemStack {
		return if (index >= 0 && index < items.size && count > 0 && !items[index].isEmpty) items[index].splitStack(count) else ItemStack.EMPTY
	}

	override fun removeStackFromSlot(index: Int): ItemStack {
		if (index >= 0 && index < items.size) {
			val stk = items[index]
			items[index] = ItemStack.EMPTY
			isDirty = true
			return stk
		}
		return ItemStack.EMPTY
	}

	override fun setInventorySlotContents(index: Int, stk: ItemStack) {
		if (index >= 0 && index < items.size) {
			//boolean f = !is.isEmpty() && is.isItemEqual(items[index]) && ItemStack.areItemStackTagsEqual(is, items[index]);
			items[index] = stk
			if (stk.count > 64)
				stk.count = 64
		}
	}

	override fun getInventoryStackLimit() = 64

	override fun isUsableByPlayer(p: EntityPlayer): Boolean {
		return world.getTileEntity(pos) === this && p.getDistanceSq(pos.x.toDouble() + 0.5, pos.y.toDouble() + 0.5, pos.z.toDouble() + 0.5) <= 64.0
	}

	override fun openInventory(p: EntityPlayer) {
		if (!world.isRemote && p is EntityPlayerMP) {
			for (i in tanks.indices)
				MCFluxNetwork.to(Msg.fluidAmount(pos, i, tanks[i].fluid), p)
		}
	}

	override fun closeInventory(p: EntityPlayer) = Unit

	override fun isItemValidForSlot(slot: Int, stk: ItemStack): Boolean {
		return slot == 0 && TileEntityFurnace.getItemBurnTime(stk) > 0 || slot == 1 && FluxGenRecipes.isCatalyst(stk)
	}

	override fun getField(id: Int): Int = vals[id]

	override fun setField(id: Int, value: Int) {
		vals[id] = value
		if (id == 0)
			nrg = value.toLong()
	}

	override fun getFieldCount() = vals.size

	override fun clear() {
		items[0] = ItemStack.EMPTY
		items[1] = ItemStack.EMPTY
	}

	override fun insertItem(slot: Int, stk: ItemStack, sim: Boolean): ItemStack {
		if (stk.isEmpty)
			return ItemStack.EMPTY
		checkSlot(slot)
		if (slot == 0 && TileEntityFurnace.getItemBurnTime(stk) == 0 || slot == 1 && !FluxGenRecipes.isCatalyst(stk))
			return stk
		var l = stk.maxStackSize
		if (l > 64) l = 64
		val sc = stk.count
		val xis = items[slot]
		if (!xis.isEmpty) {
			if (!ItemHandlerHelper.canItemStacksStack(stk, xis))
				return stk
			l -= xis.count
		}
		if (0 >= l)
			return stk
		val rl = sc > l
		if (!sim) {
			if (xis.isEmpty)
				items[slot] = if (rl) ItemHandlerHelper.copyStackWithSize(stk, l) else stk
			else
				xis.grow(if (rl) l else sc)
			isDirty = true
		}
		return if (rl) ItemHandlerHelper.copyStackWithSize(stk, sc - l) else ItemStack.EMPTY
	}

	override fun extractItem(slot: Int, amount: Int, sim: Boolean) = ItemStack.EMPTY

	override fun getSlotLimit(slot: Int) = 64

	private fun checkSlot(s: Int) {
		if (s < 0 || s >= items.size)
			throw RuntimeException("Getting slot " + s + " outside range [0," + items.size + ")")
	}

	override fun readFromNBT(nbt: NBTTagCompound) {
		super.readFromNBT(nbt)
		nbt.getTag("E")
		energy = nbt.getInteger("E").toLong()
		if (energy > maxEnergy)
			energy = maxEnergy
		var nbtl = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND)
		for (i in 0 until nbtl.tagCount()) {
			val inbt = nbtl.getCompoundTagAt(i)
			val s = inbt.getByte("Slot").toInt()
			if (0 <= s && s < items.size)
				items[s] = ItemStack(inbt)
		}
		nbtl = nbt.getTagList("Fluids", Constants.NBT.TAG_COMPOUND)
		for (i in 0 until nbtl.tagCount()) {
			val fnbt = nbtl.getCompoundTagAt(i)
			val s = fnbt.getByte("Slot").toInt()
			if (0 <= s && s < tanks.size)
				tanks[s].fluid = FluidStack.loadFluidStackFromNBT(fnbt)
		}
		val v = nbt.getIntArray("Vals")
		System.arraycopy(v, 0, vals, 0, vals.size)
	}

	override fun writeToNBT(nbt: NBTTagCompound): NBTTagCompound {
		super.writeToNBT(nbt)
		nbt.setInteger("E", energy.toInt())
		var nbtl = NBTTagList()
		for (i in items.indices) {
			if (items[i].isEmpty)
				continue
			val inbt = NBTTagCompound()
			inbt.setByte("Slot", i.toByte())
			nbtl.appendTag(items[i].writeToNBT(inbt))
		}
		nbt["Items"] = nbtl
		nbtl = NBTTagList()
		for (i in tanks.indices) {
			if (tanks[i].fluid == null || tanks[i].fluid!!.amount == 0)
				continue
			val fnbt = NBTTagCompound()
			fnbt.setByte("Slot", i.toByte())
			nbtl.appendTag(tanks[i].fluid!!.writeToNBT(fnbt))
		}
		nbt["Fluids"] = nbtl
		val v = IntArray(vals.size)
		System.arraycopy(vals, 0, v, 0, vals.size)
		nbt["Vals"] = NBTTagIntArray(v)
		return nbt
	}

	override fun getUpdateTag() = writeToNBT(NBTTagCompound())
	override fun getUpdatePacket(): SPacketUpdateTileEntity {
		return SPacketUpdateTileEntity(pos, blockMetadata, writeToNBT(NBTTagCompound()))
	}

	@Suppress("UNCHECKED_CAST")
	override fun getTankProperties(): Array<IFluidTankProperties> {
		return tanks as Array<IFluidTankProperties>
	}

	override fun fill(fs: FluidStack, doFill: Boolean): Int {
		if (fs.amount <= 0)
			return 0
		val s: Int = when {
			FluxGenRecipes.isHotFluid(fs) -> 0
			FluxGenRecipes.isCleanFluid(fs) -> 1
			else -> return 0
		}
		if (tanks[s].fluid != null && !fs.isFluidEqual(tanks[s].fluid))
			return 0
		var l = fluidCap
		if (tanks[s].fluid != null) {
			l -= tanks[s].fluid!!.amount
		}
		if (l > fs.amount)
			l = fs.amount
		if (doFill) {
			if (tanks[s].fluid == null)
				tanks[s].fluid = FluidStack(fs, l)
			else
				tanks[s].fluid!!.amount += l
			isDirty = true
			MCFluxNetwork.toDimension(Msg.fluidAmount(pos, s, tanks[s].fluid), world.provider.dimension)
		}
		return l
	}

	override fun drain(resource: FluidStack, doDrain: Boolean):FluidStack? = null
	override fun drain(maxDrain: Int, doDrain: Boolean):FluidStack?  = null

	override fun getName() = "mcflux.container.fluxgen"
	override fun hasCustomName() = false
	override fun getDisplayName() = TextComponentTranslation(name)

	@SideOnly(Side.CLIENT)
	fun updateFluid(id: Int, fl: Fluid?, amount: Int) {
		if (id < 0 || id >= tanks.size)
			return
		val tank = tanks[id]
		if (fl == null) {
			tank.fluid = null
			return
		}
		if (tank.fluid != null && tank.fluid!!.fluid === fl) {
			tank.fluid!!.amount = amount
		} else {
			tank.fluid = FluidStack(fl, amount)
		}
	}

	private class FluidStorage internal constructor(private val cap: Int, private val filling: Boolean, private val draining: Boolean) : IFluidTankProperties {
		internal var fluid: FluidStack? = null

		internal val amount: Int
			get() = if (fluid == null) 0 else fluid!!.amount

		internal fun substract(l: Int) {
			if (fluid != null) {
				fluid!!.amount -= l
				if (fluid!!.amount <= 0)
					fluid = null
			}
		}

		override fun getContents() = if (fluid == null) null else fluid!!.copy()
		override fun getCapacity() = cap
		override fun canFill() = filling
		override fun canDrain() = draining

		override fun canFillFluidType(fluidStack: FluidStack): Boolean {
			return filling && (fluid == null || fluid!!.isFluidEqual(fluidStack))
		}

		override fun canDrainFluidType(fluidStack: FluidStack): Boolean {
			return draining && (fluid == null || fluid!!.isFluidEqual(fluidStack))
		}
	}

	companion object {
		const val fluidCap = 4000
		private const val maxEnergy: Long = 500000
	}
}
