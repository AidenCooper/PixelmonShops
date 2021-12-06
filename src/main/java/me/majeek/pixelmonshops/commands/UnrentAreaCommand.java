package me.majeek.pixelmonshops.commands;

import com.google.common.reflect.TypeToken;
import de.waterdu.aquagts.atlantis.ui.api.AquaUI;
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
import java.util.UUID;

public class UnrentAreaCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(src instanceof Player) {
            Player player = (Player) src;

            try {
                ConfigurationNode node = PixelmonShops.getInstance().getDataManager().getArea(player.getPosition());
                ConfigurationNode messages = PixelmonShops.getInstance().getMessagesConfig().getLoader().load();

                UUID owner = node.getNode("owner").getValue(TypeToken.of(UUID.class));
                if(owner == null) {
                    Text text = messages.getNode("unrentArea", "noArea").getValue(TypeToken.of(Text.class));
                    player.sendMessage(text);
                } else {
                    if(owner.toString().equals(player.getUniqueId().toString())) {
                        node.getNode("owner").setValue(null);
                        node.getNode("nextPayment").setValue(null);
                        PixelmonShops.getInstance().getShopDataConfig().getLoader().save(node.getParent());

                        Text text = messages.getNode("unrentArea", "unrented").getValue(TypeToken.of(Text.class));
                        player.sendMessage(text);
                    } else {
                        Text text = messages.getNode("unrentArea", "dontOwn").getValue(TypeToken.of(Text.class));
                        player.sendMessage(text);
                    }
                }

            } catch (IOException | ObjectMappingException exception) {
                exception.printStackTrace();
            }
        }
        return CommandResult.success();
    }
}
