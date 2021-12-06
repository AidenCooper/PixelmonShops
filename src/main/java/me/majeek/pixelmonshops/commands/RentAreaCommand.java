package me.majeek.pixelmonshops.commands;

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
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.text.Text;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

public class RentAreaCommand implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if(src instanceof Player) {
            Player player = (Player) src;

            ConfigurationNode node = PixelmonShops.getInstance().getDataManager().getArea(player.getPosition());

            try {
                ConfigurationNode main = PixelmonShops.getInstance().getMainConfig().getLoader().load();
                ConfigurationNode messages = PixelmonShops.getInstance().getMessagesConfig().getLoader().load().getNode("rentArea");

                if (node != null) {
                    UUID uuid = node.getNode("owner").getValue(TypeToken.of(UUID.class));
                    if (uuid == null) {
                        Optional<EconomyService> service = Sponge.getServiceManager().provide(EconomyService.class);
                        if(!service.isPresent()) {
                            return CommandResult.success();
                        }
                        EconomyService economy = service.get();
                        UniqueAccount account = economy.getOrCreateAccount(player.getUniqueId()).get();
                        BigDecimal price = BigDecimal.valueOf(main.getNode("rent", "initialPrice").getInt());
                        EventContext context = EventContext.builder().add(EventContextKeys.PLUGIN, PixelmonShops.getInstance().getContainer()).build();

                        if(account.withdraw(economy.getDefaultCurrency(), price, Cause.of(context, PixelmonShops.getInstance())).getResult() == ResultType.SUCCESS) {
                            node.getNode("owner").setValue(TypeToken.of(UUID.class), player.getUniqueId());
                            node.getNode("nextPayment").setValue(TypeToken.of(Long.class), new Date().getTime() + (main.getNode("rent", "delay").getLong() * 60000));
                            PixelmonShops.getInstance().getShopDataConfig().getLoader().save(node.getParent());

                            Text text = messages.getNode("rented").getValue(TypeToken.of(Text.class));
                            player.sendMessage(text);
                        } else {
                            Text text = messages.getNode("noMoney").getValue(TypeToken.of(Text.class));
                            player.sendMessage(text);
                        }
                    } else {
                        if (uuid.toString().equals(player.getUniqueId().toString())) {
                            Text text = messages.getNode("alreadyOwn").getValue(TypeToken.of(Text.class));
                            player.sendMessage(text);
                        } else {
                            Text text = messages.getNode("dontOwn").getValue(TypeToken.of(Text.class));
                            player.sendMessage(text);
                        }
                    }
                } else {
                    Text text = messages.getNode("noArea").getValue(TypeToken.of(Text.class));
                    player.sendMessage(text);
                }
            } catch (ObjectMappingException | IOException exception) {
                exception.printStackTrace();
            }
        }
        return CommandResult.success();
    }
}
