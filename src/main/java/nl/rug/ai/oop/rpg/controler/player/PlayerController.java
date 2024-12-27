package nl.rug.ai.oop.rpg.controler.player;

import nl.rug.ai.oop.rpg.model.character.Character;
import nl.rug.ai.oop.rpg.model.engine.GameEngine;
import nl.rug.ai.oop.rpg.model.player.PlayerModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author Otto Bervoets
 * Controlls the player during fighting an NPC. Whether the user will fight an NPC should be managed by
 * the "NPC person (Xu)"
 */

public class PlayerController implements ActionListener {
    private final PlayerModel player;
    private final GameEngine gameEngine;

    /**
     * Constructs the controller with the player model that it's controlling
     * @param player the player model to control
     */
    public PlayerController(PlayerModel player, GameEngine gameEngine) {
        this.player = player;
        this.gameEngine = gameEngine;
    }

    /**
     * Based on the action event e lets the player use the designated attack.
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "water" -> player.attack(Character.Faction.WATER);
            case "wood" -> player.attack(Character.Faction.WOOD);
            case "fire" -> player.attack(Character.Faction.FIRE);
            case "continueGame" -> player.continueGame();
        }
        gameEngine.checkPlayerState();
    }
}
