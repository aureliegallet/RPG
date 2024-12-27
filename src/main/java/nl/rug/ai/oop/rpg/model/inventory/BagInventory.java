package nl.rug.ai.oop.rpg.model.inventory;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * General class for the player's Inventory of our game.
 * @author Aurélie Gallet
 * @version 1.0
 */
public class BagInventory extends Inventory implements Serializable {
    private HashMap<Integer,Integer> quantities = new HashMap<>(); //To store item quantities
    private ArrayList<Item> clothesList = new ArrayList<>();
    private transient ArrayList<ItemRecord> currentArrayOfClothingRecord = new ArrayList<>();
    private transient ArrayList<ItemRecord> currentArrayOfItemRecords = new ArrayList<>();
    private transient boolean hasUndergroundTicket = false;

    /**
     * Generates an inventory according to a specific owner and what he owns
     * @param owner Owner of the inventory
     */
    public BagInventory(String owner) {
        super(owner);
    }

    /**
     * Method to initialise additional elements according to what type of inventory it is
     */
    protected void initialiseAdditional() {
        try (Scanner fileInput = new Scanner(Objects.requireNonNull(BagInventory.class.getResourceAsStream("/inventory/textfiles/quantities.txt")))) {
            while (fileInput.hasNextLine()) {
                int identifier = fileInput.nextInt();
                int quantity = fileInput.nextInt();
                quantities.put(identifier, quantity);
            }
        } catch (NullPointerException e) {
            System.out.println("Inventory quantities could not be loaded");
        }
        this.currentArrayOfItemRecords = makeArrayOfRecordItems(inventoryList);
    }


    /* Item Record */

    /**
     * Item Record so that the items are passed to the view in a way that is does not have any unwanted access
     * In a way, it is a model for the view
     */
    public record ItemRecord(Item item, int quantity) {
    }

    /**
     * Method to make an array of Item Records for the items
     * @return {@link ArrayList<ItemRecord>} Array of item records
     */
    private ArrayList<ItemRecord> makeArrayOfRecordItems(ArrayList<Item> arrayToBeTransformed) {
        ArrayList<ItemRecord> arrayOfRecords = new ArrayList<>();
        for (Item item:arrayToBeTransformed) {
            arrayOfRecords.add(new ItemRecord(item, getQuantityofItem(item)));
        }
        return arrayOfRecords;
    }

    /**
     * Method to get the current array of items
     * @return {@link ArrayList<ItemRecord>} Array of items in the inventory
     */
    public ArrayList<ItemRecord> getCurrentArrayOfItemRecords() {
        return currentArrayOfItemRecords;
    }

    /**
     * Method to get the current array of worn clothing
     * @return {@link ArrayList<ItemRecord>} Array of worn clothing
     */
    public ArrayList<ItemRecord> getCurrentArrayOfClothingRecords() {
        return currentArrayOfClothingRecord;
    }


    /* Getters */

    /**
     * Getter for the quantity hashmap
     * @return Quantity hashmap
     */
    private HashMap<Integer, Integer> getQuantities() {
        return quantities;
    }

    /**
     * Getter for the quantity of a certain item
     * @param item Item for which we request the quantity
     * @return Quantity of the specified item
     */
    public int getQuantityofItem(Item item) {
        if (inventoryList.contains(item) || clothesList.contains(item)) {
            return quantities.get(item.getIdentifier());
        } else {
            return 0;
        }
    }

    /**
     * Getter for if the inventory contains the subway ticket
     * @return Boolean that indicates if the subway ticket is in the inventory
     */
    public boolean getHasUndergroundTicket() {
        return hasUndergroundTicket;
    }

    /**
     * Getter for the clothes a player is wearing
     * @return {@link ArrayList<Item>} Worn clothes
     */
    private ArrayList<Item> getClothesList() {
        return clothesList;
    }

