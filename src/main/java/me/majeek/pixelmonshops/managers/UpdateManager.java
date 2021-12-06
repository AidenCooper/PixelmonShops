package me.majeek.pixelmonshops.managers;

import com.google.common.reflect.TypeToken;
import me.majeek.pixelmonshops.PixelmonShops;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.User;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransactionType;
import org.spongepowered.api.service.economy.transaction.TransactionTypes;
import org.spongepowered.api.service.user.UserStorageService;
import org.spongepowered.api.text.Text;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class UpdateManager {
    public UpdateManager() {
        this.run();
    }

    public void run() {
        Task.builder().execute(() -> {
            try {
                ConfigurationNode main = PixelmonShops.getInstance().getMainConfig().getLoader().load();
                ConfigurationNode shop = PixelmonShops.getInstance().getShopDataConfig().getLoader().load();

                int count = 1;
                while(!shop.getNode(Integer.toString(count)).isEmpty()) {
                    if(!shop.getNode(Integer.toString(count), "nextPayment").isEmpty()) {
                        Long next = shop.getNode(Integer.toString(count), "nextPayment").getLong();
                        Long current = new Date().getTime();
                        if(current >= next) {
                            shop.getNode(Integer.toString(count), "nextPayment").setValue(TypeToken.of(Long.class), current + (main.getNode("rent", "delay").getLong() * 60000));

                            Optional<User> user = Sponge.getServiceManager().provide(UserStorageService.class).get().get(shop.getNode(Integer.toString(count), "owner").getValue(TypeToken.of(UUID.class)));

                            if(user.isPresent()) {
                                Optional<EconomyService> service = Sponge.getServiceManager().provide(EconomyService.class);
                                if(!service.isPresent()) {
                                    continue;
                                }
                                EconomyService economy = service.get();
                                UniqueAccount account = economy.getOrCreateAccount(user.get().getUniqueId()).get();
                                BigDecimal price = BigDecimal.valueOf(main.getNode("rent", "pricePerDelay").getInt());
                                EventContext context = EventContext.builder().add(EventContextKeys.PLUGIN, PixelmonShops.getInstance().getContainer()).build();

                                if(account.withdraw(economy.getDefaultCurrency(), price, Cause.of(context, PixelmonShops.getInstance())).getResult() != ResultType.SUCCESS) {
                                    // Take away
                                }
                            }
                        }
                    }

                    count++;
                }

                PixelmonShops.getInstance().getShopDataConfig().getLoader().save(shop);
            } catch (IOException | ObjectMappingException exception) {
                exception.printStackTrace();
            }
        }).async().interval(1, TimeUnit.MINUTES).name("Update rent payments").submit(PixelmonShops.getInstance());
    }
}
