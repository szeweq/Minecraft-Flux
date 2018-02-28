package szewek.mcflux.tileentities;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagIntArray;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import szewek.fl.FL;
import szewek.fl.FLU;
import szewek.fl.annotations.NamedResource;
import szewek.fl.energy.ForgeEnergyCompat;
import szewek.fl.energy.IEnergy;
import szewek.fl.fluxwork.WorkState;
import szewek.mcflux.network.MCFluxNetwork;
import szewek.mcflux.network.Msg;
import szewek.mcflux.recipes.FluxGenRecipes;
import szewek.mcflux.recipes.RecipeFluxGen;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@NamedResource("mcflux:fluxgen")
public final class TileEntityFluxGen extends TileEntity implements IEnergy, IInventory, IItemHandler, IFluidHandler, ITickable {
	public static final int fluidCap = 4000;
	private static final long maxEnergy = 500000;
	private WorkState workState = WorkState.LAZY;
	private final int[] vals = new int[5];
	protected long energy;
	private int tickCount = 0;
	private boolean isDirty = false, isReady = false, receivedRedstone = false;
	private final ItemStack[] items = szewek.fl.util.JavaUtils.makeFilledArray(new ItemStack[2], ItemStack.EMPTY);
	private final FluidStorage[] tanks = new FluidStorage[]{new FluidStorage(fluidCap, true, false), new FluidStorage(fluidCap, true, false)};
	private final ForgeEnergyCompat fec = new ForgeEnergyCompat(this);

	private int getWorkTicks() {
		RecipeFluxGen rfgCat, rfgHot, rfgClean;
		int f = TileEntityFurnace.getItemBurnTime(items[0]);
		if (f == 0)
			return 0;
		rfgCat = FluxGenRecipes.getCatalyst(items[1]);
		if (rfgCat.usage > items[1].getCount())
			return 0;
		rfgHot = FluxGenRecipes.getHotFluid(tanks[0].fluid);
		if (rfgHot.usage > tanks[0].getAmount())
			return 0;
		rfgClean = FluxGenRecipes.getCleanFluid(tanks[1].fluid);
		if (rfgClean.usage > tanks[1].getAmount())
			return 0;
		items[0].grow(-1);
		if (rfgCat.usage > 0)
			items[1].grow(-rfgCat.usage);
		if (rfgHot.usage > 0) {
			tanks[0].substract(rfgHot.usage);
			MCFluxNetwork.toDimension(Msg.fluidAmount(pos, 0, tanks[0].fluid), world.provider.getDimension());
		}
		if (rfgClean.usage > 0) {
			tanks[1].substract(rfgClean.usage);
			MCFluxNetwork.toDimension(Msg.fluidAmount(pos, 1, tanks[1].fluid), world.provider.getDimension());
		}
		isDirty = true;
		vals[3] = 40 * rfgCat.factor;
		vals[4] = rfgClean.factor < rfgCat.factor ? rfgCat.factor - rfgClean.factor : 1;
		f *= rfgHot.factor;
		return f;
	}

	@Override public void update() {
		if (world.isRemote)
			return;
		if (!isReady) {
			for (EnumFacing f : EnumFacing.VALUES) {
				if (world.getRedstonePower(pos.offset(f, 1), f) > 0) {
					receivedRedstone = true;
					break;
				}
			}
			isReady = true;
		}
		if (!receivedRedstone) {
			if (workState != WorkState.WORKING && TileEntityFurnace.getItemBurnTime(items[0]) <= 0)
				workState = WorkState.LAZY;
			else if (workState != WorkState.WORKING || vals[1] >= vals[2]) {
				vals[1] = 0;
				vals[2] = getWorkTicks();
				workState = WorkState.WORKING;
			} else if (energy + vals[3] <= maxEnergy) {
				vals[0] = (int) (energy += vals[3]);
				vals[1] += vals[4];
				if (vals[2] <= vals[1]) {
					vals[2] = 0;
					vals[3] = 0;
					workState = WorkState.FINISHED;
				}
			}
		}
		tickCount++;
		if (tickCount > 3 && energy > 0) {
			tickCount = 0;
			for (EnumFacing f : EnumFacing.VALUES) {
				BlockPos bp = pos.offset(f, 1);
				TileEntity te = world.getTileEntity(bp);
				if (te == null)
					continue;
				IEnergy ie = FLU.getEnergySafely(te, f.getOpposite());
				if (ie == null)
					continue;
				to(ie, 40000);
			}
		}
		if (isDirty)
			markDirty();
	}

