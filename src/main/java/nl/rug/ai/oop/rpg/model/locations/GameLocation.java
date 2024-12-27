package nl.rug.ai.oop.rpg.model.locations;

import nl.rug.ai.oop.rpg.model.engine.GameEngine;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;


/**
 * GameLocation model. It contains the matrix representation of the map and the coordinates of the player and other objects present on the map
 *
 * @author Luca Colli
 * @version 1.0
 */
public class GameLocation {
    /**
     * Entity representing the structural objects of the map
     */
    public enum Entity {
        WATER, SAND, GRASS, FLOWERS, TREE, BUSH, PLAYER_HOUSE, POTIONS, ARMORY, NPC_HOUSE, PATH, UNDERGROUND_STAIRS, WALL, ROAD, HQ, HQ_NON_CONNECTED, BUILDING1, BUILDING2, BUILDING3, GRAVEL, TELEPORT_ISLAND, FLOOR, TELEPORT_LEAVE_HOUSE, TABLE, TABLE_ARMORY, TABLE_POTIONS
    }

    /**
     * Map layout that can be assigned to the model
     */
    public enum Location {
        ISLAND, UNDERGROUND, PLAYER_HOUSE, NPC_HOUSE, ARMORY, POTIONS
    }

    /**
     * Direction of a move
     */
    public enum Direction {
        NORTH, SOUTH, WEST, EAST
    }

    private static final List<Entity> WALKABLE_ENTITIES = Arrays.asList(Entity.SAND, Entity.GRASS, Entity.FLOWERS, Entity.PATH, Entity.ROAD, Entity.GRAVEL, Entity.FLOOR);
    private GameEngine gameEngine;
    private Location location;
    private GameEngine.Language language = GameEngine.Language.ENGLISH;
    private final HashMap<GameEngine.Language, String> titleTranslations = new HashMap<>();
    private final ArrayList<PropertyChangeListener> listeners = new ArrayList<>();
    private final int[] dimensions = new int[2];
    private final int[] playerCoordinates = new int[2];
    private final int[] lastIslandCoords = {-1, -1};
    private final List<LocationObject> objects = new ArrayList<>();
    private Entity[][] map;
    private boolean isLoadingMap = true;
    private boolean hasWaterBackground = false;

    /**
     * @return int[2] width and height of the map
     */
    public int[] getDimensions() {
        return dimensions;
    }

    /**
     * @return true if the map should be surrounded by water, otherwise false
     */
    public boolean hasWaterBackground() {
        return hasWaterBackground;
    }

    /**
     * @return {@link Entity}[columns][rows] copy of the matrix representing the map
     */
    public Entity[][] getMap() {
        Entity[][] mapCopy = new Entity[dimensions[0]][dimensions[1]];

        for (int x = 0; x < dimensions[0]; x++) {
            System.arraycopy(map[x], 0, mapCopy[x], 0, dimensions[1]);
        }
        return mapCopy;
    }

    /**
     * Modifies the parameter location and loads the new map
     * @param location new location
     */
    private void setLocation(Location location) {
        this.location = location;
        loadLocation(true);
    }

    /**
     * Sets the parameter gameEngine
     * @param gameEngine engine model of the game
     */
    public void setGameEngine(GameEngine gameEngine) {
        this.gameEngine = gameEngine;
    }

    /**
     * Modifies the language parameter and notifies the listeners
     * @param language new language of the game
     */
    public void updateLanguage(GameEngine.Language language) {
        this.language = language;
        notifyListeners("language", getTitle());
    }

