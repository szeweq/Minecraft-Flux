package elucent.roots.capability.mana;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public interface IManaCapability {
	public float getMana();
	public float getMaxMana();
	public void setMana(EntityPlayer player, float mana);
	public void setMaxMana(float maxMana);
	public NBTTagCompound saveNBTData();
	public void loadNBTData(NBTTagCompound compound);
	public void dataChanged(EntityPlayer player);
}
