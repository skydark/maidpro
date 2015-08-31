package info.skydark.maidpro;

import net.minecraft.item.Item;

/**
 * Created by skydark on 15-8-26.
 */
public final class ModItems {
    public static Item kairosClock;
    public static Item emeraldFragment;

    public static void init() {
        kairosClock = new KairosClock();
        emeraldFragment = new EmeraldFragment();
    }
}
