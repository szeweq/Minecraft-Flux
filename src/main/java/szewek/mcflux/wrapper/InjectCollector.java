package szewek.mcflux.wrapper;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

public final class InjectCollector {
	Set<IWrapperInject<TileEntity>> tileInjects = new HashSet<>();
	Set<IWrapperInject<ItemStack>> itemInjects = new HashSet<>();
	Set<IWrapperInject<Entity>> entityInjects = new HashSet<>();
	Set<IWrapperInject<World>> worldInjects = new HashSet<>();

	InjectCollector() {}

	public void addTileWrapperInject(IWrapperInject<TileEntity> iwi) {
		tileInjects.add(iwi);
	}

	public void addItemWrapperInject(IWrapperInject<ItemStack> iwi) {
		itemInjects.add(iwi);
	}

	public void addEntityWrapperInject(IWrapperInject<Entity> iwi) {
		entityInjects.add(iwi);
	}

	public void addWorldWrapperInject(IWrapperInject<World> iwi) {
		worldInjects.add(iwi);
	}
}
