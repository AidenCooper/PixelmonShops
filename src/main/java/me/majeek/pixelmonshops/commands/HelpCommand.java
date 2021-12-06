package me.majeek.pixelmonshops.commands;

import com.google.common.collect.Lists;
import me.majeek.pixelmonshops.PixelmonShops;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.List;

public class HelpCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        Text prefix = Text.builder("> ").color(TextColors.GRAY).build();

        Text title = Text.builder("-----------------> [").color(TextColors.GRAY).append(Text.builder("PixelmonShops").color(TextColors.YELLOW).append(Text.builder("] <-----------------").color(TextColors.GRAY).build()).build()).build();
        Text createNPC = prefix.toBuilder().append(Text.of(TextColors.YELLOW, "/pms createnpc").toBuilder().append(Text.of(" - ").toBuilder().append(Text.of(TextColors.GRAY, "Creates an npc at your position.")).build()).build()).build();
        Text deleteNPC = prefix.toBuilder().append(Text.of(TextColors.YELLOW, "/pms deletenpc").toBuilder().append(Text.of(" - ").toBuilder().append(Text.of(TextColors.GRAY, "Deletes the npc you are looking at.")).build()).build()).build();
        Text help = prefix.toBuilder().append(Text.of(TextColors.YELLOW, "/pms help").toBuilder().append(Text.of(" - ").toBuilder().append(Text.of(TextColors.GRAY, "Displays the PixelmonShops commands.")).build()).build()).build();
        Text shop = prefix.toBuilder().append(Text.of(TextColors.YELLOW, "/pms shop").toBuilder().append(Text.of(" - ").toBuilder().append(Text.of(TextColors.GRAY, "Opens up the shop gui.")).build()).build()).build();

        src.sendMessage(title);

        return CommandResult.success();
    }
}
