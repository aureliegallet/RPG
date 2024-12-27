package nl.rug.ai.oop.rpg.view.inventory;

import nl.rug.ai.oop.rpg.controler.InventoryController;
import nl.rug.ai.oop.rpg.model.engine.GameEngine;
import nl.rug.ai.oop.rpg.model.inventory.Item;
import nl.rug.ai.oop.rpg.model.locations.LocationObject;
import nl.rug.ai.oop.rpg.view.GameView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import static nl.rug.ai.oop.rpg.view.GameView.MAIN_LIGHT_COLOR;
import static nl.rug.ai.oop.rpg.view.GameView.MAIN_DARK_COLOR;

/**
 * General class for the pickup view of our game.
 * @author Aurélie Gallet
 * @version 1.0
 */
public class ObjectPickupView extends JPanel {
    private final JLabel pickUp, ignore, message1, message2;
    private Item item;
    private final JPanel labels, itemImagePanel;
    private final GameView gameView;
    private JLabel itemLabel;

    /**
     * Sets the new dimensions for the labels of the pickup view panel each time revalidate is called.
     */
    public void invalidate() {
        super.invalidate();
        labels.setPreferredSize(new Dimension(this.getWidth(), this.getHeight()/5));
        message1.setFont(new Font("SansSerif", Font.PLAIN, this.getHeight()/20));
        message2.setFont(new Font("SansSerif", Font.PLAIN, this.getHeight()/20));
        ignore.setFont(new Font("SansSerif", Font.PLAIN, this.getHeight()/20));
        pickUp.setFont(new Font("SansSerif", Font.PLAIN, this.getHeight()/20));
    }

    /**
     * Generates a pickup view to be able to pick up an object
     * @param gameView GameView of our game
     */
    public ObjectPickupView(GameView gameView) {
        this.gameView = gameView;
        setBackground(MAIN_LIGHT_COLOR);
        setLayout(new BorderLayout());

        /* Message in north panel */
        this.message1 = new JLabel("Do you want to");
        this.message2 = new JLabel("pick this up?");
        message1.setAlignmentX(CENTER_ALIGNMENT);
        message2.setAlignmentX(CENTER_ALIGNMENT);
        add(makeNorthPanel(), BorderLayout.NORTH);

        /* Image */
        this.itemImagePanel = new JPanel();
        itemImagePanel.setLayout(new BorderLayout());
        itemImagePanel.setBackground(MAIN_LIGHT_COLOR);
        itemImagePanel.setBorder(BorderFactory.createLineBorder(MAIN_DARK_COLOR, 10));
        add(itemImagePanel, BorderLayout.CENTER);

        /* Labels */
        this.ignore = new JLabel("ignore");
        this.pickUp = new JLabel("pick up");
        this.labels = makeLabelsPanel();
        add(labels, BorderLayout.SOUTH);
    }

    /**
     * Method to make the north panel message view.
     * @return {@link JPanel} Message view of the pickup view
     */
    private JPanel makeNorthPanel() {
        JPanel northPanel = new JPanel();
        northPanel.setBackground(MAIN_LIGHT_COLOR);
        northPanel.setBorder(BorderFactory.createEmptyBorder(20,0,20, 0));
        northPanel.setLayout(new BoxLayout(northPanel, BoxLayout.Y_AXIS));
        northPanel.add(message1);
        northPanel.add(message2);
        return northPanel;
    }

    /**
     * Method to make the labels view.
     * @return {@link JPanel} Labels view of the pickup view
     */
    private JPanel makeLabelsPanel() {
        JPanel labels = new JPanel();
        labels.setBackground(MAIN_LIGHT_COLOR);
        labels.setBorder(BorderFactory.createEmptyBorder(20,0,20, 0));
        labels.setLayout(new BoxLayout(labels, BoxLayout.X_AXIS));
        labels.add(Box.createHorizontalGlue());

        //ignore
        ignore.setBackground(MAIN_DARK_COLOR);
        ignore.setBorder(BorderFactory.createLineBorder(MAIN_DARK_COLOR, 2));
        ignore.setAlignmentX(CENTER_ALIGNMENT);
        ignore.setAlignmentY(CENTER_ALIGNMENT);
        ignore.setCursor(new Cursor(Cursor.HAND_CURSOR));
        labels.add(ignore);
        labels.add(Box.createHorizontalStrut(20));

        //pick up
        pickUp.setBackground(MAIN_DARK_COLOR);
        pickUp.setBorder(BorderFactory.createLineBorder(MAIN_DARK_COLOR, 2));
        pickUp.setAlignmentX(CENTER_ALIGNMENT);
        pickUp.setAlignmentY(CENTER_ALIGNMENT);
        pickUp.setCursor(new Cursor(Cursor.HAND_CURSOR));
        labels.add(pickUp);
        labels.add(Box.createHorizontalGlue());

        return labels;
    }

    /**
     * Method to set up this view as a listener of the inventory and the location so that it can update accordingly
     */
    public void setup(GameEngine gameEngine, InventoryController inventoryController) {
        gameEngine.getInventory().addListener(evt -> {
            if (evt.getPropertyName().equals("languageChange")){
                updateLanguage((GameEngine.Language)evt.getNewValue());
            }
        });
        gameEngine.getLocation().addListener(evt -> {
            if (evt.getPropertyName().equals("objectAtCurrentCoordinates")) {
                updateItemToDisplay((LocationObject)evt.getNewValue());
            }
        });
        ignore.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                gameView.updateViewTo(GameEngine.GameState.WALKING);
                itemLabel.setIcon(null);
            }
        });
        pickUp.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                inventoryController.pickUpItem(item);
                itemLabel.setIcon(null);
            }
        });
    }

    /**
     * Method to update the item to display of the panel to show what can be picked up
     * @param object Object to display
     */
    public void updateItemToDisplay(LocationObject object) {
        this.item = (Item) object.object();
        ImageIcon itemImage = item.getItemImage();
        Image rescaled = itemImage.getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT);
        this.itemLabel = new JLabel(new ImageIcon(rescaled));
        itemLabel.setVerticalAlignment(JLabel.CENTER);
        itemImagePanel.add(itemLabel, BorderLayout.CENTER);
    }

    /**
     * Method to update the language of the panel
     * @param language Language to switch the panel to
     */
    public void updateLanguage(GameEngine.Language language) {
        if(language == GameEngine.Language.ENGLISH) {
            ignore.setText("ignore");
            pickUp.setText("pick up");
            message1.setText("Do you want to");
            message2.setText("pick this up?");
        } else {
            ignore.setText("negeren");
            pickUp.setText("oppakken");
            message1.setText("Wil je");
            message2.setText("dit oppakken?");
        }
        revalidate();
    }
}
