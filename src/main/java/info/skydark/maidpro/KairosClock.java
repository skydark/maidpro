package info.skydark.maidpro;

import cpw.mods.fml.common.registry.GameRegistry;
import littleMaidMobX.LMM_EntityLittleMaid;
import littleMaidMobX.LMM_EntityLittleMaidAvatarMP;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;

import java.lang.reflect.Field;

/**
 * Created by skydark on 15-8-26.
 */
public class KairosClock extends Item {
    public final static String name = "KairosClock";

    public KairosClock() {
        super();
        setMaxStackSize(1);
        setUnlocalizedName(MaidPro.MODID + "_" + name);
        GameRegistry.registerItem(this, MaidPro.MODID + "_" + name);
        setTextureName(MaidPro.MODID + ":" + name);
        setCreativeTab(CreativeTabs.tabMisc);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase entity) {
        if (player.isSneaking()) {
            if (!(entity instanceof LMM_EntityLittleMaid)) {
                return false;
            }
            LMM_EntityLittleMaid maid = (LMM_EntityLittleMaid) entity;
            if (!maid.isMaidContractOwner(player)) {
                return false;
            }
            MaidContract maidContract = new MaidContract(maid);
            if (!maidContract.setContract(0)) {
                return false;
            }
            if (!player.worldObj.isRemote) {
                player.addChatMessage(new ChatComponentText("...why?"));
            }
            return true;
        }
        return false;
    }
}