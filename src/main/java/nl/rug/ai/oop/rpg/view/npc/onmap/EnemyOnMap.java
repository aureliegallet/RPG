package nl.rug.ai.oop.rpg.view.npc.onmap;

import nl.rug.ai.oop.rpg.model.locations.GameLocation;
import nl.rug.ai.oop.rpg.model.npc.Enemy;

public class EnemyOnMap {
    private final GameLocation location;

    public EnemyOnMap(GameLocation location) {
        this.location = location;
    }


    public void addEnemy(int x, int y) {
        String imagePath = "/skins/npc.png";
        location.addObject("Enemy", new Enemy(),
                GameLocation.Location.ISLAND, x,  y, imagePath, false);
    }
}
