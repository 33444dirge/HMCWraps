package de.skyslycer.hmcwraps.integration.nexo;

import com.nexomc.nexo.api.events.NexoItemsLoadedEvent;
import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.integration.IntegrationHandler;
import net.brcdev.auctiongui.event.AuctionPreStartEvent;
import org.bukkit.Bukkit;

public class NexoHandler implements IntegrationHandler {

    private final HMCWraps plugin;

    private NexoItemsLoadedListener listener = null;

    public NexoHandler(HMCWraps plugin) {
        this.plugin = plugin;
    }

    @Override
    public void load() {
        listener = new NexoItemsLoadedListener(plugin);
        Bukkit.getPluginManager().registerEvents(listener, plugin);
    }

    @Override
    public void unload() {
        if (listener != null) {
            NexoItemsLoadedEvent.getHandlerList().unregister(listener);
        }
    }

}
