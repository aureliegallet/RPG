package nl.rug.ai.oop.rpg.view.Player;

import nl.rug.ai.oop.rpg.controler.MenuBarController;
import nl.rug.ai.oop.rpg.controler.player.PlayerBuilderController;
import nl.rug.ai.oop.rpg.model.engine.GameEngine;
import nl.rug.ai.oop.rpg.model.player.TextModel;
import nl.rug.ai.oop.rpg.view.MenuBarView;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;

/**
 * @author Otto Bervoets
 * An panel that is shown when there is no saved player found.
 */
public class BuildPlayerView extends JPanel {
    JTextField playerName;
    JLabel setName, chooseFaction, story;
    JButton submitName, language;
    JRadioButton fire, wood, water;
    JPanel factionButtons, namePanel;

    /**
     * Construct the frame, init manages all the buttons, lables etc.
     */
    public BuildPlayerView() {
        init();
    }

    /**
     * Add the appropiet controllers and listeners
     * @param playerBuilderController the playerBuilderController that is used
     * @param gameEngine We listen to the game engine to know the language.
     */
    public void setUp(PlayerBuilderController playerBuilderController, GameEngine gameEngine) {
        gameEngine.addListener(this::changeLanguage);
        playerBuilderController.setjTextField(playerName);
        submitName.addActionListener(playerBuilderController);
        fire.addActionListener(playerBuilderController);
        wood.addActionListener(playerBuilderController);
        water.addActionListener(playerBuilderController);
        language.addMouseListener(new MenuBarController(gameEngine, null, MenuBarView.LabelType.LANGUAGE));
        /*
        I think this way of checking the language is a bit cumbersome.
        However, for now this seems more efficient than making another new game engine controller
         and hence I'm bound to implementation of others.
         */
    }

    /**
     * make buttons and align properly
     */
    private void init() {
        playerName = new JTextField();
        playerName.setMaximumSize(new Dimension(250, 30));

        setName = new JLabel("Enter a player name:");
        setName.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        submitName = new JButton("Submit");
        submitName.setActionCommand("submit");

        namePanel = new JPanel();
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.X_AXIS));
        namePanel.add(setName);
        namePanel.add(playerName);
        namePanel.add(submitName);

        story = new JLabel(TextModel.introStory());
        story.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        story.setHorizontalAlignment(SwingConstants.CENTER);

        chooseFaction = new JLabel("Choose a faction");
        fire = new JRadioButton();
        wood = new JRadioButton();
        water = new JRadioButton();
        ButtonGroup factionGroup = new ButtonGroup();
        factionGroup.add(fire);
        factionGroup.add(wood);
        factionGroup.add(water);


        language = new JButton("Dutch");
        language.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        language.setHorizontalAlignment(SwingConstants.CENTER);


        fire.setActionCommand("_Fire");
        wood.setActionCommand("_Wood");
        water.setActionCommand("_Water");

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        factionButtons = new JPanel();
        factionButtons.setLayout(new FlowLayout());

        factionButtons.add(fire);
        factionButtons.add(wood);
        factionButtons.add(water);

        add(language);
        add(factionButtons);
        add(namePanel);
        add(story);
        add(new Box.Filler(new Dimension(0,0), new Dimension(1024, 800), new Dimension(1024, 800)));
        setAllEnglish();

    }

    private void changeLanguage(PropertyChangeEvent evt) {
        if(!evt.getPropertyName().equals("language")) {
            return;
        }
        switch ( (GameEngine.Language) evt.getNewValue()){
            case ENGLISH -> setAllEnglish();
            case DUTCH -> setAllDutch();
        }
        story.setText(TextModel.introStory());
    }

    private void setAllEnglish() {
        fire.setText("Fire");
        water.setText("Water");
        wood.setText("Wood");
        submitName.setText("Submit name");
        setName.setText("Enter a player name:");
        language.setText("Nederlands");
        chooseFaction.setText("Choose a faction:");
    }

    private void setAllDutch() {
        fire.setText("Vuur");
        water.setText("Water");
        wood.setText("Hout");
        submitName.setText("Dien naam in");
        setName.setText("geef een speler naam:");
        language.setText("English");
        chooseFaction.setText("Kies een fractie:");
    }
}
