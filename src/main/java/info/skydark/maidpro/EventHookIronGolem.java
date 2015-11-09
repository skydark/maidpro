package info.skydark.maidpro;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import littleMaidMobX.LMM_EntityLittleMaid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILookAtVillager;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;

/**
 * Created by skydark on 15-11-9.
 */
public class EventHookIronGolem {
    @SubscribeEvent
    public void onEntitySpawn(EntityJoinWorldEvent event) {

        if (event.entity instanceof EntityIronGolem) {
            EntityIronGolem entity = (EntityIronGolem) event.entity;
            for (Object entry : entity.tasks.taskEntries.toArray()) {
                EntityAIBase ai = ((EntityAITasks.EntityAITaskEntry) entry).action;
                if (ai instanceof EntityAILookAtVillager) {
                    entity.tasks.removeTask(ai);
                    entity.tasks.addTask(5, new EntityAILookAtVillagerAndMaid(entity));
                    break;
                }
            }
        }
    }

    public class EntityAILookAtVillagerAndMaid extends EntityAIBase {
        private EntityIronGolem theGolem;
        private Entity lookingAt;
        private int lookTime;
        private static final String __OBFID = "CL_00001602";

        public EntityAILookAtVillagerAndMaid(EntityIronGolem p_i1643_1_) {
            this.theGolem = p_i1643_1_;
            this.setMutexBits(3);
        }

        /**
         * Returns whether the EntityAIBase should begin execution.
         */
        public boolean shouldExecute() {
            if (!this.theGolem.worldObj.isDaytime() && this.theGolem.dimension != -1) {
                return false;
            } else if (this.theGolem.getRNG().nextInt(8000) != 0) {
                return false;
            } else {
                EntityVillager theNearestVillager = (EntityVillager) this.theGolem.worldObj.findNearestEntityWithinAABB(EntityVillager.class, this.theGolem.boundingBox.expand(6.0D, 2.0D, 6.0D), this.theGolem);
                LMM_EntityLittleMaid theNearestMaid = (LMM_EntityLittleMaid) this.theGolem.worldObj.findNearestEntityWithinAABB(LMM_EntityLittleMaid.class, this.theGolem.boundingBox.expand(6.0D, 2.0D, 6.0D), this.theGolem);
                if (theNearestVillager == null) {
                    lookingAt = theNearestMaid;
                } else if (theNearestMaid == null) {
                    lookingAt = theNearestVillager;
                } else {
                    lookingAt = this.theGolem.getDistanceSqToEntity(theNearestVillager) > this.theGolem.getDistanceSqToEntity(theNearestMaid) ? theNearestMaid : theNearestVillager;
                }
                return this.lookingAt != null;
            }
        }

        /**
         * Returns whether an in-progress EntityAIBase should continue executing
         */
        public boolean continueExecuting() {
            return this.lookTime > 0;
        }

        /**
         * Execute a one shot task or start executing a continuous task
         */
        public void startExecuting() {
            this.lookTime = 400;
            this.theGolem.setHoldingRose(true);
        }

        /**
         * Resets the task
         */
        public void resetTask() {
            this.theGolem.setHoldingRose(false);
            this.lookingAt = null;
        }

        /**
         * Updates the task
         */
        public void updateTask() {
            this.theGolem.getLookHelper().setLookPositionWithEntity(this.lookingAt, 30.0F, 30.0F);
            --this.lookTime;
        }
    }
}
