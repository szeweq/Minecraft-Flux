package szewek.mcflux.containers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import szewek.mcflux.recipes.FluxGenRecipes;
import szewek.mcflux.tileentities.TileEntityFluxGen;

public final class ContainerFluxGen extends Container {
	private final TileEntityFluxGen fluxGen;
	private int[] vals;

	public ContainerFluxGen(EntityPlayer p, TileEntityFluxGen tefg) {
		fluxGen = tefg;
		fluxGen.openInventory(p);
		vals = new int[fluxGen.getFieldCount()];
		addSlotToContainer(new Slot(fluxGen, 0, 67, 35));
		addSlotToContainer(new Slot(fluxGen, 1, 93, 35));
		int xBase, yBase = 84;
		for (int y = 0; y < 3; y++) {
			xBase = 8;
			for (int x = 0; x < 9; x++) {
				addSlotToContainer(new Slot(p.inventory, x + 9 * y + 9, xBase, yBase));
				xBase += 18;
			}
			yBase += 18;
		}
		xBase = 8;
		yBase += 4;
		for (int w = 0; w < 9; w++) {
			addSlotToContainer(new Slot(p.inventory, w, xBase, yBase));
			xBase += 18;
		}
	}

	@Override public void addListener(IContainerListener listener) {
		super.addListener(listener);
		listener.sendAllWindowProperties(this, fluxGen);
	}

	@Override public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for (IContainerListener icl : listeners) {
			for (int i = 0; i < vals.length; i++) {
				int f = fluxGen.getField(i);
				if (vals[i] != f) {
					vals[i] = f;
					icl.sendWindowProperty(this, i, f);
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	@Override public void updateProgressBar(int id, int data) {
		fluxGen.setField(id, data);
	}

	@Override public boolean canInteractWith(EntityPlayer p) {
		return fluxGen.isUsableByPlayer(p);
	}

	@Override public ItemStack transferStackInSlot(EntityPlayer p, int index) {
		ItemStack nis = ItemStack.EMPTY;
		Slot sl = inventorySlots.get(index);
		if (sl != null && sl.getHasStack()) {
			ItemStack bis = sl.getStack();
			nis = bis.copy();
			int s = 2, e = 38;
			if (index > 1) {
				if (TileEntityFurnace.getItemBurnTime(bis) > 0) {
					s = 0; e = 1;
				} else if (FluxGenRecipes.isCatalyst(bis)) {
					s = 1; e = 2;
				} else if (index < 29) {
					s = 29;
				} else if (index < 38) {
					e = 29;
				}
			}
			if (!mergeItemStack(bis, s, e, false))
				return ItemStack.EMPTY;
			if (nis.isEmpty())
				sl.putStack(ItemStack.EMPTY);
			else
				sl.onSlotChanged();
			if (nis.getCount() == bis.getCount())
				return ItemStack.EMPTY;
			sl.onTake(p, nis);
		}
		return nis;
	}
}
