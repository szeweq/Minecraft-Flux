package szewek.mcflux.api.wrench;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;

public interface IWrenched {
	void wrench(EntityPlayer player, EnumHand hand, WrenchMode mode);
	boolean canWrench(EntityPlayer player, EnumHand hand, WrenchMode mode);
	ItemStack getSuitableWrench();
}
