package com.hooklite.endshop.config;

import com.hooklite.endshop.data.models.Item;
import com.hooklite.endshop.data.models.Page;
import com.hooklite.endshop.data.models.Shop;

import java.util.ArrayList;
import java.util.List;

class PageLoader {

    /**
     * Creates a list of new pages from the given items.
     *
     * @param items A list of items that will be put into multiple pages.
     * @return A list EPage data models.
     */
    static List<Page> getModels(List<Item> items, Shop shop) {
        ArrayList<Page> pages = new ArrayList<>();
        int pageAmount = (int) Math.ceil(items.size() / 45.0) == 0 ? 1 : (int) Math.ceil(items.size() / 45.0);

        int j = 0;
        for(int i = 0; i < pageAmount; i++) {
            Page page = new Page();
            page.setNumber(i);

            // Loads the items into this current page
            ArrayList<Item> eItems = new ArrayList<>();
            while(j < items.size()) {
                eItems.add(items.get(j));

                if(j % 44 == 0 && j != 0) {
                    j++;
                    break;
                }
                j++;
            }

            page.setItems(eItems);


            pages.add(page);
        }

        return pages;
    }

    static void setInventory(Shop shop, int pageNumber) {
        shop.pages.get(pageNumber).setInventory(MenuLoader.getPageMenu(shop, pageNumber));
    }
}