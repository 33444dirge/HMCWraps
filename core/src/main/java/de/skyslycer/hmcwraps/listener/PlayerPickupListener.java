package de.skyslycer.hmcwraps.listener;

import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.util.PermissionUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;

public class PlayerPickupListener implements Listener {

    private final HMCWrapsPlugin plugin;

    public PlayerPickupListener(HMCWrapsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onItemPickup(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player) || !plugin.getConfiguration().getEvents().isPlayerPickup()) {
            return;
        }
        
        // Use a longer delay to help with performance on Folia servers
        // This reduces the frequency of inventory checks when picking up multiple items quickly
        int delay = Math.max(plugin.getConfiguration().getEvents().getMaxInventoryCheckDelay(), 2);
        plugin.getFoliaLib().getScheduler().runAtEntityLater(player, () -> {
            // Only check inventory if player is still online to avoid errors
            if (player.isOnline()) {
                PermissionUtil.loopThroughInventory(plugin, player, player.getInventory());
            }
        }, delay);
    }

}