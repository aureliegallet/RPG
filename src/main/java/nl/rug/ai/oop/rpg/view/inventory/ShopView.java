package nl.rug.ai.oop.rpg.view.inventory;

import nl.rug.ai.oop.rpg.controler.ShopController;
import nl.rug.ai.oop.rpg.model.engine.GameEngine;
import nl.rug.ai.oop.rpg.model.inventory.BagInventory;
import nl.rug.ai.oop.rpg.model.inventory.Inventory;
import nl.rug.ai.oop.rpg.model.inventory.Item;
import nl.rug.ai.oop.rpg.model.inventory.ProductInventory;
import nl.rug.ai.oop.rpg.view.GameView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import static nl.rug.ai.oop.rpg.view.GameView.MAIN_LIGHT_COLOR;
import static nl.rug.ai.oop.rpg.view.GameView.MAIN_DARK_COLOR;

/**
 * General class for the shop view of the shops of our game.
 * @author Aurélie Gallet
 * @version 1.0
 */
public class ShopView extends JPanel {
    private final GameView gameView;
    private ShopController shopController;
    //The other option was to get the icon of the label but storing this allows
    //to not lose image quality of the weapon images through icon resizing.
    private final ArrayList<ImageIcon> shopItemsImage = new ArrayList<>();
    private final ArrayList<JLabel> shopItems = new ArrayList<>();
    private final JLabel exit;
    private final JLabel possibleAction, price, actionImage;
    private final JPanel goods;
    private String buyString, sellString, goldString;
    private Item displayedItem;

    /**
     * Sets the new dimensions for the shown elements of the shop view each time revalidate is called.
     */
    public void invalidate() {
        super.invalidate();
        if (this.getHeight() > 0) {

            /* Selected item image */
            if (actionImage.getIcon() != null) {
                ImageIcon image = (ImageIcon) actionImage.getIcon();
                Image rescaled = image.getImage().getScaledInstance(this.getHeight() / 10, this.getHeight() / 10, Image.SCALE_DEFAULT);
                actionImage.setIcon(new ImageIcon(rescaled));
            }

            /* Shop item images */
            if (!shopItems.isEmpty()) {
                goods.removeAll();
                for (JLabel label:shopItems) {
                    ImageIcon weaponImage = shopItemsImage.get(shopItems.indexOf(label));
                    int imageSize = Math.min(this.getHeight() / 10, this.getWidth() / 25);
                    Image weaponRescaled = weaponImage.getImage().getScaledInstance(imageSize, imageSize, Image.SCALE_DEFAULT);
                    label.setIcon(new ImageIcon(weaponRescaled));
                    goods.add(label);
                }
            }
        }
    }

    /**
     * Generates a shop view for our game
     * This includes a way for players to buy and sell items
     */
    public ShopView(GameView gameView) {
        this.gameView = gameView;
        setLayout(new BorderLayout());
        setBackground(MAIN_LIGHT_COLOR);
        setBorder(BorderFactory.createEmptyBorder(10,40,10,40));

        /* Top */
        this.exit = new JLabel("Exit", JLabel.CENTER);
        exit.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        exit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        add(makeExitPanel(), BorderLayout.NORTH);

        /* Center */
        this.goods = new JPanel();
        goods.setLayout(new FlowLayout());
        goods.setBackground(MAIN_LIGHT_COLOR);
        goods.setBorder(BorderFactory.createLineBorder(MAIN_DARK_COLOR, 10, false));
        add(goods, BorderLayout.CENTER);

        /* Bottom */
        this.possibleAction = new JLabel(" ");
        this.price = new JLabel(" ");
        this.actionImage = new JLabel(" ");
        add(makeItemConcerned(), BorderLayout.SOUTH);

        updateShopLanguage(GameEngine.Language.ENGLISH);
    }

    /**
     * Method to make the exit view.
     * @return {@link JPanel} Exit view of the shop view
     */
    private JPanel makeExitPanel() {
        JPanel exitPanel = new JPanel();
        exitPanel.setBackground(MAIN_DARK_COLOR);
        exitPanel.setBorder(BorderFactory.createEmptyBorder(10,0,10,0));
        exitPanel.add(exit);
        return exitPanel;
    }

    /**
     * Method to make the inspected item view.
     * @return {@link JPanel} Inspected item view of the shop view
     */
    private JPanel makeItemConcerned() {
        JPanel itemConcerned = new JPanel();
        itemConcerned.setBackground(MAIN_DARK_COLOR);
        itemConcerned.setBorder(BorderFactory.createEmptyBorder(20,0,10,0));
        itemConcerned.setLayout(new BoxLayout(itemConcerned, BoxLayout.Y_AXIS));

        // Action
        possibleAction.setCursor(new Cursor(Cursor.HAND_CURSOR));
        possibleAction.setAlignmentX(CENTER_ALIGNMENT);
        itemConcerned.add(possibleAction);
        itemConcerned.add(Box.createVerticalStrut(10));

        // Price
        price.setAlignmentX(CENTER_ALIGNMENT);
        itemConcerned.add(price);
        itemConcerned.add(Box.createVerticalStrut(10));

        // Image
        actionImage.setAlignmentX(CENTER_ALIGNMENT);
        itemConcerned.add(actionImage);

        return itemConcerned;
    }

