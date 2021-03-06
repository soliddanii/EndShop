package com.hooklite.endshop.data.models;

import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Shop extends DataModel implements Serializable {
    public String title;
    public List<String> description;
    public ItemStack displayItem;
    public int slot;
    public List<Page> pages = new ArrayList<>();
}
