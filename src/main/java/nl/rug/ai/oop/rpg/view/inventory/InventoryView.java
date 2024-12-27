package nl.rug.ai.oop.rpg.view.inventory;

import nl.rug.ai.oop.rpg.controler.InventoryController;
import nl.rug.ai.oop.rpg.model.engine.GameEngine;
import nl.rug.ai.oop.rpg.model.inventory.BagInventory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Objects;

import static nl.rug.ai.oop.rpg.view.GameView.MAIN_LIGHT_COLOR;
import static nl.rug.ai.oop.rpg.view.GameView.MAIN_DARK_COLOR;

/**
 * General class for a general view of the Inventory of our game.
 * @author Aurélie Gallet
 * @version 1.0
 */
public class InventoryView extends JPanel {
    private final static int INVENTORY_BOX_DIMENSION = 90;
    private InventoryController inventoryController;

    /* Weapon Display */
    private final JPanel weaponDisplay;
    //Stored in an array list to avoid remaking the items' associated JPanel each time revalidate is called.
    private final ArrayList<JPanel> weapons = new ArrayList<>();
    private final GridLayout inventoryLayout;
    private int panel = 0;
    private int rows = 1;
    private int columns = 1;
    private int lastIndex = 0;

    /* Weapon Navigation */
    private final JPanel weaponNavigation;
    private final JLabel leftArrow, rightArrow, weaponLabel, discard, use;
    private BagInventory.ItemRecord inspectedItem = null;

    /* Colours */
    private final Color secondaryDarkColor = new Color(210, 90, 86);
    private final Color secondaryLightColor = new Color(222, 90, 86);
    private ArrayList<BagInventory.ItemRecord> inventoryContent;

    /**
     * Sets the new dimensions for the shown elements of the inventory view each time revalidate is called.
     */
    @Override
    public void invalidate() {
        super.invalidate();

        /* Weapon display*/
        invalidateWeaponDisplay();
        /* Navigation */
        invalidateNavigation();
    }

    /**
     * Method to invalidate the weapon display.
     * Makes the invalidate function less long.
     * The border of the display is resized each time to cover the area not covered by the displayed items.
     */
    private void invalidateWeaponDisplay() {
        if (this.getHeight() > 0) { // So that there is no height = 0 error when the panel is created
            rows = (this.getHeight() * 3 / 4) / INVENTORY_BOX_DIMENSION;
            columns = Math.min(this.getWidth() / INVENTORY_BOX_DIMENSION, 15);
            inventoryLayout.setRows(rows);
            inventoryLayout.setColumns(columns);

            weaponDisplay.removeAll();
            weaponDisplay.setBorder(BorderFactory.createMatteBorder((((this.getHeight() * 3) / 4)-(INVENTORY_BOX_DIMENSION *rows))/2,(this.getWidth()-(INVENTORY_BOX_DIMENSION *columns))/2,(((this.getHeight()*3)/4)-(INVENTORY_BOX_DIMENSION *rows))/2, (this.getWidth()-(INVENTORY_BOX_DIMENSION *columns))/2 , MAIN_DARK_COLOR));
            for (int index = 0; index < rows * columns; ++index) {
                if (index + panel * rows * columns < weapons.size()) {
                    weaponDisplay.add(weapons.get(index + panel * rows * columns));
                } else {
                    weaponDisplay.add(makeEmptyBox());
                }
            }
            lastIndex = rows * columns + panel * rows * columns - 1;
        }
    }

    /**
     * Method to invalidate the navigation display.
     * Makes the invalidate function less long.
     */
    private void invalidateNavigation() {
        weaponNavigation.setPreferredSize(new Dimension(this.getWidth(), this.getHeight()/4));

        if (this.getHeight() > 0) { // So that there is no height = 0 error when the panel is created
            addArrow(leftArrow, true);
            addArrow(rightArrow, false);
        }
        if (inspectedItem != null && inspectedItem.item() != null) {
            discard.setCursor(new Cursor(Cursor.HAND_CURSOR));
            use.setCursor(new Cursor(Cursor.HAND_CURSOR));
            if (this.getWidth() > 100) {
                weaponLabel.setText(inspectedItem.item().getName() + ": " + inspectedItem.item().getDescription());
            } else {
                weaponLabel.setText(inspectedItem.item().getName());
            }
        } else {
            discard.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            use.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            weaponLabel.setText(" ");
        }
    }

    /**
     * Method to make a general inventory view.
     * Can switch panels to view all items.
     * Can see item name, item description when clicked, item quantity and item image.
     * Can perform actions on these items.
     */
    public InventoryView() {
        setLayout(new BorderLayout());

        /* Weapon display */
        this.weaponDisplay = new JPanel();
        this.inventoryLayout = new GridLayout(rows,columns);
        weaponDisplay.setLayout(inventoryLayout);
        weaponDisplay.setBackground(MAIN_DARK_COLOR);
        add(weaponDisplay, BorderLayout.CENTER);

        /* Navigation */
        this.leftArrow = new JLabel();
        this.rightArrow = new JLabel();
        this.weaponLabel = new JLabel(" ", JLabel.CENTER);
        this.discard = new JLabel("discard", JLabel.CENTER);
        this.use = new JLabel("use", JLabel.CENTER);
        this.weaponNavigation = weaponNavigation();
        add(weaponNavigation, BorderLayout.SOUTH);
    }


