package com.hooklite.endshop.config;

import com.hooklite.endshop.data.models.Item;
import com.hooklite.endshop.data.models.Page;
import com.hooklite.endshop.data.models.Shop;

import java.util.ArrayList;
import java.util.List;

class PageFactory {

    /**
     * Creates the pages from a list of items.
     * @param items A list of items to be loaded into pages.
     * @return A list of pages generated.
     */
    static List<Page> getPages(Shop shop, List<Item> items) {
        ArrayList<Page> pages = new ArrayList<>();
        int pageAmount = (int) Math.ceil(items.size() / 45.0) == 0 ? 1 : (int) Math.ceil(items.size() / 45.0);

        int j = 1;
        for(int i = 0; i < pageAmount; i++) {
            Page page = new Page();

            // Loads the items into this current page
            List<Item> pageItems = new ArrayList<>();
            while(j <= items.size()) {
                Item item = items.get(j - 1);

                pageItems.add(item);

                if(j % 45 == 0) {
                    j++;
                    break;
                }
                j++;
            }

            page.setNumber(i);
            page.setItems(pageItems);
            page.setInventory(InventoryFactory.getPageInventory(shop, page, pageAmount));

            pages.add(page);
        }

        return pages;
    }
}
