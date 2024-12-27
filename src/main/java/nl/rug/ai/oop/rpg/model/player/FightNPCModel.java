package nl.rug.ai.oop.rpg.model.player;

import nl.rug.ai.oop.rpg.model.character.Character;

import java.util.Random;

/**
 * @author Otto Bervoets
 * Class that manages the fighting between an NPC and the player.
 */
public class FightNPCModel {
    private Character NPC;
    private String lastState = "start";
    private Character.Faction lastAttack = null;
    private int lastDamage = 0;
    private int npcMoney = 0;
    private enum State {
        FIGHTING, FINISHED
    }
    State state;
    /**
     * Constructs an NPC model. Also constructs one random new NPC (character) that attacks randomly. Is used to
     * demonstrate the fighting as the NPC class is not yet finished at time of writing this.
     */
    public FightNPCModel(){
        state = State.FIGHTING;
        resetMessageVariables();
        this.NPC = null;
    }

    /**
     * Create a default NPC to fight.
     */
    private void setDefaultNPC(){
        System.out.println("reset npc");
        this.NPC = new Character() {
            @Override
            public Faction getAttackType() {
                Random random = new Random();
                int type = random.nextInt(3);
                return Faction.values()[type];
            }
            @Override
            protected void notifyListeners() {}
        };
    }
    private void resetMessageVariables() {
        lastState = "start";
        lastAttack = null;
        lastDamage = 0;
    }
    public String getFightMessage() {
        return TextModel.fightMessage(lastState, lastAttack, lastDamage, npcMoney);
    }
    /**
     * Set an NPC to fight. Is implemented, but not yet used by the NPC person.
     * @param newNPC the new NPC to fight
     */
    public void setNPC(Character newNPC) {
        this.NPC = newNPC;
        resetMessageVariables();
    }

    /**
     * initiate a test fight.
     */
    public void testFight() {
        state = State.FIGHTING;
        setDefaultNPC();
        resetMessageVariables();
    }

    /**
     * Get the currently set NPC.
     * @return the NPC that is fought.
     */
    public Character getNPC() {
        return this.NPC;
    }

    /**
     * Manages the fighting of the players, the NPC is already known by the model
     * This method is called in the player model in the attack method.
     */
    public void playerAttack(Character.Faction playerAttack, PlayerModel player){
        if(!state.equals(State.FIGHTING)) {
            return;
        }
        Character.Faction NPCAttack = NPC.getAttackType();
        switch (whoWins(playerAttack, NPCAttack)) {
            case 0 -> {
                lastState = "equal";
                lastAttack = playerAttack;
            }
            case 1 -> {
                NPC.takeDamage(player.getAttackStrength(playerAttack));
                if (NPC.isAlive()) {
                    lastState = "playerWin";
                    lastAttack = playerAttack;
                    player.addMoney(NPC.getMoney());
                    npcMoney = NPC.getMoney();
                } else {
                    lastState = "NPCdeath";
                    state = State.FINISHED;
                }
                lastAttack = playerAttack;
                lastDamage = player.getAttackStrength(playerAttack) - NPC.getDefence();
            }
            case 2 -> {
                player.takeDamage(NPC.getAttackStrength(NPCAttack));
                if (player.isAlive()) {
                    lastState = "NPCwin";
                } else {
                    lastState = "PlayerDeath";
                    state = State.FINISHED;
                }
                lastAttack = NPCAttack;
                lastDamage = NPC.getAttackStrength(NPCAttack) - player.getDefence();
            }
        }
    }

    /**
     * Evaluates which attack defeats the other
     * @param playerAttack the attack of the player
     * @param NPCAttack the attack of the NPC
     * @return 0 if the attacks are equal, 1 if the player wins, 2 if the NPC wins.
     */
    private int whoWins(Character.Faction playerAttack, Character.Faction NPCAttack) {
        if(playerAttack == NPCAttack) {
            return 0; //attack is equal
        }
        if(isStronger(playerAttack, NPCAttack)) {
            return 1; //player wins
        }
        return 2; //NPC wins

    }

    /**
     * Evaluates if the first attack is stronger than the second
     * @param attack1 The first attack
     * @param attack2 The second attack
     * @return bool wether the first attack is stronger than the second.
     */
    private boolean isStronger(Character.Faction attack1, Character.Faction attack2) {
        return ((attack1 == Character.Faction.FIRE && attack2 == Character.Faction.WOOD) ||
                (attack1 == Character.Faction.WOOD && attack2 == Character.Faction.WATER) ||
                (attack1 == Character.Faction.WATER && attack2 == Character.Faction.FIRE)
        );
    }

    /**
     * @return bool whether the set NPC is alive.
     */
    public boolean NPCAlive() {
        return NPC.isAlive();
    }
}
