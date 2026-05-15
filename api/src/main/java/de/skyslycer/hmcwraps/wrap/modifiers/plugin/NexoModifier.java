package de.skyslycer.hmcwraps.wrap.modifiers.plugin;

import com.nexomc.nexo.api.NexoItems;
import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import de.skyslycer.hmcwraps.wrap.modifiers.WrapModifier;
import de.tr7zw.changeme.nbtapi.NBT;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

public class NexoModifier implements WrapModifier {

    private static final String NBT_PBV = "PublicBukkitValues";
    private static final String NBT_NEXO = "nexo:id";

    private final HMCWraps plugin;

    private final NamespacedKey originalKey;

    public NexoModifier(HMCWraps plugin) {
        this.plugin = plugin;
        this.originalKey = new NamespacedKey(plugin, "original-nexo-id");
    }

    @Override
    public void wrap(@Nullable Wrap wrap, @Nullable Wrap currentWrap, ItemStack item, Player player) {
        if (wrap != null && currentWrap == null) {
            setOriginalId(item, getNexoNBT(item));
        }
        if (wrap != null) {
            var nexoId = getValidNexoId(wrap);
            if (!wrap.isUseOriginalMechanic()) {
                setNexoNBT(item, nexoId);
            }
        }
        if (wrap == null) {
            setNexoNBT(item, getOriginalId(item));
            setOriginalId(item, null);
        }
    }

    private String getValidNexoId(Wrap wrap) {
        if (wrap.getId() == null || !wrap.getId().startsWith("nexo:")) return null;
        if (!Bukkit.getPluginManager().isPluginEnabled("Nexo")) return null;
        var possibleId = wrap.getId().replace("nexo:", "");
        if (NexoItems.exists(possibleId)) return possibleId;
        return null;
    }

    private void setNexoNBT(ItemStack item, String id) {
        if (id != null) {
            NBT.modify(item, nbt -> {
                var pbv = nbt.getOrCreateCompound(NBT_PBV);
                pbv.setString(NBT_NEXO, id);
            });
        } else {
            NBT.modify(item, nbt -> {
                var pbv = nbt.getCompound(NBT_PBV);
                if (pbv != null) {
                    pbv.removeKey(NBT_NEXO);
                }
            });
        }
    }

    private String getNexoNBT(ItemStack item) {
        var nbt = NBT.readNbt(item);
        var pbv = nbt.getCompound(NBT_PBV);
        if (pbv == null) return null;
        var id = pbv.getString(NBT_NEXO);
        return id.isBlank() ? null : id;
    }

    /**
     * Get the original Nexo ID of the item.
     *
     * @param item The item
     * @return The original Nexo ID
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
     * Get the real Nexo ID of the item. If the item is wrapped, the original id will be returned.
     * If it isn't wrapped, the current id will be returned.
     *
     * @param item The item
     * @return The real Nexo ID
     */
    public String getRealId(ItemStack item) {
        String id = null;
        if (plugin.getWrapper().getWrap(item) != null) {
            id = getOriginalId(item);
        } else if (Bukkit.getPluginManager().isPluginEnabled("Nexo")) {
            var itemId = NexoItems.idFromItem(item);
            if (itemId != null) {
                id = itemId;
            }
        }
        return id;
    }

}
