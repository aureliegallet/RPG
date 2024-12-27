package nl.rug.ai.oop.rpg.controler.npc;

import nl.rug.ai.oop.rpg.model.npc.Enemy;
import nl.rug.ai.oop.rpg.model.player.PlayerModel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class EnemyController implements ActionListener {
    private final Enemy enemy;

    public EnemyController(Enemy enemy) {
        this.enemy = enemy;
    }

    public void startFight(PlayerModel player){
        player.fightNPC(this.enemy);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }
}
