package nl.rug.ai.oop.rpg.controler;

import nl.rug.ai.oop.rpg.model.locations.GameLocation;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Controller for the GameLocation model.
 *
 * @author Luca Colli
 * @version 1.0
 */
public class LocationController extends KeyAdapter {
    private final GameLocation gameLocation;

    /**
     * Creates a controller for a GameLocation model
     * @param gameLocation the GameLocation model
     */
    public LocationController(GameLocation gameLocation) {
        this.gameLocation = gameLocation;
    }

    /**
     * When an arrow key is pressed, the model is updated so that player moves in the same direction as the arrow.
     * @param e the event to be processed
     */
    @Override
    public void keyPressed(KeyEvent e) {
        super.keyPressed(e);
        switch (e.getKeyCode()) {
            case 37 -> gameLocation.move(GameLocation.Direction.WEST);
            case 38 -> gameLocation.move(GameLocation.Direction.NORTH);
            case 39 -> gameLocation.move(GameLocation.Direction.EAST);
            case 40 -> gameLocation.move(GameLocation.Direction.SOUTH);
        }
    }
}
