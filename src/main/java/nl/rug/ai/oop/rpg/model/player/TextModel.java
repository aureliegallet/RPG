package nl.rug.ai.oop.rpg.model.player;

import nl.rug.ai.oop.rpg.model.character.Character;
import nl.rug.ai.oop.rpg.model.character.Health;
import nl.rug.ai.oop.rpg.model.engine.GameEngine;

import java.util.HashMap;

/**
 * Class responsible for making pretty strings to send to the view, depending on the language. All functions are static.
 * @author Otto Bervoets
 */
public class TextModel {
    public static GameEngine.Language language = GameEngine.Language.ENGLISH;

    /**
     * Generates a message over the current fight that happens
     * @param state what just happend
     * @param attack the attack strenght of the Character that won
     * @param damage the amount of damage the attack has done
     * @return a string contianing a nice message
     */
    public static String fightMessage(String state, Character.Faction attack, int damage, int money) {
        if (language.equals(GameEngine.Language.ENGLISH)) {
            switch (state) {
                case "start" -> {
                    return "The fight is yet to begin, choose your attack wisely!!";
                }
                case "equal" -> {
                    return "You both used the same attack: " + attack.toString() + "! They have canceled each-other out!";
                }
                case "playerWin" -> {
                    return "You successfully attacked the NPC with your attack " + attack.toString() + " you have dealt "
                            + damage + " damage.";
                }
                case "NPCdeath" -> {
                    return "You successfully defeated the NPC with your attack " + attack.toString() + " you have dealt "
                            + damage + " damage. You recieve " + money  + " money.";
                }
                case "NPCwin" -> {
                    return "Ouch, the NPC broke trough your defenses with the attack " + attack.toString() + " he deals "
                            + damage + " damage.";
                }
                case "PlayerDeath" -> {
                    return "Oh no! the NPC broke trough your defenses with the attack " + attack.toString() + " he deals "
                            + damage + " damage and defeats you.";
                }
            }
        }
        return switch (state) {
            case "start" -> "Het gevecht moet nog beginnen, kies je aanval wijs!";
            case "equal" ->
                    "Jullie hebben de zelfde aanval: " + attack.toString() + " gebruikt! Ze hebben elkaar opgeheven!";
            case "playerWin" ->
                    "Je hebt de NSC succes vol aangevallen met aanval: " + attack.toString() + " je hebt " + damage +
                            " schade toegericht.";
            case "NPCdeath" ->
                    "Je hebt de NSC succes vol verslagen met aanval: " + attack.toString() + " je hebt " + damage +
                            " schade toegericht. En je ontvangt " + money + " geld";
            case "NPCwin" ->
                    "Auw, de NSC is door je verdediging heen gebroken met aanval: " + attack.toString() + "! hij deelt "
                            + damage + " schade toe.";
            case "PlayerDeath" ->
                    "Ohnee! De NSC is door je verdediging heen gebroken met aanval:" + attack.toString() + "! hij deelt "
                            + damage + " schade toe en verslaat je.";
            default -> "something went wrong in the language model";
        };
    }

    /**
     * Returns a Hashmap containing a text representation for each language
     * @param attack the attack hashmap
     * @return a hashmap with a string describing the attack + strength for each attack type
     */
    public static HashMap<Character.Faction, String> buttonText(HashMap<Character.Faction, Integer> attack) {
        HashMap<Character.Faction, String> returnMap = new HashMap<>(3);
        if(language.equals(GameEngine.Language.ENGLISH)) {
            returnMap.put(Character.Faction.FIRE, "Fire (" + attack.get(Character.Faction.FIRE) + ")");
            returnMap.put(Character.Faction.WATER, "Water (" + attack.get(Character.Faction.WATER) + ")");
            returnMap.put(Character.Faction.WOOD, "Wood (" + attack.get(Character.Faction.WOOD) + ")");
            return returnMap;
            }
        returnMap.put(Character.Faction.FIRE, "Vuur (" + attack.get(Character.Faction.FIRE) + ")");
        returnMap.put(Character.Faction.WATER, "Water (" + attack.get(Character.Faction.WATER) + ")");
        returnMap.put(Character.Faction.WOOD, "Hout (" + attack.get(Character.Faction.WOOD) + ")");
        return returnMap;
    }

    /**
     * Makes string contianing the health display with various options
     * @param health The health object to describe
     * @param textInFront Whether you want a description infront
     * @param NPC True if this concerns an NPC false if it is a player
     * @return The string containing the message
     */
    public static String healthText(Health health, boolean textInFront, boolean NPC) {
        String s = "";
        if(textInFront) {
            if(language.equals(GameEngine.Language.ENGLISH)) {
                if (NPC) {
                    s = "NPC health: ";
                } else {
                    s = "Your Health: ";
                }

            } else {
                if (NPC) {
                    s = "NSP gezondheid";
                } else {
                    s = "Jou Gezondheid; ";
                }
            }
        }
        s += health.getHp() + "/" + health.getMaxHp() + ".";
        return s;
    }

