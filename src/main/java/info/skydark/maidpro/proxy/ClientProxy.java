package info.skydark.maidpro.proxy;

import cpw.mods.fml.client.registry.ClientRegistry;
import info.skydark.maidpro.TileEntityMaidBeacon;
import net.minecraft.client.renderer.tileentity.TileEntityBeaconRenderer;

/**
 * Created by skydark on 16-1-3.
 */
public class ClientProxy extends CommonProxy {
    @Override
    public void registerRender() {
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityMaidBeacon.class, new TileEntityBeaconRenderer());
    }
}
