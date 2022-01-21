/*
 * Copyright (c) 2022, Thomas Meaney
 * All rights reserved.
 *
 * This source code is licensed under the BSD-style license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.eintosti.buildsystem;

import com.eintosti.buildsystem.command.BackCommand;
import com.eintosti.buildsystem.command.BlocksCommand;
import com.eintosti.buildsystem.command.BuildCommand;
import com.eintosti.buildsystem.command.BuildSystemCommand;
import com.eintosti.buildsystem.command.ConfigCommand;
import com.eintosti.buildsystem.command.ExplosionsCommand;
import com.eintosti.buildsystem.command.GamemodeCommand;
import com.eintosti.buildsystem.command.NoAICommand;
import com.eintosti.buildsystem.command.PhysicsCommand;
import com.eintosti.buildsystem.command.SettingsCommand;
import com.eintosti.buildsystem.command.SetupCommand;
import com.eintosti.buildsystem.command.SkullCommand;
import com.eintosti.buildsystem.command.SpawnCommand;
import com.eintosti.buildsystem.command.SpeedCommand;
import com.eintosti.buildsystem.command.TimeCommand;
import com.eintosti.buildsystem.command.TopCommand;
import com.eintosti.buildsystem.command.WorldsCommand;
import com.eintosti.buildsystem.expansion.BuildSystemExpansion;
import com.eintosti.buildsystem.inventory.ArchiveInventory;
import com.eintosti.buildsystem.inventory.BlocksInventory;
import com.eintosti.buildsystem.inventory.BuilderInventory;
import com.eintosti.buildsystem.inventory.CreateInventory;
import com.eintosti.buildsystem.inventory.DeleteInventory;
import com.eintosti.buildsystem.inventory.DesignInventory;
import com.eintosti.buildsystem.inventory.EditInventory;
import com.eintosti.buildsystem.inventory.GameRuleInventory;
import com.eintosti.buildsystem.inventory.NavigatorInventory;
import com.eintosti.buildsystem.inventory.PrivateInventory;
import com.eintosti.buildsystem.inventory.SettingsInventory;
import com.eintosti.buildsystem.inventory.SetupInventory;
import com.eintosti.buildsystem.inventory.SpeedInventory;
import com.eintosti.buildsystem.inventory.StatusInventory;
import com.eintosti.buildsystem.inventory.WorldsInventory;
import com.eintosti.buildsystem.listener.AsyncPlayerChatListener;
import com.eintosti.buildsystem.listener.BlockPhysicsListener;
import com.eintosti.buildsystem.listener.BlockPlaceListener;
import com.eintosti.buildsystem.listener.EditSessionListener;
import com.eintosti.buildsystem.listener.EntitySpawnListener;
import com.eintosti.buildsystem.listener.FoodLevelChangeListener;
import com.eintosti.buildsystem.listener.InventoryCloseListener;
import com.eintosti.buildsystem.listener.InventoryCreativeListener;
import com.eintosti.buildsystem.listener.PlayerChangedWorldListener;
import com.eintosti.buildsystem.listener.PlayerCommandPreprocessListener;
import com.eintosti.buildsystem.listener.PlayerInteractAtEntityListener;
import com.eintosti.buildsystem.listener.PlayerInteractListener;
import com.eintosti.buildsystem.listener.PlayerInventoryClearListener;
import com.eintosti.buildsystem.listener.PlayerJoinListener;
import com.eintosti.buildsystem.listener.PlayerMoveListener;
import com.eintosti.buildsystem.listener.PlayerQuitListener;
import com.eintosti.buildsystem.listener.PlayerRespawnListener;
import com.eintosti.buildsystem.listener.PlayerTeleportListener;
import com.eintosti.buildsystem.listener.SignChangeListener;
import com.eintosti.buildsystem.listener.WeatherChangeListener;
import com.eintosti.buildsystem.listener.WorldManipulateListener;
import com.eintosti.buildsystem.manager.ArmorStandManager;
import com.eintosti.buildsystem.manager.InventoryManager;
import com.eintosti.buildsystem.manager.NoClipManager;
import com.eintosti.buildsystem.manager.PlayerManager;
import com.eintosti.buildsystem.manager.SettingsManager;
import com.eintosti.buildsystem.manager.SpawnManager;
import com.eintosti.buildsystem.manager.WorldManager;
import com.eintosti.buildsystem.object.internal.ServerVersion;
import com.eintosti.buildsystem.object.settings.Settings;
import com.eintosti.buildsystem.tabcomplete.ConfigTabComplete;
import com.eintosti.buildsystem.tabcomplete.EmptyTabComplete;
import com.eintosti.buildsystem.tabcomplete.GamemodeTabComplete;
import com.eintosti.buildsystem.tabcomplete.PhysicsTabComplete;
import com.eintosti.buildsystem.tabcomplete.SpawnTabComplete;
import com.eintosti.buildsystem.tabcomplete.SpeedTabComplete;
import com.eintosti.buildsystem.tabcomplete.TimeTabComplete;
import com.eintosti.buildsystem.tabcomplete.WorldsTabComplete;
import com.eintosti.buildsystem.util.ConfigValues;
import com.eintosti.buildsystem.util.Messages;
import com.eintosti.buildsystem.util.RBGUtils;
import com.eintosti.buildsystem.util.SkullCache;
import com.eintosti.buildsystem.util.external.UpdateChecker;
import com.eintosti.buildsystem.version.CustomBlocks;
import com.eintosti.buildsystem.version.GameRules;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static com.eintosti.buildsystem.object.internal.ServerVersion.UNKNOWN;

/**
 * @author einTosti
 */
