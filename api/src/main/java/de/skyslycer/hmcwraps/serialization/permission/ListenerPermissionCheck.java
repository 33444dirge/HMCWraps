package de.skyslycer.hmcwraps.serialization.permission;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class ListenerPermissionCheck {

    private boolean playerPickup = false;
    private boolean inventoryClick = true;
    private boolean playerDrop = true;
    private boolean playerInteract = true;
    private boolean playerOffHandSwitch = true;
    private boolean playerJoin = true;
    private boolean dispenserArmor = true;

    public boolean isPlayerPickup() {
        return playerPickup;
    }

    public boolean isInventoryClick() {
        return inventoryClick;
    }

    public boolean isPlayerDrop() {
        return playerDrop;
    }

    public boolean isPlayerInteract() {
        return playerInteract;
    }

    public boolean isPlayerOffHandSwitch() {
        return playerOffHandSwitch;
    }

    public boolean isPlayerJoin() {
        return playerJoin;
    }

    public boolean isDispenserArmor() {
        return dispenserArmor;
    }

}