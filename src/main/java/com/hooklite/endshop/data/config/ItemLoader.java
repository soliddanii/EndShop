package com.hooklite.endshop.data.config;

import com.hooklite.endshop.data.models.EItem;
import com.hooklite.endshop.data.rewards.RewardAction;
import com.hooklite.endshop.logging.Colors;
import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

class ItemLoader {

    /**
     * Deserializes the data from a config file.
     *
     * @param config The configuration file.
     * @return A list of EItem data models.
     * @throws InvalidConfigurationException If the configuration file is improperly configured.
     * @throws NullPointerException          If the configuration section doesn't exist.
     */
    static List<EItem> getModels(YamlConfiguration config) throws InvalidConfigurationException, NullPointerException {
        List<EItem> items = new ArrayList<>();
        Set<String> itemKeys = config.getConfigurationSection("items").getKeys(false);

        if (!itemKeys.isEmpty()) {
            for (String item : itemKeys) {
                EItem eItem = new EItem();

                Material displayItemMaterial = Material.matchMaterial(config.getString(String.format("items.%s.display-item", item)));
                List<String> description = new ArrayList<>();

                if (displayItemMaterial == null)
                    throw new InvalidConfigurationException(String.format("display-item in item \"%s\" is improperly configured!", item));

                if (!config.getStringList(String.format("items.%s.description", item)).isEmpty()) {
                    description = config.getStringList(String.format("items.%s.description", item));

                    for (int i = 0; i < description.size(); i++) {
                        description.set(i, Colors.loadColors(description.get(i)));
                    }
                }

                eItem.name = Colors.loadColors(config.getString(String.format("items.%s.name", item)));
                eItem.description = description;
                eItem.slot = config.getInt(String.format("items.%s.slot", item));
                eItem.buyPrice = config.getDouble(String.format("items.%s.buy-price", item));
                eItem.sellPrice = config.getDouble(String.format("items.%s.sell-price", item));
                eItem.buyReward = RewardLoader.getModel(config, item, RewardAction.BUY);
                eItem.sellReward = RewardLoader.getModel(config, item, RewardAction.SELL);

                ItemStack displayItem = new ItemStack(displayItemMaterial, 1);
                ItemMeta meta = displayItem.getItemMeta();
                meta.setDisplayName(eItem.name);
                meta.setLore(eItem.description);
                displayItem.setItemMeta(meta);

                eItem.displayItem = displayItem;

                items.add(eItem);
            }
        }

        return items;
    }
}
