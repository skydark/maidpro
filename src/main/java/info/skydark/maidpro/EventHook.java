package info.skydark.maidpro;

import littleMaidMobX.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.entity.player.EntityInteractEvent;


public class EventHook
{
    @SubscribeEvent
    public void interact(EntityInteractEvent event) {
        if (Config.milkmode == 0 || !(event.target instanceof LMM_EntityLittleMaid)) {
            return;
        }
        EntityPlayer player = event.entityPlayer;
        LMM_EntityLittleMaid maid = (LMM_EntityLittleMaid) event.target;
        ItemStack itemstack = player.inventory.getCurrentItem();
        if (itemstack != null && itemstack.getItem() == Items.bucket && player.isSneaking()) {
            if (Config.milkmode == 2 || (maid.isContractEX() && maid.isMaidContractOwner(player))) {
                if (!player.capabilities.isCreativeMode && --itemstack.stackSize <= 0) {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Items.milk_bucket));
                } else if (!player.inventory.addItemStackToInventory(new ItemStack(Items.milk_bucket))) {
                    player.dropPlayerItemWithRandomChoice(new ItemStack(Items.milk_bucket), false);
                }
            }
            ((LMM_EntityLittleMaid) event.target).playSound(LMM_EnumSound.living_whine, false);
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
        if (Config.clockSaveAllPets && event.entity instanceof IEntityOwnable) {
            Entity attacker = event.source.getEntity();
            if (attacker instanceof EntityPlayer) {
                if (((IEntityOwnable) event.entity).getOwner() == attacker &&
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
        if (! (event.entity instanceof LMM_EntityLittleMaidAvatarMP)) {
            return;
        }

        LMM_EntityLittleMaidAvatarMP maidMP = (LMM_EntityLittleMaidAvatarMP) event.entity;
        LMM_EntityLittleMaid maid = maidMP.avatar;

        if (!maid.isContract()) {
            return;
        }


        LMM_InventoryLittleMaid inventory = maid.maidInventory;
        boolean hasKairosClock = inventory.hasItem(ModItems.kairosClock);

        if (hasKairosClock) {
            Entity attacker = event.source.getEntity();
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
        return;
    }

    private static ItemStack findItem(InventoryPlayer inventory, Item item) {
        for (ItemStack s : inventory.mainInventory) {
            if (s != null && s.getItem() == item) {
                return s;
            }
        }
        return null;
    }
}

