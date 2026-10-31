package de.skyslycer.hmcwraps.transformation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigFileTransformations extends FileTransformations {

    public ConfigFileTransformations() {
        setLatest(3);
        addUpdateMethod(0, this::zeroToOne);
        addUpdateMethod(1, this::oneToTwo);
        addUpdateMethod(2, this::twoToThree);
    }

    private void zeroToOne(Path path) throws IOException {
        var config = Files.readString(path);
        config = config.replace("        nbt: ", "        wrap-nbt: ");
        config = config.replace("permission-settings:", "permissions:");
        config = config + "\nconfig: 1";
        Files.writeString(path, config);
    }

    private void oneToTwo(Path path) throws IOException {
        var config = Files.readString(path);
        config = config.replace("  z-auction-house:", "  auction-house:");
        config = config.replace("  # zAuctionHouse integration", "  # zAuctionHouse and AuctionGUI+ integration");
        config = config.replace("config: 1", "config: 2");
        Files.writeString(path, config);
    }

    private void twoToThree(Path path) throws IOException {
        var config = Files.readString(path);
        var listenerPermissionCheck = "\n  # Settings for permission checks in event listeners\n  # Disabling these can improve server performance, especially the player-pickup listener\n  listener-permission-check:\n    # If permission checks should be performed when a player picks up items\n    # This listener iterates through the player's inventory, which can cause lag on large servers\n    player-pickup: false\n    # If permission checks should be performed when a player clicks in inventory\n    inventory-click: true\n    # If permission checks should be performed when a player drops an item\n    player-drop: true\n    # If permission checks should be performed when a player interacts with an item\n    player-interact: true\n    # If permission checks should be performed when a player switches items between hands\n    player-off-hand-switch: true\n    # If permission checks should be performed when a player joins the server\n    player-join: true\n    # If permission checks should be performed when a dispenser equips armor to a player\n    dispenser-armor: true\n";
        config = config.replace("  inventory-check-interval: 10", "  inventory-check-interval: 10" + listenerPermissionCheck);
        config = config.replace("config: 2", "config: 3");
        Files.writeString(path, config);
    }

}