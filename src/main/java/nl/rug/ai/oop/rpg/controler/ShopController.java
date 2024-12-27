package nl.rug.ai.oop.rpg.controler;

import nl.rug.ai.oop.rpg.model.inventory.Item;
import nl.rug.ai.oop.rpg.model.inventory.ProductInventory;

/**
 * General class for the controllers of the shops of our game.
 * @author Aurélie Gallet
 * @version 1.0
 */
public class ShopController {
    private final ProductInventory shop;

    /**
     * Generates a shop controller according to a shop
     * @param shop items in the shop
     */
    public ShopController(ProductInventory shop) {
        this.shop = shop;
    }

    /**
     * Method for the merchant to sell an item
     * @param item Item sold by the merchant
     */
    public void buyFromMerchant(Item item) {
        shop.sell(item);
    }

    /**
     * Method for the merchant to buy an item
     * @param item Item sold bought the merchant
     */
    public void sellToMerchant(Item item) {
        shop.buy(item);
    }

    /**
     * Method to tell a merchant which item is being inspected
     * @param item Item that is inspected
     */
    public void setInspectedItem (Item item) {
        shop.setInspectedItem(item);
    }
}
