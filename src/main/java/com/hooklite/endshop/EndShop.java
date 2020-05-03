package com.hooklite.endshop;

import com.hooklite.endshop.commands.CommandManager;
import com.hooklite.endshop.config.Configuration;
import com.hooklite.endshop.listeners.action.TransactionListener;
import com.hooklite.endshop.listeners.inventory.InventoryClickListener;
import com.hooklite.endshop.listeners.navigation.ActionMenuOpenListener;
import com.hooklite.endshop.listeners.navigation.ItemMenuOpenListener;
import com.hooklite.endshop.listeners.navigation.PageNavigationListener;
import com.hooklite.endshop.listeners.navigation.ShopMenuOpenListener;
import org.bukkit.plugin.java.JavaPlugin;

import static org.bukkit.Bukkit.getPluginManager;


public final class EndShop extends JavaPlugin {
    @Override
    public void onEnable() {
        getPluginManager().registerEvents(new InventoryClickListener(), this);
        getPluginManager().registerEvents(new ShopMenuOpenListener(), this);
        getPluginManager().registerEvents(new ItemMenuOpenListener(), this);
        getPluginManager().registerEvents(new ActionMenuOpenListener(), this);
        getPluginManager().registerEvents(new PageNavigationListener(), this);
        getPluginManager().registerEvents(new TransactionListener(), this);
        Configuration.configurePlugin(this);

        getCommand("shop").setExecutor(new CommandManager());
        getCommand("endshop").setExecutor(new CommandManager());
    }

    @Override
    public void onDisable() {
        Configuration.closeShopMenus(this.getServer().getOnlinePlayers());
    }
}
