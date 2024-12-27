package nl.rug.ai.oop.rpg.model.npc;

import nl.rug.ai.oop.rpg.model.character.Character;
import nl.rug.ai.oop.rpg.model.engine.GameEngine;
import nl.rug.ai.oop.rpg.model.locations.GameLocation;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;

/**
 * @author Jinsheng Xu
 */
public class Enemy extends Character {
    private int money;
    private Faction faction;
    private int x;
    private int y;

    public Enemy() {
        super();
        money = 5;
    }

    public Enemy(int money) {
        super();
        this.money = money;
    }

    public HashMap<Faction, Integer> attack() {
        HashMap<Faction, Integer> map = new HashMap<>();
        switch (this.faction) {
            case FIRE -> {
                map.put(Faction.FIRE, 3);
                map.put(Faction.WOOD, 1);
                map.put(Faction.WATER, 1);
            }
            case WOOD -> {
                map.put(Faction.WOOD, 3);
                map.put(Faction.WATER, 1);
                map.put(Faction.FIRE, 1);
            }
            case WATER -> {
                map.put(Faction.WATER, 3);
                map.put(Faction.FIRE, 1);
                map.put(Faction.WOOD, 2);
            }
            default -> {
                map.put(Faction.FIRE, 1);
                map.put(Faction.WATER, 1);
                map.put(Faction.WOOD, 1);
            }
        }

        return map;
    }

    @Override
    public Faction getAttackType() {
        return Faction.values()[new Random().nextInt(Faction.values().length)];
    }

    public int getAttackStrength(Faction faction) {
        return attack.get(faction);
    }


    public void save() {
        try (ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(
                "src/main/resources/saving_data/npc.txt"))) {
            output.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void notifyListeners() {
        PropertyChangeEvent health = new PropertyChangeEvent(
                this, "health", null, this.health.getHp());
        PropertyChangeEvent attack = new PropertyChangeEvent(
                this, "attack", null, this.attack);
        PropertyChangeEvent defense = new PropertyChangeEvent(
                this, "defense", null, this.defence);
        Iterator<PropertyChangeListener> allListeners = listeners.iterator();
        while (allListeners.hasNext()) {
            PropertyChangeListener listener = allListeners.next();
            listener.propertyChange(health);
            listener.propertyChange(attack);
            listener.propertyChange(defense);
        }
    }
}
