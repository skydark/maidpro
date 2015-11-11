package info.skydark.maidpro.ai;

import littleMaidMobX.LMM_EntityLittleMaid;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIOwnerHurtTarget;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Created by skydark on 15-11-11.
 */
public class EntityAIOwnerHurtTargetPlus extends EntityAIOwnerHurtTarget {
    private EntityTameable _entity;
    public EntityAIOwnerHurtTargetPlus(EntityTameable p_i1668_1_)
    {
        super(p_i1668_1_);
        _entity = p_i1668_1_;
    }

    @Override
    protected boolean isSuitableTarget(EntityLivingBase p_75296_1_, boolean p_75296_2_) {
        if (p_75296_1_ instanceof LMM_EntityLittleMaid) {
            EntityLivingBase owner = _entity.getOwner();
            if (owner instanceof EntityPlayer && ((LMM_EntityLittleMaid) p_75296_1_).isMaidContractOwner((EntityPlayer) owner)) {
                return false;
            }
        }
        return super.isSuitableTarget(p_75296_1_, p_75296_2_);
    }
}