    /* Weapon display */

    /**
     * Method to make en empty box in the weapon display for when there is no item.
     * Belongs to weapon display.
     * @return {@link JPanel} Empty box of the weapon display
     */
    private JPanel makeEmptyBox() {
        JPanel gridBox = new JPanel();
        gridBox.setLayout(new BoxLayout(gridBox, BoxLayout.Y_AXIS));
        gridBox.setBackground(MAIN_LIGHT_COLOR);
        gridBox.setBorder(BorderFactory.createLineBorder(MAIN_DARK_COLOR, 5, false));
        return gridBox;
    }

    /**
     * Method to make en weapon box in the weapon display for when there is an item.
     * Can be clicked to display the item's description and enable performing actions on
     * the clicked item.
     * Belongs to weapon display.
     * @return {@link JPanel} Weapon box of the weapon display
     */
    private JPanel makeWeaponBox(JPanel gridBox, BagInventory.ItemRecord itemRecord) {
        /* Title */
        gridBox.add(makeTitle(itemRecord));

        // Box Image
        JPanel weaponImage = new JPanel();
        weaponImage.setBackground(MAIN_LIGHT_COLOR);
        weaponImage.setMaximumSize(new Dimension(80,60));
        ImageIcon sword = itemRecord.item().getItemImage();
        Image swordRescaled = sword.getImage().getScaledInstance(55,55, Image.SCALE_DEFAULT);
        JLabel box = new JLabel(new ImageIcon(swordRescaled));

        weaponImage.add(box);
        gridBox.add(weaponImage);

        weaponImage.setCursor(new Cursor(Cursor.HAND_CURSOR));
        weaponImage.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                inventoryController.setInspectedItem(itemRecord.item());
            }
        });
        return gridBox;
    }

    /**
     * Method to make a weapon box title.
     * Contains the weapon name and quantity.
     * Belongs to weapon display -> weapon box.
     * @return {@link JPanel} Weapon title of the weapon box
     */
    private JPanel makeTitle(BagInventory.ItemRecord itemRecord) {
        // Box Title
        JPanel title = new JPanel();
        title.setPreferredSize(new Dimension(90, 20));
        title.setMaximumSize(new Dimension(90, 20));
        title.setLayout(new BoxLayout(title, BoxLayout.X_AXIS));
        title.setBackground(secondaryDarkColor);

        // Label for weapon name
        JLabel name = new JLabel(itemRecord.item().getName());
        name.setBorder(BorderFactory.createEmptyBorder(0,2,0,0));
        name.setPreferredSize(new Dimension(60,20));
        name.setMaximumSize(new Dimension(60,20));
        title.add(name);

        //Label for weapon quantity
        JLabel quantity = new JLabel(String.valueOf(itemRecord.quantity()));
        quantity.setHorizontalAlignment(JLabel.CENTER);
        quantity.setBackground(secondaryLightColor);
        quantity.setOpaque(true);
        quantity.setPreferredSize(new Dimension(20,20));
        quantity.setMaximumSize(new Dimension(20,20));
        title.add(quantity);

        return title;
    }


    /* Navigation */

    /**
     * Method to make a navigation bar.
     * Can click arrows to change display panel.
     * Belongs to weapon navigation.
     * @return {@link JPanel} Navigation bar.
     */
    private JPanel weaponNavigation() {
        JPanel weaponNavigation = new JPanel();
        weaponNavigation.setLayout(new BorderLayout());

        /* Left arrow */
        JPanel leftArrowPanel = makeArrowBox(true);
        leftArrowPanel.setBorder(BorderFactory.createEmptyBorder(0,20,10,0));
        leftArrowPanel.add(leftArrow);
        weaponNavigation.add(leftArrowPanel, BorderLayout.WEST);

        /* Center box */
        weaponNavigation.add(makeCenterBox(), BorderLayout.CENTER);

        /* Right Arrow */
        JPanel rightArrowPanel = makeArrowBox(false);
        rightArrowPanel.setBorder(BorderFactory.createEmptyBorder(0,0,10,20));
        rightArrowPanel.add(rightArrow);
        weaponNavigation.add(rightArrowPanel, BorderLayout.EAST);

        return weaponNavigation;
    }

    /**
     * Method to make a center display bar.
     * Displays the inspected item and actions to perform on it.
     * Belongs to weapon navigation.
     * @return {@link JPanel} Center display bar.
     */
    private JPanel makeCenterBox() {
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createMatteBorder(0,5,10,5, MAIN_DARK_COLOR));
        centerPanel.setBackground(MAIN_LIGHT_COLOR);

        /* Weapon Name and Description */
        centerPanel.add(weaponLabel, BorderLayout.CENTER);

        /* Discard */
        centerPanel.add(makeActionButtonPanel(centerPanel, true), BorderLayout.WEST);
        /* Use */
        centerPanel.add(makeActionButtonPanel(centerPanel, false), BorderLayout.EAST);

        return centerPanel;
    }

    /**
     * Method to make action buttons.
     * Can click buttons to affect the inventory.
     * Belongs to weapon navigation -> Center bar.
     * @return {@link JPanel} Center action buttons.
     */
    private JPanel makeActionButtonPanel(JPanel centerPanel, boolean isDiscardPanel) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(80, centerPanel.getHeight()));
        panel.setBackground(secondaryLightColor);
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (inspectedItem != null && inspectedItem.item() != null) {
                    if (isDiscardPanel) {
                        inventoryController.discardItem(inspectedItem.item());
                    } else {
                        inventoryController.useItem(inspectedItem.item());
                    }
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                if (inspectedItem != null && inspectedItem.item() != null) {
                    super.mouseEntered(e);
                    panel.setBackground(secondaryDarkColor);
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                panel.setBackground(secondaryLightColor);
            }
        });
        if (isDiscardPanel) {
            panel.add(discard);
        } else {
            panel.add(use);
        }

        return panel;
    }

    /**
     * Method to make an arrow box.
     * Can click arrow box to change panels.
     * Belongs to weapon navigation -> arrow box.
     * @return {@link JPanel} Arrow buttons.
     */
    private JPanel makeArrowBox(boolean isLeftArrow) {
        JPanel arrowBox = new JPanel();
        arrowBox.setLayout(new BorderLayout());
        arrowBox.setBackground(MAIN_DARK_COLOR);
        arrowBox.setCursor(new Cursor(Cursor.HAND_CURSOR));

        arrowBox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                changePanel(isLeftArrow);
                inventoryController.setInspectedItem(null);
            }
        });
        return arrowBox;
    }

    /**
     * Method to make/resize an arrow in the arrow box.
     * Can click arrow box to change panels.
     * Belongs to weapon navigation -> arrow box -> arrow.
     */
    private void addArrow(JLabel arrowLabel, boolean isLeftArrow) {
        ImageIcon arrow;
        if (isLeftArrow) {
            arrow = new ImageIcon(Objects.requireNonNull(InventoryView.class.getResource("/inventory/navigationbar/leftarrow.png")));
        } else {
            arrow = new ImageIcon(Objects.requireNonNull(InventoryView.class.getResource("/inventory/navigationbar/rightarrow.png")));
        }
        Image rescaledArrow = arrow.getImage().getScaledInstance(this.getHeight()/4 + 20,this.getHeight()/4, Image.SCALE_DEFAULT);
        arrowLabel.setIcon(new ImageIcon(rescaledArrow));
        arrowLabel.setHorizontalAlignment(JLabel.CENTER);
    }

    /**
     * Method to change panels according to the pressed arrow.
     * Belongs to weapon navigation -> arrow box -> arrow.
     */
    private void changePanel(boolean isLeftArrow) {
        if (isLeftArrow) {
            if (panel == 0) {
                if (weapons.size() % (rows * columns) == 0) {
                    panel = weapons.size() / (rows * columns) - 1;
                } else {
                    panel = weapons.size() / (rows * columns);
                }
            } else {
                --panel;
            }
        } else {
            if (lastIndex+1 < weapons.size()) {
                ++panel;
            } else {
                panel = 0;
            }
        }
    }


    /**
     * Method to set up this view as a listener of the inventory.
     * @param inventory Shop inventory of products
     * @param inventoryController Controller of the player's inventory
     */
    public void setup(BagInventory inventory, InventoryController inventoryController) {
        this.inventoryController = inventoryController;
        updateInventoryContent(inventory.getCurrentArrayOfItemRecords());
        inventory.addListener(evt -> {
            switch (evt.getPropertyName()) {
                case "languageChange" -> updateLanguage((GameEngine.Language) evt.getNewValue());
                case "inventoryChange" -> updateInventoryContent((ArrayList<BagInventory.ItemRecord>) evt.getNewValue());
                case "inspectedItemChange" -> updateDisplayedItem((BagInventory.ItemRecord) evt.getNewValue());
            }
        });
    }

    /**
     * Method to update the inventory content.
     * @param newValue New updated inventory
     */
    private void updateInventoryContent(ArrayList<BagInventory.ItemRecord> newValue) {
        this.inventoryContent = newValue;
        weapons.clear();
        for (BagInventory.ItemRecord item:newValue) {
            weapons.add(makeWeaponBox(makeEmptyBox(), item));
        }
        revalidate();
    }

    /**
     * Method to update the displayed item that can be either discarded or used.
     * @param newValue New displayed item
     */
    private void updateDisplayedItem(BagInventory.ItemRecord newValue) {
        this.inspectedItem = newValue;
        revalidate();
    }

    /**
     * Method to update the language of the panel
     * @param language Language to switch the panel to
     */
    public void updateLanguage(GameEngine.Language language) {
        if (language == GameEngine.Language.ENGLISH) {
            use.setText("use");
            discard.setText("discard");
        } else {
            use.setText("gebruik");
            discard.setText("weggooien");
        }
        updateInventoryContent(inventoryContent);
        revalidate();
    }
}