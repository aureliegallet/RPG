package nl.rug.ai.oop.rpg.view;

import nl.rug.ai.oop.rpg.controler.InventoryController;
import nl.rug.ai.oop.rpg.controler.LocationController;
import nl.rug.ai.oop.rpg.controler.ShopController;
import nl.rug.ai.oop.rpg.controler.npc.EnemyController;
import nl.rug.ai.oop.rpg.controler.player.PlayerController;
import nl.rug.ai.oop.rpg.model.character.Character;
import nl.rug.ai.oop.rpg.model.engine.GameEngine;
import nl.rug.ai.oop.rpg.model.locations.LocationObject;
import nl.rug.ai.oop.rpg.view.Player.FightView;
import nl.rug.ai.oop.rpg.view.Player.PlayerDeath;
import nl.rug.ai.oop.rpg.view.inventory.BigInventoryView;
import nl.rug.ai.oop.rpg.view.inventory.InventoryView;
import nl.rug.ai.oop.rpg.view.inventory.ObjectPickupView;
import nl.rug.ai.oop.rpg.view.inventory.ShopView;
import nl.rug.ai.oop.rpg.view.location.LocationView;
import nl.rug.ai.oop.rpg.view.location.MapOnlyView;
import nl.rug.ai.oop.rpg.view.npc.onmap.DialogView;

import javax.swing.*;
import java.awt.*;


/**
 * General class for the overall Game View of our game.
 * @author Aurélie Gallet (with some outside additions by Xu that do not work properly, signaled by remaining TODOs)
 * @version 1.0
 */
public class GameView extends JPanel {
    public static final Color MAIN_DARK_COLOR = new Color(136, 76, 22);
    public static final Color MAIN_LIGHT_COLOR = new Color(185, 122, 86);

    private final JFrame frame;

    /* All views */
    private final MenuBarView menuBarView;
    private final LocationView locationView;
    private final MapOnlyView bigMapView;
    private final InventoryView inventoryView;
    private final BigInventoryView bigInventoryView;
    private final ObjectPickupView objectPickupView;
    private final DialogView dialogView;
    private final FightView fightView;
    private final ShopView shopViewPotions;
    private final ShopView shopViewArmory;
    private final PlayerDeath playerDeath;

    private GameEngine.GameState currentView = GameEngine.GameState.WALKING;

    /**
     * Sets the new dimensions for the shown elements of the game view each time revalidate is called.
     */
    @Override
    public void invalidate() {
        super.invalidate();
        menuBarView.setPreferredSize(new Dimension(frame.getWidth(), frame.getHeight() / 14));

        switch (currentView) {
            case WALKING -> inventoryView.setPreferredSize(new Dimension(frame.getWidth(), frame.getHeight() / 4));
            case INVENTORY -> bigInventoryView.setPreferredSize(new Dimension(frame.getWidth(), frame.getHeight() * 13 / 14));
            case MAP -> bigMapView.setPreferredSize(new Dimension(frame.getWidth(), frame.getHeight() - frame.getHeight() / 14));
            case PICKUP -> {
                objectPickupView.setPreferredSize(new Dimension(frame.getWidth() / 4, frame.getHeight() * 19 / 28));
                locationView.setPreferredSize(new Dimension(frame.getWidth() * 3 / 4, frame.getHeight() * 19 / 28));
            }
        }
    }

    /**
     * Method to request focus for the location view when we go from the main view to this view
     */
    @Override
    public void requestFocus() {
        super.requestFocus();
        locationView.requestFocus();
    }

    /**
     * Generates a game view for our game
     * @param frame Frame of our game
     */
    public GameView(JFrame frame) {
        this.frame = frame;
        setLayout(new BorderLayout());

        /* Menu Bar */
        this.menuBarView = new MenuBarView();
        add(menuBarView, BorderLayout.NORTH);

        /* Location */
        this.locationView = new LocationView();
        this.bigMapView = new MapOnlyView();
        add(locationView, BorderLayout.CENTER);

        /* Inventory and Shops*/
        this.inventoryView = new InventoryView();
        this.bigInventoryView = new BigInventoryView();
        this.shopViewPotions = new ShopView(this);
        this.shopViewArmory = new ShopView(this);
        add(inventoryView, BorderLayout.SOUTH);

        /* ObjectPickupPrompt */
        this.objectPickupView = new ObjectPickupView(this);

        /* Dialog */
        this.dialogView = new DialogView(this);

        /* Fight view */
        this.fightView = new FightView();

        /* Player death */
        this.playerDeath = new PlayerDeath();
    }

