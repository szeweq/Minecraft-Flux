package szewek.mcflux.assistant;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import szewek.mcflux.api.assistant.IFluxAssistant;
import szewek.mcflux.api.assistant.QueryElement;
import szewek.mcflux.api.assistant.QueryEvent;

import java.util.ArrayList;
import java.util.List;

public enum FluxAssistant implements IFluxAssistant {
	INSTANCE;
	private final List<QueryElement<?>> queries = new ArrayList<>();

	private static <T> List<T> getAnswers(QueryEvent<T> qev) {
		MinecraftForge.EVENT_BUS.post(qev);
		return qev.answer.getAll();
	}

	@Override public <T> QueryElement<T> getQueryElement(String name, Class<T> c) {
		for (QueryElement<?> qe : queries) {
			if (qe.type == c && qe.name.equals(name)) {
				return qe.getCasted();
			}
		}
		QueryElement<T> qe = new QueryElement<T>(name, c);
		queries.add(qe);
		return qe;
	}

	@Override public <T> List<T> ask(EntityPlayer ep, QueryElement<T> qe) {
		return getAnswers(new QueryEvent<T>(ep, qe));

	}

	@Override public <T> List<T> askWithItemStack(EntityPlayer ep, QueryElement<T> qe, ItemStack is) {
		return getAnswers(new QueryEvent.QueryItemStack<T>(ep, qe, is));
	}

	@Override public <T> List<T> askInWorld(EntityPlayer ep, QueryElement<T> qe, World w, BlockPos bp, EnumFacing f) {
		return getAnswers(new QueryEvent.QueryWorld<T>(ep, qe, w, bp, f));
	}

	@Override public <T> List<T> askWithEntity(EntityPlayer ep, QueryElement<T> qe, World w, Entity e) {
		return getAnswers(new QueryEvent.QueryEntity<>(ep, qe, w, e));
	}
}
