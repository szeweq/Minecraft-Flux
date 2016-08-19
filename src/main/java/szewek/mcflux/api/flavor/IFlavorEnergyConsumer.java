package szewek.mcflux.api.flavor;

import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.INBTSerializable;

public interface IFlavorEnergyConsumer extends INBTSerializable<NBTBase> {
	default long consumeFlavorEnergy(EnumFacing from, FlavorEnergy fe, boolean simulate) {
		return consumeFlavorEnergy(fe, simulate);
	}

	long consumeFlavorEnergy(FlavorEnergy fe, boolean simulate);
}
