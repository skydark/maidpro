package info.skydark.maidpro;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;

/**
 * Created by skydark on 15-8-26.
 */
public class EmeraldFragment extends Item {
    public final static String name = "EmeraldFragment";
    public final static int MAX_MANA = 100000;
    public final static String TAG_MANA = "mana";

    public EmeraldFragment() {
        super();
        setMaxStackSize(1);
        setMaxDamage(1000);
        setNoRepair();
        setUnlocalizedName(MaidPro.MODID + "_" + name);
        GameRegistry.registerItem(this, MaidPro.MODID + "_" + name);
        setTextureName(MaidPro.MODID + ":" + name);
        setCreativeTab(CreativeTabs.tabMisc);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs creativeTabs, List itemList) {
        ItemStack empty = new ItemStack(item, 1, 1000);
        setMana(empty, 0);
        itemList.add(empty);

        ItemStack full = new ItemStack(item, 1, 1);
        setMana(full, MAX_MANA);
        itemList.add(full);

        ItemStack eternal = new ItemStack(item, 1, 0);
        setMana(eternal, -1);
        itemList.add(eternal);
    }

    @Override
    public int getDamage(ItemStack stack) {
        return getManaRaw(stack) < 0 ? 0 : 1000 - (int) (getMana(stack) * 1000.0 / (MAX_MANA + 1));
    }

    @Override
    public int getDisplayDamage(ItemStack stack) {
        return getDamage(stack);
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltips, boolean isAdvanced) {
        String tooltip;
        if (isEternal(stack)) {
            tooltip = StatCollector.translateToLocal("tooltip.maidpro.Ethernal.desc").trim();
        } else {
            tooltip = StatCollector.translateToLocal("tooltip.maidpro.Mana.desc").trim() + ": "
                    + Integer.toString(getMana(stack)) + "/" + Integer.toString(MAX_MANA);
        }
        tooltips.add(tooltip);
    }

    public boolean isEternal(ItemStack stack) {
        return getManaRaw(stack) < 0;
    }

    public int getManaRaw(ItemStack stack) {
        NBTTagCompound nbtTagCompound = stack.getTagCompound();
        if (nbtTagCompound == null || !nbtTagCompound.hasKey(TAG_MANA)) {
            return 0;
        }
        return nbtTagCompound.getInteger(TAG_MANA);
    }

    public int getMana(ItemStack stack) {
        int mana = getManaRaw(stack);
        return mana < 0 ? MAX_MANA : mana;
    }

    public void setMana(ItemStack stack, int mana) {
        if (isEternal(stack)) {
            return;
        }
        NBTTagCompound nbtTagCompound = stack.getTagCompound();
        if (nbtTagCompound == null) {
            nbtTagCompound = new NBTTagCompound();
        }
        nbtTagCompound.setInteger(TAG_MANA, mana);
        stack.setTagCompound(nbtTagCompound);
    }

    public boolean consumeMana(ItemStack stack, int mana) {
        if (isEternal(stack)) {
            return true;
        }
        if (getMana(stack) < mana) {
            return false;
        }
        setMana(stack, getMana(stack) - mana);
        return true;
    }

    public boolean isManaFull(ItemStack stack) {
        return isEternal(stack) || getMana(stack) >= MAX_MANA;
    }

    public void fillMana(ItemStack stack, int mana) {
        if (isEternal(stack)) {
            return;
        }
        mana += getMana(stack);
        setMana(stack, mana >= MAX_MANA ? MAX_MANA : mana);
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (player.isSneaking()) {
            boolean isfilled = false;
            while (eatEmerald(stack, player)) {
                isfilled = true;
            }
            if (isfilled && !world.isRemote) {
                player.addChatMessage(new ChatComponentText("All your emerald are belong to us!"));
            }
        } else {
            eatEmerald(stack, player);
            /*if (!world.isRemote) {
                player.addChatMessage(new ChatComponentText("Mana: " + Integer.toString(getMana(stack))));
            }*/
        }
        return super.onItemRightClick(stack, world, player);
    }

    private boolean eatEmerald(ItemStack stack, EntityPlayer player) {
        if (isManaFull(stack)) {
            return false;
        }
        if (player.inventory.consumeInventoryItem(Items.emerald)) {
            fillMana(stack, Config.manaPerEmerald);
            return true;
        }
        if (player.inventory.consumeInventoryItem(Item.getItemFromBlock(Blocks.emerald_block))) {
            fillMana(stack, Config.manaPerEmerald * 9);
            return true;
        }
        return false;
    }
}
