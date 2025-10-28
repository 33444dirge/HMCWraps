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

    private final NamespacedKey originalNexoKey;

    public NexoModifier(HMCWraps plugin) {
        this.plugin = plugin;
        this.originalNexoKey = new NamespacedKey(plugin, "original-nexo-id");
    }

    @Override
    public void wrap(@Nullable Wrap wrap, @Nullable Wrap currentWrap, ItemStack item, Player player) {
        if (wrap != null && currentWrap == null) {
            setOriginalNexoId(item, getNexoNBT(item));
            setNexoNBT(item, null);
        }
        if (wrap == null) {
            setNexoNBT(item, getOriginalNexoId(item));
            setOriginalNexoId(item, null);
        }
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
    public String getOriginalNexoId(ItemStack item) {
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        return container.get(originalNexoKey, PersistentDataType.STRING);
    }

    private void setOriginalNexoId(ItemStack item, String nexoId) {
        var meta = item.getItemMeta();
        if (nexoId != null) {
            meta.getPersistentDataContainer().set(originalNexoKey, PersistentDataType.STRING, nexoId);
        } else {
            meta.getPersistentDataContainer().remove(originalNexoKey);
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
    public String getRealNexoId(ItemStack item) {
        String nexoId = null;
        if (plugin.getWrapper().getWrap(item) != null) {
            nexoId = getOriginalNexoId(item);
        } else if (Bukkit.getPluginManager().isPluginEnabled("Nexo")) {
            var id = NexoItems.idFromItem(item);
            if (id != null) {
                nexoId = id;
            }
        }
        return nexoId;
    }

}
