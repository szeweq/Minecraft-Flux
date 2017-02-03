package szewek.mcflux.wrapper.botania;

import net.minecraft.tileentity.TileEntity;
import szewek.mcflux.util.IInjectRegistry;
import szewek.mcflux.util.InjectCond;
import szewek.mcflux.util.InjectRegistry;
import szewek.mcflux.util.MCFluxLocation;
import szewek.mcflux.wrapper.InjectWrappers;
import vazkii.botania.common.block.tile.mana.TilePool;

@InjectRegistry(requires = InjectCond.MOD, args = "Botania")
public class BotaniaInjectRegistry implements IInjectRegistry {
	static final String BOTANIA_MANA = "botania:mana";
	private static final MCFluxLocation MANA_RL = new MCFluxLocation("mana");
	@Override public void registerInjects() {
		InjectWrappers.addTileWrapperInject(BotaniaInjectRegistry::wrapBotaniaTile);
	}

	private static void wrapBotaniaTile(TileEntity te, InjectWrappers.Registry reg) {
		if (te instanceof TilePool) {
			reg.register(MANA_RL, new ManaPoolFlavorWrapper((TilePool) te));
		}
	}
}
