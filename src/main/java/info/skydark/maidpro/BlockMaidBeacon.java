package info.skydark.maidpro;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.BlockBeacon;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * Created by skydark on 15-11-11.
 */
public class BlockMaidBeacon extends BlockBeacon {
    public final static String name = "MaidBeacon";

    public BlockMaidBeacon()
    {
        super();
        setBlockName(MaidPro.MODID + "_" + name);
        setBlockTextureName("minecraft:beacon");
        GameRegistry.registerBlock(this, MaidPro.MODID + "_" + this.name);
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
    {
        return new TileEntityMaidBeacon();
    }

    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(World p_149727_1_, int p_149727_2_, int p_149727_3_, int p_149727_4_, EntityPlayer p_149727_5_, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
    {
        if (p_149727_1_.isRemote)
        {
            return true;
        }
        else
        {
            TileEntityMaidBeacon tileentitybeacon = (TileEntityMaidBeacon)p_149727_1_.getTileEntity(p_149727_2_, p_149727_3_, p_149727_4_);

            if (tileentitybeacon != null)
            {
                p_149727_5_.func_146104_a(tileentitybeacon);
            }

            return true;
        }
    }
    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(World p_149689_1_, int p_149689_2_, int p_149689_3_, int p_149689_4_, EntityLivingBase p_149689_5_, ItemStack p_149689_6_)
    {
        super.onBlockPlacedBy(p_149689_1_, p_149689_2_, p_149689_3_, p_149689_4_, p_149689_5_, p_149689_6_);

        if (p_149689_6_.hasDisplayName())
        {
            ((TileEntityMaidBeacon)p_149689_1_.getTileEntity(p_149689_2_, p_149689_3_, p_149689_4_)).func_145999_a(p_149689_6_.getDisplayName());
        }
    }
}
