package info.skydark.maidpro;

import net.minecraft.block.Block;

/**
 * Created by skydark on 15-11-11.
 */
public class ModBlocks {
    public static Block maidBeacon;

    public static void init() {
        maidBeacon = new BlockMaidBeacon();
    }
}
