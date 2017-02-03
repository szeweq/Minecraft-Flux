package vazkii.botania.common.block.tile.mana;

import net.minecraft.tileentity.TileEntity;

// Replacement class
public class TilePool extends TileEntity {
	public int manaCap;

	public void recieveMana(int mana) {}

	public int getCurrentMana() {return 0;}

	public int getAvailableSpaceForMana() {return 0;}

	public boolean isFull() {return false;}
}
