package de.skyslycer.hmcwraps.serialization.integration;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.serialization.Toggleable;
import org.bukkit.inventory.ItemStack;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;

import java.util.List;

@ConfigSerializable
public class AuctionHouseIntegration extends Toggleable {

    private boolean blacklistVirtual;
    private boolean blacklistPhysical;
    private List<String> blacklistedWraps;

    public boolean isBlacklistVirtual() {
        return blacklistVirtual;
    }

    public boolean isBlacklistPhysical() {
        return blacklistPhysical;
    }

    public List<String> getBlacklistedWraps() {
        return blacklistedWraps;
    }

    public boolean isBlacklisted(HMCWraps plugin, ItemStack itemStack) {
        if (!this.isEnabled()) {
            return false;
        }
        var wrap = plugin.getWrapper().getWrap(itemStack);
        if (wrap == null) {
            return false;
        }
        var physical = plugin.getWrapper().isPhysical(itemStack);
        if (this.isBlacklistVirtual() && !physical) {
            return true;
        }
        if (this.isBlacklistPhysical() && physical) {
            return true;
        }
        if (this.getBlacklistedWraps().contains(wrap.getUuid())) {
            return true;
        }
        return false;
    }

}
