package nl.rug.ai.oop.rpg.model.locations;

/**
 * LocationObject to be displayed on the map.
 * It contains:
 * <ul>
 *     <li>{@link GameLocation.Location} location - location where the item will be displayed</li>
 *     <li>{@link String} reference - reference to the object</li>
 *     <li>{@link String} imagePath - path to the object image</li>
 *     <li>{@link Object} object - object to be stored</li>
 *     <li>int x - x coordinate of the object</li>
 *     <li>int y - y coordinate of the object</li>
 *     <li>boolean walkable - true if it is possible to walk on the object, otherwise false</li>
 * </ul>
 *
 * @author Luca Colli
 * @version 1.0
 */
public record LocationObject (
        GameLocation.Location location,
        Object object,
        String reference,
        String imagePath,
        int x,
        int y,
        boolean walkable
) {}
