Project grading criteria
========================

*   General (for all sections)
    *   The code must showcase adherence to OOP principles.
        *   Classes are properly encapsulated.
        *   Classes are appropriately decoupled.
        *   Exceptions are used appropriately.
    *   The code must showcase adherence to MVC principles.
        *   The game logic is independent from the way it is presented to the player.
        *   The view does not directly access the game logic.
        *   The controller does not update the view directly.
    *   It must be possible to store and restore a game state at any point in the game.
    *   The game allows for multiple languages.
        *   The game is completely playable in two separate languages.
    *   Every class is properly attributed to the @author.
*    Inventory
    *   There is a system set up for custom objects (items).
    *   There is a collection system (inventory) set up for items.
    *   Through normal interaction with the program, it is possible to add items to and remove items from the inventory.
    *   Through normal interaction with the program, it is possible to interact with items through the inventory.
    *   Interacting with items in the inventory is treated differently from interacting with items outside the inventory.
*   Location
    *   There is a system set up for custom objects (locations).
    *   At any point in time, there is one particular location that is unique (player location).
    *   Through normal interaction with the game, it is possible to change the player location.
    *   Changing player location may be subject to conditions.
    *   There is at least one initially impossible player location change that becomes possible (or vice versa) through normal interaction with the game.
*   Non-player characters (NPCs)
    *   There is a system set up for custom objects (NPCs).
    *   NPCs exist independently from inventory and location.
    *   Through normal interaction with the game, the player can choose whether or not to interact with NPCs.
    *   There is a (generic) setup that allows interaction with NPC objects to affect other objects.
*   Player classes
    *   There is a system set up for custom objects (classes).
    *   At any point in time, there is one particular class that is unique (player class).
    *   The player must be able to choose or influence the player class at some point in the game.
    *   The player class modulates interaction in the game.
    *   There is a single set of actions a player can perform some particular class that will complete the game, but which is not possible for some other class.
