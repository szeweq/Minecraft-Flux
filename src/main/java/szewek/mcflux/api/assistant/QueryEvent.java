package szewek.mcflux.api.assistant;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.GenericEvent;

public class QueryEvent<T> extends GenericEvent<T> {
	public final EntityPlayer player;
	public final QueryElement<T> query;
	public final AnswerElement<T> answer;

	public QueryEvent(EntityPlayer ep, QueryElement<T> qe) {
		super(qe.type);
		player = ep;
		query = qe;
		answer = qe.createAnswer();
	}

	public static class QueryItemStack<T> extends QueryEvent<T> {
		public final ItemStack stack;

		public QueryItemStack(EntityPlayer ep, QueryElement<T> qe, ItemStack stack) {
			super(ep, qe);
			this.stack = stack;
		}
	}

	public static class QueryWorld<T> extends QueryEvent<T> {
		public final World world;
		public final BlockPos pos;
		public final EnumFacing facing;

		public QueryWorld(EntityPlayer ep, QueryElement<T> qe, World world, BlockPos pos, EnumFacing facing) {
			super(ep, qe);
			this.world = world;
			this.pos = pos;
			this.facing = facing;
		}
	}

	public static class QueryEntity<T> extends QueryEvent<T> {
		public final World world;
		public final Entity entity;

		public QueryEntity(EntityPlayer ep, QueryElement<T> qe, World world, Entity entity) {
			super(ep, qe);
			this.world = world;
			this.entity = entity;
		}
	}
}
