package szewek.mcflux.fluxable;

import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import szewek.mcflux.api.CapabilityEnergy;
import szewek.mcflux.api.IEnergyConsumer;

public class PigEnergy implements IEnergyConsumer, ICapabilityProvider {
	private final EntityPig pig;
	
	public PigEnergy(EntityPig ep) {
		pig = ep;
	}

	@Override
	public boolean hasCapability(Capability<?> cap, EnumFacing f) {
		return cap == CapabilityEnergy.ENERGY_CONSUMER;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(Capability<T> cap, EnumFacing f) {
		if (cap == CapabilityEnergy.ENERGY_CONSUMER)
			return (T) this;
		return null;
	}

	@Override
	public int getEnergy() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getEnergyCapacity() {
		// TODO Auto-generated method stub
		return 1;
	}

	@Override
	public int consumeEnergy(int amount, boolean simulate) {
		if (!simulate) {
			EntityPigZombie pigman = new EntityPigZombie(pig.worldObj);
			pigman.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_AXE));
			pigman.setLocationAndAngles(pig.posX, pig.posY, pig.posZ, pig.rotationYaw, pig.rotationPitch);
			pigman.setNoAI(pig.isAIDisabled());
			if (pig.hasCustomName()) {
				pigman.setCustomNameTag(pig.getCustomNameTag());
				pigman.setAlwaysRenderNameTag(pig.getAlwaysRenderNameTag());
            }
			pig.worldObj.spawnEntityInWorld(pigman);
			pig.setDead();
		}
		return 1;
	}

}
