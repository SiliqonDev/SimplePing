package com.siliqon.simpleping;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import static com.siliqon.simpleping.SimplePing.getConfigBoolean;
import static com.siliqon.simpleping.SimplePing.sendMessage;

@CommandAlias("ping")
public class PingCommand extends BaseCommand {

    private static final SimplePing plugin = SimplePing.getInstance();

    @Default
    public static void pingCommandBase(Player player) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        boolean enabled = Boolean.TRUE.equals(pdc.get(plugin.pingEnabledKey, PersistentDataType.BOOLEAN));

        sendMessage(player, ("Pings "+ (enabled ? "&aEnabled" : "&cDisabled"))
                .replace("&", "§"));
    }

    @Subcommand("toggle")
    public static void togglePing(Player player) {
        if (!getConfigBoolean("allow-disable")) {
            sendMessage(player, "&cCannot disable pings."
                    .replace("&", "§"));
            return;
        }

        PersistentDataContainer pdc = player.getPersistentDataContainer();
        boolean status = Boolean.TRUE.equals(pdc.get(plugin.pingEnabledKey, PersistentDataType.BOOLEAN));

        pdc.set(plugin.pingEnabledKey, PersistentDataType.BOOLEAN, !status);

        sendMessage(player, ("Pings "+ (!status ? "&aEnabled" : "&cDisabled"))
                .replace("&", "§"));
    }
}
