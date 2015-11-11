package info.skydark.maidpro;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * Created by skydark on 15-8-26.
 */
public final class Recipes {
    public static void init() {
        ItemStack emptyEmeraldFragment = new ItemStack(ModItems.emeraldFragment, 1, 1000);
        ((EmeraldFragment) emptyEmeraldFragment.getItem()).setMana(emptyEmeraldFragment, 0);
        ItemStack fullEmeraldFragment = new ItemStack(ModItems.emeraldFragment, 1, 1);
        ((EmeraldFragment) fullEmeraldFragment.getItem()).setMana(fullEmeraldFragment, EmeraldFragment.MAX_MANA);
        ItemStack eternalEmeraldFragment = new ItemStack(ModItems.emeraldFragment, 1, 0);
        Item dragon_egg = Item.getItemFromBlock(Blocks.dragon_egg);
        ((EmeraldFragment) eternalEmeraldFragment.getItem()).setContainerItem(dragon_egg);
        ((EmeraldFragment) eternalEmeraldFragment.getItem()).setMana(eternalEmeraldFragment, -1);

        GameRegistry.addShapelessRecipe(new ItemStack(ModBlocks.maidBeacon),
                Blocks.beacon, Items.emerald, Items.bone, ModItems.kairosClock
        );
        if (Config.easymode) {
            GameRegistry.addRecipe(new ItemStack(ModItems.kairosClock), new Object[]{
                    "ABA",
                    "BCB",
                    "ADA",
                    'A', Blocks.emerald_block,
                    'B', Items.glowstone_dust,
                    'C', Items.clock,
                    'D', Items.ender_eye
            });
            GameRegistry.addRecipe(emptyEmeraldFragment, new Object[]{
                    "ACA",
                    "DBE",
                    "AFA",
                    'A', Items.emerald,
                    'B', Blocks.obsidian,
                    'C', Items.redstone,
                    'D', Items.gunpowder,
                    'E', Items.spider_eye,
                    'F', Items.feather
            });
            GameRegistry.addRecipe(eternalEmeraldFragment, new Object[]{
                    "ABA",
                    "BCB",
                    "ADA",
                    'A', Blocks.emerald_block,
                    'B', Items.ender_eye,
                    'C', fullEmeraldFragment,
                    'D', dragon_egg
            });
        } else {
            GameRegistry.addRecipe(new ItemStack(ModItems.kairosClock), new Object[]{
                    "ABA",
                    "BCB",
                    "ADA",
                    'A', Blocks.emerald_block,
                    'B', Items.ender_eye,
                    'C', Items.clock,
                    'D', Items.nether_star
            });
            GameRegistry.addRecipe(emptyEmeraldFragment, new Object[]{
                    "ACA",
                    "DBE",
                    "AFA",
                    'A', Items.emerald,
                    'B', Items.ender_eye,
                    'C', new ItemStack(Items.skull, 1, 1),
                    'D', Items.blaze_rod,
                    'E', Items.spider_eye,
                    'F', Items.feather
            });
            GameRegistry.addRecipe(eternalEmeraldFragment, new Object[]{
                    "ABA",
                    "BCB",
                    "ADA",
                    'A', Blocks.emerald_block,
                    'B', Items.nether_star,
                    'C', fullEmeraldFragment,
                    'D', dragon_egg
            });
        }
    }
}