    /**
     * Creates a description describing the faction of the player
     * @param faction the faction of the player
     * @return a string containing a text representation of the
     */
    public static String factionText(Character.Faction faction) {
        if(language.equals(GameEngine.Language.ENGLISH)) {
            return switch (faction){
                case FIRE -> "Faction: Fire.";
                case WOOD -> "Faction: Wood.";
                case WATER -> "Faction: Water.";
            };
        }
        return switch (faction) {
            case WOOD -> "Fractie: Hout.";
            case FIRE -> "Fractie: Vuur.";
            case WATER -> "Fractie: Water.";
        };
    }

    /**
     * Returns a string describing the name of the player with "name" in the correct language in front.
     * @param name the name of the player
     * @return a string with "name" or "naam" infront of the acutual name
     */
    public static String nameText(String name) {
        return switch (language) {
            case ENGLISH -> "Name: " + name;
            case DUTCH -> "Naam: " + name;
            };
    }

    /**
     * Generates a nice intro story.
     * @return A string to display in html format.
     */
    public static String introStory() {
        return switch (language) {
            case ENGLISH -> "<html>" +
                    "You have entered the magical world of LuXuOtAuré. After many centuries of debate, three factions <br/>" +
                    "have formed to determine whose power is the strongest. Choose a faction to join to help them fight <br/>" +
                    "for supremacy! Signing up in faction has great impact on this game as your goal is to defeat all <br/>" +
                    "players from the two other factions. To help you in this quest, you will receive a set of light armor <br/>" +
                    "and some goods that will help you along the way. Use these items carefully.... Good luck!<br/>" +
                    "</html>";
            case DUTCH -> "<html>" +
                    "Welkom in de magische wereld van LuXuOtAuré. Na jaren onenigheid zijn er drie fracties gevormd <br/>" +
                    "om te bepalen welke kracht het sterkte is. Kies een fractie en help hun in het gevecht voor ode overheersing <br/>" +
                    "Een fractie kiezen heeft grote impact op het, jou doel is namelijk om de spelers van alle andere fracties te <br/>" +
                    "verslaan. Om je met dit avontuur te helpen zal je een set van lichte pantser ontvangen en wat andere items <br/>" +
                    "die je onderweg goed van pas zullen komen. Gebruik deze items wijs.... Veel plezier!" +
                    "</html>";
        };
    }

    /**
     * @return the text for a button to continue with the game in the appropriate language
     */
    public static String continueGame() {
        return switch (language){
            case DUTCH -> "Ga door met het spel";
            case ENGLISH -> "Continue the game";
        };
    }

    /**
     * With the defence constructs a string in the correct language
     * @param defence the defence
     * @return the string with defence and text infront in the correct language
     */
    public static String defenceText(int defence) {
        return switch (language) {
            case ENGLISH -> "Defence: " + defence;
            case DUTCH ->  "Verdediging: " + defence;
        };
    }

    /**
     * @return a death text in the correct language
     */
    public static String deathText() {
        return switch (language) {
            case DUTCH -> "Je hebt verloren, je bent dood, het spel is afgelopen";
            case ENGLISH -> "You lost, you died, the game is over now";
        };
    }

    /**
     * Generate text for a language button
     * @return the text for the language button (hence the opposite language is shown here)
     */
    public static String languageText() {
        return switch (language) {
            case DUTCH -> "English";
            case ENGLISH -> "Nederlands";
        };
    }
    public static String characterDescription(Character character) {
        return switch (language) {
            case DUTCH -> "<html>" +
                    "Naam: " + character.getName() + "<br/>" +
                    "Fractie: " + character.getFaction() + "<br/>" +
                    "Geld: " + character.getMoney() + "<br/>" +
                    "Water aanvalskracht: " + character.getAttackStrength(Character.Faction.WATER) + "<br/>" +
                    "Vuur aanvalskracht: " + character.getAttackStrength(Character.Faction.FIRE) + "<br/>" +
                    "Hout aanvalskracht: " + character.getAttackStrength(Character.Faction.WOOD) + "<br/>" +
                    "Verderigings sterkte: " + character.getDefence() + "<br/> <br/>" +
                    "Levens (hp): " + character.getHealth().getHp() + "<br/>" +
                    "Maximale Levens: " + character.getHealth().getMaxHp() + "<br/>" +
                    "<html/>";
            case ENGLISH -> "<html>" +
                    "Name: " + character.getName() + "<br/>" +
                    "Fraction: " + character.getFaction() + "<br/>" +
                    "Money: " + character.getMoney() + "<br/>" +
                    "Water attack: " + character.getAttackStrength(Character.Faction.WATER) + "<br/>" +
                    "Fire attack: " + character.getAttackStrength(Character.Faction.FIRE) + "<br/>" +
                    "Wood attack: " + character.getAttackStrength(Character.Faction.WOOD) + "<br/>" +
                    "Defense strenght: " + character.getDefence() + "<br/> <br/>" +
                    "Lifes (hp): " + character.getHealth().getHp() + "<br/>" +
                    "Maximum lifes: " + character.getHealth().getMaxHp() + "<br/>" +
                    "<html/>";
        };
    }
}
