package info.skydark.maidpro;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import info.skydark.maidpro.ai.EntityAIHurtByTargetPlus;
import info.skydark.maidpro.ai.EntityAIOwnerHurtByTargetPlus;
import info.skydark.maidpro.ai.EntityAIOwnerHurtTargetPlus;
import littleMaidMobX.LMM_EntityLittleMaid;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;

/**
 * Created by skydark on 15-11-11.
 */
public class EventHookWolf {
    @SubscribeEvent
    public void onEntitySpawn(EntityJoinWorldEvent event) {
        if (event.entity instanceof EntityWolf) {
            EntityWolf entity = (EntityWolf) event.entity;
            for (Object entry : entity.targetTasks.taskEntries.toArray()) {
                EntityAIBase ai = ((EntityAITasks.EntityAITaskEntry) entry).action;
                if (ai instanceof EntityAIOwnerHurtByTarget || ai instanceof EntityAIOwnerHurtTarget || ai instanceof EntityAIHurtByTarget) {
                    entity.targetTasks.removeTask(ai);
                }
            }
            entity.targetTasks.addTask(1, new EntityAIOwnerHurtByTargetPlus(entity));
            entity.targetTasks.addTask(2, new EntityAIOwnerHurtTargetPlus(entity));
            entity.targetTasks.addTask(3, new EntityAIHurtByTargetPlus(entity, true));
        }
    }

    @SubscribeEvent
    public void onSetAttackTarget(LivingSetAttackTargetEvent event) {
        if (!(event.entity instanceof EntityWolf))
            return;
        EntityWolf wolf = (EntityWolf) event.entity;
        EntityLivingBase target = wolf.getAttackTarget();
        if (target == null || !(target instanceof LMM_EntityLittleMaid))
            return;
        EntityLivingBase owner = wolf.getOwner();
        if (owner instanceof EntityPlayer && ((LMM_EntityLittleMaid) target).isMaidContractOwner((EntityPlayer) owner)) {
            wolf.setAttackTarget(null);
            wolf.setTarget(null);
            wolf.setRevengeTarget(null);
        }
    }
}