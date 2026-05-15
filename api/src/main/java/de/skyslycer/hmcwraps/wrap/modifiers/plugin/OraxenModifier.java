package de.skyslycer.hmcwraps.wrap.modifiers.plugin;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import de.skyslycer.hmcwraps.wrap.modifiers.WrapModifier;
import io.th0rgal.oraxen.api.OraxenItems;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public class OraxenModifier implements WrapModifier {

    private final HMCWraps plugin;

    private final NamespacedKey originalKey;

    public OraxenModifier(HMCWraps plugin) {
        this.plugin = plugin;
        this.originalKey = new NamespacedKey(plugin, "original-oraxen-id");
    }

    @Override
    public void wrap(@Nullable Wrap wrap, @Nullable Wrap currentWrap, ItemStack item, Player player) {
        if (currentWrap != null) {
            setOriginalId(item, getRealId(item));
        }
    }

    /**
     * Get the original Oraxen ID of the item.
     *
     * @param item The item
     * @return The original Oraxen ID
     */
    public String getOriginalId(ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        return container.get(originalKey, PersistentDataType.STRING);
    }

    private void setOriginalId(ItemStack item, String id) {
        var meta = item.getItemMeta();
        if (id != null) {
            meta.getPersistentDataContainer().set(originalKey, PersistentDataType.STRING, id);
        } else {
            meta.getPersistentDataContainer().remove(originalKey);
        }
        item.setItemMeta(meta);
    }

    /**
     * Get the real Oraxen ID of the item. If the item is wrapped, the original ID will be returned.
     * If it isn't wrapped, the current ID will be returned.
     * @param item The item
     * @return The real Oraxen ID
     */
    public String getRealId(ItemStack item) {
        String id = null;
        if (plugin.getWrapper().getWrap(item) != null) {
            id = getOriginalId(item);
        } else if (Bukkit.getPluginManager().isPluginEnabled("Oraxen")) {
            var itemId = OraxenItems.getIdByItem(item);
            if (itemId != null) {
                id = itemId;
            }
        }
        return id;
    }

}