    /**
     * Getter for if the inventory allows an item to be moved from the item list to the current outfit
     * Makes code more readable
     * @param item Item for which we request if it can be worn in the outfit
     * @return Boolean that indicates if an item can be worn in the outfit
     */
    private boolean getIsWearable(Item item) {
        return (item.getEffect() == Item.Effect.ATTACKING || item.getEffect() == Item.Effect.DEFENDING);
    }

    /**
     * Getter for if the inventory allows an item to be moved from it
     * Makes code more readable
     * @param item Item for which we request if it can be moved from the inventory
     * @return Boolean that indicates if an item can be moved from the inventory
     */
    private boolean getCanBeModified(Item item) {
        return (item.getEffect() != Item.Effect.SUBWAY);
    }


    /* Actions */

    /**
     * Removes an item from the inventory while updating the quantities hashmap accordingly
     * @param item Item to be removed
     */
    private void removeWithQuantityUpdate(Item item) {
        int newQuantity = quantities.get(item.getIdentifier()) - 1;
        if (newQuantity == 0) {
            quantities.replace(item.getIdentifier(), 0);
            inventoryList.remove(item);
            inspectedItem = null;
            notifyListeners("inspectedItemChange");
        } else {
            quantities.replace(item.getIdentifier(), newQuantity);
        }
        notifyListenersInventoryChange();
    }

    /**
     * Method to pick up an item and add it to the inventory
     * @param item Item being picked up
     */
    public void addItem(Item item) {
        ArrayList<Item> correspondingItem = inventoryList.stream().filter(item1 -> item1.getIdentifier().equals(item.getIdentifier())).collect(Collectors.toCollection(ArrayList::new));
        if (!correspondingItem.isEmpty()) {
            quantities.replace(item.getIdentifier(), quantities.get(item.getIdentifier()) + 1);
        } else {
            inventoryList.add(item);
            if(quantities.containsKey(item.getIdentifier())){
                quantities.replace(item.getIdentifier(), quantities.get(item.getIdentifier()) + 1);
            } else {
                quantities.put(item.getIdentifier(), 1);
            }
        }
        if (item.getIdentifier() == 200) {
            hasUndergroundTicket = true;
        }
        notifyListenersInventoryChange();
    }

    /**
     * Method to discard an item from the inventory
     * A subway ticket cannot be discarded
     * @param item Item being discarded
     */
    public void removeItem(Item item) {
        if (inventoryList.contains(item) && getCanBeModified(item)) {
            removeWithQuantityUpdate(item);
        }
    }

    /**
     * Method to use an item and remove one unit of it from the inventory
     * If it is wearable, it gets worn
     * If it concerns health, it updates the player's health
     * If it concerns boosting attack or defense, the ites that are worn become more powerful
     * A subway ticket cannot be used
     * @param item Item being used
     */
    public void useItem (Item item) {
        if (inventoryList.contains(item) && getCanBeModified(item)) {
            if (!getIsWearable(item)) {
                if (item.getEffect() == Item.Effect.HEALING || item.getEffect() == Item.Effect.HEALTH_BOOST){
                    gameEngine.updateBoost(item.getEffect(), item.getBoost());
                } else if (item.getEffect() == Item.Effect.DEFENSE_BOOST) {
                    for (Item clothing:clothesList) {
                        if (clothing.getEffect() == Item.Effect.DEFENDING) {
                            clothing.setBoost(clothing.getBoost()+item.getBoost());
                        }
                    }
                } else if (item.getEffect() == Item.Effect.ATTACK_BOOST) {
                    for (Item clothing:clothesList) {
                        if (clothing.getEffect() == Item.Effect.ATTACKING) {
                            clothing.setBoost(clothing.getBoost()+item.getBoost());
                        }
                    }
                }
                removeWithQuantityUpdate(item);
            } else {
                updateClothingList(item);
            }
        }
    }


    /* Additional */

