package nl.rug.ai.oop.rpg.model.character;

import java.beans.PropertyChangeListener;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Otto Bervoets
 * Abstract class that can be used to construct both players and NPC's. To make sure players are savable,
 * we implement Serializable.
 */
public abstract class Character implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public enum Faction {
        FIRE, WOOD, WATER
    }
    protected Faction faction;
    protected String name;

    protected HashMap<Faction, Integer> attack = new HashMap<>(3);
    protected int defence;
    protected Health health = new Health(20 ,20);
    protected int money = 1;


    transient protected ArrayList<PropertyChangeListener> listeners = new ArrayList<>(); //array list containing the listeners

    /**
     * Default constructor, fills the hashmap
     */
    protected Character(){
        this.name = "demo character";
        attack.put(Faction.WATER, 4);
        attack.put(Faction.WOOD, 4);
        attack.put(Faction.FIRE, 4);
    }

    /**
     * Constructs a player with a set faction
     * @param faction the faction of the player
     */
    protected Character(Faction faction) {
        attack.put(Faction.WATER, 4);
        attack.put(Faction.WOOD, 4);
        attack.put(Faction.FIRE, 4);
        this.faction = faction;
    }

    /**
     * lets the player take damage to its health.
     * @param damage damage to take. Note: the function will correct the damage with the defense.
     */
    public void takeDamage(int damage) {
        notifyListeners();
        health.takeDamage(damage - defence);
    }

    /**
     * Check if the player is alive
     * @return True if the player is alive.
     */
    public Boolean isAlive() {
        return health.isAlive();
    }

    /**
     * Set the name of the character.
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the attack strength of a specific attack
     * @param attack The attack type of which the strength needs to be returned
     * @return The strength of an attack
     */
    public int getAttackStrength(Faction attack) {
        return this.attack.get(attack);
    }

    /**
     * Get the defence strength of a Character
     * @return the defence strength of a Character
     */
    public int getDefence() {
        return defence;
    }

    /**
     * Get the faction of the Character
     * @return the faction
     */
    public Faction getFaction() {
        return faction;
    }

    /**
     * Get the health (object)
     * @return the health, which is an object.
     */
    public Health getHealth(){
        return health;
    }

    /**
     * Get the name of the player
     * @return the name of the player.
     */
    public String getName() {
        return name;
    }

    public int getMoney() {
        return money;
    }

    /**
     * Only used by NPCs to get the type of attack they send (per faction)
     * @return the type of attack (same enum as faction) that the player will use.
     */
    abstract public Faction getAttackType();

    /**
     * Add a listener to the arraylist of listeners
     * @param listener the listener to add
     */
    public void addListener(PropertyChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * remove a listener of this player
     * @param listener a property change listener to be removed
     */
    public void removeListener(PropertyChangeListener listener) {
        listeners.remove(listener);
    }

    /**
     * Abstract method, notifies the listeners.
     */
    abstract protected void notifyListeners();
}
