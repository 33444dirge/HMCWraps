package de.skyslycer.hmcwraps.commands.exception;

import de.skyslycer.hmcwraps.HMCWraps;
import de.skyslycer.hmcwraps.messages.Messages;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.jetbrains.annotations.NotNull;
import revxrsal.commands.bukkit.actor.BukkitCommandActor;
import revxrsal.commands.bukkit.exception.*;
import revxrsal.commands.exception.MissingArgumentException;
import revxrsal.commands.exception.NoPermissionException;
import revxrsal.commands.node.ParameterNode;

public class CustomExceptionHandler extends BukkitExceptionHandler {

    HMCWraps plugin;

    public CustomExceptionHandler(HMCWraps plugin) {
        this.plugin = plugin;
    }

    @HandleException
    public void onInvalidWrap(InvalidWrapException exception, BukkitCommandActor actor) {
        plugin.getMessageHandler().send(actor.sender(), Messages.COMMAND_INVALID_WRAP, Placeholder.parsed("uuid", exception.getMessage()));
    }

    @Override
    public void onInvalidWorld(InvalidWorldException exception, BukkitCommandActor actor) {
        plugin.getMessageHandler().send(actor.sender(), Messages.COMMAND_INVALID_WORLD, Placeholder.parsed("world", exception.input()));
    }

    @Override
    public void onSenderNotPlayer(SenderNotPlayerException e, BukkitCommandActor actor) {
        plugin.getMessageHandler().send(actor.sender(), Messages.COMMAND_PLAYER_ONLY);
    }

    @Override
    public void onNoPermission(@NotNull NoPermissionException e, @NotNull BukkitCommandActor actor) {
        plugin.getMessageHandler().send(actor.sender(), Messages.NO_PERMISSION);
    }

    @Override
    public void onMissingArgument(@NotNull MissingArgumentException e, @NotNull BukkitCommandActor actor, @NotNull ParameterNode<BukkitCommandActor, ?> parameter) {
        plugin.getMessageHandler().send(actor.sender(), Messages.COMMAND_MISSING_ARGUMENT,
                Placeholder.parsed("argument", parameter.name()));
    }

    @Override
    public void onInvalidPlayer(InvalidPlayerException e, BukkitCommandActor actor) {
        plugin.getMessageHandler().send(actor.sender(), Messages.COMMAND_INVALID_PLAYER, Placeholder.parsed("player", e.input()));
    }

    @Override
    public void onEmptyEntitySelector(EmptyEntitySelectorException e, BukkitCommandActor actor) {
        plugin.getMessageHandler().send(actor.sender(), Messages.COMMAND_INVALID_PLAYER, Placeholder.parsed("player", e.input()));
    }
}
