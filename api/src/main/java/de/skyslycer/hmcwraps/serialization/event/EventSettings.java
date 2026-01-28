package de.skyslycer.hmcwraps.serialization.event;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class EventSettings {

    private boolean playerPickup = true;
    private boolean playerDrop = true;
    private boolean playerJoin = true;
    private boolean inventoryClick = true;
    private boolean dispenserArmor = true;
    private int maxInventoryCheckDelay = 5; // Maximum delay in ticks before checking inventory

    public boolean isPlayerPickup() {
        return playerPickup;
    }

    public boolean isPlayerDrop() {
        return playerDrop;
    }

    public boolean isPlayerJoin() {
        return playerJoin;
    }

    public boolean isInventoryClick() {
        return inventoryClick;
    }

    public boolean isDispenserArmor() {
        return dispenserArmor;
    }

    public int getMaxInventoryCheckDelay() {
        return maxInventoryCheckDelay;
    }
}