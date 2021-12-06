package me.majeek.pixelmonshops;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import me.majeek.pixelmonshops.commands.*;
import me.majeek.pixelmonshops.configs.MainConfig;
import me.majeek.pixelmonshops.configs.MessagesConfig;
import me.majeek.pixelmonshops.configs.ShopDataConfig;
import me.majeek.pixelmonshops.listeners.DataListener;
import me.majeek.pixelmonshops.managers.DataManager;
import me.majeek.pixelmonshops.managers.UpdateManager;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.Text;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Plugin(
        id = PixelmonShops.id,
        name = PixelmonShops.name,
        description = PixelmonShops.description,
        version = PixelmonShops.version,
        url = PixelmonShops.url,
        authors = {
                "Majeek"
        },
        dependencies = {
                @Dependency(id = "gts")
        }
)
public class PixelmonShops {
    public static final String id = "pixelmonshops";
    public static final String name = "PixelmonShops";
    public static final String description = "Pixelmon NPC GTS";
    public static final String version = "8.3.4";
    public static final String url = "https://www.fiverr.com/users/majeek_/";

    private static PixelmonShops instance = null;

    private MainConfig mainConfig = null;
    private MessagesConfig messagesConfig = null;
    private ShopDataConfig shopDataConfig = null;

    private DataManager dataManager = null;

    private List<CommandSpec> commands = Lists.newArrayList();

    @Inject
    @ConfigDir(sharedRoot = false)
    private File configDirectory;

    @Inject
    private Game game;

    @Inject
    private Logger logger;

    @Inject
    private PluginContainer pluginContainer;

    @Listener
    public void onServerInitialize(GameInitializationEvent event) {
        instance = this;
        
        this.loadConfigs();
        this.loadManagers();
        this.registerCommands();
        this.registerEvents();
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
        new UpdateManager();
    }

    public static PixelmonShops getInstance() {
        return instance;
    }

    public MainConfig getMainConfig() {
        return this.mainConfig;
    }

    public MessagesConfig getMessagesConfig() {
        return this.messagesConfig;
    }

    public ShopDataConfig getShopDataConfig() {
        return this.shopDataConfig;
    }

    public DataManager getDataManager() {
        return this.dataManager;
    }

    public List<CommandSpec> getCommands() {
        return this.commands;
    }

    public PluginContainer getContainer() {
        return this.pluginContainer;
    }

    private void loadConfigs() {
        this.game.getScheduler().createAsyncExecutor(this).execute(new Runnable() {
            @Override
            public void run() {
                if(!configDirectory.exists()) {
                    configDirectory.mkdir();
                }

                mainConfig = new MainConfig(configDirectory);
                messagesConfig = new MessagesConfig(configDirectory);
                shopDataConfig = new ShopDataConfig(configDirectory);
            }
        });
    }

    private void loadManagers() {
        this.dataManager = new DataManager();
    }

    private void registerCommands() {
        CommandSpec createAreaCommand = CommandSpec.builder()
                .permission("pixelmonshops.admin.createarea")
                .description(Text.of("Create an area for a new shop"))
                .executor(new CreateAreaCommand())
                .build();
        CommandSpec deleteAreaCommand = CommandSpec.builder()
                .permission("pixelmonshops.admin.deletearea")
                .description(Text.of("Delete a shop area"))
                .executor(new DeleteAreaCommand())
                .build();

        CommandSpec createNPCCommand = CommandSpec.builder()
                .permission("pixelmonshops.createnpc")
                .description(Text.of("Creates an npc at your position."))
                .executor(new CreateNPCCommand())
                .build();

        CommandSpec helpCommand = CommandSpec.builder()
                .permission("pixelmonshops.help")
                .description(Text.of("Displays the PixelmonShops commands."))
                .executor(new HelpCommand())
                .build();

        CommandSpec rentAreaCommand = CommandSpec.builder()
                .permission("pixelmonshops.rentarea")
                .description(Text.of("Rent an area for your shop."))
                .executor(new RentAreaCommand())
                .build();
        CommandSpec unrentAreaCommand = CommandSpec.builder()
                .permission("pixelmonshops.unrentarea")
                .description(Text.of("Unrent an area you own."))
                .executor(new UnrentAreaCommand())
                .build();

        CommandSpec setPos1Command = CommandSpec.builder()
                .permission("pixelmonshops.admin.setpos1")
                .description(Text.of("Sets the 1st position for creating a new shop area."))
                .executor(new SetPos1Command())
                .build();
        CommandSpec setPos2Command = CommandSpec.builder()
                .permission("pixelmonshops.admin.setpos2")
                .description(Text.of("Sets the 2nd position for creating a new shop area."))
                .executor(new SetPos2Command())
                .build();

        CommandSpec baseCommand = CommandSpec.builder()
                .permission("pixelmonshops.help")
                .description(Text.of("Main PixelmonShops command."))
                .child(createAreaCommand, "createarea")
                .child(deleteAreaCommand, "deletearea")
                .child(createNPCCommand, "createnpc")
                .child(helpCommand, "help")
                .child(rentAreaCommand, "rentarea")
                .child(unrentAreaCommand,"unrentarea")
                .child(setPos1Command, "setpos1", "sp1")
                .child(setPos2Command, "setpos2", "sp2")
                .executor(new HelpCommand())
                .build();

        this.game.getCommandManager().register(this, baseCommand, "pixelmonshops", "pms");

        this.commands = Arrays.asList(
                baseCommand,
                createAreaCommand,
                deleteAreaCommand,
                createNPCCommand,
                helpCommand,
                rentAreaCommand,
                unrentAreaCommand,
                setPos1Command,
                setPos2Command
        );
    }

    private void registerEvents() {
        this.game.getEventManager().registerListeners(this, new DataListener());
    }
}