public class BuildSystem extends JavaPlugin {

    public static final int SPIGOT_ID = 60441;
    public static final int METRICS_ID = 7427;

    private String version;

    private WorldsCommand worldsCommand;

    private ArmorStandManager armorStandManager;
    private InventoryManager inventoryManager;
    private NoClipManager noClipManager;
    private PlayerManager playerManager;
    private SettingsManager settingsManager;
    private SpawnManager spawnManager;
    private WorldManager worldManager;

    private ArchiveInventory archiveInventory;
    private BlocksInventory blocksInventory;
    private BuilderInventory builderInventory;
    private CreateInventory createInventory;
    private DeleteInventory deleteInventory;
    private DesignInventory designInventory;
    private EditInventory editInventory;
    private GameRuleInventory gameRuleInventory;
    private NavigatorInventory navigatorInventory;
    private PrivateInventory privateInventory;
    private SettingsInventory settingsInventory;
    private SetupInventory setupInventory;
    private SpeedInventory speedInventory;
    private StatusInventory statusInventory;
    private WorldsInventory worldsInventory;

    private Messages messages;
    private ConfigValues configValues;
    private CustomBlocks customBlocks;
    private GameRules gameRules;
    private SkullCache skullCache;

    @Override
    public void onLoad() {
        createLanguageFile();
        createTemplateFolder();
        parseServerVersion();
    }

    @Override
    public void onEnable() {
        this.getConfig().options().copyDefaults(true);
        this.saveConfig();
        this.configValues = new ConfigValues(this);

        initVersionedClasses();
        initClasses();

        registerCommands();
        registerTabCompleter();
        registerListeners();
        registerStats();
        registerPlaceholders();

        performUpdateCheck();

        worldManager.load();
        settingsManager.load();
        spawnManager.load();

        Bukkit.getOnlinePlayers().forEach(pl -> {
            getSkullCache().cacheSkull(pl.getName());

            settingsManager.createSettings(pl);
            Settings settings = settingsManager.getSettings(pl);
            if (settings.isScoreboard()) {
                settingsManager.startScoreboard(pl);
            }
            if (settings.isNoClip()) {
                noClipManager.startNoClip(pl);
            }
        });

        Bukkit.getConsoleSender().sendMessage(ChatColor.RESET + "BuildSystem » Plugin " + ChatColor.GREEN + "enabled" + ChatColor.RESET + "!");
    }

