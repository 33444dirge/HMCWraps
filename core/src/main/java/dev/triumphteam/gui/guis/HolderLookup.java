package dev.triumphteam.gui.guis;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

/**
 * Resolves an inventory holder without forcing a block entity snapshot.
 * <p>
 * The GUI listeners run for every inventory click/drag/open/close on the server. On Paper,
 * {@link Inventory#getHolder()} creates a full block state snapshot (loading and serializing every
 * item) when the inventory belongs to a chest, barrel, shulker box, etc. The listeners only need an
 * {@code instanceof BaseGui} check, and a {@link BaseGui} is never a block entity, so Paper's
 * {@code getHolder(false)} gives the same answer without the snapshot. On platforms without that
 * overload this falls back to {@link Inventory#getHolder()}.
 */
public final class HolderLookup {

    private static final MethodHandle GET_HOLDER_WITHOUT_SNAPSHOT = findGetHolderWithoutSnapshot();

    private HolderLookup() {}

    private static @Nullable MethodHandle findGetHolderWithoutSnapshot() {
        try {
            return MethodHandles.publicLookup().findVirtual(Inventory.class, "getHolder",
                    MethodType.methodType(InventoryHolder.class, boolean.class));
        } catch (NoSuchMethodException | IllegalAccessException ignored) {
            return null;
        }
    }

    public static @Nullable InventoryHolder getHolder(final @NotNull Inventory inventory) {
        if (GET_HOLDER_WITHOUT_SNAPSHOT == null) return inventory.getHolder();
        try {
            return (InventoryHolder) GET_HOLDER_WITHOUT_SNAPSHOT.invokeExact(inventory, false);
        } catch (RuntimeException | Error exception) {
            throw exception;
        } catch (Throwable throwable) {
            throw new IllegalStateException("Failed to resolve inventory holder", throwable);
        }
    }

    public static @Nullable BaseGui getGui(final @NotNull Inventory inventory) {
        final InventoryHolder holder = getHolder(inventory);
        return holder instanceof BaseGui ? (BaseGui) holder : null;
    }
}
