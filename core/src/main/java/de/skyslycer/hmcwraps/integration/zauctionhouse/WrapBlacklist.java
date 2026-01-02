package de.skyslycer.hmcwraps.integration.zauctionhouse;

import de.skyslycer.hmcwraps.HMCWraps;
import fr.maxlego08.zauctionhouse.api.blacklist.ItemChecker;
import org.bukkit.inventory.ItemStack;

public class WrapBlacklist implements ItemChecker {

    private final HMCWraps plugin;

    public WrapBlacklist(HMCWraps plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "Wrap";
    }

    @Override
    public boolean checkItemStack(ItemStack itemStack) {
        return plugin.getConfiguration().getPluginIntegrations().getAuctionHouse().isBlacklisted(plugin, itemStack);
    }

}
