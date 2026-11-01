package de.skyslycer.hmcwraps.wrap;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.serialization.Config;
import de.skyslycer.hmcwraps.serialization.preservation.PreservationSettings;
import de.skyslycer.hmcwraps.serialization.preservation.ValuePreservationSettings;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import de.skyslycer.hmcwraps.wrap.modifiers.minecraft.LoreModifier;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class LoreModifierTest {
    static final NamespacedKey ORIGINAL = new NamespacedKey("hmcwraps", "original-lore");
    static final NamespacedKey ADDED = new NamespacedKey("hmcwraps", "added-lore");

    @Test void legacyFallbackRestoresEcoPrefix() {
        var f = new Fixture();
        f.data.put(ORIGINAL, "&z&aOLD_ENCHANT");
        f.modifier.wrap(null, new Wrap(), f.item, null);
        verify(f.meta).setLore(List.of("\u00a7z\u00a7aOLD_ENCHANT"));
        assertFalse(f.data.containsKey(ORIGINAL));
    }

    @Test void newBackupRoundTripsRawStringsAndLineBoundaries() throws Exception {
        var f = new Fixture();
        var lines = List.of("\u00a7z\u00a7aENCHANT", "literal &a &z A&B ;!;", "", "\u00a7x\u00a71\u00a72\u00a73\u00a74\u00a75\u00a76RGB", "");
        Method save = LoreModifier.class.getDeclaredMethod("setOriginalLore", ItemStack.class, List.class, boolean.class);
        save.setAccessible(true);
        save.invoke(f.modifier, f.item, lines, true);
        assertEquals(lines, f.modifier.getOriginalLore(f.item));
    }

    @Test void addedLoreRoundTripsLiteralAmpersandsAndSeparators() throws Exception {
        var f = new Fixture();
        var lines = List.of("\u00a7zFOOTER", "&a ;!;", "");
        Method save = LoreModifier.class.getDeclaredMethod("setAddedLore", ItemStack.class, List.class);
        save.setAccessible(true);
        save.invoke(f.modifier, f.item, lines);
        assertEquals(lines, f.modifier.getAddedLore(f.item));
    }

    @Test void normalRemovalKeepsUpdatedCurrentLore() {
        var f = new Fixture();
        f.data.put(ORIGINAL, "&z&aOLD_ENCHANT");
        f.data.put(ADDED, "FOOTER");
        when(f.meta.getLore()).thenReturn(List.of("CURRENT", "FOOTER"));
        f.modifier.wrap(null, new Wrap(), f.item, null);
        verify(f.meta).setLore(List.of("CURRENT"));
        assertTrue(f.data.isEmpty());
    }

    static class Fixture {
        final Map<NamespacedKey, Object> data = new HashMap<>();
        final HMCWraps plugin = mock(HMCWraps.class);
        final ItemStack item = mock(ItemStack.class);
        final ItemMeta meta = mock(ItemMeta.class);
        final LoreModifier modifier;

        Fixture() {
            when(plugin.getName()).thenReturn("HMCWraps");
            var config = mock(Config.class);
            var preservation = mock(PreservationSettings.class);
            @SuppressWarnings("unchecked") ValuePreservationSettings<List<String>> settings = mock(ValuePreservationSettings.class);
            when(plugin.getConfiguration()).thenReturn(config);
            when(config.getPreservation()).thenReturn(preservation);
            when(preservation.getLore()).thenReturn(settings);
            when(settings.isOriginalEnabled()).thenReturn(true);
            when(item.getType()).thenReturn(Material.NETHERITE_HELMET);
            when(item.getItemMeta()).thenReturn(meta);
            var pdc = mock(PersistentDataContainer.class);
            when(meta.getPersistentDataContainer()).thenReturn(pdc);
            when(pdc.get(any(), any())).thenAnswer(c -> data.get(c.getArgument(0)));
            when(pdc.has(any(), any())).thenAnswer(c -> {
                var value = data.get(c.getArgument(0));
                var type = c.getArgument(1, PersistentDataType.class);
                return value != null && type.getPrimitiveType().isInstance(value);
            });
            doAnswer(c -> { data.put(c.getArgument(0), c.getArgument(2)); return null; }).when(pdc).set(any(), any(), any());
            doAnswer(c -> { data.remove(c.getArgument(0)); return null; }).when(pdc).remove(any());
            modifier = new LoreModifier(plugin);
        }
    }
}
