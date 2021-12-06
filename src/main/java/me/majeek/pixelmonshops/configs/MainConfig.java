package me.majeek.pixelmonshops.configs;

import java.io.File;

public class MainConfig extends Config {
    public MainConfig(File directory) {
        super(directory, "config.conf", true);
    }
}
