package com.hooklite.endshop.config;

import com.hooklite.endshop.commands.CommandStatus;
import com.hooklite.endshop.config.interfaces.ConfigKey;
import com.hooklite.endshop.config.interfaces.RequiredKey;
import com.hooklite.endshop.config.item.*;
import com.hooklite.endshop.config.item.requirement.RequirementAmount;
import com.hooklite.endshop.config.item.requirement.RequirementReq;
import com.hooklite.endshop.config.item.reward.RewardAmount;
import com.hooklite.endshop.config.item.reward.RewardConfigKey;
import com.hooklite.endshop.config.shop.ShopDescription;
import com.hooklite.endshop.config.shop.ShopDisplayItem;
import com.hooklite.endshop.config.shop.ShopSlot;
import com.hooklite.endshop.config.shop.ShopTitle;
import com.hooklite.endshop.data.models.Shop;
import com.hooklite.endshop.data.requirements.BalanceRequirement;
import com.hooklite.endshop.data.requirements.ItemRequirement;
import com.hooklite.endshop.data.requirements.Requirement;
import com.hooklite.endshop.data.rewards.BalanceReward;
import com.hooklite.endshop.data.rewards.CommandReward;
import com.hooklite.endshop.data.rewards.ItemReward;
import com.hooklite.endshop.data.rewards.Reward;
import com.hooklite.endshop.logging.MessageSender;
import com.hooklite.endshop.holders.ActionMenuHolder;
import com.hooklite.endshop.holders.ConfirmMenuHolder;
import com.hooklite.endshop.holders.ItemMenuHolder;
import com.hooklite.endshop.holders.ShopMenuHolder;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

public class Configuration {
    private static final List<RequiredKey> requiredKeys = new ArrayList<>();
    private static final List<ConfigKey> configKeys = new ArrayList<>();
    private static final List<Reward> rewards = new ArrayList<>();
    private static YamlConfiguration defaultConfig;
    private static Map<String, YamlConfiguration> shopConfigs;
    private static List<Shop> shops = new ArrayList<>();
    private static final List<Requirement> requirements = new ArrayList<>();
    private static Plugin plugin;
    private static Economy econ;
    private static Permission perms;
    public static NamespacedKey AMOUNT_KEY;

    static {
        addRequirement(new ItemRequirement());
        addRequirement(new BalanceRequirement());

        addReward(new CommandReward());
        addReward(new BalanceReward());
        addReward(new ItemReward());

        addRequiredKey(new ItemDescription());
        addRequiredKey(new ShopDescription());
        addRequiredKey(new ShopTitle());
        addRequiredKey(new ItemSellable());
        addRequiredKey(new ItemBuyable());
        addRequiredKey(new ItemDisplayItem());
        addRequiredKey(new ItemName());

        addConfigKey(new ItemSlot());
        addConfigKey(new ShopDisplayItem());
        addConfigKey(new ShopSlot());
        addConfigKey(new RequirementAmount());
        addConfigKey(new RequirementReq());
        addConfigKey(new RewardAmount());
        addConfigKey(new RewardConfigKey());
    }

    public static YamlConfiguration getDefaultConfig() {
        return defaultConfig;
    }

    public static List<Shop> getShops() {
        return shops;
    }

    static List<Reward> getRewards() {
        return rewards;
    }

    public static List<RequiredKey> getRequiredKeys() {
        return requiredKeys;
    }

    /**
     * Registers a new reward type if it isn't already registered.
     *
     * @param reward An instance of EReward.
     */
    public static void addReward(Reward reward) {
        if(!rewards.contains(reward))
            rewards.add(reward);
    }

    public static void addRequirement(Requirement requirement) {
        if(!requirements.contains(requirement))
            requirements.add(requirement);
    }

    public static void addRequiredKey(RequiredKey key) {
        if(!configKeys.contains(key) && !requiredKeys.contains(key))
            requiredKeys.add(key);
    }

    public static void addConfigKey(ConfigKey key) {
        if(!configKeys.contains(key) && !requiredKeys.contains(key))
            configKeys.add(key);
    }

    public static List<ConfigKey> getConfigKeys() {
        return configKeys;
    }

    public static Permission getPerms() {
        return perms;
    }

