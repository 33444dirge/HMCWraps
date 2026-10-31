package de.skyslycer.hmcwraps.listener;

import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.util.PermissionUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerJoinListener implements Listener {

    private final HMCWrapsPlugin plugin;

    public PlayerJoinListener(HMCWrapsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getFoliaLib().getScheduler().runLaterAsync(() -> plugin.getUpdateChecker().checkPlayer(event.getPlayer()), 5);
        if (!plugin.getConfiguration().getPermissions().getListenerPermissionCheck().isPlayerJoin()) {
            return;
        }
        plugin.getFoliaLib().getScheduler().runAtEntityLater(event.getPlayer(), () -> PermissionUtil.loopThroughInventory(plugin, event.getPlayer(), event.getPlayer().getInventory()), 1);
    }

}