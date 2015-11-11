package info.skydark.maidpro;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAILookAtVillager;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.entity.monster.EntityIronGolem;
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
}