    public static Economy getEcon() {
        return econ;
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    public static List<Requirement> getRequirements() {
        return requirements;
    }

    /**
     * Loads all the required configurations for the plugin to function.
     * <p>
     * THIS METHOD SHOULD ONLY BE CALLED ONCE UPON THE APPLICATION'S LIFETIME!
     */
    public static void configurePlugin(Plugin pl) {
        try {
            plugin = pl;

            AMOUNT_KEY = new NamespacedKey(Configuration.getPlugin(), "amount");

            CommandStatus.enableShopCommand();
            setDefaultConfig();
            setShopConfigs();

            econ = VaultLoader.getEcon();
            perms = VaultLoader.getPerms();
            shops = ShopFactory.getShops(shopConfigs);

            listRegisteredShops();
        }
        catch(Exception e) {
            MessageSender.toConsole(e.getMessage());
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }

    /**
     * Closes the shop inventory if the player has it open.
     */
    public static void closeShopMenus(Collection<? extends Player> players) {
        for(Player player : players) {
            Inventory topInventory = player.getOpenInventory().getTopInventory();

            if(topInventory.getHolder() instanceof ShopMenuHolder || topInventory.getHolder() instanceof ItemMenuHolder || topInventory.getHolder() instanceof ActionMenuHolder || topInventory.getHolder() instanceof ConfirmMenuHolder)
                player.closeInventory();
        }
    }

    /**
     * Reloads all configuration files and gui of the plugin.
     */
    public static void reloadPlugin() {
        try {
            closeShopMenus(plugin.getServer().getOnlinePlayers());

            CommandStatus.enableShopCommand();
            reloadDefaultConfig();
            setShopConfigs();

            shops = ShopFactory.getShops(shopConfigs);
            listRegisteredShops();
        }
        catch(Exception e) {
            MessageSender.toConsole(e.getMessage());
            e.printStackTrace();
            Bukkit.getPluginManager().disablePlugin(plugin);
        }
    }

    /**
     * Sets, reads then saves the default plugin configuration file.
     */
    private static void setDefaultConfig() {
        defaultConfig = (YamlConfiguration) plugin.getConfig();
        plugin.saveDefaultConfig();
    }

    private static void reloadDefaultConfig() {
        plugin.reloadConfig();
        defaultConfig = (YamlConfiguration) plugin.getConfig();
    }

    /**
     * Creates the shops directory and loads the configuration files if they don't exist.
     *
     * @throws NullPointerException          If the list element of config.yml is null.
     * @throws IOException                   If the function failed creating the shops directory or the files that come with it.
     * @throws InvalidConfigurationException If the configuration file is improperly configured.
     */
    private static void setShopConfigs() throws IOException, InvalidConfigurationException {
        shopConfigs = new HashMap<>();

        // Creates the shops directory
        File shopsDirectory = new File(plugin.getDataFolder().getPath(), "shops");
        if(!shopsDirectory.exists()) {
            if(!shopsDirectory.mkdirs())
                throw new IOException("Unable to create shops directory!");
        }

        // Creates a new file for the registered shop if it doesn't exist, then loads the example into it
        for(String shop : defaultConfig.getStringList("shops")) {
            File file = new File(shopsDirectory.getPath(), shop + ".yml");
            if(!file.exists()) {
                if(file.createNewFile())
                    loadExampleConfig(file);
                else
                    throw new IOException("Unable to create shop file!");
            }

            YamlConfiguration configuration = new YamlConfiguration();
            try {
                configuration.load(file);
            }
            catch(InvalidConfigurationException e) {
                throw new InvalidConfigurationException(String.format("File \"%s\" is improperly configured!", file));
            }

            shopConfigs.put(shop + ".yml", configuration);
        }
    }

    /**
     * Outputs the registered shops to the console.
     */
    private static void listRegisteredShops() {
        List<String> shopList = defaultConfig.getStringList("shops");
        StringBuilder registeredShops = new StringBuilder();

        // Creates a string of registered shops then outputs it into console.
        for(int i = 0; i < shopList.size(); i++) {
            if(shopList.size() == 1)
                registeredShops.append(shopList.get(i));
            else if(i == shopList.size() - 1)
                registeredShops.append(shopList.get(i));
            else
                registeredShops.append(shopList.get(i)).append(", ");
        }

        MessageSender.toConsole("Loaded shops: " + registeredShops.toString());
    }

    /**
     * Loads the example configuration data from a resource stream to a file.
     *
     * @param file The file that the data will be loaded into.
     * @throws IOException If the resource stream data could not be loaded.
     */
    private static void loadExampleConfig(File file) throws IOException {

        // Reads the example config file from the resources folder, then saves it to a file.
        new YamlConfiguration();
        YamlConfiguration exampleConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(Configuration.class.getResourceAsStream("/example.yml")));
        exampleConfig.save(file);
    }
}