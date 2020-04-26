package com.hooklite.endshop.listeners;

import com.hooklite.endshop.data.config.Configuration;
import com.hooklite.endshop.data.models.EShop;
import com.hooklite.endshop.shop.ShopMenu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class InventoryEventListener implements Listener {
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getClickedInventory() != null) {
            int clickedSlot = event.getSlot();
            Inventory clickedInventory = event.getClickedInventory();

            if (clickedInventory.getHolder() instanceof ShopMenu) {
                for (EShop shop : Configuration.getShops()) {
                    if (shop.slot == clickedSlot) {
                        event.getWhoClicked().openInventory(shop.pages.get(0).getInventory());
                        break;
                    }
                }
                event.setCancelled(true);
            }
        }
    }
}
