package nl.rug.ai.oop.rpg.view.Player;

import nl.rug.ai.oop.rpg.controler.MenuBarController;
import nl.rug.ai.oop.rpg.model.engine.GameEngine;
import nl.rug.ai.oop.rpg.model.player.TextModel;
import nl.rug.ai.oop.rpg.view.MenuBarView;

import javax.swing.*;
import java.beans.PropertyChangeEvent;

/**
 * @author Otto Bervoets
 * Class that displays the death screen of the player
 */
public class PlayerDeath extends JPanel {
    JLabel message;
    JButton language;

    /**
     * Constructs the panel
     */
    public PlayerDeath() {
        init();
    }

    /**
     * Adds a listener to the game engine and makes sure that the button can control the language of the game engine.
     * @param gameEngine the instance of the current game engine.
     */
    public void setup(GameEngine gameEngine) {
        gameEngine.addListener(this::updateText);
        language.addMouseListener(new MenuBarController(gameEngine, null, MenuBarView.LabelType.LANGUAGE));
        /*
        I think this way of checking the language is a bit cumbersome.
        However, for now this seems more efficient than making another new game engine controller
         and hence I'm bound to implementation of others.
         */
    }

    private void init() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        message = new JLabel();
        language = new JButton();
        message.setHorizontalAlignment(SwingConstants.CENTER);
        message.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        language.setHorizontalAlignment(SwingConstants.CENTER);
        language.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        message.setText(TextModel.deathText());
        language.setText(TextModel.languageText());
        add(message);
        add(language);

    }

    private void updateText(PropertyChangeEvent evt) {
        message.setText(TextModel.deathText());
        language.setText(TextModel.languageText());
    }
}
