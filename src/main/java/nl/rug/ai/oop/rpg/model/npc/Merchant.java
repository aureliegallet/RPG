package nl.rug.ai.oop.rpg.model.npc;

import nl.rug.ai.oop.rpg.model.inventory.BagInventory;
import nl.rug.ai.oop.rpg.model.locations.GameLocation;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;

public class Merchant {
    private String itemName;
    private int price;
    private final ArrayList<PropertyChangeListener> listeners = new ArrayList<>(); //array list containing the listeners

    public Merchant(String itemName, int price) {
        this.itemName = itemName;
        this.price = price;
    }

    public Merchant() {

    }

    /**
     * There should be a way to feed the amount of money in the wallet in the method.
     * @param wallet the amount of money in the wallet
     * @return whether this item is affordable.
     */
    boolean isAffordable(int wallet) {
        return price <= wallet;
    }

    /**
     * Read coordinates from a file and update the NPC to the view.
     *
     * @param file       the file
     * @param profession the profession of the NPC
     */
    public void merchantPainter(File file, String profession) {
        try (Scanner fileInput = new Scanner(Objects.requireNonNull(
                BagInventory.class.getResourceAsStream("/npc_setup/npc_setup.txt")))) {
            while (fileInput.hasNextLine()) {
                final GameLocation location = new GameLocation();
                String nameEN = fileInput.nextLine();
                String nameNL = fileInput.nextLine();
                String descriptionEN = fileInput.nextLine();
                String descriptionNL = fileInput.nextLine();
                String itemName = fileInput.nextLine();
                int price = Integer.parseInt(fileInput.nextLine());
                int xLocation = fileInput.nextInt();
                System.out.println(xLocation);
                int yLocation = fileInput.nextInt();
                System.out.println(yLocation);
//                new Merchant().addMerchant(x, y, itemName, price, location);
                fileInput.nextLine();
            }
        }
    }

//    private void notifyListeners() {
//        Iterator<PropertyChangeListener> allListeners = listeners.iterator();
//        PropertyChangeEvent payload = new PropertyChangeEvent(this, "speed", null, getSpeed());
//        while (allListeners.hasNext()) {
//            allListeners.next().propertyChange(payload);
//        }
//    }
}
