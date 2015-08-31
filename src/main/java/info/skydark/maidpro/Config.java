package info.skydark.maidpro;

import net.minecraftforge.common.config.Configuration;

import java.io.File;

/**
 * Created by skydark on 15-8-28.
 */
public final class Config {
    public static Configuration config;
    public static int manaPerEmerald;
    public static int manaPerLife;
    public static int manaPerHealth;
    public static int manaPerFire;
    public static int manaPerPoison;
    public static int manaPerExplosionLimit;
    public static int manaPerFallingLimit;
    public static int explosionResistancePercent;
    public static int fallingResistancePercent;
    public static int generalResistancePercent;
    public static int enableFireResistance;
    public static int enablePoisonResistance;
    public static int enableExplosionResistance;
    public static int enableFallingResistance;
    public static int enableGeneralResistance;
    public static int tickPerContract;
    public static int tickPerContractCheck;
    public static int healthPerHeal;
    public static int tickPerHealCheck;
    public static boolean easymode;

    public static void init(File configFile) {
        config = new Configuration(configFile);
        config.load();
        manaPerEmerald = loadInt("manaPerEmerald", 40, "one emerald has 40 mana");
        tickPerContractCheck = loadInt("tickPerContractCheck", 6000, "check and try to recontract your maid every 6000 ticks");
        tickPerContract = loadInt("tickPerContract", 20000, "extend 20000 ticks to your maid's contract one time");
        tickPerHealCheck = loadInt("tickPerHealCheck", 40, "check and try to heal your maid every 40 ticks");
        healthPerHeal = loadInt("HealthPerHeal", 2, "heal your maid 2 hp one time");
        manaPerLife = loadInt("manaPerLife", 64, "the Clock of Kairos uses 64 mana to save your maid's life");
        manaPerHealth = loadInt("manaPerHealth", 1, "use 1 mana to heal one hp");
        explosionResistancePercent = loadInt("explosionResistancePercent", 80, "reduce explosion damage by 80%");
        manaPerExplosionLimit = loadInt("manaPerExplosionLimit", 32, "reduce explosion damage with at most 32 mana");
        enableExplosionResistance = loadInt("enableExplosionResistance", 640, "reduce explosion damage only if your maid has at least 640 mana");
        fallingResistancePercent = loadInt("fallingResistancePercent", 80, "reduce falling damage by 80%");
        manaPerFallingLimit = loadInt("manaPerFallingLimit", 20, "reduce falling damage with at most 20 mana");
        enableFallingResistance = loadInt("enableFallingResistance", 160, "reduce falling damage only if your maid has at least 160 mana");
        enableFireResistance = loadInt("enableFireResistance", 320, "your maid spells the magic of fire resistence only if she has at least 320 mana");
        manaPerFire = loadInt("manaPerFire", 16, "your maid uses 16 mana to spell the magic of fire resistence");
        generalResistancePercent = loadInt("generalResistancePercent", 40, "every damage not mentioned can be reduced by 40%");
        enableGeneralResistance = loadInt("enableGeneralResistance", 2560, "every damage not mentioned can be reduced only if your maid has at least 2560 mana");
        manaPerPoison = loadInt("manaPerPoison", 4, "not finished yet due to a concurrency bug");
        enablePoisonResistance = loadInt("enablePoisonResistance", 640, "not finished yet due to a concurrency bug");
        easymode = loadBoolean("easymode", true, "use easier recipe");

        config.save();
    }

    public static int loadInt(String name, int _default, String comment) {
        return config.get(Configuration.CATEGORY_GENERAL, name, _default, comment).getInt(_default);
    }

    public static boolean loadBoolean(String name, boolean _default, String comment) {
        return config.get(Configuration.CATEGORY_GENERAL, name, _default, comment).getBoolean(_default);
    }
}
