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
import org.spongepowered.api.data.DataTransactionResult;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.mutable.RotationalData;
import org.spongepowered.api.data.manipulator.mutable.entity.AgentData;
import org.spongepowered.api.data.manipulator.mutable.entity.GravityData;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import org.spongepowered.api.data.manipulator.mutable.entity.SkinData;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.ValueContainer;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.Human;
import org.spongepowered.api.entity.living.Humanoid;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.event.CauseStackManager;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.cause.entity.spawn.SpawnTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.rotation.Rotations;

import java.io.IOException;
import java.util.UUID;

public class CreateNPCCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(src instanceof Player) {
            Player player = (Player) src;

            try {
                ConfigurationNode area = PixelmonShops.getInstance().getDataManager().getArea(player.getPosition());
                ConfigurationNode messages = PixelmonShops.getInstance().getMessagesConfig().getLoader().load();

                if(area != null) {
                    if(area.getNode("owner").getValue(TypeToken.of(UUID.class)).toString().equals(player.getUniqueId().toString())) {
                        Human npc = (Human) player.getLocation().getExtent().createEntity(EntityTypes.HUMAN, player.getLocation().getPosition());

                        npc.offer(Keys.AI_ENABLED, false);
                        npc.offer(Keys.DISPLAY_NAME, Text.of(player.getName()));
                        npc.offer(Keys.INVULNERABLE, true);
                        npc.offer(Keys.HAS_GRAVITY, false);
                        if(npc.getOrCreate(SkinData.class).isPresent()) {
                            npc.offer(npc.getOrCreate(SkinData.class).get().set(Keys.SKIN_UNIQUE_ID, player.getUniqueId()));
                        }

                        npc.setLocationAndRotation(player.getLocation(), player.getRotation());

                        CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame();
                        frame.addContext(EventContextKeys.SPAWN_TYPE, SpawnTypes.PLUGIN);
                        player.getLocation().getExtent().spawnEntity(npc);

                        area.getNode("npcId").setValue(npc.getUniqueId());
                        PixelmonShops.getInstance().getShopDataConfig().getLoader().save(area.getParent());

                        Text text = messages.getNode("createNPC", "created").getValue(TypeToken.of(Text.class));
                        player.sendMessage(text);
                    } else {
                        Text text = messages.getNode("createNPC", "dontOwn").getValue(TypeToken.of(Text.class));
                        player.sendMessage(text);
                    }
                } else {
                    Text text = messages.getNode("createNPC", "notInArea").getValue(TypeToken.of(Text.class));
                    player.sendMessage(text);
                }
            } catch (IOException | ObjectMappingException exception) {
                exception.printStackTrace();
            }
        }

        return CommandResult.success();
    }

    private void spawnNPC(Player player) {
        Human npc = (Human) player.getLocation().getExtent().createEntity(EntityTypes.HUMAN, player.getLocation().getPosition());

        npc.offer(Keys.AI_ENABLED, false);
        npc.offer(Keys.DISPLAY_NAME, Text.of(player.getName()));
        npc.offer(Keys.INVULNERABLE, true);
        npc.offer(Keys.HAS_GRAVITY, false);
        if(npc.getOrCreate(SkinData.class).isPresent()) {
            npc.offer(npc.getOrCreate(SkinData.class).get().set(Keys.SKIN_UNIQUE_ID, player.getUniqueId()));
        }

        npc.setLocationAndRotation(player.getLocation(), player.getRotation());

        try (CauseStackManager.StackFrame frame = Sponge.getCauseStackManager().pushCauseFrame()) {
            frame.addContext(EventContextKeys.SPAWN_TYPE, SpawnTypes.PLUGIN);
            player.getLocation().getExtent().spawnEntity(npc);
        }
    }
}
