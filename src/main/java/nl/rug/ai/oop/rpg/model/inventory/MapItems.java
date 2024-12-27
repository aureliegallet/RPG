package nl.rug.ai.oop.rpg.model.inventory;

import nl.rug.ai.oop.rpg.model.engine.GameEngine;
import nl.rug.ai.oop.rpg.model.locations.GameLocation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.util.*;

/**
 * General class for the Map Items of our game.
 * @author Aurélie Gallet
 * @version 1.0
 */
public class MapItems implements Serializable {
    private GameEngine gameEngine;
    private final Collection<PropertyChangeListener> listeners = new ArrayList<>();
    private ArrayList<Item> mapItems = new ArrayList<>();
    private final ArrayList<int[]> possibleLocations = new ArrayList<>();
    public void addListener(PropertyChangeListener listener){
        listeners.add(listener);
    }


    /**
     * Generates a repertoire of map items
     */
    public MapItems(){
    }

    /**
     * Method to initialise all items and their possible locations
     */
    public void initialiseMapItems(boolean hasPreviousTryFailed) {
        try (Scanner fileInput = new Scanner(Objects.requireNonNull(MapItems.class.getResourceAsStream("/inventory/textfiles/mapItems.txt")))) {
            while (fileInput.hasNextLine()) {
                int quantity = fileInput.nextInt();
                Item item = Item.initialiseAnItem(fileInput);
                for (int repetition = 0; repetition < quantity; ++repetition) {
                    mapItems.add(item);
                }
                fileInput.nextLine();
            }
        } catch (NullPointerException e) {
            System.out.println("The map item file could not be retrieved");
        }
        if (hasPreviousTryFailed) {
            System.out.println("The map items were initialised back to their original state");
        }
        initialisePossiblePositions();
    }

    /**
     * Method to initialise all saved map items and their possible locations
     */
    public void initialiseSavedMapItems() {
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream("src/main/resources/saving_data/mapItems.txt"))) {
            this.mapItems = (ArrayList<Item>) input.readObject();
            notifyListeners();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("The map items could not be retrieved.");
            initialiseMapItems(true);
        }
        initialisePossiblePositions();
    }

    /**
     * Initialises the possible positions for the items
     */
    private void initialisePossiblePositions() {
        try (Scanner fileInput = new Scanner(Objects.requireNonNull(MapItems.class.getResourceAsStream("/inventory/textfiles/possibleLocations.txt")))) {
            while (fileInput.hasNextLine()) {
                int[] coordinates = new int[2];
                coordinates[0] = fileInput.nextInt();
                coordinates[1] = fileInput.nextInt();
                possibleLocations.add(coordinates);
            }
        } catch (NullPointerException e) {
            System.out.println("The map item positions file could not be retrieved");
        }
        notifyListeners();
    }

    /**
     * Method to set the game engine variable of this class
     * @param gameEngine game engine of our game
     */
    public void setGameEngine(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    /**
     * Method to put all items on the map
     */
    public void putAllItems() {
        if (!possibleLocations.isEmpty()) {
            for (Item item:mapItems) {
                Random random = new Random();
                int positionIndex = random.nextInt(possibleLocations.size());
                int[] position = possibleLocations.get(positionIndex);
                possibleLocations.remove(positionIndex);
                gameEngine.getLocation().addObject(item.getIdentifier().toString(), item, GameLocation.Location.ISLAND, position[0], position[1], item.getItemImagePath(),true);
            }
        }
    }

    /**
     * Method to remove an item from the map after it gets picked up
     */
    public void removeItem(Item item) {
        mapItems.remove(item);
        gameEngine.getLocation().removeObjectAtPlayerCoordinates();
    }

    /**
     * Method to save the remaining items that should be on the map
     */
    public void save() {
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream("src/main/resources/saving_data/mapItems.txt"))){
            output.writeObject(mapItems);
        } catch (IOException e) {
            System.out.println("Failed save.");
        }
    }

    /**
     * Method to notify listeners if the map content has changed
     */
    private void notifyListeners() {
        PropertyChangeEvent event = new PropertyChangeEvent(this, "mapContent", null, null);
        for (PropertyChangeListener listener: listeners) {
            listener.propertyChange(event);
        }
    }
}
