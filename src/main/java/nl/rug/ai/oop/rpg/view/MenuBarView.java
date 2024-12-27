package nl.rug.ai.oop.rpg.view;

import nl.rug.ai.oop.rpg.controler.MenuBarController;
import nl.rug.ai.oop.rpg.model.character.Character;
import nl.rug.ai.oop.rpg.model.engine.GameEngine;
import nl.rug.ai.oop.rpg.view.Player.PlayerMoving;
import nl.rug.ai.oop.rpg.view.inventory.InventoryView;

import javax.swing.*;
import java.awt.*;
import java.util.Objects;

import static nl.rug.ai.oop.rpg.view.GameView.MAIN_LIGHT_COLOR;
import static nl.rug.ai.oop.rpg.view.GameView.MAIN_DARK_COLOR;


/**
 * General class for the view of the Menu Bar of our game.
 * @author Aurélie Gallet
 * @version 1.0
 */
public class MenuBarView extends JPanel {
    private int money;

    /* Button image icons */
    private ImageIcon profileImage;
    private static final ImageIcon MAP_IMAGE = new ImageIcon(Objects.requireNonNull(MenuBarView.class.getResource("/menu_bar/map.png")));
    private static final ImageIcon INVENTORY_IMAGE = new ImageIcon(Objects.requireNonNull(MenuBarView.class.getResource("/menu_bar/inventory.png")));
    private static final ImageIcon DUTCH_IMAGE = new ImageIcon(Objects.requireNonNull(MenuBarView.class.getResource("/menu_bar/dutch.png")));
    private static final ImageIcon ENGLISH_IMAGE = new ImageIcon(Objects.requireNonNull(MenuBarView.class.getResource("/menu_bar/english.png")));
    private static final ImageIcon SAVE_IMAGE = new ImageIcon(Objects.requireNonNull(MenuBarView.class.getResource("/menu_bar/save.png")));
    private static final ImageIcon GOLD_IMAGE = new ImageIcon(Objects.requireNonNull(MenuBarView.class.getResource("/menu_bar/gold.png")));

    /**
     * Enumeration for the different label-buttons of the menu bar
     */
    public enum LabelType {
        MAP, INVENTORY, LANGUAGE, SAVE
    }

    private final JLabel profileLabel, mapLabel, inventoryLabel, languageLabel, saveLabel, walletLabel;
    private final PlayerMoving playerMoving;
    private GameEngine.Language flagShown = GameEngine.Language.DUTCH;


    /**
     * Sets the new dimensions for the shown elements of the menubar view each time revalidate is called.
     */
    @Override
    public void invalidate() {
        super.invalidate();
        if (this.getHeight() > 0) {
            Dimension buttonDimension = new Dimension(this.getHeight() - 10, this.getHeight() - 10);
            profileLabel.setPreferredSize(buttonDimension);
            mapLabel.setPreferredSize(buttonDimension);
            inventoryLabel.setPreferredSize(buttonDimension);
            languageLabel.setPreferredSize(buttonDimension);
            saveLabel.setPreferredSize(buttonDimension);
            walletLabel.setPreferredSize(new Dimension(this.getWidth() / 5, this.getHeight() - 10));

            /* Modifies icons according to view dimensions */
            addIcon(profileLabel, profileImage);
            addIcon(mapLabel, MAP_IMAGE);
            addIcon(inventoryLabel, INVENTORY_IMAGE);
            if (flagShown == GameEngine.Language.DUTCH) {
                addIcon(languageLabel, DUTCH_IMAGE);
            } else if (flagShown == GameEngine.Language.ENGLISH) {
                addIcon(languageLabel, ENGLISH_IMAGE);
            }
            addIcon(saveLabel, SAVE_IMAGE);
            addIcon(walletLabel, GOLD_IMAGE);
        }
    }

    /**
     * Generates a menu bar view for our game
     * This includes buttons to see different views, the player's status and the player's wealth.
     */
    public MenuBarView() {
        setLayout(new BorderLayout());
        setBackground(MAIN_DARK_COLOR);
        setBorder(BorderFactory.createEmptyBorder(0,5,0,10));

        /* Menu buttons */
        this.profileLabel = makeLabel();
        this.mapLabel = makeLabel();
        this.inventoryLabel = makeLabel();
        this.languageLabel = makeLabel();
        this.saveLabel = makeLabel();
        add(makeButtonsView(), BorderLayout.WEST);

        /* Player status */
        playerMoving = new PlayerMoving();
        add(playerMoving, BorderLayout.CENTER);
        playerMoving.setBackground(MAIN_DARK_COLOR);

        /* Money */
        JPanel moneyView = new JPanel();
        moneyView.setLayout(new BorderLayout());
        moneyView.setBorder(BorderFactory.createLineBorder(MAIN_DARK_COLOR,5, false));
        moneyView.setBackground(MAIN_LIGHT_COLOR);
        this.walletLabel = new JLabel("Gold:", JLabel.CENTER);
        moneyView.add(walletLabel);
        add(moneyView, BorderLayout.EAST);
    }

