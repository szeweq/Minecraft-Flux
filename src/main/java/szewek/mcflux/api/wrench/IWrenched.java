package szewek.mcflux.api.wrench;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

public interface IWrenched {
	void wrench(EntityPlayer player, EnumHand hand);
}