    /**
     * Loads the map matrix from a location file and places the player in the map, and notifies the listeners.
     * The files must contain the width and height, the default x and y position of the player, the title in English and Dutch and a matrix of symbols representing the entities.
     * If the map is an island, waterBackground is set to true, otherwise false.
     * @param useDefaultCoordinates true if the user should be placed in the default position in the map, false if it should use playerCoordinates
     */
    private void loadLocation(boolean useDefaultCoordinates) {
        try (Scanner fileInput = new Scanner(Objects.requireNonNull(GameLocation.class.getResourceAsStream("/locations/textfiles/" + String.valueOf(location).toLowerCase() + ".txt")))) {
            dimensions[0] = fileInput.nextInt();
            dimensions[1] = fileInput.nextInt();
            fileInput.nextLine();

            if (useDefaultCoordinates) {
                playerCoordinates[0] = fileInput.nextInt();
                playerCoordinates[1] = fileInput.nextInt();
            }
            fileInput.nextLine();

            titleTranslations.put(GameEngine.Language.ENGLISH, fileInput.nextLine());
            titleTranslations.put(GameEngine.Language.DUTCH, fileInput.nextLine());

            map = new Entity[dimensions[0]][dimensions[1]];

            for (int y = 0; y < dimensions[1]; y++) {
                String line = fileInput.nextLine();
                for (int x = 0; x < dimensions[0]; x++) {
                    map[x][y] = switch (line.substring(x, x + 1)) {
                        case "-" -> Entity.SAND;
                        case "#" -> Entity.GRASS;
                        case "^" -> Entity.FLOWERS;
                        case "|" -> Entity.TREE;
                        case "/" -> Entity.BUSH;
                        case "@" -> Entity.PLAYER_HOUSE;
                        case "!" -> Entity.POTIONS;
                        case "&" -> Entity.ARMORY;
                        case "$" -> Entity.NPC_HOUSE;
                        case "*" -> Entity.PATH;
                        case "u" -> Entity.UNDERGROUND_STAIRS;
                        case "_" -> Entity.ROAD;
                        case "(" -> Entity.BUILDING1;
                        case "[" -> Entity.BUILDING2;
                        case "{" -> Entity.BUILDING3;
                        case "%" -> Entity.GRAVEL;
                        case "§" -> Entity.HQ;
                        case "±" -> Entity.HQ_NON_CONNECTED;
                        case "~" -> Entity.WALL;
                        case "i" -> Entity.TELEPORT_ISLAND;
                        case ":" -> Entity.FLOOR;
                        case "I" -> Entity.TELEPORT_LEAVE_HOUSE;
                        case ">" -> Entity.TABLE;
                        case ")" -> Entity.TABLE_ARMORY;
                        case "]" -> Entity.TABLE_POTIONS;
                        default -> Entity.WATER;
                    };
                }
            }
        } catch (NullPointerException e) {
            System.out.println("It was not possible to load the following location: " + String.valueOf(location).toLowerCase() + ".txt");
            System.exit(-1);
        }
        hasWaterBackground = location == Location.ISLAND;
        isLoadingMap = false;
        notifyListeners("map", getMap());
        notifyListeners("playerCoords", getPlayerCoordinates());
    }

    /**
     * @return x and y coordinates of the player
     */
    public int[] getPlayerCoordinates() {
        return playerCoordinates;
    }

    /**
     * Enters a building on the island and saves the last coordinates of the player.
     * @param direction the direction of the movement
     * @param newLocation the location to enter
     */
    private void enterBuilding(Direction direction, Location newLocation) {
        if (direction == Direction.NORTH) {
            lastIslandCoords[0] = playerCoordinates[0];
            lastIslandCoords[1] = playerCoordinates[1];
            setLocation(newLocation);
        }
    }

    /**
     * Checks if the move is allowed and, if it is, moves the player one tile in the wanted direction.
     * If a portal is encountered, checks if the player has the requirements to enter the new location.
     * It is not allowed to move on non-walkable entities or objects.
     * The listeners get notified if the player is on an object or is bumping into an object.
     * @param direction the direction of the movement
     */
    public void move(Direction direction) {
        if (isLoadingMap) {
            return;
        }

        int newX = playerCoordinates[0];
        int newY = playerCoordinates[1];

        switch (direction) {
            case WEST -> --newX;
            case NORTH -> --newY;
            case EAST -> ++newX;
            case SOUTH -> ++newY;
        }

        if (newX < 0 || newX >= dimensions[0] || newY < 0 || newY >= dimensions[1]) {
            return;
        }

        switch (map[newX][newY]) {
            case PLAYER_HOUSE -> enterBuilding(direction, Location.PLAYER_HOUSE);
            case NPC_HOUSE -> enterBuilding(direction, Location.NPC_HOUSE);
            case ARMORY -> enterBuilding(direction, Location.ARMORY);
            case POTIONS -> enterBuilding(direction, Location.POTIONS);
            case TABLE_POTIONS -> gameEngine.setGameState(GameEngine.GameState.POTIONS);
            case TABLE_ARMORY -> gameEngine.setGameState(GameEngine.GameState.ARMORY);
            case UNDERGROUND_STAIRS -> {
                if (direction == Direction.NORTH && gameEngine.hasUndergroundTicket()) {
                    setLocation(GameLocation.Location.UNDERGROUND);
                }
            }
            case TELEPORT_LEAVE_HOUSE -> {
                setLocation(Location.ISLAND);
                if (lastIslandCoords[0] != -1 && lastIslandCoords[1] != -1) {
                    playerCoordinates[0] = lastIslandCoords[0];
                    playerCoordinates[1] = lastIslandCoords[1];
                    notifyListeners("playerCoords", getPlayerCoordinates());
                }
            }
            case TELEPORT_ISLAND -> setLocation(Location.ISLAND);
            default -> {
                LocationObject objectNearby = getObjectAtCoordinates(newX, newY);

                if (objectNearby != null && !objectNearby.walkable()) {
                    notifyListeners("bumpingIntoObject", objectNearby);
                } else if (WALKABLE_ENTITIES.contains(map[newX][newY])) {
                    playerCoordinates[0] = newX;
                    playerCoordinates[1] = newY;
                    notifyListeners("playerCoords", getPlayerCoordinates());
                    if (objectNearby != null) {
                        gameEngine.setGameState(GameEngine.GameState.PICKUP);
                        notifyListeners("objectAtCurrentCoordinates", objectNearby);
                    }
                }
            }
        }
    }

