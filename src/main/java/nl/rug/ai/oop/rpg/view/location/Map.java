package nl.rug.ai.oop.rpg.view.location;

import nl.rug.ai.oop.rpg.model.locations.GameLocation;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


/**
 * Abstract class for map view. It extends JPanel and contains the methods used by all its instances.
 *
 * @author Luca Colli
 * @version 1.0
 */
public abstract class Map extends JPanel {
    private final static List<GameLocation.Entity> ENTITIES_NORTH_OF_PATH = Arrays.asList(GameLocation.Entity.NPC_HOUSE, GameLocation.Entity.PLAYER_HOUSE, GameLocation.Entity.ARMORY, GameLocation.Entity.POTIONS);
    private final static List<GameLocation.Entity> ENTITIES_LINKING_PATHS = Arrays.asList(GameLocation.Entity.PATH, GameLocation.Entity.ROAD, GameLocation.Entity.SAND, GameLocation.Entity.HQ);

    /**
     * Matrix of images [columns][rows] representing all the structural entities in the current location.
     */
    protected Image[][] images;

    /**
     * Current coordinates of the player
     */
    protected int[] playerCoordinates;

    /**
     * Tries to load an image from the resources folder with the given path.
     * @param resource path of the image
     * @return the image or, if this is not possible, null
     */
    protected Image loadImage(String resource) {
        try {
            return ImageIO.read(Objects.requireNonNull(getClass().getResource(resource)));
        } catch (IOException | NullPointerException e) {
            System.out.println("It was not possible to load the following resource: " + resource);
            return null;
        }
    }

    /**
     * Returns all the directions in which a path or road should have connections.
     * @param entities matrix of entities representing the map
     * @param x x coordinate of the entity
     * @param y y coordinate of the entity
     * @return the partial path string representing the directions
     */
    private String getPathDirections(GameLocation.Entity[][] entities, int x, int y) {
        String path = "";

        if (x > 0 && ENTITIES_LINKING_PATHS.contains(entities[x - 1][y])) {
            path += "w";
        }
        if (y > 0 && (ENTITIES_LINKING_PATHS.contains(entities[x][y - 1]) || ENTITIES_NORTH_OF_PATH.contains(entities[x][y - 1]))) {
            path += "n";
        }
        if (x < entities.length - 1 && ENTITIES_LINKING_PATHS.contains(entities[x + 1][y])) {
            path += "e";
        }
        if (y < entities[0].length - 1 && ENTITIES_LINKING_PATHS.contains(entities[x][y + 1])) {
            path += "s";
        }

        return path;
    }

    /**
     * Loads the structural entities images from a file.
     * When possible, different images of the same tile are alternated, in order to have a more diverse map.
     * @param gameLocation model
     */
    protected void setGameLocationImages(GameLocation gameLocation) {
        images = new Image[gameLocation.getDimensions()[0]][gameLocation.getDimensions()[1]];
        GameLocation.Entity[][] entities = gameLocation.getMap();

        for (int x = 0; x < gameLocation.getDimensions()[0]; x++) {
            for (int y = 0; y < gameLocation.getDimensions()[1]; y++) {
                boolean showVariation = (y + x) % 2 == 0;

                String resource = switch (entities[x][y]) {
                    case GRASS -> showVariation ? "/locations/tiles/island/grass1.png" : "/locations/tiles/island/grass2.png";
                    case SAND -> showVariation ? "/locations/tiles/island/sand1.png" : "/locations/tiles/island/sand2.png";
                    case WATER -> showVariation ? "/locations/tiles/island/water1.png" : "/locations/tiles/island/water2.png";
                    case FLOWERS -> showVariation ? "/locations/tiles/island/flowers1.png" : "/locations/tiles/island/flowers2.png";
                    case TREE -> showVariation ? "/locations/tiles/island/tree1.png" : "/locations/tiles/island/tree2.png";
                    case BUSH -> showVariation ? "/locations/tiles/island/bush1.png" : "/locations/tiles/island/bush2.png";
                    case PLAYER_HOUSE -> "/locations/tiles/island/house-player.png";
                    case ARMORY -> "/locations/tiles/island/house-armory.png";
                    case POTIONS -> "/locations/tiles/island/house-potions.png";
                    case NPC_HOUSE -> "/locations/tiles/island/house-npc.png";
                    case PATH -> "/locations/tiles/island/path-" + getPathDirections(entities, x, y) + ".png";
                    case UNDERGROUND_STAIRS -> "/locations/tiles/island/underground.png";
                    case WALL -> "/locations/tiles/underground/wall.png";
                    case ROAD -> "/locations/tiles/underground/blackroad-" + getPathDirections(entities, x, y) + ".png";
                    case HQ, HQ_NON_CONNECTED -> "/locations/tiles/underground/hq.png";
                    case BUILDING1 -> "/locations/tiles/underground/building1.png";
                    case BUILDING2 -> "/locations/tiles/underground/building2.png";
                    case BUILDING3 -> "/locations/tiles/underground/building3.png";
                    case GRAVEL -> showVariation ? "/locations/tiles/underground/gravel1.png" : "/locations/tiles/underground/gravel2.png";
                    case TELEPORT_ISLAND -> "/locations/tiles/underground/teleport.png";
                    case FLOOR -> "/locations/tiles/island/floor.png";
                    case TELEPORT_LEAVE_HOUSE -> "/locations/tiles/island/teleport-floor.png";
                    case TABLE -> showVariation ? "/locations/tiles/island/table1.png" : "/locations/tiles/island/table2.png";
                    case TABLE_ARMORY -> showVariation ? "/locations/tiles/island/table-armory1.png" : "/locations/tiles/island/table-armory2.png";
                    case TABLE_POTIONS -> showVariation ? "/locations/tiles/island/table-potions1.png" : "/locations/tiles/island/table-potions2.png";
                };

                images[x][y] = loadImage(resource);
            }
        }
    }
}
