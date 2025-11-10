package de.skyslycer.hmcwraps.serialization.wrap;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.serialization.item.SerializableItem;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.HashMap;
import java.util.List;

@ConfigSerializable
public class PhysicalWrap extends SerializableItem {

    private boolean keepAfterUnwrap;
    private @Nullable HashMap<String, HashMap<String, List<String>>> actions;

    public PhysicalWrap(String id, String name, @Nullable Boolean glow,
                        @Nullable List<String> lore, @Nullable Integer modelId) {
        super(id, name, glow, lore, null, modelId, null, null, null);
        this.keepAfterUnwrap = true;
    }

    public PhysicalWrap() {
    }

    public boolean isKeepAfterUnwrap() {
        return keepAfterUnwrap;
    }

    public @Nullable HashMap<String, HashMap<String, List<String>>> getActions() {
        return actions;
    }

    @Override
    public ItemStack toItem(HMCWraps plugin, Player player) {
        var item = super.toItem(plugin, player);
        var meta = item.getItemMeta();
        if (meta != null && plugin.getConfiguration().getWrapping().isMakeWrappersUnstackable()) {
            meta.setMaxStackSize(1);
            item.setItemMeta(meta);
        }
        return item;
    }

}
