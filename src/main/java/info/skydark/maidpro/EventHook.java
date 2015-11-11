package info.skydark.maidpro;

import littleMaidMobX.*;
import net.minecraft.block.BlockFlower;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;

import java.util.List;


public class EventHook
{
    private static final double CALLING_DISTANCE = 16.0;
    @SubscribeEvent
    public void interact(EntityInteractEvent event) {
        if (!(event.target instanceof LMM_EntityLittleMaid)) {
            return;
        }
        EntityPlayer player = event.entityPlayer;
        LMM_EntityLittleMaid maid = (LMM_EntityLittleMaid) event.target;
        ItemStack itemstack = player.inventory.getCurrentItem();
        if (itemstack != null && player.isSneaking()) {
            Item item = itemstack.getItem();
            if (Config.milkmode != 0 && item == Items.bucket) {
                boolean is_contracted = maid.isContractEX() && maid.isMaidContractOwner(player);
                if (Config.milkmode != 1 || is_contracted) {
                    if (!player.capabilities.isCreativeMode && --itemstack.stackSize <= 0) {
                        player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Items.milk_bucket));
                    } else if (!player.inventory.addItemStackToInventory(new ItemStack(Items.milk_bucket))) {
                        player.dropPlayerItemWithRandomChoice(new ItemStack(Items.milk_bucket), false);
                    }
                    if (is_contracted) {
                        maid.playSound(LMM_EnumSound.laughter, false);
                    } else {
                        if (Config.milkmode == 3) {
                            maid.setSwing(20, LMM_EnumSound.attack);
                            maid.maidAvatar.attackTargetEntityWithCurrentItem(player);
                        } else {
                            maid.playSound(LMM_EnumSound.living_whine, false);
                        }
                    }
                }
            } else if (item instanceof ItemBlock && ((ItemBlock) item).field_150939_a instanceof BlockFlower && maid.getRNG().nextInt(3) == 0) {
                maid.playSound(LMM_EnumSound.laughter, false);
            }
        }
    }

    @SubscribeEvent
    public void onLivingUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.entity.worldObj.isRemote) {
            return;
        }
        if (!(event.entity instanceof LMM_EntityLittleMaid)) {
            return;
        }

        LMM_EntityLittleMaid maid = (LMM_EntityLittleMaid) event.entity;
        if (!maid.isContract()) {
            return;
        }

        if (event.entity.ticksExisted % Config.tickPerContractCheck == 0) {
            if (maid.getContractLimitDays() <= 6 && maid.maidInventory.hasItem(ModItems.kairosClock)) {
                MaidContract maidContract = new MaidContract(maid);
                maidContract.addContract(Config.tickPerContract);
            }
        }
        if (event.entity.ticksExisted % Config.tickPerHealCheck == 0) {
            if (maid.getMaxHealth() - maid.getHealth() < 0.5) {
                return;
            }
            ItemStack manaSourceStack = findItem(maid.maidInventory, ModItems.emeraldFragment);
            if (manaSourceStack != null) {
                EmeraldFragment manaSource = (EmeraldFragment) manaSourceStack.getItem();
                if (manaSource.consumeMana(manaSourceStack, Config.manaPerHealth * Config.healthPerHeal)) {
                    maid.heal(Config.healthPerHeal);
                }
            }
        }
    }

    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent event) {
        if (Config.clockSaveAllPets) {
            Entity _entity = event.entity;
            if (event.entity instanceof LMM_EntityLittleMaidAvatarMP) {
                _entity = ((LMM_EntityLittleMaidAvatarMP) event.entity).avatar;
            }
            if (_entity instanceof IEntityOwnable) {
                Entity attacker = event.source.getEntity();
                if (attacker instanceof EntityPlayer) {
                    if (((IEntityOwnable) _entity).getOwner() == attacker &&
                            !attacker.isSneaking()) {
                        InventoryPlayer inventory = ((EntityPlayer) attacker).inventory;
                        if (inventory != null && inventory.hasItem(ModItems.kairosClock)) {
                            event.ammount = 0;
                            event.setCanceled(true);
                            return;
                        }
                    }
                }
            }
        }

        if (! (event.entity instanceof LMM_EntityLittleMaidAvatarMP)) {
            return;
        }

        LMM_EntityLittleMaidAvatarMP maidMP = (LMM_EntityLittleMaidAvatarMP) event.entity;
        LMM_EntityLittleMaid maid = maidMP.avatar;

        if (!maid.isContract()) {
            return;
        }

        Entity attacker = event.source.getEntity();

        // call for wolves help
        if (Config.wolflove && event.ammount > 0 && attacker instanceof EntityLivingBase) {
            callWolves(maid, (EntityLivingBase) attacker, CALLING_DISTANCE);
        }

        LMM_InventoryLittleMaid inventory = maid.maidInventory;
        boolean hasKairosClock = inventory.hasItem(ModItems.kairosClock);

        if (hasKairosClock) {
            if (attacker instanceof EntityPlayer && maid.isMaidContractOwner((EntityPlayer) attacker)) {
                event.ammount = 0;
                event.setCanceled(true);
                return;
            }
        }

        ItemStack manaSourceStack = findItem(inventory, ModItems.emeraldFragment);
        if (manaSourceStack == null) {
            return;
        }

        EmeraldFragment manaSource = (EmeraldFragment) manaSourceStack.getItem();

        int manalimit = manaSource.getMana(manaSourceStack);
        if (manalimit <= 0) {
            return;
        }

        int recover = 0;
        int manalost = 0;
        float damage = event.ammount;
        DamageSource source = event.source;
        if (source.isExplosion()) {
            if (manalimit >= Config.enableExplosionResistance) {
                recover = (int) (Config.explosionResistancePercent * damage / 100);
                manalost = Config.manaPerExplosionLimit;
            }
        } else if (source.isFireDamage()) {
            if (manalimit >= Config.enableFireResistance && manaSource.consumeMana(manaSourceStack, Config.manaPerFire)) {
                maid.addPotionEffect(new PotionEffect(Potion.fireResistance.id, 1200));
                event.setCanceled(true);
                maid.extinguish();
                return;
            }
        } else if (source == DamageSource.fall) {
            if (manalimit >= Config.enableFallingResistance) {
                recover = (int) (Config.fallingResistancePercent * damage / 100);
                manalost = Config.manaPerFallingLimit;
            }
        } else {    // general recover
            // Working...
            if (source == DamageSource.magic) {
//            if (maid.isPotionActive(Potion.poison)) {
//                if (manalimit >= Config.enablePoisonResistance
//                        && manaSource.consumeMana(manaSourceStack, Config.manaPerPoison)) {
//                    maid.removePotionEffect(Potion.poison.id);
//                    event.setCanceled(true);
//                    return;
//                }
//            }
            } else if (source == DamageSource.wither) {
            } else if (source == DamageSource.cactus) {
            } else {
            }
            if (manalimit >= Config.enableGeneralResistance) {
                recover = (int) (Config.generalResistancePercent * damage / 100);
                manalost = EmeraldFragment.MAX_MANA;
            }
        }

        manalost = manalost > recover * Config.manaPerHealth ? recover * Config.manaPerHealth : manalost;
        int finalmanalost = manalost > manalimit ? manalimit : manalost;
        int finalrecover = finalmanalost > 0 ? finalmanalost * recover / manalost : 0;
        if (finalrecover > 0 && manaSource.consumeMana(manaSourceStack, finalmanalost)) {
            event.ammount -= finalrecover;
        }

        if (hasKairosClock && event.ammount >= maid.getHealth()) {
            if (manaSource.consumeMana(manaSourceStack, Config.manaPerLife)) {
                maid.setHealth(maid.getMaxHealth());
                event.ammount = 0;
                event.setCanceled(true);
            }
        }
    }

    private static ItemStack findItem(InventoryPlayer inventory, Item item) {
        for (ItemStack s : inventory.mainInventory) {
            if (s != null && s.getItem() == item) {
                return s;
            }
        }
        return null;
    }

    private void callWolves(LMM_EntityLittleMaid maid, EntityLivingBase attacker, double d0) {
        EntityLivingBase owner = maid.getOwner();
        if (owner == null || attacker == null || attacker == owner) {
            return;
        }
        if (attacker instanceof IEntityOwnable && ((IEntityOwnable) attacker).getOwner() == owner) {
            return;
        }
        // owner.setRevengeTarget(attacker);
        List list = maid.worldObj.getEntitiesWithinAABB(EntityWolf.class, AxisAlignedBB.getBoundingBox(maid.posX, maid.posY, maid.posZ, maid.posX + 1.0D, maid.posY + 1.0D, maid.posZ + 1.0D).expand(d0, 10.0D, d0));

        for (Object creature: list) {
            EntityWolf wolf = (EntityWolf) creature;
            EntityLivingBase _owner = wolf.getOwner();
            if (_owner instanceof  EntityPlayer && maid.isMaidContractOwner((EntityPlayer) _owner) && wolf.getAttackTarget() == null && !wolf.isOnSameTeam(attacker))
            {
                wolf.setRevengeTarget(attacker);
            }
        }
    }
}

