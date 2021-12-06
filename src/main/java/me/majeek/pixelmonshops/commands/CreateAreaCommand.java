package me.majeek.pixelmonshops.commands;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.reflect.TypeToken;
import me.majeek.pixelmonshops.PixelmonShops;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
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

public class CreateAreaCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(src instanceof Player) {
            String[] nodeLocation = new String[2];
            nodeLocation[0] = "createArea";

            Player player = (Player) src;

            Vector3d[] positions = PixelmonShops.getInstance().getDataManager().getSelectionData().get(player.getUniqueId());

            if(positions == null) {
                nodeLocation[1] = "noPositions";
            } else {
                if(positions[0] == null) {
                    nodeLocation[1] = "noPosition2";
                } else if(positions[1] == null) {
                    nodeLocation[1] = "noPosition1";
                } else {
                    nodeLocation[1] = "created";

                    for(Vector3d element : PixelmonShops.getInstance().getDataManager().getAllPositions(positions[0], positions[1])) {
                        if(PixelmonShops.getInstance().getDataManager().getArea(element) != null) {
                            try {
                                ConfigurationNode node = PixelmonShops.getInstance().getMessagesConfig().getLoader().load();
                                Text text = node.getNode("createArea", "existing").getValue(TypeToken.of(Text.class));

                                player.sendMessage(text);
                            } catch (ObjectMappingException | IOException exception) {
                                exception.printStackTrace();
                            }

                            return CommandResult.success();
                        }
                    }

                    try {
                        ConfigurationNode node = PixelmonShops.getInstance().getShopDataConfig().getLoader().load();

                        int count = 1;
                        while(!node.getNode(Integer.toString(count)).isEmpty()) {
                            count++;
                        }

                        node.getNode(Integer.toString(count), "pos1", "x").setValue(positions[0].getFloorX());
                        node.getNode(Integer.toString(count), "pos1", "y").setValue(positions[0].getFloorY());
                        node.getNode(Integer.toString(count), "pos1", "z").setValue(positions[0].getFloorZ());
                        node.getNode(Integer.toString(count), "pos2", "x").setValue(positions[1].getFloorX());
                        node.getNode(Integer.toString(count), "pos2", "y").setValue(positions[1].getFloorY());
                        node.getNode(Integer.toString(count), "pos2", "z").setValue(positions[1].getFloorZ());
                        // node.getNode(count, "owner").setValue(null);
                        // node.getNode(count, "nextPayment").setValue(null);
                        // node.getNode(count, "npcId").setValue(null);

                        PixelmonShops.getInstance().getShopDataConfig().getLoader().save(node);
                    } catch (IOException exception) {
                        exception.printStackTrace();
                        return CommandResult.success();
                    }

                    HashMap<UUID, Vector3d[]> data = PixelmonShops.getInstance().getDataManager().getSelectionData();
                    data.remove(player.getUniqueId());
                    PixelmonShops.getInstance().getDataManager().setSelectionData(data);
                }
            }

            try {
                ConfigurationNode node = PixelmonShops.getInstance().getMessagesConfig().getLoader().load();
                Text text = node.getNode(nodeLocation).getValue(TypeToken.of(Text.class));

                player.sendMessage(text);
            } catch (ObjectMappingException | IOException exception) {
                exception.printStackTrace();
            }
        }

        return CommandResult.success();
    }
}
