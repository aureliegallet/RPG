package nl.rug.ai.oop.rpg.view.inventory;

import nl.rug.ai.oop.rpg.controler.InventoryController;
import nl.rug.ai.oop.rpg.model.engine.GameEngine;
import nl.rug.ai.oop.rpg.model.inventory.BagInventory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import static nl.rug.ai.oop.rpg.view.GameView.MAIN_LIGHT_COLOR;
import static nl.rug.ai.oop.rpg.view.GameView.MAIN_DARK_COLOR;

/**
 * General class for the full screen view of the Inventory of our game.
 * @author Aurélie Gallet
 * @version 1.0
 */
public class BigInventoryView extends JPanel {
    private InventoryController inventoryController;
    private InventoryView inventoryView;
    private final JPanel clothingView, itemView, description;
    private final JLabel message1, message2, message3, message4, message5;
    private ArrayList<JLabel> currentClothes = new ArrayList<>();

    /* Strings */
    private String nameString = "Name";
    private String descriptionString = "Description";
    private String effectString = "Effect";
    private String boostString = "Boost";
    private String priceString = "Price";
    private String goldString = "gold";

    private BagInventory.ItemRecord displayedItem;

    /**
     * Sets the new dimensions for the elements of the big inventory view panel each time revalidate is called.
     */
    @Override
    public void invalidate() {
        super.invalidate();
        if (this.getHeight()>0) {
            clothingView.setPreferredSize(new Dimension(this.getWidth() / 3, this.getHeight()));
            description.setPreferredSize(new Dimension(this.getWidth() * 2 / 3, this.getHeight() / 3));
            Font textDescription = new Font("Serif", Font.PLAIN, itemView.getHeight() / 30);
            message1.setFont(textDescription);
            message2.setFont(textDescription);
            message3.setFont(textDescription);
            message4.setFont(textDescription);
            message5.setFont(textDescription);
            for (JLabel currentClothing : currentClothes) {
                ImageIcon imageIcon = (ImageIcon) currentClothing.getIcon();
                Image rescaled = imageIcon.getImage().getScaledInstance(this.getHeight() / 7, this.getHeight() / 7, Image.SCALE_DEFAULT);
                currentClothing.setIcon(new ImageIcon(rescaled));
            }
        }
    }

    /**
     * Generates a big inventory view with the basic elements of the view
     */
    public BigInventoryView() {
        setLayout(new BorderLayout());

        //For the clothes
        this.clothingView = new JPanel();
        clothingView.setBackground(Color.GRAY);
        clothingView.setLayout(new BoxLayout(clothingView, BoxLayout.Y_AXIS));
        add(clothingView, BorderLayout.WEST);

        //For the inventory and item description
        this.itemView = new JPanel();
        itemView.setLayout(new BorderLayout());

        //For the item description
        this.message1 = new JLabel();
        this.message2 = new JLabel();
        this.message3 = new JLabel();
        this.message4 = new JLabel();
        this.message5 = new JLabel();

        this.description = makeDescription();
        itemView.add(description, BorderLayout.SOUTH);

        add(itemView, BorderLayout.CENTER);
    }

    /**
     * Method to make the description view.
     * @return {@link JPanel} Description view of the big inventory view
     */
    private JPanel makeDescription() {
        JPanel description = new JPanel();
        description.setBorder(BorderFactory.createLineBorder(MAIN_DARK_COLOR, 20));
        description.setBackground(MAIN_LIGHT_COLOR);
        description.setLayout(new BoxLayout(description, BoxLayout.Y_AXIS));
        description.add(message1);
        description.add(message2);
        description.add(message3);
        description.add(message4);
        description.add(message5);
        return description;
    }

