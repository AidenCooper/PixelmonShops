package me.majeek.pixelmonshops.commands;

import com.flowpowered.math.vector.Vector3d;
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
import java.util.HashMap;
import java.util.UUID;

public class SetPos1Command implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(src instanceof Player) {
            Player player = (Player) src;

            HashMap<UUID, Vector3d[]> data = PixelmonShops.getInstance().getDataManager().getSelectionData();
            Vector3d[] array = data.get(player.getUniqueId());

            Vector3d position = player.getPosition();
            UUID uuid = player.getUniqueId();

            if(array == null) {
                data.put(uuid, new Vector3d[]{
                        position,
                        null
                });
            } else {
                if(array[1] == null) {
                    array[0] = position;

                    data.put(uuid, array);
                } else {
                    Vector3d offset = new Vector3d(position.getFloorX() - array[1].getFloorX(), position.getFloorY() - array[1].getFloorY(), position.getFloorZ() - array[1].getFloorZ());
                    double area = (offset.getFloorX() + 1) * (offset.getFloorY() + 1) * (offset.getFloorZ() + 1);

                    if(area > 500 || area < -500) {
                        try {
                            ConfigurationNode node = PixelmonShops.getInstance().getMessagesConfig().getLoader().load();
                            Text text = node.getNode("setPos1", "exceedAreaLimit").getValue(TypeToken.of(Text.class));

                            player.sendMessage(text);
                        } catch (ObjectMappingException | IOException exception) {
                            exception.printStackTrace();
                        }

                        return CommandResult.success();
                    } else {
                        array[0] = position;

                        data.put(uuid, array);
                    }
                }
            }

            PixelmonShops.getInstance().getDataManager().setSelectionData(data);

            try {
                ConfigurationNode node = PixelmonShops.getInstance().getMessagesConfig().getLoader().load();
                Text text = node.getNode("setPos1", "positionSet").getValue(TypeToken.of(Text.class));

                player.sendMessage(text);
            } catch (ObjectMappingException | IOException exception) {
                exception.printStackTrace();
            }
        }

        return CommandResult.success();
    }
}