    @Override
    public void onDisable() {
        Bukkit.getOnlinePlayers().forEach(pl -> {
            settingsManager.stopScoreboard(pl);
            noClipManager.stopNoClip(pl.getUniqueId());
            playerManager.closeNavigator(pl);
        });

        reloadConfig();
        reloadConfigData(false);
        saveConfig();

        worldManager.save();
        settingsManager.save();
        spawnManager.save();
        inventoryManager.save();

        Bukkit.getConsoleSender().sendMessage(ChatColor.RESET + "BuildSystem » Plugin " + ChatColor.RED + "disabled" + ChatColor.RESET + "!");
    }

    private void initVersionedClasses() {
        ServerVersion serverVersion = ServerVersion.matchServerVersion(version);
        if (serverVersion == UNKNOWN) {
            getLogger().log(Level.SEVERE, "BuildSystem does not support your server version: " + version);
            getLogger().log(Level.SEVERE, "Disabling plugin... ");
            this.setEnabled(false);
            return;
        }

        this.customBlocks = serverVersion.initCustomBlocks();
        this.gameRules = serverVersion.initGameRules();
    }

    private void initClasses() {
        this.armorStandManager = new ArmorStandManager();
        this.playerManager = new PlayerManager(this);
        this.inventoryManager = new InventoryManager(this);
        this.inventoryManager.loadTypes();
        this.inventoryManager.loadStatus();
        this.noClipManager = new NoClipManager(this);
        this.worldManager = new WorldManager(this);
        this.settingsManager = new SettingsManager(this);
        this.spawnManager = new SpawnManager(this);

        this.archiveInventory = new ArchiveInventory(this);
        this.blocksInventory = new BlocksInventory(this);
        this.builderInventory = new BuilderInventory(this);
        this.createInventory = new CreateInventory(this);
        this.deleteInventory = new DeleteInventory(this);
        this.designInventory = new DesignInventory(this);
        this.editInventory = new EditInventory(this);
        this.gameRuleInventory = new GameRuleInventory(this);
        this.navigatorInventory = new NavigatorInventory(this);
        this.privateInventory = new PrivateInventory(this);
        this.settingsInventory = new SettingsInventory(this);
        this.setupInventory = new SetupInventory(this);
        this.speedInventory = new SpeedInventory(this);
        this.statusInventory = new StatusInventory(this);
        this.worldsInventory = new WorldsInventory(this);

        this.skullCache = new SkullCache(version);
    }

    private void parseServerVersion() {
        try {
            this.version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
            getLogger().log(Level.INFO, "Detected server version: " + version);
        } catch (ArrayIndexOutOfBoundsException e) {
            getLogger().log(Level.SEVERE, "Unknown server version");
        }
    }

    private void registerCommands() {
        new BackCommand(this);
        new BlocksCommand(this);
        new BuildCommand(this);
        new BuildSystemCommand(this);
        new ConfigCommand(this);
        new ExplosionsCommand(this);
        new GamemodeCommand(this);
        new NoAICommand(this);
        new PhysicsCommand(this);
        new SettingsCommand(this);
        new SetupCommand(this);
        new SkullCommand(this);
        new SpawnCommand(this);
        new SpeedCommand(this);
        new TimeCommand(this);
        new TopCommand(this);
        this.worldsCommand = new WorldsCommand(this);
    }

    private void registerTabCompleter() {
        new ConfigTabComplete(this);
        new EmptyTabComplete(this);
        new GamemodeTabComplete(this);
        new PhysicsTabComplete(this);
        new SpawnTabComplete(this);
        new SpeedTabComplete(this);
        new TimeTabComplete(this);
        new WorldsTabComplete(this);
    }

