package de.skyslycer.hmcwraps.listener;

import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.util.PermissionUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseArmorEvent;

public class DispenserArmorListener implements Listener {

    private final HMCWrapsPlugin plugin;

    public DispenserArmorListener(HMCWrapsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onArmorEquip(BlockDispenseArmorEvent event) {
        if (!(event.getTargetEntity() instanceof Player player)){
            return;
        }
        
        if (!plugin.getConfiguration().getEvents().isDispenserArmor()) {
            return;
        }
        
        // Use configurable delay to help with performance on Folia servers
        int delay = Math.min(plugin.getConfiguration().getEvents().getMaxInventoryCheckDelay(), 1);
        plugin.getFoliaLib().getScheduler().runAtEntityLater(player, () -> PermissionUtil.loopThroughInventory(plugin, player, player.getInventory()), delay);
    }

}