	public boolean getReceivedRedstone() {
		return receivedRedstone;
	}

	public void setReceivedRedstone(boolean b) {
		receivedRedstone = b;
	}

	public float getWorkFill() {
		return vals[2] == 0 ? 0 : (float) vals[1] / (float) vals[2];
	}

	public float getEnergyFill() {
		return (float) vals[0] / (float) maxEnergy;
	}

	@Override public boolean hasCapability(Capability<?> cap, @Nullable EnumFacing f) {
		return cap == FL.ENERGY_CAP || cap == CapabilityEnergy.ENERGY || cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(cap, f);
	}

	@SuppressWarnings("unchecked")
	@Nullable @Override public <T> T getCapability(Capability<T> cap, @Nullable EnumFacing f) {
		if (cap == FL.ENERGY_CAP || cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
			return (T) this;
		if (cap == CapabilityEnergy.ENERGY)
			return (T) fec;
		return super.getCapability(cap, f);
	}

	@Override public long getEnergy() {
		return energy;
	}

	@Override public long getEnergyCapacity() {
		return maxEnergy;
	}

	@Override public boolean canInputEnergy() {
		return false;
	}

	@Override public boolean canOutputEnergy() {
		return true;
	}

	@Override public long inputEnergy(long amount, boolean sim) {
		return 0;
	}

	@Override public long outputEnergy(long amount, boolean sim) {
		if (amount > energy)
			amount = energy;
		if (!sim) {
			vals[0] = (int) (energy -= amount);
			isDirty = true;
		}
		return amount;
	}

	@Override public boolean hasNoEnergy() {
		return energy == 0;
	}

	@Override public boolean hasFullEnergy() {
		return energy == maxEnergy;
	}

	@Override
	public long to(IEnergy ie, long amount) {
		if (amount > 0 && ie != null && ie.canInputEnergy()) {
			if (amount > energy)
				amount = energy;
			final long r = ie.inputEnergy(amount, true);
			if (r > 0) {
				vals[0] = (int) (energy -= r);
				isDirty = true;
				return ie.inputEnergy(r, false);
			}
		}
		return 0;
	}

	@Override public int getSlots() {
		return 2;
	}

	@Override public int getSizeInventory() {
		return 2;
	}

	@Override public boolean isEmpty() {
		return items[0].isEmpty() && items[1].isEmpty();
	}

	@Nonnull @Override public ItemStack getStackInSlot(int slot) {
		checkSlot(slot);
		return items[slot];
	}

	@Override public ItemStack decrStackSize(int index, int count) {
		return index >= 0 && index < items.length && count > 0 && !items[index].isEmpty() ? items[index].splitStack(count) : ItemStack.EMPTY;
	}

	@Override public ItemStack removeStackFromSlot(int index) {
		if (index >= 0 && index < items.length) {
			ItemStack is = items[index];
			items[index] = ItemStack.EMPTY;
			isDirty = true;
			return is;
		}
		return ItemStack.EMPTY;
	}

	@Override public void setInventorySlotContents(int index, ItemStack is) {
		if (index >= 0 && index < items.length) {
			//boolean f = !is.isEmpty() && is.isItemEqual(items[index]) && ItemStack.areItemStackTagsEqual(is, items[index]);
			items[index] = is;
			if (is.getCount() > 64)
				is.setCount(64);
		}
	}

	@Override public int getInventoryStackLimit() {
		return 64;
	}

	@Override public boolean isUsableByPlayer(EntityPlayer p) {
		return world.getTileEntity(pos) == this && p.getDistanceSq((double) pos.getX() + 0.5D, (double) pos.getY() + 0.5D, (double) pos.getZ() + 0.5D) <= 64.0D;
	}

	@Override public void openInventory(EntityPlayer p) {
		if (!world.isRemote && p instanceof EntityPlayerMP) {
			for (int i = 0; i < tanks.length; i++)
				MCFluxNetwork.to(Msg.fluidAmount(pos, i, tanks[i].fluid), (EntityPlayerMP) p);
		}
	}

	@Override public void closeInventory(EntityPlayer p) {

	}

	@Override public boolean isItemValidForSlot(int slot, ItemStack is) {
		return (slot == 0 && TileEntityFurnace.getItemBurnTime(is) > 0) || (slot == 1 && FluxGenRecipes.isCatalyst(is));
	}

	@Override public int getField(int id) {
		return vals[id];
	}

	@Override public void setField(int id, int value) {
		vals[id] = value;
		if (id == 0)
			energy = value;
	}

	@Override public int getFieldCount() {
		return vals.length;
	}

	@Override public void clear() {
		items[0] = ItemStack.EMPTY;
		items[1] = ItemStack.EMPTY;
	}

	@Nonnull @Override public ItemStack insertItem(int slot, @Nonnull ItemStack is, boolean sim) {
		if (is.isEmpty())
			return ItemStack.EMPTY;
		checkSlot(slot);
		if ((slot == 0 && TileEntityFurnace.getItemBurnTime(is) == 0) || (slot == 1 && !FluxGenRecipes.isCatalyst(is)))
			return is;
		int l = is.getMaxStackSize();
		if (l > 64) l = 64;
		int sc = is.getCount();
		ItemStack xis = items[slot];
		if (!xis.isEmpty()) {
			if (!ItemHandlerHelper.canItemStacksStack(is, xis))
				return is;
			l -= xis.getCount();
		}
		if (0 >= l)
			return is;
		boolean rl = sc > l;
		if (!sim) {
			if (xis.isEmpty())
				items[slot] = rl ? ItemHandlerHelper.copyStackWithSize(is, l) : is;
			else
				xis.grow(rl ? l : sc);
			isDirty = true;
		}
		return rl ? ItemHandlerHelper.copyStackWithSize(is, sc - l) : ItemStack.EMPTY;
	}

	@Nonnull @Override public ItemStack extractItem(int slot, int amount, boolean sim) {
		return ItemStack.EMPTY;
	}

	@Override public int getSlotLimit(int slot) {
		return 64;
	}

	private void checkSlot(int s) {
		if (s < 0 || s >= items.length)
			throw new RuntimeException("Getting slot " + s + " outside range [0," + items.length + ")");
	}

	@Override public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		energy = nbt.getInteger("E");
		if (energy > maxEnergy)
			energy = maxEnergy;
		NBTTagList nbtl = nbt.getTagList("Items", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < nbtl.tagCount(); i++) {
			NBTTagCompound inbt = nbtl.getCompoundTagAt(i);
			byte s = inbt.getByte("Slot");
			if (0 <= s && s < items.length)
				items[s] = new ItemStack(inbt);
		}
		nbtl = nbt.getTagList("Fluids", Constants.NBT.TAG_COMPOUND);
		for (int i = 0; i < nbtl.tagCount(); i++) {
			NBTTagCompound fnbt = nbtl.getCompoundTagAt(i);
			byte s = fnbt.getByte("Slot");
			if (0 <= s && s < tanks.length)
				tanks[s].fluid = FluidStack.loadFluidStackFromNBT(fnbt);
		}
		int[] v = nbt.getIntArray("Vals");
		System.arraycopy(v, 0, vals, 0, vals.length);
	}

