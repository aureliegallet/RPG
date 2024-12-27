package nl.rug.ai.oop.rpg.controler.player;

import nl.rug.ai.oop.rpg.model.character.Character;
import nl.rug.ai.oop.rpg.model.engine.GameEngine;
import nl.rug.ai.oop.rpg.model.player.PlayerModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * @author ottobervoets
 * Controlls the "Building" phase of the player. The view controlling this conroller is the BuildPlayerView
 *
 */

public class PlayerBuilderController implements ActionListener {
    private JTextField jTextField;
    private final PlayerModel player;
    private final GameEngine gameEngine;

    /**
     * Set the textfield the controller needs to extract the player name from.
     * @param jTextField textfield that contains the player name
     */
    public void setjTextField(JTextField jTextField) {
        this.jTextField = jTextField;
    }

    /**
     * Constructs a player builder controller.
     * @param player playerModel to control.
     * @param gameEngine The game engine is needed to let the game engine check whether the player is build
     *                   and if it can change the state from BUILDING_PLAYER to WALKING.
     */
    public PlayerBuilderController(PlayerModel player, GameEngine gameEngine) {
        this.player = player;
        this.gameEngine = gameEngine;
    }

    /**
     * Processes the recieved action. Either sets the faction of the player or sets the name of the player.
     * Each time the gameEngine needs to check whether the player is build as the playerModel does not know
     * the gameEngine.
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "_Fire" -> player.setFaction(Character.Faction.FIRE);
            case "_Wood" -> player.setFaction(Character.Faction.WOOD);
            case "_Water" -> player.setFaction(Character.Faction.WATER);
            case "submit" -> player.setName(jTextField.getText());
        }
        gameEngine.checkPlayerState();
    }
}