    /**
     * Adds a custom {@link LocationObject} to the map.
     * @param reference reference to the object
     * @param object object to be stored
     * @param location location where the item will be displayed
     * @param x x coordinate of the object
     * @param y y coordinate of the object
     * @param imagePath path to the object image
     * @param walkable true if it is possible to walk on the object, otherwise false
     */
    public void addObject(String reference, Object object, GameLocation.Location location, int x, int y, String imagePath, boolean walkable) {
        objects.add(new LocationObject(location, object, reference, imagePath, x, y, walkable));
        notifyListeners("objects", objects);
    }

    /**
     * @return list of {@link LocationObject} in the current location.
     */
    public ArrayList<LocationObject> getObjectsInCurrentLocation() {
        ArrayList<LocationObject> objectsCopy = new ArrayList<>();
        for (LocationObject object: objects) {
            if (object.location() == location) {
                objectsCopy.add(object);
            }
        }
        return objectsCopy;
    }

    /**
     * @return {@link LocationObject} at the given coordinates. If none is found, returns null.
     */
    private LocationObject getObjectAtCoordinates(int x, int y) {
        for (LocationObject object: getObjectsInCurrentLocation()) {
            if (object.x() == x  && object.y() == y) {
                return object;
            }
        }
        return null;
    }

    /**
     * If the given object was previously added to the model, it gets removed.
     * It should be called after fighting with an NPC.
     * @param storedObject the object stored in the {@link LocationObject}
     */
    public void removeObject(Object storedObject) {
        for (LocationObject object: objects) {
            if (storedObject.equals(object.object())) {
                objects.remove(object);
                notifyListeners("objects", objects);
                return;
            }
        }
    }

    /**
     * If there is a {@link LocationObject} at the current coordinates of the player, it gets removed.
     */
    public void removeObjectAtPlayerCoordinates() {
        LocationObject object = getObjectAtCoordinates(playerCoordinates[0], playerCoordinates[1]);
        if (object != null) {
            objects.remove(object);
            notifyListeners("objects", objects);
        }
    }

    /**
     * Constructor of the GameLocation. It tries to load the information about the current location and the current position from a text file.
     * If this is not possible, it loads the game with the default location and the default coordinates/
     */
    public GameLocation() {
        Properties properties = new Properties();
        try (FileInputStream file = new FileInputStream("src/main/resources/saving_data/location.txt")) {
            properties.load(file);
            if (properties.containsKey("location") && properties.containsKey("playerX") && properties.containsKey("playerY") && properties.containsKey("lastIslandX") && properties.containsKey("lastIslandY")) {
                this.location = Location.valueOf(properties.getProperty("location"));
                this.playerCoordinates[0] = Integer.parseInt(properties.getProperty("playerX"));
                this.playerCoordinates[1] = Integer.parseInt(properties.getProperty("playerY"));
                this.lastIslandCoords[0] = Integer.parseInt(properties.getProperty("lastIslandX"));
                this.lastIslandCoords[1] = Integer.parseInt(properties.getProperty("lastIslandY"));
                loadLocation(false);
            } else {
                setLocation(Location.ISLAND);
            }
        } catch (IOException | NullPointerException e) {
            setLocation(Location.ISLAND);
        }
    }

    /**
     * @return title of the location in the current language
     */
    public String getTitle() {
        return switch (language) {
            case ENGLISH -> titleTranslations.get(GameEngine.Language.ENGLISH);
            case DUTCH -> titleTranslations.get(GameEngine.Language.DUTCH);
        };
    }

    /**
     * Saves the location and the current coordinates of the player to a text file.
     */
    public void save() {
        Properties properties = new Properties();
        properties.setProperty("location", String.valueOf(location));
        properties.setProperty("lastIslandX", String.valueOf(lastIslandCoords[0]));
        properties.setProperty("lastIslandY", String.valueOf(lastIslandCoords[1]));
        properties.setProperty("playerX", String.valueOf(playerCoordinates[0]));
        properties.setProperty("playerY", String.valueOf(playerCoordinates[1]));
        try (FileOutputStream file = new FileOutputStream("src/main/resources/saving_data/location.txt")) {
            properties.store(file, "Current location and player coordinates");
        } catch (IOException | NullPointerException e) {
            System.out.println("It was not possible to save location data");
        }
    }

    /**
     * Adds a listener to property changes in the GameLocation
     * @param listener listener to be added
     */
    public void addListener(PropertyChangeListener listener) {
        listeners.add(listener);
    }

    /**
     * Notifies all the listeners of a change in a parameter of the model.
     * @param propertyName the name of the changed parameter
     * @param value the new value of the parameter
     */
    private void notifyListeners(String propertyName, Object value) {
        Iterator<PropertyChangeListener> allListeners = listeners.iterator();
        PropertyChangeEvent payload = new PropertyChangeEvent(this, propertyName, null, value);
        while (allListeners.hasNext()) {
            allListeners.next().propertyChange(payload);
        }
    }
}