    /**
     * Method to set up this view as a listener of the inventory so that it can update accordingly.
     * @param inventory Inventory of the game
     * @param inventoryController Inventory controller of the player's inventory
     */
    public void setup(BagInventory inventory, InventoryController inventoryController) {
        this.inventoryController = inventoryController;
        inventory.addListener(evt -> {
            switch(evt.getPropertyName()) {
                case "languageChange" ->
                    updateLanguage((GameEngine.Language) evt.getNewValue());
                case "inspectedItemChange" ->
                    updateDisplayedItem((BagInventory.ItemRecord) evt.getNewValue());
                case "clothingChange" ->
                    updateClothing((ArrayList<BagInventory.ItemRecord>) evt.getNewValue());
            }
        });
        updateClothing(inventory.getCurrentArrayOfClothingRecords());
    }

    /**
     * Method to transfer the inventory view from the normal view to the big inventory view
     * This is done as they both display the same inventory and so that the view for the player's inventory
     * is not made twice.
     * @param inventoryView Inventory View of the game
     */
    public void setInventoryViewBig(InventoryView inventoryView) {
        this.inventoryView = inventoryView;
        itemView.add(inventoryView, BorderLayout.CENTER);
    }

    /**
     * Method to transfer the inventory view from the big inventory view to the normal view
     * This is done as they both display the same inventory and so that the view for the player's inventory
     * is not made twice.
     * @return {@link InventoryView} Inventory View of the game
     */
    public InventoryView getInventoryFromBig () {
        remove(inventoryView);
        return inventoryView;
    }


    /* Updating */

    /**
     * Updates the displayed item's information
     * @param newValue New value of the displayed item
     */
    private void updateDisplayedItem(BagInventory.ItemRecord newValue) {
        this.displayedItem = newValue;
        if (newValue != null && newValue.item() != null) {
            message1.setText(" - " + nameString + ": " + newValue.item().getName());
            message2.setText(" - " + descriptionString + ": " + newValue.item().getDescription());
            message3.setText(" - " + effectString + ": " + newValue.item().getEffectString());
            message4.setText(" - " + boostString + ": " + newValue.item().getBoost());
            message5.setText(" - " + priceString + ": " + newValue.item().getPrice() + " " + goldString);
        } else {
            message1.setText(" ");
            message2.setText(" ");
            message3.setText(" ");
            message4.setText(" ");
            message5.setText(" ");
        }
        revalidate();
    }

    /**
     * Method to update the viewed worn clothes when they change
     * @param newValue New ArrayList of worn clothes
     */
    private void updateClothing(ArrayList<BagInventory.ItemRecord> newValue) {
        ArrayList<JLabel> clothes = new ArrayList<>();
        clothingView.removeAll();
        if (newValue != null) {
            clothingView.add(Box.createVerticalGlue());
            for (int index = 0; index < newValue.size(); ++index) {
                if (newValue.get(index) != null) {
                    ImageIcon clothingImage = newValue.get(index).item().getItemImage();
                    JLabel clothesLabel;
                    if (this.getHeight() == 0) {
                        clothesLabel = new JLabel(clothingImage);
                    } else {
                        Image rescaled = clothingImage.getImage().getScaledInstance(this.getHeight()/7, this.getHeight()/7, Image.SCALE_DEFAULT);
                        clothesLabel = new JLabel(new ImageIcon(rescaled));
                    }
                    clothesLabel.setAlignmentX(CENTER_ALIGNMENT);
                    int finalIndex = index;
                    clothesLabel.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseClicked(MouseEvent e) {
                            inventoryController.setInspectedItem(newValue.get(finalIndex).item());
                        }
                    });
                    clothes.add(clothesLabel);
                    clothingView.add(clothesLabel);
                }
            }
            clothingView.add(Box.createVerticalGlue());
        }
        this.currentClothes = clothes;
        revalidate();
    }

    /**
     * Method to update the language of the panel
     * @param language Language to switch the panel to
     */
    private void updateLanguage(GameEngine.Language language) {
        if (language == GameEngine.Language.ENGLISH) {
            nameString = "Name";
            descriptionString = "Description";
            effectString = "Effect";
            boostString = "Boost";
            priceString = "Price";
            goldString = "gold";
        } else {
            nameString = "Naam";
            descriptionString = "Beschrijving";
            effectString = "Effect";
            boostString = "Stimuleren";
            priceString = "Prijs";
            goldString = "goud";
        }
        updateDisplayedItem(displayedItem);
    }
}
