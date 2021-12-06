package me.majeek.pixelmonshops.managers;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import me.majeek.pixelmonshops.PixelmonShops;
import ninja.leaping.configurate.ConfigurationNode;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class DataManager {
    private HashMap<UUID, Vector3d[]> selectionData = Maps.newHashMap();

    public HashMap<UUID, Vector3d[]> getSelectionData() {
        return selectionData;
    }

    public void setSelectionData(HashMap<UUID, Vector3d[]> selectionData) {
        this.selectionData = selectionData;
    }

    public List<Vector3d> getAllPositions(Vector3d pos1, Vector3d pos2) {
        Vector3d low = new Vector3d(
                Math.min(pos1.getFloorX(), pos2.getFloorX()),
                Math.min(pos1.getFloorY(), pos2.getFloorY()),
                Math.min(pos1.getFloorZ(), pos2.getFloorZ())
        );
        Vector3d high = new Vector3d(
                Math.max(pos1.getFloorX(), pos2.getFloorX()),
                Math.max(pos1.getFloorY(), pos2.getFloorY()),
                Math.max(pos1.getFloorZ(), pos2.getFloorZ())
        );

        List<Vector3d> positions = Lists.newArrayList();
        for(int x = low.getFloorX(); x <= high.getFloorX(); x++) {
            for(int y = low.getFloorY(); y <= high.getFloorY(); y++) {
                for(int z = low.getFloorZ(); z <= high.getFloorZ(); z++) {
                    positions.add(new Vector3d(x, y, z));
                }
            }
        }

        return positions;
    }

    public ConfigurationNode getArea(Vector3d position) {
        try {
            ConfigurationNode node = PixelmonShops.getInstance().getShopDataConfig().getLoader().load();

            int count = 1;
            while(!node.getNode(Integer.toString(count)).isEmpty()) {
                Vector3d pos1 = new Vector3d(
                        node.getNode(Integer.toString(count), "pos1", "x").getInt(),
                        node.getNode(Integer.toString(count), "pos1", "y").getInt(),
                        node.getNode(Integer.toString(count), "pos1", "z").getInt()
                );
                Vector3d pos2 = new Vector3d(
                        node.getNode(Integer.toString(count), "pos2", "x").getInt(),
                        node.getNode(Integer.toString(count), "pos2", "y").getInt(),
                        node.getNode(Integer.toString(count), "pos2", "z").getInt()
                );

                for(Vector3d element : this.getAllPositions(pos1, pos2)) {
                    if(element.getFloorX() == position.getFloorX() && element.getFloorY() == position.getFloorY() && element.getFloorZ() == position.getFloorZ()) {
                        return node.getNode(Integer.toString(count));
                    }
                }

                count++;
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        return null;
    }
}
