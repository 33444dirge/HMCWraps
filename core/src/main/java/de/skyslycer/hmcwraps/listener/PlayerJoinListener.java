package de.skyslycer.hmcwraps.listener;

import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.util.PermissionUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
public class PlayerJoinListener implements Listener {

    private final HMCWrapsPlugin plugin;

    public PlayerJoinListener(HMCWrapsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (!plugin.getConfiguration().getEvents().isPlayerJoin()) {
            return;
        }
        
        // Use configurable delay to help with performance on Folia servers
        int delay = Math.min(plugin.getConfiguration().getEvents().getMaxInventoryCheckDelay(), 1);
        plugin.getFoliaLib().getScheduler().runAtEntityLater(event.getPlayer(), () -> {
            PermissionUtil.loopThroughInventory(plugin, event.getPlayer(), event.getPlayer().getInventory());
            PermissionUtil.loopThroughInventory(plugin, event.getPlayer(), event.getPlayer().getEnderChest());
        }, delay);
        plugin.getFoliaLib().getScheduler().runLaterAsync(() -> plugin.getUpdateChecker().checkPlayer(event.getPlayer()), 5);
    }

}