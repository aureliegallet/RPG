package nl.rug.ai.oop.rpg.model.inventory;

import nl.rug.ai.oop.rpg.model.engine.GameEngine;

import javax.swing.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Scanner;

/**
 * General class for the Items of our game.
 * Items are mostly not modifiable as most of their properties are final except for the boost.
 * In this implementation, items know only about themselves and not what we do with them.
 * @author Aurélie Gallet
 * @version 1.0
 */
public class Item implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private GameEngine.Language language = GameEngine.Language.ENGLISH;
    private final int identifier;
    private final String nameEN;
    private final String nameNL;
    private final ImageIcon itemImage;
    private final String descriptionEN;
    private final String descriptionNL;

    /**
     * Enumeration of effects an item can have
     * Implemented this way because all items have a singular effect and boost associated with it
     * The only item for which it is not the case gets a boost of 0 and a special effect (subway)
     */
    public enum Effect {
        ATTACKING, ATTACK_BOOST, DEFENDING, DEFENSE_BOOST, HEALING, HEALTH_BOOST, SUBWAY
    }
    private final Effect effect;
    private int boost;
    private final int price;


    /**
     * Generates an item according to its properties
     * @param identifier Unique item type identifier
     * @param nameEN English item name
     * @param nameNL Dutch item name
     * @param descriptionEN English item description
     * @param descriptionNL Dutch item description
     * @param effect Effect of the item
     * @param boost Boost of the item
     * @param value Value of the item
     */
    public Item(int identifier, String nameEN, String nameNL, String descriptionEN, String descriptionNL, Effect effect, int boost, int value) {
        this.identifier = identifier;
        this.nameEN = nameEN;
        this.nameNL = nameNL;
        this.itemImage = new ImageIcon(Objects.requireNonNull(Item.class.getResource("/inventory/items/" + identifier + ".png")));
        this.descriptionEN = descriptionEN;
        this.descriptionNL = descriptionNL;
        this.effect = effect;
        this.boost = boost;
        this.price = value;
    }

    /**
     * Method to read item properties from a text file and instantiate them
     * @param fileInput file to be read
     * @return {@link Item} Corresponding item
     */
    public static Item initialiseAnItem(Scanner fileInput) {
        int identification = fileInput.nextInt();
        fileInput.nextLine(); //goes to next line
        String nameEN = fileInput.nextLine();
        String nameNL = fileInput.nextLine();
        String descriptionEN = fileInput.nextLine();
        String descriptionNL = fileInput.nextLine();
        Item.Effect effect = null;
        String effectString = fileInput.nextLine();
        switch (effectString) {
            case "attack" -> effect = Item.Effect.ATTACKING;
            case "attackboost" -> effect = Item.Effect.ATTACK_BOOST;
            case "defense" -> effect = Item.Effect.DEFENDING;
            case "defenseboost" -> effect = Item.Effect.DEFENSE_BOOST;
            case "healing" -> effect = Item.Effect.HEALING;
            case "health" -> effect = Item.Effect.HEALTH_BOOST;
            case "subway" -> effect = Item.Effect.SUBWAY;
        }
        int boost = fileInput.nextInt();
        int price = fileInput.nextInt();
        return new Item(identification, nameEN, nameNL, descriptionEN, descriptionNL, effect, boost, price);
    }


    /* Getters */

    /**
     * Getter for the item identifier
     * Integer so that it can be evaluated in a filter
     * @return {@link Integer} item identifier
     */
    public Integer getIdentifier() {
        return identifier;
    }

    /**
     * Getter for the item name according to the current game language
     * @return {@link String} item name
     */
    public String getName() {
        if (language == GameEngine.Language.ENGLISH) {
            return nameEN;
        } else if (language == GameEngine.Language.DUTCH) {
            return nameNL;
        }
        return null;
    }

    /**
     * Getter for the item image
     * @return {@link ImageIcon} item image
     */
    public ImageIcon getItemImage() {
        return itemImage;
    }

    /**
     * Getter for the item image path
     * @return {@link String} item image path
     */
    public String getItemImagePath() {
        return "/inventory/items/" + identifier + ".png";
    }

    /**
     * Getter for the item description according to the current game language
     * @return {@link String} item description
     */
    public String getDescription() {
        if (language == GameEngine.Language.ENGLISH) {
            return descriptionEN;
        } else if (language == GameEngine.Language.DUTCH) {
            return descriptionNL;
        }
        return null;
    }

    /**
     * Getter for the item effect
     * @return {@link Effect} item effect
     */
    public Effect getEffect() {
        return effect;
    }

    /**
     * Getter for the item effect string
     * @return {@link String} item effect string
     */
    public String getEffectString() {
        String effectString = null;
        if (language == GameEngine.Language.ENGLISH) {
            switch (effect) {
                case ATTACKING -> effectString = "Attack";
                case ATTACK_BOOST -> effectString = "General Attack Boost";
                case DEFENDING -> effectString = "Defense";
                case DEFENSE_BOOST -> effectString = "General Defense Boost";
                case HEALING -> effectString = "Healing";
                case HEALTH_BOOST -> effectString = "General Health Boost";
                case SUBWAY -> effectString = "Subway";
            }
        } else {
            switch (effect) {
                case ATTACKING -> effectString = "Aanval";
                case ATTACK_BOOST -> effectString = "Algemene aanvalsboost";
                case DEFENDING -> effectString = "Verdediging";
                case DEFENSE_BOOST -> effectString = "Algemene defensieboost";
                case HEALING -> effectString = "Genezing";
                case HEALTH_BOOST -> effectString = "Algemene gezondheidsboost";
                case SUBWAY -> effectString = "Metro";
            }
        }
        return effectString;
    }

    /**
     * Getter for the item effect boost
     * @return item effect boost
     */
    public int getBoost() {
        return boost;
    }

    /**
     * Getter for the item price
     * @return item price
     */
    public int getPrice() {
        return price;
    }

    /**
     * Setter for the item effect boost
     * @param boost new item effect boost
     */
    public void setBoost(int boost) {
        this.boost = boost;
    }

    /**
     * Method to update the language of the item model
     * @param language Language to be updated to
     */
    public void updateLanguage(GameEngine.Language language) {
        this.language = language;
    }

    /**
     * Method to make the item serializable and generate a string of the item
     * @return {@link String} item string with all parameters
     */
    public String toString() {
        return identifier + " " + nameEN + " " + nameNL + " " + descriptionEN + " " + descriptionNL + " " + boost + " " + price;
    }
}
