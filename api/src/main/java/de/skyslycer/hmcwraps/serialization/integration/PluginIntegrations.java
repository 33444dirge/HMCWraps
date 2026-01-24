package de.skyslycer.hmcwraps.serialization.integration;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class PluginIntegrations {

    private AuctionHouseIntegration auctionHouse;

    public AuctionHouseIntegration getAuctionHouse() {
        return auctionHouse;
    }

}
