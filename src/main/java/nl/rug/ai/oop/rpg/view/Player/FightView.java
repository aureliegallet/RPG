package nl.rug.ai.oop.rpg.view.Player;

import nl.rug.ai.oop.rpg.controler.player.PlayerController;
import nl.rug.ai.oop.rpg.model.character.Character;
import nl.rug.ai.oop.rpg.model.player.PlayerModel;
import nl.rug.ai.oop.rpg.model.player.TextModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.util.HashMap;

/**
 * @author Otto Bervoets
 * Panel that is shown during the fighting the NPC.
 */
public class FightView extends JPanel {
    JButton water, wood, fire, continueGame;
    JLabel ownHealth, opponentHealth;

    JTextField previousAttackRound;
    JPanel attackOptions, healthDisplay;
    //TODO display NPC name and faction during fighting

    /**
     * Constructor, initializes all the items.
     */
    public FightView() {
        init();
    }

    /**
     * Adds the appropriated listeners and controllers.
     * @param playerController The player controller listens to this view
     * @param playerModel This view listens to the player model.
     */
    public void setup(PlayerController playerController, PlayerModel playerModel) {
        playerModel.addListener(this::updatePlayerProperties);
        water.addActionListener(playerController);
        fire.addActionListener(playerController);
        wood.addActionListener(playerController);
        continueGame.addActionListener(playerController);
    }

    private void init() {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        water = new JButton();
        wood = new JButton();
        fire = new JButton();
        continueGame = new JButton();

        water.setActionCommand("water");
        wood.setActionCommand("wood");
        fire.setActionCommand("fire");
        continueGame.setActionCommand("continueGame");

        attackOptions = new JPanel();
        attackOptions.setLayout(new FlowLayout());
        attackOptions.add(water);
        attackOptions.add(wood);
        attackOptions.add(fire);

        previousAttackRound = new JTextField("display what just happened");
        previousAttackRound.setMaximumSize(new Dimension(2000, 100));


        ownHealth = new JLabel("own health");
        opponentHealth = new JLabel("opponent health");
        healthDisplay = new JPanel();
        healthDisplay.setLayout(new FlowLayout());
        healthDisplay.add(ownHealth);
        healthDisplay.add(opponentHealth);

        add(attackOptions);
        add(previousAttackRound);
        add(healthDisplay);

    }

    private void updatePlayerProperties(PropertyChangeEvent evt) {
        switch (evt.getPropertyName()) {
            case "playerDescription" -> ownHealth.setText((String) evt.getNewValue());
            case "NPCdescription" -> opponentHealth.setText((String) evt.getNewValue());
            case "fightMessage" -> previousAttackRound.setText((String) evt.getNewValue());
            case "attackText" -> {
                HashMap<Character.Faction, String> buttonText = (HashMap<Character.Faction, String>) evt.getNewValue();
                water.setText(buttonText.get(Character.Faction.WATER));
                fire.setText(buttonText.get(Character.Faction.FIRE));
                wood.setText(buttonText.get(Character.Faction.WOOD));
            }
            case "continue" -> {
                remove(attackOptions);
                continueGame.setText(TextModel.continueGame());
                add(continueGame);
            }
            case "setFightBack" -> {
                removeAll();
                add(attackOptions);
                add(previousAttackRound);
                add(healthDisplay);            }
        }
    }
    private final static Dimension FRAME_DIMENSION = new Dimension(1024, 768);
    public static void main(String[] args) {
        JFrame frame = new JFrame("Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(FRAME_DIMENSION);
        frame.setMinimumSize(FRAME_DIMENSION);
        FightView fightView = new FightView();
        frame.add(fightView);
        frame.pack();
        frame.setVisible(true);
    }
}
