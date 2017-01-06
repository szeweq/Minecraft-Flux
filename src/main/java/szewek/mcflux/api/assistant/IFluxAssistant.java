package szewek.mcflux.api.assistant;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public interface IFluxAssistant {
	<T> QueryElement<T> getQueryElement(String name, Class<T> c);
	<T> List<T> ask(EntityPlayer ep, QueryElement<T> qe);
	<T> List<T> askWithItemStack(EntityPlayer ep, QueryElement<T> qe, ItemStack is);
	<T> List<T> askInWorld(EntityPlayer ep, QueryElement<T> qe, World w, BlockPos bp, EnumFacing f);
	<T> List<T> askWithEntity(EntityPlayer ep, QueryElement<T> qe, World w, Entity e);
}
