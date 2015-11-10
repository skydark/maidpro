package info.skydark.maidpro;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.MinecraftForge;


@Mod(modid = MaidPro.MODID, version = MaidPro.VERSION, dependencies="required-after:lmmx")
public class MaidPro
{
    public static final String MODID = "${MOD_ID}";
    public static final String VERSION = "${MOD_VERSION}";

    @EventHandler
    public void preinit(FMLPreInitializationEvent event)
    {
        Config.init(event.getSuggestedConfigurationFile());
        ModItems.init();
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        Recipes.init();
    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new EventHook());
        if (Config.golemlove) {
            MinecraftForge.EVENT_BUS.register(new EventHookIronGolem());
        }
    }
}
