package szewek.mcflux.fluxable;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.monster.EntityCreeper;
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

public class EntityActionEnergy implements ICapabilityProvider, IEnergyConsumer {
	private boolean charged = false;
	private final EntityCreature creature;
	
	public EntityActionEnergy(EntityCreature ec) {
		creature = ec;
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
		return 0;
	}

	@Override
	public int getEnergyCapacity() {
		return 1;
	}

	@Override
	public int consumeEnergy(int amount, boolean simulate) {
		if (!simulate && !charged) {
			if (creature instanceof EntityPig) {
				EntityPigZombie pigman = new EntityPigZombie(creature.worldObj);
				pigman.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_AXE));
				pigman.setLocationAndAngles(creature.posX, creature.posY, creature.posZ, creature.rotationYaw, creature.rotationPitch);
				pigman.setNoAI(creature.isAIDisabled());
				if (creature.hasCustomName()) {
					pigman.setCustomNameTag(creature.getCustomNameTag());
					pigman.setAlwaysRenderNameTag(creature.getAlwaysRenderNameTag());
	            }
				creature.worldObj.spawnEntityInWorld(pigman);
				creature.setDead();
			} else if (creature instanceof EntityCreeper)
				creature.onStruckByLightning(null);
			charged = true;
			return 1;
		}
		return charged ? 0 : 1;
	}

}
