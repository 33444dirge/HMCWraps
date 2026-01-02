package de.skyslycer.hmcwraps.transformation;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigFileTransformations extends FileTransformations {

    public ConfigFileTransformations() {
        setLatest(2);
        addUpdateMethod(0, this::zeroToOne);
        addUpdateMethod(1, this::oneToTwo);
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
        config = config + "\nconfig: 2";
        Files.writeString(path, config);
    }

}
