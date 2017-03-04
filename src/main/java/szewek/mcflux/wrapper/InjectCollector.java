package szewek.mcflux.wrapper;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;

public final class InjectCollector {
	Set<BiConsumer<TileEntity, WrapperRegistry>> tileInjects = new HashSet<>();
	Set<BiConsumer<ItemStack, WrapperRegistry>> itemInjects = new HashSet<>();
	Set<BiConsumer<Entity, WrapperRegistry>> entityInjects = new HashSet<>();

	InjectCollector() {}

	public void addTileWrapperInject(BiConsumer<TileEntity, WrapperRegistry> bc) {
		tileInjects.add(bc);
	}

	public void addItemWrapperInject(BiConsumer<ItemStack, WrapperRegistry> bc) {
		itemInjects.add(bc);
	}

	public void addEntityWrapperInject(BiConsumer<Entity, WrapperRegistry> bc) {
		entityInjects.add(bc);
	}
}
