package de.skyslycer.hmcwraps.util;

import de.tr7zw.changeme.nbtapi.*;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.changeme.nbtapi.iface.ReadableNBT;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

public class WrapNBTUtil {

    private static final String SAVE_KEY = "hmcwrapsoriginalnbt";

    /**
     * Applies the NBT to the item and saves the original NBT data in the item.
     *
     * @param stack The item to apply the NBT to
     * @param nbt   The NBT to apply
     */
    public static void wrap(ItemStack stack, String nbt) {
        try {
            new NBTContainer(nbt);
        } catch (NbtApiException e) {
            Bukkit.getLogger().warning("A provided NBT data is invalid in a HMCWraps wrap!");
            return;
        }
        var newNbt = NBT.parseNBT(nbt);
        unwrap(stack);
        NBT.modify(stack, itemNbt -> {
            var difference = itemNbt.getOrCreateCompound(SAVE_KEY);
            difference.clearNBT();
            apply(itemNbt, newNbt, difference);
        });
    }

    /**
     * Restore the original NBT data.
     *
     * @param stack The item to restore the NBT from
     */
    public static void unwrap(ItemStack stack) {
        NBT.modify(stack, itemNbt -> {
            if (itemNbt.hasTag(SAVE_KEY)) {
                var originalNbt = itemNbt.getCompound(SAVE_KEY);
                rollback(originalNbt, itemNbt);
                itemNbt.removeKey(SAVE_KEY);
            }
        });
    }

    private static void apply(ReadWriteNBT original, ReadableNBT config, ReadWriteNBT difference) {
        config.getKeys().forEach(key -> {
            if (key.equals(SAVE_KEY)) {
                return;
            }
            if ((!original.hasTag(key) || original.getType(key) == NBTType.NBTTagCompound) && config.getType(key) == NBTType.NBTTagCompound) {
                var originalCompound = original.getOrCreateCompound(key);
                var configCompound = config.getCompound(key);
                var differenceCompound = difference.getOrCreateCompound(key);
                apply(originalCompound, configCompound, differenceCompound);
            } else {
                if (original.hasTag(key)) {
                    set(original, key, difference);
                } else {
                    difference.setByte(key + ".hmcwraps", (byte) 12);
                }
                set(config, key, original);
            }
        });
    }

    private static void rollback(ReadableNBT source, ReadWriteNBT target) {
        source.getKeys().forEach(key -> {
            if (source.getType(key) == NBTType.NBTTagCompound) {
                var sourceCompound = source.getCompound(key);
                if (target.hasTag(key) && target.getType(key) != NBTType.NBTTagCompound) {
                    target.removeKey(key);
                }
                var targetCompound = target.getOrCreateCompound(key);
                rollback(sourceCompound, targetCompound);
                if (targetCompound.getKeys().isEmpty()) {
                    target.removeKey(key);
                }
            } else {
                if (key.endsWith(".hmcwraps") && source.getType(key) == NBTType.NBTTagByte && source.getByte(key) == 12) {
                    target.removeKey(key.substring(0, key.length() - ".hmcwraps".length()));
                } else {
                    set(source, key, target);
                }
            }
        });
    }

    private static void set(ReadableNBT source, String key, ReadWriteNBT target) {
        // Replace the value rather than appending lists; also preserve compounds,
        // long arrays and values whose NBT type was changed by the wrap.
        var value = new NBTContainer();
        value.mergeCompound(source);
        for (String entry : value.getKeys()) {
            if (!entry.equals(key)) {
                value.removeKey(entry);
            }
        }
        target.removeKey(key);
        target.mergeCompound(value);
    }

}