    /**
     * Method to set up this view as a listener of the inventory and the player model.
     * Adds mouse adapters to the labels to buy an item and exit the store
     * @param inventory Shop inventory of products
     * @param playerInventory Inventory of the game's player
     * @param shopController Controller of the shop
     */
    public void setup (ProductInventory inventory, Inventory playerInventory, ShopController shopController) {
        this.shopController = shopController;
        updateShopItems(inventory.getCurrentArrayOfProductRecords());

        inventory.addListener(evt -> {
            if (evt.getPropertyName().equals("inspectedItemChange")) {
                updateDisplayedItem((ProductInventory.ProductRecord) evt.getNewValue());
            } else if (evt.getPropertyName().equals("languageChange")) {
                updateShopLanguage((GameEngine.Language)evt.getNewValue());
            }
        });
        playerInventory.addListener(evt -> {
            if (evt.getPropertyName().equals("inspectedItemChange")) {
                updateDisplayedItem((BagInventory.ItemRecord) evt.getNewValue());
            }
        });

        exit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                gameView.updateViewTo(GameEngine.GameState.WALKING);
            }
        });
        possibleAction.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (possibleAction.getText().equals("Buy") || possibleAction.getText().equals("Kopen")) {
                    shopController.buyFromMerchant(displayedItem);
                } else {
                    shopController.sellToMerchant(displayedItem);
                }
            }
        });
    }

    /* Updating */

    /**
     * Method to update the displayed item that can be either bought by the player or sold by the merchant
     * depending on where the item comes from.
     * Here it is overloaded to be used for products from the merchant that can be sold to the player.
     * @param newValue New displayed item
     */
    private void updateDisplayedItem(ProductInventory.ProductRecord newValue) {
        this.displayedItem = newValue.item();
        if (possibleAction.getBorder() == null) {
            possibleAction.setBorder(BorderFactory.createLineBorder(Color.BLACK,3));
        }
        possibleAction.setText(buyString);
        price.setText(displayedItem.getPrice() + goldString);
        actionImage.setIcon(newValue.item().getItemImage());
        revalidate();
    }

    /**
     * Method to update the displayed item that can be either bought by the player or sold by the merchant
     * depending on where the item comes from.
     * Here it is overloaded to be used for items from the player that can be bought by the merchant.
     * If the player has not selected an item to sell to the merchant, the displayed item shows nothing.
     * @param newValue New displayed item
     */
    private void updateDisplayedItem(BagInventory.ItemRecord newValue) {
        this.displayedItem = newValue.item();
        if (newValue.item() == null) {
            possibleAction.setText(" ");
            possibleAction.setBorder(null);
            price.setText(" ");
            actionImage.setIcon(null);
        } else {
            possibleAction.setText(sellString);
            if (possibleAction.getBorder() == null) {
                possibleAction.setBorder(BorderFactory.createLineBorder(Color.BLACK,3));
            }
            price.setText(displayedItem.getPrice()*3/4 + goldString);
            actionImage.setIcon(newValue.item().getItemImage());
        }
        revalidate();
    }

    /**
     * Method used to show all the shop items.
     * Their labels and images are stored in an array list for later resizing.
     * @param inventory Inventory of items to be displayed.
     */
    private void updateShopItems(ArrayList<ProductInventory.ProductRecord> inventory) {
        for (ProductInventory.ProductRecord item :inventory) {
            ImageIcon itemImage = item.item().getItemImage();
            JLabel itemLabel = new JLabel(itemImage);
            itemLabel.setAlignmentX(CENTER_ALIGNMENT);
            itemLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            itemLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                   shopController.setInspectedItem(item.item());
                }
            });
            shopItems.add(itemLabel);
            shopItemsImage.add(itemImage);
            goods.add(itemLabel);
        }
    }

    /**
     * Updates the shop language
     * @param newValue Language the shop should be in
     */
    private void updateShopLanguage(GameEngine.Language newValue) {
        if (newValue == GameEngine.Language.ENGLISH) {
            exit.setText("Exit");
            buyString = "Buy";
            sellString = "Sell";
            goldString = " gold";
        } else {
            exit.setText("Uitgang");
            buyString = "Kopen";
            sellString = "Verkopen";
            goldString = " goud";
        }

        /* Changes the possible action label according to what action can be done */
        if (possibleAction.getText().equals("Kopen") || possibleAction.getText().equals("Buy")) {
            possibleAction.setText(buyString);
            price.setText(displayedItem.getPrice() + goldString);
        } else if (possibleAction.getText().equals("Verkopen") || possibleAction.getText().equals("Sell")) {
            possibleAction.setText(sellString);
            price.setText(displayedItem.getPrice()*3/4 + goldString);
        }
    }
}
