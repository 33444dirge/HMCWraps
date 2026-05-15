package de.skyslycer.hmcwraps.wrap.modifiers.plugin;

import com.ssomar.score.api.executableitems.ExecutableItemsAPI;
import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import de.skyslycer.hmcwraps.wrap.modifiers.WrapModifier;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public class MMOItemsModifier implements WrapModifier {

    private final HMCWraps plugin;

    private final NamespacedKey originalKey;

    public MMOItemsModifier(HMCWraps plugin) {
        this.plugin = plugin;
        this.originalKey = new NamespacedKey(plugin, "original-mmoitems-id");
    }

    @Override
    public void wrap(@Nullable Wrap wrap, @Nullable Wrap currentWrap, ItemStack item, Player player) {
        if (wrap != null && currentWrap == null) {
            setOriginalId(item, getRealId(item));
        }
        if (wrap == null) {
            setOriginalId(item, null);
        }
    }

    /**
     * Get the original Mythic ID of the item.
     *
     * @param item The item
     * @return The original mythic ID
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
     * Get the real Mythic ID of the item. If the item is wrapped, the original ID will be returned.
     * If it isn't wrapped, the current ID will be returned.
     *
     * @param item The item
     * @return The real mythic ID
     */
    public String getRealId(ItemStack item) {
        String id = null;
        if (plugin.getWrapper().getWrap(item) != null) {
            id = getOriginalId(item);
        } else if (Bukkit.getPluginManager().isPluginEnabled("MMOItems") || true) {
            String itemId = NBT.get(item, nbt -> (String) nbt.getString("MMOITEMS_ITEM_ID"));
            if (itemId != null) {
                id = itemId;
            }
        }
        return id;
    }

}
