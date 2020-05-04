package com.hooklite.endshop.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class ActionMenuOpenEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();
    private final Player PLAYER;
    private final ItemStack ITEM;
    private final int SLOT;

    public ActionMenuOpenEvent(Player player, ItemStack item, int slot) {
        PLAYER = player;
        ITEM = item;
        SLOT = slot;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public Player getWhoOpened() {
        return PLAYER;
    }

    public ItemStack getItem() {
        return ITEM;
    }

    public int getSlot() {
        return SLOT;
    }
}