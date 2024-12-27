package nl.rug.ai.oop.rpg.model.player;

import nl.rug.ai.oop.rpg.model.character.Character;
import nl.rug.ai.oop.rpg.model.character.Health;
import nl.rug.ai.oop.rpg.model.engine.GameEngine;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;

/**
 * @author Otto Bervoets
 * A class representing the player that is used by the user. It extends Character and implements Serializable as
 * a player is a character, and it needs to be saveable. Also lets Harmen create a special player...
 * *
 * NOTE: below I will give a small description of the things that are missing and are hard to implement as the NPC
 * class is at this time of writing (8 hours prior to the deadline) far from finished.
 */
public class PlayerModel extends Character implements Serializable {
    @Serial
    private static final long serialVersionUID = 13L;
    private boolean playerBuild = false;
    final transient private FightNPCModel fightNPCModel;
    public enum State {
        FIGHTING, WALKING, DEATH, BUILDING, WAIT_TO_CONTINUE
    }

    private volatile State state = State.WALKING;

    /**
     * Constructs a player model. If a saved player is there this will be used. Otherwise, the player will
     * be constructed with default values. If this is the case, the view will be asked to set the name and faction
     */
    public PlayerModel() {
        super();
        this.fightNPCModel = new FightNPCModel();
        PlayerModel savedPlayer = readPlayer();
        if(savedPlayer != null) {
            setupFromSavedPlayer(savedPlayer);
            return;
        }
        this.attack.replace(Faction.FIRE,5);
        this.attack.replace(Faction.WOOD,5);
        this.attack.replace(Faction.WATER,5);
        this.defence = 3;
        this.health = new Health(30,50);
        this.playerBuild = true;
        this.faction = null;
        this.name = null;
        this.state = State.BUILDING;
        this.money = 15;
    }

    /**
     * Lets the player recover a health amount of amount
     * @param amount the amount to recover
     */
    public void recover(int amount) {
        health.recovery(amount);
        notifyListeners();
    }

    /**
     * Increases all attack of this player (for example by using some item).
     * @param amount the mount to increase the attack.
     */
    public void increaseAllAttack(int amount){
        for(Faction fact:Faction.values()){
            attack.replace(fact, attack.get(fact) + amount);
        }
        notifyListeners();
    }

    /**
     * Increase the attack amount of one specific attack kind. Currently not used, as there are no such items
     * @param attackKind the attack type that will be increased
     * @param amount the amount to increase this attack with
     */
    public void increaseAttack(Faction attackKind, int amount) {
        attack.replace(attackKind, attack.get(attackKind) + amount);
    }

    /**
     * Increase the defence of the player
     * @param amount The amount to increase the defence with. This amount can also be negative as defence
     *               can decrease (by removing some item that has defence).
     */
    public void increaseDefense(int amount) {
        defence += amount;
        if (defence < 0) {
            defence = 0;
        }
        notifyListeners();
    }

    /**
     * Function called by the controller to execute attack the user wants. Can only be done if the state is fighting
     * @param attack The attack the user decides to do.
     */
    public void attack(Faction attack) {
        if(!state.equals(State.FIGHTING)) {
            return;
        }
        fightNPCModel.playerAttack(attack, this);
        notifyListeners();
        if(!isAlive()) {
            state = State.WAIT_TO_CONTINUE;
        }
        if(!fightNPCModel.NPCAlive()) {
            state = State.WAIT_TO_CONTINUE;
        }
        notifyListeners();
    }

    public void continueGame() {
        if(isAlive()) {
            state = State.WALKING;
            notifyListenersResetFightView();
        } else {
            state = State.DEATH;
        }
        notifyListeners();
    }

    /**
     * Sets the name of the player, controlled by the PlayerBuilderController, name can only set when building
     * @param name the name to set
     */
    public void setName(String name) {
        if(!state.equals(State.BUILDING) || name.equals("")) {
            return;
        }
        this.name = name;
        if(this.name != null && faction != null) {
            checkHarmen();
            state = State.WALKING;
            System.out.println("Player is build with name: " + this.name + " and faction: " + faction + ".");
            notifyListeners();
        }
    }

