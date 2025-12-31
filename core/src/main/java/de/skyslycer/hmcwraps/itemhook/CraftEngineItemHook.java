package de.skyslycer.hmcwraps.itemhook;

import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.inventory.ItemStack;

public class CraftEngineItemHook extends ItemHook {

    @Override
    public String getPrefix() {
        return "craftengine:";
    }

    @Override
    public ItemStack get(String id) {
        var item = CraftEngineItems.byId(Key.of(id));
        return item != null ? item.buildItemStack() : null;
    }

}
