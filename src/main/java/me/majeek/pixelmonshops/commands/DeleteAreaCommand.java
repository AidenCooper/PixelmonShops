package me.majeek.pixelmonshops.commands;

import com.google.common.reflect.TypeToken;
import me.majeek.pixelmonshops.PixelmonShops;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

import java.io.IOException;

public class DeleteAreaCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(src instanceof Player) {
            Player player = (Player) src;

            ConfigurationNode target = PixelmonShops.getInstance().getDataManager().getArea(player.getPosition());
            if(target != null) {
                try {
                    ConfigurationNode node = PixelmonShops.getInstance().getShopDataConfig().getLoader().load();
                    node.removeChild(target.getKey());
                    PixelmonShops.getInstance().getShopDataConfig().getLoader().save(node);

                    ConfigurationNode messages = PixelmonShops.getInstance().getMessagesConfig().getLoader().load();
                    Text text = messages.getNode("deleteArea", "deleted").getValue(TypeToken.of(Text.class));

                    player.sendMessage(text);
                } catch (ObjectMappingException | IOException exception) {
                    exception.printStackTrace();
                }
            } else {
                try {
                    ConfigurationNode node = PixelmonShops.getInstance().getMessagesConfig().getLoader().load();
                    Text text = node.getNode("deleteArea", "notInArea").getValue(TypeToken.of(Text.class));

                    player.sendMessage(text);
                } catch (ObjectMappingException | IOException exception) {
                    exception.printStackTrace();
                }
            }
        }
        return CommandResult.success();
    }
}
