package nl.rug.ai.oop.rpg.view.Player;

import nl.rug.ai.oop.rpg.model.character.Character;
import nl.rug.ai.oop.rpg.model.player.PlayerModel;
import nl.rug.ai.oop.rpg.model.player.TextModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;

/**
 * @author Otto Bervoets
 * Displays the players properties during the walking phase of the game.
 */
public class PlayerMoving extends JPanel {
    JLabel health, attack, defense, faction, playerName;

    /**
     * Constructs the player moving screen
     */
    public PlayerMoving() {
        init();
    }

    /**
     * Listens to the state of the player
     * @param player the player model to listen to.
     */
    public void setup(PlayerModel player) {
        player.addListener(this::updateProperties);
    }

    /**
     * Initializes all the labels.
     */
    private void init(){
        setLayout(new FlowLayout());

        health = new JLabel("1");
        attack = new JLabel("2");
        defense = new JLabel("3");
        faction = new JLabel("4");
        playerName = new JLabel("5");

        add(health);
        add(attack);
        add(defense);
        add(faction);
        add(playerName);
    }

    private void updateProperties(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case "health" -> this.health.setText((String) evt.getNewValue());
            case "attackText" -> {
                HashMap<Character.Faction, String> textHash = (HashMap<Character.Faction, String>) evt.getNewValue();
                this.attack.setText(textHash.get(Character.Faction.FIRE) + textHash.get(Character.Faction.WATER) + textHash.get(Character.Faction.WOOD));
            }
            case "defense" -> this.defense.setText(TextModel.defenceText(Integer.parseInt(evt.getNewValue().toString())));
            case "factionText" -> this.faction.setText(evt.getNewValue().toString());
            case "name" -> this.playerName.setText(evt.getNewValue().toString());
        }
    }
}
