package de.skyslycer.hmcwraps.serialization.item;

import de.skyslycer.hmcwraps.HMCWraps;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class PhysicalUnwrapper extends SerializableItem {

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