    /**
     * Method to make the buttons view for the buttons.
     * Makes the constructor less long.
     * @return {@link JPanel} Buttons View of the menu bar
     */
    private JPanel makeButtonsView() {
        JPanel buttonsView = new JPanel();
        FlowLayout menuBarView = new FlowLayout();
        menuBarView.setHgap(10);
        buttonsView.setLayout(menuBarView);
        buttonsView.setBackground(MAIN_DARK_COLOR);

        buttonsView.add(profileLabel);
        buttonsView.add(mapLabel);
        buttonsView.add(inventoryLabel);
        buttonsView.add(languageLabel);
        buttonsView.add(saveLabel);
        return buttonsView;
    }

    /**
     * Method to set up this view as a listener of the game engine and the player model.
     * Sets up the player status.
     * Add controllers to the menu buttons.
     * @param gameEngine Game engine of our game
     */
    public void setup(GameEngine gameEngine) {
        /* Player status */
        playerMoving.setup(gameEngine.getPlayerModel());

        gameEngine.addListener(evt -> {
            if (evt.getPropertyName().equals("language")) {
                updateLanguage((GameEngine.Language) evt.getNewValue());
            }
        });
        gameEngine.getPlayerModel().addListener(evt -> {
            if (evt.getPropertyName().equals("money")) {
                updateMoney((int) evt.getNewValue());
            }
        });

        /* Adds controllers to the four functional buttons */
        MenuBarController mapLabelController = new MenuBarController(gameEngine, mapLabel, LabelType.MAP);
        mapLabel.addMouseListener(mapLabelController);
        MenuBarController inventoryLabelController = new MenuBarController(gameEngine, inventoryLabel, LabelType.INVENTORY);
        inventoryLabel.addMouseListener(inventoryLabelController);
        MenuBarController languageLabelController = new MenuBarController(gameEngine, languageLabel, LabelType.LANGUAGE);
        languageLabel.addMouseListener(languageLabelController);
        MenuBarController saveLabelController = new MenuBarController(gameEngine, saveLabel, LabelType.SAVE);
        saveLabel.addMouseListener(saveLabelController);
    }

    /**
     * Sets the profile image for the menu bar
     * @param faction Player's faction
     */
    public void setFaction(Character.Faction faction) {
        if (faction == Character.Faction.FIRE) {
            profileImage = new ImageIcon(Objects.requireNonNull(InventoryView.class.getResource("/skins/redguy.png")));
        } else if (faction == Character.Faction.WATER) {
            profileImage = new ImageIcon(Objects.requireNonNull(InventoryView.class.getResource("/skins/blueguy.png")));
        } else if (faction == Character.Faction.WOOD) {
            profileImage = new ImageIcon(Objects.requireNonNull(InventoryView.class.getResource("/skins/greenguy.png")));
        }
    }

    /**
     * Add/Resizes an icon to a JLabel according to the view size
     * @param label Label to be modified
     * @param image Image to be added
     */
    private void addIcon(JLabel label, ImageIcon image) {
        Image rescaled = image.getImage().getScaledInstance(this.getHeight()-20, this.getHeight()-20, Image.SCALE_DEFAULT);
        label.setIcon(new ImageIcon(rescaled));
        label.setHorizontalAlignment(JLabel.CENTER);
    }


    /**
     * Method to make the label-buttons of the menu bar
     * @return JLabel for the buttons of the menu bar
     */
    private JLabel makeLabel() {
        JLabel label = new JLabel();
        label.setBackground(MAIN_LIGHT_COLOR);
        label.setOpaque(true);
        label.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return label;
    }

    /* Updates */


    /**
     * Updates the displayed money
     * @param money Amount to display
     */
    public void updateMoney(int money) {
        this.money = money;
        walletLabel.setText("Gold: " + money);
        revalidate();
    }

    /**
     * Updates the displayed language
     * @param language Language to display
     */
    public void updateLanguage(GameEngine.Language language) {
        if (language == GameEngine.Language.ENGLISH) {
            walletLabel.setText("Gold:" + money);
            flagShown = GameEngine.Language.DUTCH;
        } else if (language == GameEngine.Language.DUTCH) {
            walletLabel.setText("Goud:" + money);
            flagShown = GameEngine.Language.ENGLISH;
        }
        revalidate();
    }
}