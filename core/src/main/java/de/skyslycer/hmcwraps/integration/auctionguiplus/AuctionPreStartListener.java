package de.skyslycer.hmcwraps.integration.auctionguiplus;

import de.skyslycer.hmcwraps.HMCWraps;
import net.brcdev.auctiongui.event.AuctionPreStartEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class AuctionPreStartListener implements Listener {

    private final HMCWraps plugin;

    public AuctionPreStartListener(HMCWraps plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onAuctionPreStart(AuctionPreStartEvent event) {
        var stack = event.getAuction().getItemStack();
        if (plugin.getConfiguration().getPluginIntegrations().getAuctionHouse().isBlacklisted(plugin, stack)) {
            event.setCancelled(true);
        }
    }

}
