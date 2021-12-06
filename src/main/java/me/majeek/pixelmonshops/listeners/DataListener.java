package me.majeek.pixelmonshops.listeners;

import com.flowpowered.math.vector.Vector3d;
import me.majeek.pixelmonshops.PixelmonShops;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.MoveEntityEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import java.util.HashMap;
import java.util.UUID;

public class DataListener {
    @Listener
    public void onPlayerLeave(ClientConnectionEvent.Disconnect event) {
        HashMap<UUID, Vector3d[]> data = PixelmonShops.getInstance().getDataManager().getSelectionData();
        data.remove(event.getTargetEntity().getUniqueId());
        PixelmonShops.getInstance().getDataManager().setSelectionData(data);
    }

    @Listener
    public void onPlayerTeleport(MoveEntityEvent.Teleport event) {
        if(!event.getFromTransform().getExtent().getName().equals(event.getToTransform().getExtent().getName())) {
            HashMap<UUID, Vector3d[]> data = PixelmonShops.getInstance().getDataManager().getSelectionData();
            data.remove(event.getTargetEntity().getUniqueId());
            PixelmonShops.getInstance().getDataManager().setSelectionData(data);
        }
    }
}
