package de.skyslycer.hmcwraps.commands.annotation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import revxrsal.commands.Lamp;
import revxrsal.commands.annotation.list.AnnotationList;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.command.CommandPermission;

public class AnyPermissionFactory implements CommandPermission.Factory<BukkitCommandActor> {

    @Nullable
    @Override
    public CommandPermission<BukkitCommandActor> create(@NotNull AnnotationList annotations, @NotNull Lamp<BukkitCommandActor> lamp) {
        AnyPermission anyPermission = annotations.get(AnyPermission.class);
        if (anyPermission == null) return null;
        var permissions = anyPermission.value();
        return actor -> {
            var sender = actor.sender();
            for (var permission : permissions) {
                if (sender.hasPermission(permission)) return true;
            }
            return false;
        };
    }

}
