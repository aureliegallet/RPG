package nl.rug.ai.oop.rpg.view.location;

import nl.rug.ai.oop.rpg.controler.LocationController;
import nl.rug.ai.oop.rpg.model.character.Character;
import nl.rug.ai.oop.rpg.model.locations.GameLocation;
import nl.rug.ai.oop.rpg.model.locations.LocationObject;

import java.awt.*;


/**
 * Map view. It only displays one part of the map, with the player always in the center.
 * Custom {@link LocationObject} are also shown, and it is possible to interact with them.
 *
 * @author Luca Colli
 * @version 1.0
 */
public class LocationView extends Map {
    private Image[][] objectImages;
    private Image[] backgroundImages;
    private Image playerImage;
    private String title;
    private boolean hasBackground;

    /**
     * Loads the player image from a file.
     */
    public void setPlayerImage(Character.Faction faction) {
        playerImage = switch (faction) {
            case FIRE -> loadImage("/skins/redguy.png");
            case WOOD -> loadImage("/skins/greenguy.png");
            case WATER -> loadImage("/skins/blueguy.png");
        };
    }

    /**
     * Loads the water background images from a file.
     */
    private void setBackgroundImages() {
        backgroundImages = new Image[2];

        backgroundImages[0] = loadImage("/locations/tiles/island/water1.png");
        backgroundImages[1] = loadImage("/locations/tiles/island/water2.png");
    }

    /**
     * Loads the objects images from a file.
     * @param gameLocation model
     */
    private void setObjectImages(GameLocation gameLocation) {
        objectImages = new Image[gameLocation.getDimensions()[0]][gameLocation.getDimensions()[1]];

        for (LocationObject object : gameLocation.getObjectsInCurrentLocation()) {
            objectImages[object.x()][object.y()] = loadImage(object.imagePath());
        }
    }

    /**
     * Constructor of LocationView. Sets the default background color and loads the background and player images.
     */
    public LocationView() {
        setBackground(Color.BLACK);
        setBackgroundImages();
    }

    /**
     * Sets the default values of the view, and adds property change listeners and the key listener.
     * @param gameLocation location model
     * @param locationController location controller
     */
    public void setup(GameLocation gameLocation, LocationController locationController) {
        setGameLocationImages(gameLocation);
        updateLocation(gameLocation);
        gameLocation.addListener(evt -> {
            switch (evt.getPropertyName()) {
                case "map" -> updateLocation(gameLocation);
                case "objects" -> updateObjects(gameLocation);
                case "playerCoords" -> {
                    playerCoordinates = (int[]) evt.getNewValue();
                    repaint();
                }
                case "language" -> {
                    title = (String) evt.getNewValue();
                    repaint();
                }
            }
        });
        this.addKeyListener(locationController);
        this.setFocusable(true);
    }

    /**
     * Updates the current location
     * @param gameLocation model
     */
    private void updateLocation(GameLocation gameLocation) {
        setGameLocationImages(gameLocation);
        setObjectImages(gameLocation);
        this.playerCoordinates = gameLocation.getPlayerCoordinates();
        this.title = gameLocation.getTitle();
        this.hasBackground = gameLocation.hasWaterBackground();
    }


    /**
     * Updates the object images
     * @param gameLocation model
     */
    private void updateObjects(GameLocation gameLocation) {
        setObjectImages(gameLocation);
    }

    /**
     * Draws a white title with a black shadow
     * @param g graphics object
     */
    private void drawTitle(Graphics g) {
        int[] titleCoords = {15, 25};
        g.setFont(new Font(Font.MONOSPACED, Font.BOLD, 15));

        // Draw title shadow
        g.setColor(Color.BLACK);
        g.drawString(title, titleCoords[0] + 1, titleCoords[1] + 1);
        g.drawString(title, titleCoords[0] + 1, titleCoords[1] - 1);
        g.drawString(title, titleCoords[0] - 1, titleCoords[1] + 1);
        g.drawString(title, titleCoords[0] - 1, titleCoords[1] - 1);

        // Draw title
        g.setColor(Color.WHITE);
        g.drawString(title, titleCoords[0], titleCoords[1]);
    }

    /**
     * Paints the component adding a map with tile size = 60 and centered according to the current player coordinates.
     * If some parts of the panel are out of the view, a background is printed instead.
     * Custom objects, the player and the title are also added on the map.
     * @param g the <code>Graphics</code> object to protect
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int tileSize = 60;
        int firstCol = playerCoordinates[0] - (getWidth() / 2) / tileSize - 1;
        int firstRow = playerCoordinates[1] - (getHeight() / 2) / tileSize - 1;

        int numberOfColumns = firstCol + getWidth() / tileSize + 2;
        int numberOfRows = firstRow + getHeight() / tileSize + 2;

        int deltaX = (getWidth() / 2 % tileSize) - 3 * tileSize / 2;
        int deltaY = (getHeight() / 2 % tileSize) - 3 * tileSize / 2;

        g.clipRect(0, 0, getWidth(), getHeight());

        // Grid
        for (int col = firstCol; col < numberOfColumns; col++) {
            for (int row = firstRow; row < numberOfRows; row++) {
                if (row >= 0 && col >= 0 && col < images.length && row < images[0].length) {
                    g.drawImage(images[col][row], (col - firstCol) * tileSize + deltaX, (row - firstRow) * tileSize + deltaY, tileSize, tileSize, null);
                    if (objectImages[col][row] != null) {
                        g.drawImage(objectImages[col][row], (col - firstCol) * tileSize + tileSize / 4 + deltaX, (row - firstRow) * tileSize + tileSize / 4 + deltaY, tileSize / 2, tileSize / 2, null);
                    }
                } else if (hasBackground) {
                    int backgroundVariation = (col + row) % 2 == 0 ? 0 : 1;
                    Image backgroundImage = backgroundImages[backgroundVariation];
                    g.drawImage(backgroundImage, (col - firstCol) * tileSize + deltaX, (row - firstRow) * tileSize + deltaY, tileSize, tileSize, null);
                }
            }
        }

        // Player
        g.drawImage(playerImage, (playerCoordinates[0] - firstCol) * tileSize + 3 + deltaX, (playerCoordinates[1] - firstRow) * tileSize + 2 + deltaY, tileSize - 6, tileSize - 6, null);

        // Title
        drawTitle(g);

        g.dispose();
    }

}
