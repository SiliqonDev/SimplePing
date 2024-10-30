package com.siliqon.simpleping;

import co.aikar.commands.PaperCommandManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class SimplePing extends JavaPlugin {
    private static SimplePing INSTANCE; {INSTANCE = this;}
    private static final double PLUGIN_VERSION = 1.0;

    public final NamespacedKey pingEnabledKey = new NamespacedKey(this, "ping-enabled");
    public final NamespacedKey pingCooldownKey = new NamespacedKey(this, "ping-cooldown");
    public static final String PREFIX = ChatColor.translateAlternateColorCodes('&',"&aSimplePing &7>&f ");

    private PaperCommandManager commandManager;

    @Override
    public void onEnable() {
        // Plugin startup logic
        log("Attempting startup.");
        if (!getConfigBoolean("plugin-enabled")) {
            logError("Plugin is disabled in config.yml. Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        // config
        saveDefaultConfig();
        // command stuff
        commandManager = new PaperCommandManager(this);
        registerListeners();
        registerCommands();
        registerCommandCompletions();
        // done
        log("Enabled successfully.");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        log("Disabled successfully.");
    }

    private void registerCommandCompletions() {
        commandManager.getCommandCompletions().registerCompletion("AllPlayers", c -> {
            List<String> nameList = new ArrayList<>();
            for (OfflinePlayer player: Bukkit.getOfflinePlayers()) {
                nameList.add(player.getName());
            }
            return nameList;
        });
    }
    private void registerCommands() {
        commandManager.registerCommand(new PingCommand());
    }
    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new Listeners(), this);
    }

    public static void log(String message) {
        INSTANCE.getLogger().info(message);
    }
    public static void logError(String message) {
        INSTANCE.getLogger().severe(message);
    }
    public static void sendMessage(Player player, String message) {
        player.sendMessage(PREFIX + message);
    }
    public static void sendActionbar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&', message)));
    }
    public static boolean getConfigBoolean(String path) {
        return INSTANCE.getConfig().getBoolean(path);
    }
    public static int getConfigInt(String path) { return INSTANCE.getConfig().getInt(path); }

    public static SimplePing getInstance() {
        return INSTANCE;
    }
}