    /**
     * Special function that only gives Harmen an insanely strong player to conquer the world!
     */
    private void checkHarmen() {
        if(this.name.equals("Harmen de Weerd")) {
            increaseAllAttack(100);
            increaseMaxHealth(100);
            recover(100);
            money = 10000;
        }
    }

    /**
     * Only used by NPC class
     * @return --- not used
     */
    @Override
    public Faction getAttackType() {
        return faction;
    }

    /**
     * Sets the faction, can only be used if the player is in the building phase
     * @param faction faction of the player.
     */
    public void setFaction(Faction faction) {
        if(state != State.BUILDING) {
            return;
        }
        this.faction = faction;
        attack.replace(faction, 10);
        if(name != null && faction != null) {
            state = State.WALKING;
            System.out.println("Player is build with name: " + name + " and faction: " + this.faction + ".");
            notifyListeners();
        }
    }

    /**
     * Sets the NPC that the player is going to fight iin the fightNPCmodel
     * @param NPC to fight
     */
    private void setFightNPCModel(Character NPC) {
        fightNPCModel.setNPC(NPC);
    }

    /**
     * Sets the state to fighting and sets which NPC the player is to fight.
     * @param NPC the NPC to fight
     */
    public void fightNPC(Character NPC){
        setFightNPCModel(NPC);
        state = State.FIGHTING;
        notifyListeners();
    }

    /**
     * Performs a test fight with a default NPC as the NPC class is not yet functional
     */
    public void testFight() {
        state = State.FIGHTING;
        fightNPCModel.testFight();
        notifyListeners();
    }

    /**
     * Gets the current state of the game.
     * @return the state of the game
     */
    public State getState() {
        return state;
    }

    /**
     * Add money to the player
     * @param amount the amount of money to add
     */
    public void addMoney(int amount) {
        if(amount < 0) {
            return;
        }
        System.out.println("add money called");
        money += amount;
        notifyListeners();
    }

    /**
     * Updates the language of the text model
     * @param language the new language.
     */
    public void updateLanguage(GameEngine.Language language) {
        TextModel.language = language;
        notifyListeners();
    }

    /**
     * Increases the max health of the player, used by the inventory.
     * @param amount the amount to increase the health with.
     */
    public void increaseMaxHealth (int amount) {
        health.increaseMaxHp(amount);
        notifyListeners();
    }

    /**
     * Subtracts money from the player. (If the player has enough is checked by the inventory person)
     * @param amount the amount to spend should be positive.
     */
    public void spendMoney(int amount) {
        money -= amount;
        notifyListeners();
    }

    /**
     * Check whether the amount can be spend. Currently not used.
     * @param amount the amount to check
     * @return whether this amount can be spend
     */
    public boolean canSpend(int amount) {
        return (amount <= money);
    }

    /**
     * Notifies listeners, uses the game state to determine what to send.
     */
    @Override
    protected void notifyListeners() {
        switch (state) {
            case WALKING -> notifyListenersWalking();
            case FIGHTING -> notifyListenersFighting();
            case DEATH -> notifyListenersDeath();
            case WAIT_TO_CONTINUE -> notifyListenersContinue();
        }

    }
    private void notifyListenersWalking() {
        PropertyChangeEvent health = new PropertyChangeEvent(this, "health", null, TextModel.healthText(this.health, true, false));
        PropertyChangeEvent attackText = new PropertyChangeEvent(this, "attackText", null, TextModel.buttonText(attack));
        PropertyChangeEvent defense = new PropertyChangeEvent(this, "defense", null, this.defence);
        PropertyChangeEvent state = new PropertyChangeEvent(this, "state", null, this.state);
        PropertyChangeEvent money = new PropertyChangeEvent(this, "money", null, this.money);
        PropertyChangeEvent faction = new PropertyChangeEvent(this, "faction", null, this.faction);
        PropertyChangeEvent factionText = new PropertyChangeEvent(this, "factionText", null, TextModel.factionText(this.faction));
        PropertyChangeEvent name = new PropertyChangeEvent(this, "name", null, TextModel.nameText(this.name));
        for (PropertyChangeListener listener : listeners) {
            listener.propertyChange(health);
            listener.propertyChange(attackText);
            listener.propertyChange(defense);
            listener.propertyChange(state);
            listener.propertyChange(money);
            listener.propertyChange(faction);
            listener.propertyChange(name);
            listener.propertyChange(factionText);
        }
    }

