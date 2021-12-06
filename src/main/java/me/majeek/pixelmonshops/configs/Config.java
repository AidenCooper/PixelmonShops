package me.majeek.pixelmonshops.configs;

import me.majeek.pixelmonshops.PixelmonShops;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.Sponge;

import java.io.File;
import java.io.IOException;

public class Config {
    private File file;
    private ConfigurationLoader<CommentedConfigurationNode> loader;

    public Config(File directory, String fileName, boolean isAsset) {
        this.file = new File(directory, fileName);
        this.loader = HoconConfigurationLoader.builder().setFile(this.file).build();

        try {
            if(isAsset && !file.exists()) {
                Sponge.getAssetManager().getAsset(PixelmonShops.getInstance(), fileName).get().copyToFile(file.toPath(), false, true);
            }

            ConfigurationNode node = this.loader.load();
            this.loader.save(node);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public ConfigurationLoader<CommentedConfigurationNode> getLoader() {
        return this.loader;
    }
}