    /**
     * Method to set up this view and all playing subviews of the game.
     * Allows this view to listen to different models using the game engine get methods.
     * Makes this view a listener of the player, location and gameEngine so that it can update accordingly
     * @param playerController Controller of the player
     * @param locationController Controller of the location
     * @param inventoryController Controller of the player's inventory
     * @param enemyController Controller of the enemy (unfortunately never used here but should be used)
     * @param potionsController Controller of the potions shop
     * @param armoryController Controller of the armory shop
     * @param gameEngine Game engine of our game
     */
    public void setup(PlayerController playerController, LocationController locationController, InventoryController inventoryController, EnemyController enemyController, ShopController potionsController, ShopController armoryController, GameEngine gameEngine){
        /* Sets up the other views */
        menuBarView.setup(gameEngine);
        locationView.setup(gameEngine.getLocation(), locationController);
        bigMapView.setup(gameEngine.getLocation());
        inventoryView.setup(gameEngine.getInventory(), inventoryController);
        bigInventoryView.setup(gameEngine.getInventory(), inventoryController);
        objectPickupView.setup(gameEngine, inventoryController);
        fightView.setup(playerController, gameEngine.getPlayerModel());
        shopViewPotions.setup(gameEngine.getPotions(), gameEngine.getInventory(), potionsController);
        shopViewArmory.setup(gameEngine.getArmory(), gameEngine.getInventory(), armoryController);
        playerDeath.setup(gameEngine);

        /* Makes this view a listener of some models */
        gameEngine.getPlayerModel().addListener(evt -> {
            if (evt.getPropertyName().equals("faction")) {
                locationView.setPlayerImage((Character.Faction) evt.getNewValue());
                menuBarView.setFaction((Character.Faction) evt.getNewValue());
            }
        });
        gameEngine.getLocation().addListener(evt -> {
            if (evt.getPropertyName().equals("bumpingIntoObject")) {
                LocationObject obj = (LocationObject) evt.getNewValue();
                if (obj.reference().equals("Enemy")) {
                    updateViewTo(GameEngine.GameState.FIGHTING);
                    gameEngine.demoFight(); //Otto Bervoets: as the NPC class is still disfunctional, this atleast showcases a demo fight.
                }
            }
        });
        gameEngine.addListener(evt -> {
            if (evt.getPropertyName().equals("gameState")) {
                updateViewTo((GameEngine.GameState) evt.getNewValue());
            }
        });
    }

    /**
     * Updates the game view according to the current game state and sets the current view.
     * An easy way to test the dialogue view is to replace the fighting view with the dialogue view.
     * @param newView Game State the view should be changed to
     */
    public void updateViewTo (GameEngine.GameState newView) {
        BorderLayout layout = (BorderLayout)this.getLayout();

        /* Removes what generally should be removed if possible */
        if (currentView == GameEngine.GameState.PICKUP) {
            this.remove(layout.getLayoutComponent(BorderLayout.WEST));
            this.remove(layout.getLayoutComponent(BorderLayout.EAST));
        } else if (currentView != GameEngine.GameState.BUILDING_PLAYER) {
            this.remove(layout.getLayoutComponent(BorderLayout.CENTER));
        }

        /* Adds what should be added according to the game state and removes anything extra if needed */
        switch (newView) {
            case WALKING -> {
                add(locationView, BorderLayout.CENTER);
                locationView.requestFocus();
                if (currentView == GameEngine.GameState.INVENTORY) {
                    add(bigInventoryView.getInventoryFromBig(), BorderLayout.SOUTH);
                } else {
                    add(inventoryView, BorderLayout.SOUTH);
                }
            }
            case MAP -> {
                if (layout.getLayoutComponent(BorderLayout.SOUTH) != null) {
                    this.remove(layout.getLayoutComponent(BorderLayout.SOUTH));
                }
                add(bigMapView, BorderLayout.CENTER);
            }
            case INVENTORY -> {
                if (layout.getLayoutComponent(BorderLayout.SOUTH) != null) {
                    this.remove(layout.getLayoutComponent(BorderLayout.SOUTH));
                }
                bigInventoryView.setInventoryViewBig(inventoryView);
                add(bigInventoryView, BorderLayout.CENTER);
            }
            case PICKUP -> {
                add(objectPickupView, BorderLayout.EAST);
                add(locationView, BorderLayout.WEST);
            }
            case FIGHTING -> {
                if (layout.getLayoutComponent(BorderLayout.SOUTH) != null) {
                    this.remove(layout.getLayoutComponent(BorderLayout.SOUTH));
                }
                add(fightView, BorderLayout.CENTER);
            }
            case POTIONS -> add(shopViewPotions, BorderLayout.CENTER);
            case ARMORY -> add(shopViewArmory, BorderLayout.CENTER);
            case DEATH -> {
                remove(menuBarView);
                if (layout.getLayoutComponent(BorderLayout.SOUTH) != null) {
                    this.remove(layout.getLayoutComponent(BorderLayout.SOUTH));
                }
                add(playerDeath, BorderLayout.CENTER);
            }
        }
        currentView = newView;
        revalidate();
        repaint();
    }

    //TODO Xu remember to address this
    public void addDialogView() {
        add(dialogView, BorderLayout.CENTER);
        revalidate();
    }

    //TODO Xu remember to address this
    public void removeDialogView() {
        remove(locationView);
        remove(dialogView);
        add(locationView, BorderLayout.CENTER);
        locationView.requestFocus();
        revalidate();
    }
}