    private void registerListeners() {
        new AsyncPlayerChatListener(this);
        new BlockPhysicsListener(this);
        new BlockPlaceListener(this);
        new EntitySpawnListener(this);
        new FoodLevelChangeListener(this);
        new InventoryCloseListener(this);
        new InventoryCreativeListener(this);
        new PlayerChangedWorldListener(this);
        new PlayerCommandPreprocessListener(this);
        new PlayerInteractAtEntityListener(this);
        new PlayerInteractListener(this);
        new PlayerInventoryClearListener(this);
        new PlayerJoinListener(this);
        new PlayerMoveListener(this);
        new PlayerQuitListener(this);
        new PlayerRespawnListener(this);
        new PlayerTeleportListener(this);
        new SignChangeListener(this);
        new WeatherChangeListener(this);
        new WorldManipulateListener(this);

        if (isWorldEdit() && configValues.isBlockWorldEditNonBuilder()) {
            new EditSessionListener(this);
        }
    }

    private boolean isWorldEdit() {
        PluginManager pluginManager = Bukkit.getPluginManager();
        return pluginManager.getPlugin("FastAsyncWorldEdit") != null || pluginManager.getPlugin("WorldEdit") != null;
    }

    private void registerStats() {
        Metrics metrics = new Metrics(this, METRICS_ID);

        metrics.addCustomChart(new SimplePie("scoreboard", () -> String.valueOf(configValues.isScoreboard())));
        metrics.addCustomChart(new SimplePie("archive_vanish", () -> String.valueOf(configValues.isArchiveVanish())));
        metrics.addCustomChart(new SimplePie("join_quit_messages", () -> String.valueOf(configValues.isJoinQuitMessages())));
        metrics.addCustomChart(new SimplePie("lock_weather", () -> String.valueOf(configValues.isLockWeather())));
        metrics.addCustomChart(new SimplePie("unload_worlds", () -> String.valueOf(configValues.isUnloadWorlds())));
        metrics.addCustomChart(new SimplePie("void_block", () -> String.valueOf(configValues.isVoidBlock())));
        metrics.addCustomChart(new SimplePie("update_checker", () -> String.valueOf(configValues.isUpdateChecker())));
        metrics.addCustomChart(new SimplePie("block_world_edit", () -> String.valueOf(configValues.isBlockWorldEditNonBuilder())));
    }

