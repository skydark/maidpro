package info.skydark.maidpro;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import littleMaidMobX.LMM_EntityLittleMaid;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntityBeacon;
import net.minecraft.util.AxisAlignedBB;

import java.util.Iterator;
import java.util.List;

/**
 * Created by skydark on 15-11-11.
 */
public class TileEntityMaidBeacon extends TileEntityBeacon
{
    public void updateEntity()
    {
        super.updateEntity();
        if (this.worldObj.getTotalWorldTime() % 80L == 0L)
        {
            this.func_146000_x_2();
        }
    }

    private void func_146000_x_2()
    {
        int primaryEffect = getPrimaryEffect();
        int secondaryEffect = getSecondaryEffect();
        int level = getLevels();
        if (level > 0 && !this.worldObj.isRemote && primaryEffect > 0)
        {
            double d0 = (double)(level * 10 + 10);
            byte b0 = 0;

            if (level >= 4 && primaryEffect == secondaryEffect)
            {
                b0 = 1;
            }

            AxisAlignedBB axisalignedbb = AxisAlignedBB.getBoundingBox((double)this.xCoord, (double)this.yCoord, (double)this.zCoord, (double)(this.xCoord + 1), (double)(this.yCoord + 1), (double)(this.zCoord + 1)).expand(d0, d0, d0);
            axisalignedbb.maxY = (double)this.worldObj.getHeight();
            List list = this.worldObj.getEntitiesWithinAABB(LMM_EntityLittleMaid.class, axisalignedbb);
            Iterator iterator = list.iterator();
            LMM_EntityLittleMaid maid;

            while (iterator.hasNext())
            {
                maid = (LMM_EntityLittleMaid)iterator.next();
                maid.addPotionEffect(new PotionEffect(primaryEffect, 180, b0, true));
            }

            if (level >= 4 && primaryEffect != secondaryEffect && secondaryEffect > 0)
            {
                iterator = list.iterator();

                while (iterator.hasNext())
                {
                    maid = (LMM_EntityLittleMaid)iterator.next();
                    maid.addPotionEffect(new PotionEffect(secondaryEffect, 180, 0, true));
                }
            }
            list = this.worldObj.getEntitiesWithinAABB(EntityWolf.class, axisalignedbb);
            iterator = list.iterator();
            EntityWolf wolf;

            while (iterator.hasNext())
            {
                wolf = (EntityWolf)iterator.next();
                wolf.addPotionEffect(new PotionEffect(primaryEffect, 180, b0, true));
            }

            if (level >= 4 && primaryEffect != secondaryEffect && secondaryEffect > 0)
            {
                iterator = list.iterator();

                while (iterator.hasNext())
                {
                    wolf = (EntityWolf)iterator.next();
                    wolf.addPotionEffect(new PotionEffect(secondaryEffect, 180, 0, true));
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getRenderBoundingBox()
    {
        return INFINITE_EXTENT_AABB;
    }
}