    /**
     * Method to update the worn clothes of a player
     * If there is already an item of the same type, that item goes back to the inventory
     * and the new item is worn.
     * The boost is updated according to the item's boost or
     * according to the boost difference of the two swapped items.
     * @param item Item added to the clothes of the player
     */
    protected void updateClothingList(Item item) {
        //To check if there is an item of the same type
        ArrayList<Item> correspondingClothes = clothesList.stream().filter(item1 -> item1.getName().equals(item.getName())).collect(Collectors.toCollection(ArrayList::new));
        if (getQuantityofItem(item) == 1) {
            inspectedItem = null;
            notifyListeners("inspectedItemChange");
        }
        if (correspondingClothes.isEmpty()) {
            clothesList.add(item);
            removeWithQuantityUpdate(item);
            gameEngine.updateBoost(item.getEffect(), item.getBoost());
        } else {
            clothesList.remove(correspondingClothes.get(0));
            addItem(correspondingClothes.get(0));
            removeWithQuantityUpdate(item);
            clothesList.add(item);
            gameEngine.updateBoost(item.getEffect(), item.getBoost()-correspondingClothes.get(0).getBoost());
        }
        notifyListeners("clothingChange");
        notifyListenersInventoryChange();
    }

    /**
     * Retrieves the inventory from the saved files according to the owner
     */
    public void initialiseSavedInventory() {
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream("src/main/resources/saving_data/" + owner + "Inventory.txt"))) {
            BagInventory temporary = (BagInventory)input.readObject();
            inventoryList = temporary.getInventoryList();
            clothesList = temporary.getClothesList();
            quantities = temporary.getQuantities();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("An inventory save file could not be retrieved properly.");
            initialiseOriginalInventory(true);
        }
        this.currentArrayOfClothingRecord = makeArrayOfRecordItems(clothesList);
        this.currentArrayOfItemRecords = makeArrayOfRecordItems(inventoryList);
    }

    /**
     * Method to save the game
     * Uses the fact that arraylists and hashmaps are serializable
     */
    public void save() {
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("src/main/resources/saving_data/" + owner + "Inventory.txt"))){
            output.writeObject(this);
        } catch (IOException e) {
            System.out.println("Failed save.");
        }
    }

    /**
     * Method to make to generate a string of the inventory -- also for serializable
     * @return {@link String} inventory string with the contents and the quantities
     */
    public String toString() {
        return inventoryList + " " + clothesList + " " + quantities;
    }

    /**
     * Method to notify listeners if the inventory has been changed
     * The lists of item records are updated before being sent
     */
    private void notifyListenersInventoryChange() {
        this.currentArrayOfItemRecords = makeArrayOfRecordItems(inventoryList);
        PropertyChangeEvent inventoryChange = new PropertyChangeEvent(this, "inventoryChange", null, currentArrayOfItemRecords);
        for (PropertyChangeListener listener: listeners) {
            listener.propertyChange(inventoryChange);
        }
    }

    /**
     * Method to notify listeners if additional parts of the model have been changed
     * Such as language change, displayed item change or a clothing change
     * The lists of item records are updated before being sent
     * @param event String description of the additionally changed event
     */
    @Override
    protected void notifyListeners(String event) {
        PropertyChangeEvent eventChange = null;
        switch (event) {
            case "languageChange" -> eventChange = new PropertyChangeEvent(this, "languageChange", null, language);
            case "inspectedItemChange" -> {
                ItemRecord inspectedItemRecord;
                if (inspectedItem != null) {
                    inspectedItemRecord = new ItemRecord(inspectedItem, getQuantityofItem(inspectedItem));
                } else {
                    inspectedItemRecord = new ItemRecord(null, 0);
                }
                eventChange = new PropertyChangeEvent(this, "inspectedItemChange", null, inspectedItemRecord);
            }
            case "clothingChange" -> {
                this.currentArrayOfClothingRecord = makeArrayOfRecordItems(clothesList);
                eventChange = new PropertyChangeEvent(this, "clothingChange", null, currentArrayOfClothingRecord);
            }
        }
        for (PropertyChangeListener listener: listeners) {
            if (eventChange != null) {
                listener.propertyChange(eventChange);
            }
        }
    }
}
