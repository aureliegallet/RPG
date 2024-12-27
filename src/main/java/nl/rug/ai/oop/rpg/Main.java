package nl.rug.ai.oop.rpg;

import nl.rug.ai.oop.rpg.controler.InventoryController;
import nl.rug.ai.oop.rpg.controler.LocationController;
import nl.rug.ai.oop.rpg.controler.ShopController;
import nl.rug.ai.oop.rpg.controler.npc.EnemyController;
import nl.rug.ai.oop.rpg.controler.player.PlayerBuilderController;
import nl.rug.ai.oop.rpg.controler.player.PlayerController;
import nl.rug.ai.oop.rpg.model.engine.GameEngine;
import nl.rug.ai.oop.rpg.model.inventory.BagInventory;
import nl.rug.ai.oop.rpg.model.inventory.MapItems;
import nl.rug.ai.oop.rpg.model.inventory.ProductInventory;
import nl.rug.ai.oop.rpg.model.locations.GameLocation;
import nl.rug.ai.oop.rpg.model.npc.Enemy;
import nl.rug.ai.oop.rpg.model.player.PlayerModel;
import nl.rug.ai.oop.rpg.view.MainView;

import javax.swing.*;
import java.awt.*;

/**
 * General main class for our game.
 *
 * @author Otto Bervoets
 * @version 1.0
 */
public class Main {
    private final static Dimension FRAME_DIMENSION = new Dimension(1024, 768);
    public static void main(String[] args) {

        JFrame frame = new JFrame("Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(FRAME_DIMENSION);
        frame.setMinimumSize(FRAME_DIMENSION);
        frame.setLocationRelativeTo(null);

        /* Models */
        GameLocation location = new GameLocation();
        BagInventory inventory = new BagInventory("player");
        MapItems mapItems = new MapItems();
        ProductInventory armory =  new ProductInventory("armory");
        ProductInventory potions =  new ProductInventory("potions");
        Enemy enemy = new Enemy();
        PlayerModel playerModel = new PlayerModel();
        GameEngine gameEngine = new GameEngine(location, inventory, enemy, playerModel, mapItems, potions, armory);

        /* Controllers */
        LocationController locationController = new LocationController(location);
        InventoryController inventoryController = new InventoryController(gameEngine, inventory, mapItems);
        ShopController potionsController = new ShopController(potions);
        ShopController armoryController = new ShopController(armory);
        EnemyController enemyController = new EnemyController(enemy);
        PlayerController playerController = new PlayerController(playerModel, gameEngine);
        PlayerBuilderController playerBuilderController = new PlayerBuilderController(gameEngine.getPlayerModel(), gameEngine);

        MainView mainView = new MainView(frame);
        mainView.setup(playerBuilderController, playerController, locationController, inventoryController, enemyController, potionsController, armoryController, gameEngine);
        frame.add(mainView);
        playerModel.takeDamage(0);
        frame.setVisible(true);
    }
}
