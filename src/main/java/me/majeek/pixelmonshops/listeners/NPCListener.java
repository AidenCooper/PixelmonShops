package me.majeek.pixelmonshops.listeners;

import com.google.common.reflect.TypeToken;
import me.majeek.pixelmonshops.PixelmonShops;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.entity.Entity;
import org.spongepowered.api.entity.living.Human;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.CollideEntityEvent;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.entity.InteractEntityEvent;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.util.Identifiable;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class NPCListener {
    @Listener
    public void onEntityClick(InteractEntityEvent event) {
        if(event.getTargetEntity() instanceof Human) {
            try {
                ConfigurationNode node = PixelmonShops.getInstance().getShopDataConfig().getLoader().load();

                int count = 1;
                while (!node.getNode(Integer.toString(count)).isEmpty()) {
                    if(event.getTargetEntity().getUniqueId().toString().equals(node.getNode(Integer.toString(count), "npcId").getValue(TypeToken.of(UUID.class)).toString())) {
                        
                    }

                    count++;
                }
            } catch (IOException | ObjectMappingException exception) {
                exception.printStackTrace();
            }
        }
    }

    @Listener
    public void onEntityCollide(CollideEntityEvent event) {
        List<Human> humans = event.getEntities().stream().filter(entity -> entity instanceof Human).map(entity -> (Human) entity).collect(Collectors.toList());
        if(humans.size() == 0) {
            return;
        }

        try {
            List<UUID> uuidList = humans.stream().map(Identifiable::getUniqueId).collect(Collectors.toList());
            ConfigurationNode node = PixelmonShops.getInstance().getShopDataConfig().getLoader().load();

            int count = 1;
            while (!node.getNode(Integer.toString(count)).isEmpty()) {
                for(UUID uuid : uuidList) {
                    if(uuid.toString().equals(node.getNode(Integer.toString(count), "npcId").getValue(TypeToken.of(UUID.class)).toString())) {
                        event.setCancelled(true);
                        return;
                    }
                }

                count++;
            }
        } catch (IOException | ObjectMappingException exception) {
            exception.printStackTrace();
        }
    }

    @Listener
    public void onEntityMove(MoveEntityEvent event) {
        if(!(event.getTargetEntity() instanceof Human)) {
            return;
        }

        try {
            ConfigurationNode node = PixelmonShops.getInstance().getShopDataConfig().getLoader().load();

            int count = 1;
            while (!node.getNode(Integer.toString(count)).isEmpty()) {
                if(event.getTargetEntity().getUniqueId().toString().equals(node.getNode(Integer.toString(count), "npcId").getValue(TypeToken.of(UUID.class)).toString())) {
                    event.setCancelled(true);

                    return;
                }

                count++;
            }
        } catch (IOException | ObjectMappingException exception) {
            exception.printStackTrace();
        }
    }

    @Listener
    public void onEntityDeath(DestructEntityEvent event) {
        if(!(event.getTargetEntity() instanceof Human)) {
            return;
        }

        try {
            ConfigurationNode node = PixelmonShops.getInstance().getShopDataConfig().getLoader().load();

            int count = 1;
            while (!node.getNode(Integer.toString(count)).isEmpty()) {
                if(event.getTargetEntity().getUniqueId().toString().equals(node.getNode(Integer.toString(count), "npcId").getValue(TypeToken.of(UUID.class)).toString())) {
                    node.getNode(Integer.toString(count), "npcId").setValue(null);
                    PixelmonShops.getInstance().getShopDataConfig().getLoader().save(node);

                    return;
                }

                count++;
            }
        } catch (IOException | ObjectMappingException exception) {
            exception.printStackTrace();
        }
    }
}
