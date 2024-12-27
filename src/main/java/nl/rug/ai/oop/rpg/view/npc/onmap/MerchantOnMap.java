package nl.rug.ai.oop.rpg.view.npc.onmap;

import nl.rug.ai.oop.rpg.model.locations.GameLocation;
import nl.rug.ai.oop.rpg.model.npc.Merchant;

public class MerchantOnMap {

    public void addMerchant(int x, int y, String itemName, int price, GameLocation location) {
        String imagePath = "/skins/greenguy.png";
        location.addObject("Merchant", new Merchant(itemName, price),
                GameLocation.Location.ISLAND, x, y, imagePath, false);
    }
}
