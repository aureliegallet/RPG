package nl.rug.ai.oop.rpg.controler;

import nl.rug.ai.oop.rpg.model.engine.GameEngine;
import nl.rug.ai.oop.rpg.view.MenuBarView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static nl.rug.ai.oop.rpg.view.GameView.MAIN_LIGHT_COLOR;


/**
 * Controller for an item of the menu bar.
 * Disclaimer:
 * since the JLabel component is not a mandatory field (can be null) and only its background is slightly modified,
 * without altering any variables, text or the way the user can interact with the interface,
 * I assume that an MVC pattern would be excessive for this kind of application.
 * Therefore, the background color is modified directly in the controller.
 *
 * @author Luca Colli
 * @version 1.0
 */
public class MenuBarController extends MouseAdapter {
    private static final Color BUTTON_HOVERED = new Color(195, 102, 76);
    private final GameEngine gameEngine;
    private final JLabel label;
    private final MenuBarView.LabelType action;

    /**
     * Creates the controller for a menu bar item. When the item is clicked, an action is performed.
     * When the item is hovered, its background color can change.
     *
     * @param gameEngine - general model of the game
     * @param label - a clickable JLabel. Set to null if you don't want the background to become darker on hovering
     * @param action - the type of action that should be executed when clicking on the component
     */
    public MenuBarController(GameEngine gameEngine, JLabel label, MenuBarView.LabelType action) {
        this.gameEngine = gameEngine;
        this.label = label;
        this.action = action;
    }

    /**
     * Sets a different background when the JLabel is hovered
     * @param e - the event to be processed
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        super.mouseEntered(e);
        if (label != null) {
            label.setBackground(BUTTON_HOVERED);
        }
    }

    /**
     * Sets the standard background when the JLabel is not hovered
     * @param e - the event to be processed
     */
    @Override
    public void mouseExited(MouseEvent e) {
        super.mouseExited(e);
        if (label != null) {
            label.setBackground(MAIN_LIGHT_COLOR);
        }
    }

    /**
     * When the JLabel is clicked, it executes an action according to the label type
     * @param e - the event to be processed
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        super.mouseClicked(e);
        switch (action) {
            case MAP -> {
                if (gameEngine.getGameState() == GameEngine.GameState.MAP){
                    gameEngine.setGameState(GameEngine.GameState.WALKING);
                } else {
                    gameEngine.setGameState(GameEngine.GameState.MAP);
                }
            }
            case INVENTORY -> {
                if (gameEngine.getGameState() == GameEngine.GameState.INVENTORY){
                    gameEngine.setGameState(GameEngine.GameState.WALKING);
                } else {
                    gameEngine.setGameState(GameEngine.GameState.INVENTORY);
                }
            }
            case LANGUAGE -> gameEngine.changeLanguage();
            case SAVE -> gameEngine.save();
        }
    }
}
