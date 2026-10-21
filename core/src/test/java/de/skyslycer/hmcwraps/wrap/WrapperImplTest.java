package de.skyslycer.hmcwraps.wrap;

import de.skyslycer.hmcwraps.HMCWrapsPlugin;
import de.skyslycer.hmcwraps.serialization.wrap.PhysicalWrap;
import de.skyslycer.hmcwraps.serialization.wrap.Wrap;
import de.skyslycer.hmcwraps.util.PlayerUtil;
import de.skyslycer.hmcwraps.util.WrapNBTUtil;
import de.skyslycer.hmcwraps.wrap.modifiers.WrapModifiers;
import de.skyslycer.hmcwraps.wrap.modifiers.minecraft.ColorModifier;
import de.skyslycer.hmcwraps.wrap.modifiers.minecraft.ModelDataModifier;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.PluginManager;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class WrapperImplTest {

    @Test
    void switchingToWrapWithoutColorRemovesOldColorAndPreservesOriginalModel() {
        try (var fixture = new Fixture()) {
            var original = new ItemState(7, Color.BLUE).item;
            var first = fixture.wrap("first", 100, Color.RED);
            var second = fixture.wrap("second", 200, null);

            var wrapped = fixture.wrapper.setWrap(first, original, false, fixture.player);
            assertEquals(Color.RED, ((LeatherArmorMeta) wrapped.getItemMeta()).getColor());
            var switched = fixture.wrapper.setWrap(second, wrapped, false, fixture.player);
            assertEquals(Color.BLUE, ((LeatherArmorMeta) switched.getItemMeta()).getColor());
            assertEquals(200, switched.getItemMeta().getCustomModelData());

            var restored = fixture.wrapper.removeWrap(switched, fixture.player);
            assertEquals(7, restored.getItemMeta().getCustomModelData());
            assertEquals(Color.BLUE, ((LeatherArmorMeta) restored.getItemMeta()).getColor());
            assertNull(fixture.wrapper.getWrap(restored));
            assertEquals(7, original.getItemMeta().getCustomModelData());
        }
    }

    @Test
    void repeatedSwitchesAndSameWrapReapplicationDoNotChangeOriginalAppearance() {
        try (var fixture = new Fixture()) {
            var original = new ItemState(null, Color.BLUE).item;
            var first = fixture.wrap("first", 100, Color.RED);
            var second = fixture.wrap("second", 200, Color.GREEN);
            var item = original;
            for (int i = 0; i < 5; i++) {
                item = fixture.wrapper.setWrap(first, item, false, fixture.player);
                item = fixture.wrapper.setWrap(first, item, false, fixture.player);
                item = fixture.wrapper.setWrap(second, item, false, fixture.player);
            }
            item = fixture.wrapper.removeWrap(item, fixture.player);
            assertFalse(item.getItemMeta().hasCustomModelData());
            assertEquals(Color.BLUE, ((LeatherArmorMeta) item.getItemMeta()).getColor());
            assertFalse(fixture.wrapper.isPhysical(item));
        }
    }

    @Test
    void switchingPhysicalWrapReturnsOldWrapperOnlyOnce() {
        try (var fixture = new Fixture(); var playerUtil = mockStatic(PlayerUtil.class)) {
            var original = new ItemState(7, Color.BLUE).item;
            var first = fixture.wrap("first", 100, Color.RED);
            var physical = mock(PhysicalWrap.class);
            when(first.getPhysical()).thenReturn(physical);
            when(physical.isKeepAfterUnwrap()).thenReturn(true);
            when(physical.toItem(fixture.plugin, fixture.player)).thenReturn(original);
            var wrapped = fixture.wrapper.setWrap(first, original, true, fixture.player);
            var switched = fixture.wrapper.setWrap(fixture.wrap("second", 200, null), wrapped, false, fixture.player);

            playerUtil.verify(() -> PlayerUtil.give(eq(fixture.player), any(ItemStack.class)), times(1));
            assertFalse(fixture.wrapper.isPhysical(switched));
        }
    }

    @Test
    void customNbtIsRolledBackBeforeRestoringAppearance() {
        try (var fixture = new Fixture()) {
            var first = fixture.wrap("first", 100, Color.RED);
            var wrapped = fixture.wrapper.setWrap(first, new ItemState(7, Color.BLUE).item, false, fixture.player);
            clearInvocations(fixture.modifiers.nbt(), fixture.modifiers.modelData());
            fixture.nbt.clearInvocations();
            fixture.nbt.when(() -> WrapNBTUtil.unwrap(any(ItemStack.class))).thenAnswer(call -> {
                var stack = call.getArgument(0, ItemStack.class);
                // Model restoration has not yet run when NBT rollback starts.
                assertEquals(100, stack.getItemMeta().getCustomModelData());
                return null;
            });
            // Check the first rollback in isolation; subsequent calls happen after restoration.
            fixture.wrapper.removeWrap(wrapped, fixture.player);
            fixture.nbt.verify(() -> WrapNBTUtil.unwrap(any(ItemStack.class)), times(1));
            verify(fixture.modifiers.nbt()).wrap(isNull(), eq(first), any(), eq(fixture.player));
        }
    }

    private static final class Fixture implements AutoCloseable {
        final HMCWrapsPlugin plugin = mock(HMCWrapsPlugin.class, RETURNS_DEEP_STUBS);
        final Player player = mock(Player.class);
        final WrapModifiers modifiers = mock(WrapModifiers.class, RETURNS_DEEP_STUBS);
        final Map<String, Wrap> wraps = new HashMap<>();
        final MockedStatic<Bukkit> bukkit = mockStatic(Bukkit.class);
        final MockedStatic<WrapNBTUtil> nbt = mockStatic(WrapNBTUtil.class);
        final WrapperImpl wrapper;

        Fixture() {
            when(plugin.getName()).thenReturn("HMCWraps");
            when(plugin.getWrapsLoader().getWraps()).thenReturn(wraps);
            when(plugin.getConfiguration().getPreservation().getModelId().isOriginalEnabled()).thenReturn(true);
            when(plugin.getConfiguration().getPreservation().getColor().isOriginalEnabled()).thenReturn(true);
            bukkit.when(Bukkit::getPluginManager).thenReturn(mock(PluginManager.class));
            wrapper = spy(new WrapperImpl(plugin));
            doReturn(modifiers).when(wrapper).getModifiers();
            var modelData = spy(new ModelDataModifier(plugin));
            var color = new ColorModifier(plugin);
            when(modifiers.modelData()).thenReturn(modelData);
            when(modifiers.color()).thenReturn(color);
        }

        Wrap wrap(String id, int model, Color color) {
            var wrap = mock(Wrap.class);
            when(wrap.getUuid()).thenReturn(id);
            when(wrap.getModelId()).thenReturn(model);
            when(wrap.getColor()).thenReturn(color);
            wraps.put(id, wrap);
            return wrap;
        }

        @Override
        public void close() {
            nbt.close();
            bukkit.close();
        }
    }

    /** Small inventory fixture; the actual color/model modifiers and wrapper run unchanged. */
    private static final class ItemState {
        private static final Material MATERIAL = mock(Material.class);
        final ItemStack item = mock(ItemStack.class);
        final LeatherArmorMeta meta = mock(LeatherArmorMeta.class);
        final Map<NamespacedKey, Object> data = new HashMap<>();
        Integer model;
        Color color;

        @SuppressWarnings({"unchecked", "rawtypes"})
        ItemState(Integer model, Color color) {
            this.model = model;
            this.color = color;
            var pdc = mock(PersistentDataContainer.class);
            when(item.getType()).thenReturn(MATERIAL);
            when(item.getItemMeta()).thenReturn(meta);
            when(meta.getPersistentDataContainer()).thenReturn(pdc);
            when(pdc.get(any(NamespacedKey.class), any(PersistentDataType.class)))
                    .thenAnswer(call -> data.get(call.getArgument(0)));
            doAnswer(call -> { data.put(call.getArgument(0), call.getArgument(2)); return null; })
                    .when(pdc).set(any(NamespacedKey.class), any(PersistentDataType.class), any());
            doAnswer(call -> { data.remove(call.getArgument(0)); return null; }).when(pdc).remove(any());
            when(meta.hasCustomModelData()).thenAnswer(call -> this.model != null);
            when(meta.getCustomModelData()).thenAnswer(call -> this.model);
            doAnswer(call -> { this.model = call.getArgument(0); return null; }).when(meta).setCustomModelData(any());
            when(meta.getColor()).thenAnswer(call -> this.color);
            doAnswer(call -> { this.color = call.getArgument(0); return null; }).when(meta).setColor(any());
            when(item.clone()).thenAnswer(call -> {
                var copy = new ItemState(this.model, this.color);
                copy.data.putAll(data);
                return copy.item;
            });
        }
    }
}