    private void registerPlaceholders() {
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new BuildSystemExpansion(this).register();
        }
    }

    private void performUpdateCheck() {
        if (!configValues.isUpdateChecker()) {
            return;
        }

        UpdateChecker.init(this, SPIGOT_ID).requestUpdateCheck().whenComplete((result, e) -> {
                    if (result.requiresUpdate()) {
                        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[BuildSystem] Great! a new update is available:" + ChatColor.GREEN + "v" + result.getNewestVersion());
                        Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + " ➥ Your current version: " + ChatColor.RED + this.getDescription().getVersion());
                        return;
                    }

                    UpdateChecker.UpdateReason reason = result.getReason();
                    switch (reason) {
                        case COULD_NOT_CONNECT:
                        case INVALID_JSON:
                        case UNAUTHORIZED_QUERY:
                        case UNKNOWN_ERROR:
                        case UNSUPPORTED_VERSION_SCHEME:
                            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[BuildSystem] Could not check for a new version of BuildSystem. Reason: " + reason);
                            break;
                    }
                }
        );
    }

    private void createTemplateFolder() {
        File templateFolder = new File(getDataFolder() + File.separator + "templates");
        if (templateFolder.mkdir()) {
            getLogger().log(Level.INFO, "Created \"templates\" folder");
        }
    }

    private void createLanguageFile() {
        if (getDataFolder().mkdir()) {
            getLogger().log(Level.INFO, "Created \"BuildSystem\" folder");
        }

        this.messages = new Messages(this);
        messages.createMessageFile();
    }

    public String getPrefixString() {
        String prefix = messages.getMessageData().get("prefix");
        try {
            final String defaultPrefix = "§8× §bBuildSystem §8┃";
            return prefix != null ? ChatColor.translateAlternateColorCodes('&', RBGUtils.color(prefix)) : defaultPrefix;
        } catch (NullPointerException e) {
            messages.createMessageFile();
            return getPrefixString();
        }
    }

    public String getString(String key) {
        try {
            String message = messages.getMessageData().get(key).replace("%prefix%", getPrefixString());
            return ChatColor.translateAlternateColorCodes('&', RBGUtils.color(message));
        } catch (NullPointerException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[BuildSystem] Could not find message with key: " + key);
            messages.createMessageFile();
            return getString(key);
        }
    }

    public List<String> getStringList(String key) {
        try {
            List<String> list = new ArrayList<>();
            String string = messages.getMessageData().get(key);
            String[] splitString = string.substring(1, string.length() - 1).split(", ");
            for (String s : splitString) {
                String message = s.replace("%prefix%", getPrefixString());
                list.add(ChatColor.translateAlternateColorCodes('&', RBGUtils.color(message)));
            }
            return list;
        } catch (NullPointerException e) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[BuildSystem] Could not get list with key: " + key);
            messages.createMessageFile();
            return getStringList(key);
        }
    }

    public boolean canBypass(Player player) {
        return player.hasPermission("buildsystem.admin")
                || player.hasPermission("buildsystem.bypass.archive")
                || playerManager.getBuildPlayers().contains(player.getUniqueId());
    }

    public void sendPermissionMessage(CommandSender sender) {
        sender.sendMessage(getString("no_permissions"));
    }

    public void reloadConfigData(boolean init) {
        for (Player pl : Bukkit.getOnlinePlayers()) {
            getSettingsManager().stopScoreboard(pl);
        }

        configValues.setConfigValues();

        if (init) {
            initVersionedClasses();
            if (configValues.isScoreboard()) {
                getSettingsManager().startScoreboard();
            } else {
                getSettingsManager().stopScoreboard();
            }
        }
    }

    public WorldsCommand getWorldsCommand() {
        return worldsCommand;
    }

    public ArmorStandManager getArmorStandManager() {
        return armorStandManager;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

    public NoClipManager getNoClipManager() {
        return noClipManager;
    }

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public SpawnManager getSpawnManager() {
        return spawnManager;
    }

    public WorldManager getWorldManager() {
        return worldManager;
    }

    public ArchiveInventory getArchiveInventory() {
        return archiveInventory;
    }

    public BlocksInventory getBlocksInventory() {
        return blocksInventory;
    }

    public BuilderInventory getBuilderInventory() {
        return builderInventory;
    }

    public CreateInventory getCreateInventory() {
        return createInventory;
    }

    public DeleteInventory getDeleteInventory() {
        return deleteInventory;
    }

    public DesignInventory getDesignInventory() {
        return designInventory;
    }

    public EditInventory getEditInventory() {
        return editInventory;
    }

    public GameRuleInventory getGameRuleInventory() {
        return gameRuleInventory;
    }

    public NavigatorInventory getNavigatorInventory() {
        return navigatorInventory;
    }

    public PrivateInventory getPrivateInventory() {
        return privateInventory;
    }

    public SettingsInventory getSettingsInventory() {
        return settingsInventory;
    }

    public SetupInventory getSetupInventory() {
        return setupInventory;
    }

    public SpeedInventory getSpeedInventory() {
        return speedInventory;
    }

    public StatusInventory getStatusInventory() {
        return statusInventory;
    }

    public WorldsInventory getWorldsInventory() {
        return worldsInventory;
    }

    public ConfigValues getConfigValues() {
        return configValues;
    }

    public CustomBlocks getCustomBlocks() {
        return customBlocks;
    }

    public GameRules getGameRules() {
        return gameRules;
    }

    public SkullCache getSkullCache() {
        return skullCache;
    }
}