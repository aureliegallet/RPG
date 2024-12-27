package nl.rug.ai.oop.rpg.view.location;

import nl.rug.ai.oop.rpg.model.locations.GameLocation;
import java.awt.*;


/**
 * Map only view. It displays the entire map, without allowing any actions.
 *
 * @author Luca Colli
 * @version 1.0
 */
public class MapOnlyView extends Map {

    /**
     * Sets the default values of the view, and adds listeners to changes in the map or in the player location.
     * @param gameLocation {@link GameLocation} model
     */
    public void setup(GameLocation gameLocation) {
        this.setBackground(new Color(185, 122, 86));
        setGameLocationImages(gameLocation);
        this.playerCoordinates = gameLocation.getPlayerCoordinates();

        gameLocation.addListener(evt -> {
            switch (evt.getPropertyName()) {
                case "map" -> setGameLocationImages(gameLocation);
                case "playerCoords" -> this.playerCoordinates = gameLocation.getPlayerCoordinates();
            }
        });
    }

    /**
     * Paints the map matrix and the position of the player on the component.
     * The size of a tile is the biggest possible to fit the window.
     * This is calculated using the following proportion: width/height = cols/rows.
     * The player is represented as a red circle.
     * @param g the <code>Graphics</code> object to protect
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int tileSize;
        if (getWidth() < (getHeight() * images.length / images[0].length)) {
            tileSize = Math.floorDiv(getWidth(), images.length);
        } else {
            tileSize = Math.floorDiv(getHeight(), images[0].length);
        }

        int deltaX = (getWidth() - tileSize * images.length) / 2;
        int deltaY = (getHeight() - tileSize * images[0].length) / 2;

        g.clipRect(0, 0, getWidth(), getHeight());

        for (int col = 0; col < images.length; col++) {
            for (int row = 0; row < images[0].length; row++) {
                g.drawImage(images[col][row], col * tileSize + deltaX, row * tileSize + deltaY, tileSize, tileSize, null);
            }
        }

        g.setColor(Color.RED);
        g.fillOval(playerCoordinates[0] * tileSize + 2 + deltaX, playerCoordinates[1] * tileSize + 2 + deltaY, tileSize - 4, tileSize - 4);

        g.dispose();
    }

}
