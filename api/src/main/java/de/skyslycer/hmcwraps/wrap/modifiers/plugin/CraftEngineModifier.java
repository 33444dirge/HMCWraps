package de.skyslycer.hmcwraps.wrap.modifiers.plugin;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import de.skyslycer.hmcwraps.wrap.modifiers.WrapModifier;
import io.th0rgal.oraxen.api.OraxenItems;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public class CraftEngineModifier implements WrapModifier {

    private final HMCWraps plugin;

    private final NamespacedKey originalCraftEngineKey;

    public CraftEngineModifier(HMCWraps plugin) {
        this.plugin = plugin;
        this.originalCraftEngineKey = new NamespacedKey(plugin, "original-craft-engine-id");
    }

    @Override
    public void wrap(@Nullable Wrap wrap, @Nullable Wrap currentWrap, ItemStack item, Player player) {
        if (currentWrap != null) {
            setOriginalCraftEngineId(item, getRealCraftEngineId(item));
        }
    }

    /**
     * Get the original CraftEngine ID of the item.
     *
     * @param item The item
     * @return The original CraftEngine ID
     */
    public String getOriginalCraftEngineId(ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        return container.get(originalCraftEngineKey, PersistentDataType.STRING);
    }

    private void setOriginalCraftEngineId(ItemStack item, String oraxenId) {
        var meta = item.getItemMeta();
        if (oraxenId != null) {
            meta.getPersistentDataContainer().set(originalCraftEngineKey, PersistentDataType.STRING, oraxenId);
        } else {
            meta.getPersistentDataContainer().remove(originalCraftEngineKey);
        }
        item.setItemMeta(meta);
    }

    /**
     * Get the real CraftEngine ID of the item. If the item is wrapped, the original ID will be returned.
     * If it isn't wrapped, the current ID will be returned.
     *
     * @param item The item
     * @return The real CraftEngine ID
     */
    public String getRealCraftEngineId(ItemStack item) {
        String craftEngineId = null;
        if (plugin.getWrapper().getWrap(item) != null) {
            craftEngineId = getOriginalCraftEngineId(item);
        } else if (Bukkit.getPluginManager().isPluginEnabled("CraftEngine")) {
            var id = CraftEngineItems.getCustomItemId(item);
            if (id != null) {
                craftEngineId = id.toString();
            }
        }
        return craftEngineId;
    }

}
