package de.skyslycer.hmcwraps.serialization.wrapping;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;

@ConfigSerializable
public class WrappingSettings {

    private RewrapSettings rewrap;
    private boolean makeWrappersUnstackable;

    public RewrapSettings getRewrap() {
        return rewrap;
    }

    public boolean isMakeWrappersUnstackable() {
        return makeWrappersUnstackable;
    }

}
