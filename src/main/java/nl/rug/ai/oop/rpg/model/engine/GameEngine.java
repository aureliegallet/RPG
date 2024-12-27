package nl.rug.ai.oop.rpg.model.engine;

import nl.rug.ai.oop.rpg.model.character.Character;
import nl.rug.ai.oop.rpg.model.inventory.BagInventory;
import nl.rug.ai.oop.rpg.model.inventory.Item;
import nl.rug.ai.oop.rpg.model.inventory.MapItems;
import nl.rug.ai.oop.rpg.model.inventory.ProductInventory;
import nl.rug.ai.oop.rpg.model.locations.GameLocation;
import nl.rug.ai.oop.rpg.model.npc.Enemy;
import nl.rug.ai.oop.rpg.model.player.FightNPCModel;
import nl.rug.ai.oop.rpg.model.player.PlayerModel;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.*;

/**
 * General class for the game engine that runs our game.
 * @author All, authors are stated per function.
 */
public class GameEngine {

    //TODO Xu remember to address this NPCs list
    List<Character> NPCs = new ArrayList<>();

    private final Collection<PropertyChangeListener> listeners = new ArrayList<>();

    /**
     * Enumeration of possible game languages
     */
    public enum Language {
        ENGLISH, DUTCH
    }
    private Language language = Language.ENGLISH;

    /**
     * Enumeration of possible game states
     */
    public enum GameState{
        WALKING, BUILDING_PLAYER, MAP, INVENTORY, PICKUP, FIGHTING, POTIONS, ARMORY, DEATH
    }
    private GameState gameState;


    /* All the models of our game */
    private final PlayerModel playerModel;
    private final GameLocation location;
    private final BagInventory inventory;
    private Enemy enemy; //TODO Xu remember to address this variable
    private final MapItems mapItems;
    private final ProductInventory potions;
    private final ProductInventory armory;
    private FightNPCModel fightNPCModel;


    /* Getters for all the needed models or state of our game */

    /**
     * Getter for the game state
     * @author Aurélie Gallet
     * @return {@link GameState} Game state of our game
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * Getter for the player model
     * @author Otto Bervoets
     * @return {@link PlayerModel} Player model of our game
     */
    public PlayerModel getPlayerModel() {
        return playerModel;
    }

    /**
     * Getter for the location model of our game
     * @author Aurélie Gallet
     * @return {@link GameLocation} Location model of our game
     */
    public GameLocation getLocation() {
        return location;
    }

    /**
     * Getter for the player's inventory model of our game
     * @author Aurélie Gallet
     * @return {@link BagInventory} Player's inventory of our game
     */
    public BagInventory getInventory() {
        return inventory;
    }

    /**
     * Getter for the potion shop's model of our game
     * @author Aurélie Gallet
     * @return {@link ProductInventory} Potions shop items of our game
     */
    public ProductInventory getPotions() {
        return potions;
    }

    /**
     * Getter for the armory shop's model of our game
     * @author Aurélie Gallet
     * @return {@link ProductInventory} Armory shop items of our game
     */
    public ProductInventory getArmory() {
        return armory;
    }


    /**
     * Generates a game engine for the game which is a way for our models to communicate
     * Sets all models as variables of this class.
     * Initialises all models that need to be initialised according to if we are starting a new game
     * or if we are using the saved data.
     * Passes this game engine to the models that need it.
     * Adds remaining data that needs to be added.
     * @author Aurélie Gallet
     * @param location Location model
     * @param inventory Inventory model
     * @param enemy Enemy model
     * @param playerModel Player model
     * @param mapItems Map items model
     * @param potions Potion shop model
     * @param armory Armory shop model
     */
    public GameEngine(GameLocation location, BagInventory inventory, Enemy enemy, PlayerModel playerModel, MapItems mapItems, ProductInventory potions, ProductInventory armory) {
        this.location = location;
        this.inventory = inventory;
        this.playerModel = playerModel;
        this.enemy = enemy;
        this.mapItems = mapItems;
        this.potions = potions;
        this.armory = armory;
        location.setGameEngine(this);
        inventory.setGameEngine(this);
        mapItems.setGameEngine(this);
        potions.setGameEngine(this);
        armory.setGameEngine(this);
        if (playerModel.getState() == PlayerModel.State.BUILDING) {
            gameState = GameState.BUILDING_PLAYER;
            inventory.initialiseOriginalInventory(false);
            potions.initialiseOriginalInventory(false);
            armory.initialiseOriginalInventory(false);
            mapItems.initialiseMapItems(false);
        } else {
            gameState = GameState.WALKING;
            inventory.initialiseSavedInventory();
            potions.initialiseSavedInventory();
            armory.initialiseSavedInventory();
            mapItems.initialiseSavedMapItems();
        }
        mapItems.putAllItems();
        addEnemy();
    }

