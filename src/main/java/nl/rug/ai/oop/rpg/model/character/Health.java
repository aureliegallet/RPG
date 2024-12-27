package nl.rug.ai.oop.rpg.model.character;

import java.io.Serializable;

/**
 * @author Otto Bervoets
 * Class that can manage the health of a Character. Needs to be saveable just as a character, hence implements
 *  Serializable.
 */
public class Health implements Serializable {
    private static final long serialVersionUID = 1L;
    private int hp;  // health point
    private int maxHp; // maximum health points

    /**
     * default constructor, hp and maxHP are initialized to zero
     */
    public Health() {
        this.hp = 0;
        this.maxHp = 0;
    }

    /**
     * Constructs a health object. If the current HP is higher than the max HP, the current will be the maxHP.
     * @param hp The current HP
     * @param maxHp the max HP
     */
    public Health(int hp, int maxHp) {
        this.maxHp = maxHp;
        if(maxHp < 0) {
            this.maxHp = 0;
        }
        setHealth(hp);
    }

    /**
     * returns whether the hp is above 0
     * @return True if the health is above 0.
     */
    boolean isAlive() {
        return hp > 0;
    }

    /**
     * Increases the hp with a certain amount with a max of the maxHp.
     * @param amount the amount to increase the hp with. If negative nothing will happen.
     */
    public void recovery(int amount) {
        if(amount < 0) {
            return;
        }
        hp += amount;
        if (hp > maxHp) {
            hp = maxHp;
        }
    }

    /**
     * Decreases the hp with the damage
     * @param amount The amount of damage to subtract from the health. If amount is negative nothing happens.
     */
    public void takeDamage(int amount) {
        if (amount < 0) {
            return;
        }
        hp -= amount;
        if (hp < 0) {
            hp = 0;
        }
    }

    /**
     * Increases the maxHp with the amount.
     * @param amount the amount to increase the maxHp with, if the amount is negative, nothing happens
     */
    public void increaseMaxHp(int amount) {
        if(amount < 0) {
            return;
        }
        maxHp += amount;
    }

    /**
     * Get the hp of the health object
     * @return the hp.
     */
    public int getHp() {
        return hp;
    }

    /**
     * Get the maxHp of the health object
     * @return the maxHp
     */
    public int getMaxHp() {
        return maxHp;
    }

    /**
     * Set the hp to a certain amount. If this amount is negative the hp will be set to zero.
     * @param amount
     */
    public void setHealth(int amount) {
        if (amount < 0) {
            hp = 0;
            return;
        }
        if(amount > maxHp) {
            amount = maxHp;
        }
        hp = amount;
    }
}
