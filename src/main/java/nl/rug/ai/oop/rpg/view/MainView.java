package nl.rug.ai.oop.rpg.view;

import nl.rug.ai.oop.rpg.controler.InventoryController;
import nl.rug.ai.oop.rpg.controler.LocationController;
import nl.rug.ai.oop.rpg.controler.ShopController;
import nl.rug.ai.oop.rpg.controler.npc.EnemyController;
import nl.rug.ai.oop.rpg.controler.player.PlayerBuilderController;
import nl.rug.ai.oop.rpg.controler.player.PlayerController;
import nl.rug.ai.oop.rpg.model.engine.GameEngine;
import nl.rug.ai.oop.rpg.model.player.PlayerModel;
import nl.rug.ai.oop.rpg.view.Player.BuildPlayerView;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;

/**
 * Main view of the game. It allows switching between the game panel and the character selection panel.
 *
 * @author Luca Colli & Otto Bervoets. The main author is indicated above each function.
 * @version 1.0
 */
public class MainView extends JPanel {
    GameView gameView;
    BuildPlayerView buildPlayerView;

    /**
     * Constructor of the view. The game panel and the character selection panel are created here.
     * @param frame the frame of the game.
     *
     * @author Luca Colli
     */
    public MainView(JFrame frame) {
        gameView = new GameView(frame);
        buildPlayerView = new BuildPlayerView();

        setLayout(new BorderLayout());
        add(buildPlayerView);
    }

    /**
     * Setup function of the view. All the required controllers are passed to the inner views.
     * @param playerBuilderController the controller of the player builder
     * @param playerController the controller of the player
     * @param locationController the controller of the location
     * @param inventoryController the controller of the inventory
     * @param enemyController the controller of the enemy
     * @param potionsController the controller of the potions shop
     * @param armoryController the controller of the armory
     * @param gameEngine the general engine of the game
     *
     * @author Luca Colli
     */
    public void setup(PlayerBuilderController playerBuilderController, PlayerController playerController, LocationController locationController, InventoryController inventoryController, EnemyController enemyController, ShopController potionsController, ShopController armoryController, GameEngine gameEngine) {
        buildPlayerView.setUp(playerBuilderController, gameEngine);
        gameView.setup(playerController, locationController, inventoryController, enemyController, potionsController, armoryController, gameEngine);

        gameEngine.getPlayerModel().addListener(this::displayGame);
    }

    /**
     * Method to display the game
     * @author Otto Bervoets
     */
    private void displayGame(PropertyChangeEvent evt){
        if(!evt.getPropertyName().equals("state")) {
            return;
        }
        if (evt.getNewValue().equals(PlayerModel.State.BUILDING)) {
            removeAll();
            add(buildPlayerView);
        } else {
            removeAll();
            add(gameView);
            gameView.requestFocus();
        }
    }

}