    /**
     * Updates the players statistics depening on the item used
     * @author Otto Bervoets
     * @param effect The effect of the item
     * @param boost The ammount the item effects
     */
    public void updateBoost(Item.Effect effect, int boost) {
        switch (effect) {
            case HEALING -> playerModel.recover(boost);
            case ATTACKING -> playerModel.increaseAllAttack(boost);
            case DEFENDING -> playerModel.increaseDefense(boost);
            case HEALTH_BOOST -> playerModel.increaseMaxHealth(boost);

            /* Otto:more may be added depending on the effect it is also
            possible to influence the strength of one single attack,
            however such items do not yet exist
             */

        }
    }

    /**
     *
     * @author Otto Bervoets
     */
    public void checkPlayerState() {
        if (playerModel.getState().equals(PlayerModel.State.WALKING)) {
            gameState = GameState.WALKING;
        }
        if (playerModel.getState().equals(PlayerModel.State.DEATH)) {
            System.out.println("set state death");
            gameState = GameState.DEATH;
        }
        notifyListenersGameState(gameState);
    }

    /**
     * Needs to be called by Xu with the NPC the user is going to fight
     * @author Otto Bervoets
     * @param NPC the NPC the user is going to fight
     */
    public void playerFight(Character NPC) {
        if (!gameState.equals(GameState.WALKING)) {
            return; //only allowed to start a fight when walking
        }
        gameState = GameState.FIGHTING;
        playerModel.fightNPC(NPC);
        notifyListenersGameState(gameState);
    }

    /**
     * As the NPC's are not done, this can demonstrate how the fighting would show if it was implemented.
     * @author Otto Berveots
     */
    public void demoFight() {
        if (!gameState.equals(GameState.WALKING)) {
            return; //only allowed to start a fight when walking
        }
        gameState = GameState.FIGHTING;
        playerModel.testFight();
        notifyListenersGameState(gameState);
    }

    /**
     * Changes the state of the game according to which view the player should be looking at
     * @author Aurélie Gallet
     * @param changeGameState Indicating the GameState the game should be changing to
     */
    public void setGameState(GameState changeGameState) {
        if (gameState != changeGameState) {
            gameState = changeGameState;
        }
        notifyListenersGameState(gameState);
    }

    /**
     * @return true if the underground ticket is in the inventory; otherwise, false.
     * @author Luca Colli
     */
    public boolean hasUndergroundTicket() {
        return inventory.getHasUndergroundTicket();
    }

    public void addEnemy() {
        try (Scanner fileInput = new Scanner(Objects.requireNonNull(
                BagInventory.class.getResourceAsStream("/npc_setup/enemy_setup.txt")))) {
            while (fileInput.hasNextLine()) {
                int xLocation = fileInput.nextInt();
                int yLocation = fileInput.nextInt();
                String imagePath = "/skins/redguy.png";
                location.addObject("Enemy", this,
                        GameLocation.Location.ISLAND, xLocation,  yLocation, imagePath, false);
            }
        }
    }


    /* Saving, updating and notifying */

    /**
     * Switches to a different language, updating the language of each model.
     * The listeners are then notified.
     * @author Luca Colli
     */
    public void changeLanguage() {
        language = switch (language) {
            case ENGLISH -> Language.DUTCH;
            case DUTCH -> Language.ENGLISH;
        };
        inventory.updateLanguage(language);
        location.updateLanguage(language);
        playerModel.updateLanguage(language);
        potions.updateLanguage(language);
        armory.updateLanguage(language);
        notifyListenersLanguage(language);
    }

    /**
     * Saves the current game progress by calling the save function of each model.
     * @author Luca Colli
     */
    public void save() {
        location.save();
        inventory.save();
        mapItems.save();
        playerModel.save();
        potions.save();
        armory.save();
    }

    /**
     * Allows a view to listen to model changes by adding itself as a listener
     * @author Aurélie Gallet
     * @param listener View that is listening to the game engine
     */
    public void addListener(PropertyChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * Notifies listeners if the game state has been changed
     * @author Aurélie Gallet
     * @param gameState New value of the game state
     */
    private void notifyListenersGameState(GameState gameState) {
        PropertyChangeEvent event = new PropertyChangeEvent(this, "gameState", null, gameState);
        for (PropertyChangeListener listener: listeners) {
            listener.propertyChange(event);
        }
    }

    /**
     * Notifies listeners if the language has been changed
     * @author Aurélie Gallet
     * @param language New value of the language
     */
    private void notifyListenersLanguage(Language language) {
        PropertyChangeEvent event = new PropertyChangeEvent(this, "language", null, language);
        for (PropertyChangeListener listener: listeners) {
            listener.propertyChange(event);
        }
    }
}
