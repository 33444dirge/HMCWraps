package de.skyslycer.hmcwraps.integration.auctionguiplus;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.integration.IntegrationHandler;
import net.brcdev.auctiongui.event.AuctionPreStartEvent;
import org.bukkit.Bukkit;

public class AuctionGuiPlusHandler implements IntegrationHandler {

    private final HMCWraps plugin;

    private AuctionPreStartListener listener = null;

    public AuctionGuiPlusHandler(HMCWraps plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load() {
        if (!plugin.getConfiguration().getPluginIntegrations().getAuctionHouse().isEnabled()) {
            return;
        }
        listener = new AuctionPreStartListener(plugin);
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    @Override
    public void unload() {
        if (listener != null) {
            AuctionPreStartEvent.getHandlerList().unregister(listener);
        }
    }

}