	@Nonnull @Override public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		nbt.setInteger("E", (int) energy);
		NBTTagList nbtl = new NBTTagList();
		for (byte i = 0; i < items.length; i++) {
			if (items[i].isEmpty())
				continue;
			NBTTagCompound inbt = new NBTTagCompound();
			inbt.setByte("Slot", i);
			nbtl.appendTag(items[i].writeToNBT(inbt));
		}
		nbt.setTag("Items", nbtl);
		nbtl = new NBTTagList();
		for (byte i = 0; i < tanks.length; i++) {
			if (tanks[i].fluid == null || tanks[i].fluid.amount == 0)
				continue;
			NBTTagCompound fnbt = new NBTTagCompound();
			fnbt.setByte("Slot", i);
			nbtl.appendTag(tanks[i].fluid.writeToNBT(fnbt));
		}
		nbt.setTag("Fluids", nbtl);
		int[] v = new int[vals.length];
		System.arraycopy(vals, 0, v, 0, vals.length);
		nbt.setTag("Vals", new NBTTagIntArray(v));
		return nbt;
	}

	@Override public NBTTagCompound getUpdateTag() {
		return writeToNBT(new NBTTagCompound());
	}

	@Override public SPacketUpdateTileEntity getUpdatePacket() {
		return new SPacketUpdateTileEntity(pos, getBlockMetadata(), writeToNBT(new NBTTagCompound()));
	}

	@Override public IFluidTankProperties[] getTankProperties() {
		return tanks;
	}

	@Override public int fill(FluidStack fs, boolean doFill) {
		if (fs == null || fs.amount <= 0)
			return 0;
		byte s;
		if (FluxGenRecipes.isHotFluid(fs))
			s = 0;
		else if (FluxGenRecipes.isCleanFluid(fs))
			s = 1;
		else
			return 0;
		if (tanks[s].fluid != null && !fs.isFluidEqual(tanks[s].fluid))
			return 0;
		int l = fluidCap;
		if (tanks[s].fluid != null) {
			l -= tanks[s].fluid.amount;
		}
		if (l > fs.amount)
			l = fs.amount;
		if (doFill) {
			if (tanks[s].fluid == null)
				tanks[s].fluid = new FluidStack(fs, l);
			else
				tanks[s].fluid.amount += l;
			isDirty = true;
			MCFluxNetwork.toDimension(Msg.fluidAmount(pos, s, tanks[s].fluid), world.provider.getDimension());
		}
		return l;
	}

	@Nullable @Override public FluidStack drain(FluidStack resource, boolean doDrain) {
		return null;
	}

	@Nullable @Override public FluidStack drain(int maxDrain, boolean doDrain) {
		return null;
	}

	@Override public String getName() {
		return "mcflux.container.fluxgen";
	}

	@Override public boolean hasCustomName() {
		return false;
	}

	@Override public ITextComponent getDisplayName() {
		return new TextComponentTranslation(getName());
	}

	@SideOnly(Side.CLIENT)
	public void updateFluid(int id, @Nullable Fluid fl, int amount) {
		if (id < 0 || id >= tanks.length)
			return;
		FluidStorage tank = tanks[id];
		if (fl == null) {
			tank.fluid = null;
			return;
		}
		if (tank.fluid != null && tank.fluid.getFluid() == fl) {
			tank.fluid.amount = amount;
		} else {
			tank.fluid = new FluidStack(fl, amount);
		}
	}

	private static class FluidStorage implements IFluidTankProperties {
		private FluidStack fluid;
		private final int cap;
		private final boolean filling, draining;

		private FluidStorage(int c, boolean f, boolean d) {
			cap = c;
			filling = f;
			draining = d;
		}

		private int getAmount() {
			return fluid == null ? 0 : fluid.amount;
		}

		private void substract(int l) {
			if (fluid != null) {
				fluid.amount -= l;
				if (fluid.amount <= 0)
					fluid = null;
			}
		}

		@Nullable @Override public FluidStack getContents() {
			return fluid == null ? null : fluid.copy();
		}

		@Override public int getCapacity() {
			return cap;
		}

		@Override public boolean canFill() {
			return filling;
		}

		@Override public boolean canDrain() {
			return draining;
		}

		@Override public boolean canFillFluidType(FluidStack fluidStack) {
			return filling && (fluid == null || fluid.isFluidEqual(fluidStack));
		}

		@Override public boolean canDrainFluidType(FluidStack fluidStack) {
			return draining && (fluid == null || fluid.isFluidEqual(fluidStack));
		}
	}
}