    private void notifyListenersFighting(){
        notifyListenersWalking();
        PropertyChangeEvent NPCdescription = new PropertyChangeEvent(this, "NPCdescription", null, TextModel.characterDescription(fightNPCModel.getNPC()));
        PropertyChangeEvent playerDescription =  new PropertyChangeEvent(this, "playerDescription", null, TextModel.characterDescription(this));
        PropertyChangeEvent fightMessage = new PropertyChangeEvent(this, "fightMessage", null, fightNPCModel.getFightMessage());
        for (PropertyChangeListener listener : listeners) {
            listener.propertyChange(NPCdescription);
            listener.propertyChange(playerDescription);
            listener.propertyChange(fightMessage);
        }
    }
    private void notifyListenersDeath() {
        PropertyChangeEvent playerDeath = new PropertyChangeEvent(this, "playerDeath", null, "playerDeath");
        for (PropertyChangeListener listener : listeners) {
            listener.propertyChange(playerDeath);
        }
    }

    private void notifyListenersContinue() {
        notifyListenersFighting();
        PropertyChangeEvent continueGame = new PropertyChangeEvent(this, "continue", null, null);
        for (PropertyChangeListener listener : listeners) {
            listener.propertyChange(continueGame);
        }
    }
    private void notifyListenersResetFightView() {
        PropertyChangeEvent setFightBack = new PropertyChangeEvent(this, "setFightBack", null, null);
        for (PropertyChangeListener listener : listeners) {
            listener.propertyChange(setFightBack);
        }
    }


    /**
     * Saves the player, using the write object function.
     */
    public void save(){
        try (ObjectOutputStream output = new ObjectOutputStream(
                new FileOutputStream("src/main/resources/saving_data/playerModel.txt"))){
            output.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
    private PlayerModel readPlayer() {
        PlayerModel savedPlayer = null;
        try (ObjectInputStream input = new ObjectInputStream(new FileInputStream("src/main/resources/saving_data/playerModel.txt"))) {
            savedPlayer = (PlayerModel) input.readObject();
        } catch (FileNotFoundException e){
            System.out.println("No save file found, construct a new player");
        }
        catch (IOException | ClassNotFoundException e) {
            System.out.println("The found player is not correct, construct a new player ");
        }
        return savedPlayer;
    }

    private void setupFromSavedPlayer(PlayerModel savedPlayer) {
        System.out.println("Loading saved player");
        this.attack = savedPlayer.attack;
        this.playerBuild = savedPlayer.playerBuild;
        this.faction = savedPlayer.faction;
        this.defence = savedPlayer.defence;
        this.health = savedPlayer.health;
        this.name = savedPlayer.name;
        this.money = savedPlayer.money;
    }
}
/**
 * So since the NPC class is just non functional at this point in time it is hard to check the win condition.
 * During the brainstorming we agreed on a beeing victorious in the game if all NPC's of the other factions are
 * defeated. However, this win condition is now not possible to check. I also made a class to be able to fight NPCs
 * however, as there are no NPC's in the map to fight, to show case this something needs to be done.
 * To fixt the fighting issue, the fighting class has a function testFight that lets us fight a NPC that is constructed
 * in the default constructor of the figth class. Hence, when bumping in to a NPC the is just generated some default NPC
 * which can be fought to show how the fighting works. Each time you fight the NPC you get his amount of money, which in the
 * default case is equal to 1, just to show it works.
 */