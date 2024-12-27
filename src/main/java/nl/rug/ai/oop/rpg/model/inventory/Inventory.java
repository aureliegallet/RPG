package nl.rug.ai.oop.rpg.model.inventory;

import nl.rug.ai.oop.rpg.model.engine.GameEngine;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.*;

/**
 * General abstract class for any inventory of our game.
 * @author Aurélie Gallet
 * @version 1.0
 */
public abstract class Inventory implements Serializable {
    protected transient final String owner;
    protected transient GameEngine.Language language = GameEngine.Language.ENGLISH;
    protected transient GameEngine gameEngine;
    protected transient final Collection<PropertyChangeListener> listeners = new ArrayList<>();
    protected transient Item inspectedItem; //The inventory knows if one of its items is being inspected by the player
    protected ArrayList<Item> inventoryList = new ArrayList<>();


    /**
     * Generates an inventory according to a specific owner and what he owns
     * @param owner Owner of the inventory
     */
    public Inventory(String owner) {
        this.owner = owner;
    }

    /**
     * Generates the default inventory if it is a new game or if the saved inventory has failed to load
     * @param hasPreviousTryFailed Boolean for if the previous inventory has failed to load
     */
    public void initialiseOriginalInventory(boolean hasPreviousTryFailed) {
        try (Scanner fileInput = new Scanner(Objects.requireNonNull(Inventory.class.getResourceAsStream("/inventory/textfiles/" + owner + "Items.txt")))) {
            while (fileInput.hasNextLine()) {
                inventoryList.add(Item.initialiseAnItem(fileInput));
                fileInput.nextLine();
            }
            if (hasPreviousTryFailed) {
                System.out.println("Error in loading saving file. The inventory was set back to its original form.");
            }
            initialiseAdditional();
        } catch (NullPointerException e) {
            System.out.println("Original inventory file not found");
        }
    }

    /**
     * Abstract method to initialise additional elements according to what type of inventory it is
     */
    protected abstract void initialiseAdditional();

    /**
     * Abstract method that retrieves the inventory from the saved files according to the owner
     */
    public abstract void initialiseSavedInventory();


    /* Setting information */

    /**
     * Setter for the GameEngine
     * @param gameEngine GameEngine of our game
     */
    public void setGameEngine(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    /**
     * Setter for so that the inventory is aware of which item is being inspected
     * @param item Item being set as the one being inspected
     */
    public void setInspectedItem(Item item) {
        inspectedItem = item;
        notifyListeners("inspectedItemChange");
    }


    /* Getters */

    /**
     * Method to get the item list of the inventory
     * @return {@link ArrayList<Item>} Items of the inventory
     */
    protected ArrayList<Item> getInventoryList() {
        return inventoryList;
    }


    /* Additional */

    /**
     * Method that allows to add the views to be added as listeners for model changes
     * @param listener Listener to model changes to be added
     */
    public void addListener(PropertyChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * Method that updates the language the inventory is in
     * @param language New language of the inventory
     */
    public void updateLanguage(GameEngine.Language language) {
        this.language = language;
        for (Item item:inventoryList) {
            item.updateLanguage(language);
        }
        notifyListeners("languageChange");
    }

    /**
     * Method to save the game
     * Uses the fact that arraylists and hashmaps are serializable
     */
    public abstract void save();

    /**
     * Method to make to generate a string of the inventory -- also for serializable
     * @return {@link String} inventory string with the contents and the quantities
     */
    public abstract String toString();

    /**
     * Method to notify listeners if the model has been changed
     * @param event String description of the additionally changed event
     */
    protected abstract void notifyListeners(String event);
}
