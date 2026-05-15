package de.skyslycer.hmcwraps.listener;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.util.PlayerUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerDeathListener implements Listener {

    private final HMCWraps plugin;
    private final NamespacedKey respawnWrapsKey;
    private final NamespacedKey respawnUnwrappersKey;

    public PlayerDeathListener(HMCWraps plugin) {
        this.plugin = plugin;
        this.respawnWrapsKey = new NamespacedKey(plugin, "respawn-wraps");
        this.respawnUnwrappersKey = new NamespacedKey(plugin, "respawn-unwrappers");
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (!plugin.getConfiguration().getWrapping().isGiveWrapperAfterDeath()) {
            return;
        }
        var wraps = new HashMap<String, Integer>(); // Wrap ID, Amount
        var unwrappers = 0;
        var drops = event.getDrops();
        var iterator = drops.iterator();
        var unwrappedDrops = new ArrayList<ItemStack>();

        while (iterator.hasNext()) {
            var item = iterator.next();
            if (item == null || item.getType().isAir()) {
                continue;
            }

            // Save physical wrappers
            var physicalWrapper = plugin.getWrapper().getPhysicalWrapper(item);
            if (physicalWrapper != null) {
                wraps.merge(physicalWrapper, item.getAmount(), Integer::sum);
                iterator.remove();
                continue;
            }

            // Save physical unwrappers
            if (plugin.getWrapper().isPhysicalUnwrapper(item)) {
                unwrappers += item.getAmount();
                iterator.remove();
                continue;
            }

            // Save physically wrapped items (only the wraps and unwrap the items)
            var wrap = plugin.getWrapper().getWrap(item);
            if (wrap == null || wrap.getPhysical() == null || !plugin.getWrapper().isPhysical(item)) {
                continue;
            }

            wraps.merge(wrap.getUuid(), item.getAmount(), Integer::sum);
            iterator.remove();

            var nonPhysicalItem = plugin.getWrapper().setPhysical(item, false); // otherwise the player would receive the physical wrapper back
            var unwrappedItem = plugin.getWrapper().setWrap(null, nonPhysicalItem, false, event.getEntity());
            unwrappedDrops.add(unwrappedItem);
        }

        drops.addAll(unwrappedDrops);

        if (!wraps.isEmpty()) {
            event.getEntity().getPersistentDataContainer().set(respawnWrapsKey, PersistentDataType.LIST.strings(), serializeWraps(wraps));
        } else {
            event.getEntity().getPersistentDataContainer().remove(respawnWrapsKey);
        }
        if (unwrappers > 0) {
            event.getEntity().getPersistentDataContainer().set(respawnUnwrappersKey, PersistentDataType.INTEGER, unwrappers);
        } else {
            event.getEntity().getPersistentDataContainer().remove(respawnUnwrappersKey);
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        var serializedWraps = event.getPlayer().getPersistentDataContainer().get(respawnWrapsKey, PersistentDataType.LIST.strings());
        var unwrappers = event.getPlayer().getPersistentDataContainer().get(respawnUnwrappersKey, PersistentDataType.INTEGER);
        if ((serializedWraps == null || serializedWraps.isEmpty()) && (unwrappers == null || unwrappers <= 0)) {
            return;
        }
        var wraps = serializedWraps == null ? new HashMap<String, Integer>() : deserializeWraps(serializedWraps);
        var unwrapperAmount = unwrappers == null ? 0 : unwrappers;
        plugin.getFoliaLib().getScheduler().runAtEntityLater(event.getPlayer(), () -> {
            for (var entry : wraps.entrySet()) {
                var wrap = plugin.getWrapsLoader().getWraps().get(entry.getKey());
                if (wrap == null || wrap.getPhysical() == null) {
                    continue;
                }
                for (var i = 0; i < entry.getValue(); i++) {
                    var wrapper = wrap.getPhysical().toItem(plugin, event.getPlayer());
                    PlayerUtil.give(event.getPlayer(), plugin.getWrapper().setPhysicalWrapper(wrapper, wrap));
                }
            }
            for (var i = 0; i < unwrapperAmount; i++) {
                var unwrapper = plugin.getConfiguration().getUnwrapper().toItem(plugin, event.getPlayer());
                PlayerUtil.give(event.getPlayer(), plugin.getWrapper().setPhysicalUnwrapper(unwrapper));
            }
            event.getPlayer().getPersistentDataContainer().remove(respawnWrapsKey);
            event.getPlayer().getPersistentDataContainer().remove(respawnUnwrappersKey);
        }, 1L);
    }

    private List<String> serializeWraps(HashMap<String, Integer> wraps) {
        var serialized = new ArrayList<String>();
        for (var entry : wraps.entrySet()) {
            serialized.add(entry.getKey());
            serialized.add(String.valueOf(entry.getValue()));
        }
        return serialized;
    }

    private HashMap<String, Integer> deserializeWraps(List<String> serialized) {
        var wraps = new HashMap<String, Integer>();
        for (var i = 0; i + 1 < serialized.size(); i += 2) {
            try {
                wraps.merge(serialized.get(i), Integer.parseInt(serialized.get(i + 1)), Integer::sum);
            } catch (NumberFormatException ignored) { }
        }
        return wraps;
    }

}
