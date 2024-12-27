package nl.rug.ai.oop.rpg.model.inventory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.ArrayList;

/**
 * General class for the shop inventories of our game.
 * @author Aurélie Gallet
 * @version 1.0
 */
public class ProductInventory extends Inventory implements Serializable {
    private transient ArrayList<ProductRecord> currentArrayOfProductRecords;

    /**
     * Generates a product inventory according to a specific owner and what he owns
     * @param owner Owner of the inventory
     */
    public ProductInventory(String owner) {
        super(owner);
    }

    /**
     * Method to initialise additional elements according to what type of inventory it is
     */
    public void initialiseAdditional() {
        this.currentArrayOfProductRecords = makeArrayOfRecordProducts(inventoryList);
    }

    /**
     * Method that enables a merchant to sell an item to a player
     * The merchant's inventory does not get updated because he should have infinity amount of everything
     * @param item Item that is sold by the merchant
     */
    public void sell(Item item) {
        if (inventoryList.contains(item) && gameEngine.getPlayerModel().canSpend(item.getPrice())) {
            gameEngine.getInventory().addItem(item);
            gameEngine.getPlayerModel().spendMoney(item.getPrice());
        }
    }

    /**
     * Method that enables a merchant to buy an item from a player
     * The merchant's inventory does not get updated because he should have infinity amount of everything
     * @param item Item that is bought by the merchant
     */
    public void buy(Item item) {
        gameEngine.getPlayerModel().addMoney(item.getPrice()*3/4);
        gameEngine.getInventory().removeItem(item);
    }


    /* Product Record */

    /**
     * Product Record so that the products are passed to the view in a way that is does not have any unwanted access
     * In a way, it is a model for the view
     */
    public record ProductRecord(Item item) {
    }

    /**
     * Method to make an array of Product Records for the products
     * @return {@link ArrayList<ProductRecord>} Array of product records
     */
    public ArrayList<ProductRecord> makeArrayOfRecordProducts(ArrayList<Item> arrayToBeTransformed) {
        ArrayList<ProductRecord> arrayOfRecords = new ArrayList<>();
        for (Item item:arrayToBeTransformed) {
            arrayOfRecords.add(new ProductRecord(item));
        }
        return arrayOfRecords;
    }

    /**
     * Method to get the current array of products
     * @return {@link ArrayList<ProductRecord>} Array of product records
     */
    public ArrayList<ProductRecord> getCurrentArrayOfProductRecords() {
        return currentArrayOfProductRecords;
    }

    /**
     * Retrieves the inventory from the saved files according to the owner
     */
    public void initialiseSavedInventory() {
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream("src/main/resources/saving_data/" + owner + "Inventory.txt"))) {
            inventoryList = (ArrayList<Item>)input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("An inventory save file could not be retrieved properly.");
            initialiseOriginalInventory(true);
        }
        this.currentArrayOfProductRecords = makeArrayOfRecordProducts(inventoryList);
    }

    /**
     * Method to save the game
     * Uses the fact that arraylists and hashmaps are serializable
     */
    public void save() {
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("src/main/resources/saving_data/" + owner + "Inventory.txt"))){
            output.writeObject(inventoryList);
        } catch (IOException e) {
            System.out.println("Failed save.");
        }
    }

    /**
     * Method to make to generate a string of the inventory -- also for serializable
     * @return {@link String} inventory string with the contents and the quantities
     */
    public String toString() {
        return inventoryList.toString();
    }

    /**
     * Method to notify listeners if the model has been changed in terms of language or inspected item
     * The list of product records is updated before being sent
     * @param eventName String description of the changed event
     */
    @Override
    protected void notifyListeners(String eventName) {
        PropertyChangeEvent event = null;
        if (eventName.equals("languageChange")) {
            event = new PropertyChangeEvent(this, "languageChange", null, language);
        } else if (eventName.equals("inspectedItemChange")) {
            ProductRecord item = new ProductRecord(inspectedItem);
            event = new PropertyChangeEvent(this, "inspectedItemChange", null, item);
        }
        for (PropertyChangeListener listener: listeners) {
            if (eventName.equals("languageChange")) {
                listener.propertyChange(event);
            } else if (eventName.equals("inspectedItemChange")) {
                listener.propertyChange(event);
            }
        }
    }
}
