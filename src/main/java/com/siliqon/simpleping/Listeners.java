package com.siliqon.simpleping;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import static com.siliqon.simpleping.SimplePing.*;

public class Listeners implements Listener {

    private static final SimplePing plugin = SimplePing.getInstance();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        PersistentDataContainer pdc = player.getPersistentDataContainer();
        if (!pdc.has(plugin.pingEnabledKey)) {
            pdc.set(plugin.pingEnabledKey, PersistentDataType.BOOLEAN, true);
        }

        boolean enabled = Boolean.TRUE.equals(pdc.get(plugin.pingEnabledKey, PersistentDataType.BOOLEAN));
        sendMessage(player, ("Pings "+ (enabled ? "&aEnabled" : "&cDisabled"))
                .replace("&", "§"));
    }

    @EventHandler
    public void onMessage(AsyncPlayerChatEvent e) {
        Player sender = e.getPlayer();
        String message = e.getMessage().toLowerCase();

        if (getConfigBoolean("ping-cooldown")) {
            long last = 0;
            if (sender.getPersistentDataContainer().has(plugin.pingCooldownKey)) {
                last = sender.getPersistentDataContainer().get(plugin.pingCooldownKey, PersistentDataType.LONG);
            }
            long since = System.currentTimeMillis() - last;

            System.out.println(last + " " + System.currentTimeMillis() + " " + since);
            if ((since/1000) < getConfigInt("ping-cooldown-time")) {
                sendMessage(sender, "&cYou need to wait a bit before pinging more!"
                        .replace("&", "§"));
                return;
            }
        }

        if (getConfigBoolean("allow-ping-everyone") && message.contains("@everyone")) {
            sendPingAll(sender);
        } else {
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                if (message.contains("@" + player.getName().toLowerCase())) {
                    sendPing(sender, player);
                }
            }
        }

        sender.getPersistentDataContainer().set(plugin.pingCooldownKey, PersistentDataType.LONG, System.currentTimeMillis());
    }

    public void sendPingAll(Player sender) {
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            sendPing(sender, player);
        }
    }
    public void sendPing(Player sender, Player player) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        boolean enabled = Boolean.TRUE.equals(pdc.get(plugin.pingEnabledKey, PersistentDataType.BOOLEAN));

        if (!enabled) return;
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 1.0f);
        sendActionbar(player, "&a" + sender.getName() + " pinged you!");
    }
}
