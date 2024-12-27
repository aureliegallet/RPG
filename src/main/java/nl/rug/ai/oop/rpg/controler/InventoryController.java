package nl.rug.ai.oop.rpg.controler;

import nl.rug.ai.oop.rpg.model.engine.GameEngine;
import nl.rug.ai.oop.rpg.model.inventory.BagInventory;
import nl.rug.ai.oop.rpg.model.inventory.Item;
import nl.rug.ai.oop.rpg.model.inventory.MapItems;


/**
 * General class for the controller of the player inventory and map items list of our game.
 * @author Aurélie Gallet
 * @version 1.0
 */
public class InventoryController {
    private final GameEngine gameEngine;
    private final BagInventory inventory;
    private final MapItems mapItems;

    /**
     * Generates an inventory controller that controls the player's inventory and the map items that go into the inventory
     * the player's inventory
     * @param inventory player's inventory
     * @param mapItems items in the map
     */
    public InventoryController(GameEngine gameEngine, BagInventory inventory, MapItems mapItems){
        this.gameEngine = gameEngine;
        this.inventory = inventory;
        this.mapItems = mapItems;
    }

    /**
     * Method to discard an item
     * @param item Item to be discarded
     */
    public void discardItem (Item item) {
        inventory.removeItem(item);
    }

    /**
     * Method to use an item
     * @param item Item to be used
     */
    public void useItem (Item item) {
        inventory.useItem(item);
    }

    /**
     * Method to tell an inventory which item is being inspected
     * @param item Item that is inspected
     */
    public void setInspectedItem(Item item) {
        inventory.setInspectedItem(item);
    }

    /**
     * Method to pick up an item
     * @param item Item to be picked up
     */
    public void pickUpItem(Item item) {
        mapItems.removeItem(item);
        inventory.addItem(item);
        gameEngine.setGameState(GameEngine.GameState.WALKING);
    }
}
