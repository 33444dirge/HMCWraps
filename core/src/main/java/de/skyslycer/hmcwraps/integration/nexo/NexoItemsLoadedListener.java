package de.skyslycer.hmcwraps.integration.nexo;

import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.api.events.NexoItemsLoadedEvent;
import com.nexomc.nexo.items.UpdateCallback;
import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.wrap.modifiers.plugin.NexoModifier;
import net.kyori.adventure.key.Key;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

public class NexoItemsLoadedListener implements Listener {

    private final HMCWraps plugin;

    public NexoItemsLoadedListener(HMCWraps plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onNexoItemsLoaded(NexoItemsLoadedEvent event) {
        NexoItems.registerUpdateCallback(
                Key.key("hmcwraps:nexo-modifier"),
                new UpdateCallback() {
                    @Override
                    public @Nullable ItemStack preUpdate(@NonNull ItemStack itemStack) {
                        if (plugin.getWrapper().getWrap(itemStack) != null) {
                            return null;
                        } else {
                            return itemStack;
                        }
                    }

                    @Override
                    public @NonNull ItemStack postUpdate(@NonNull ItemStack itemStack) {
                        // do nothing
                        return itemStack;
                    }
                }
        );
    }

}
