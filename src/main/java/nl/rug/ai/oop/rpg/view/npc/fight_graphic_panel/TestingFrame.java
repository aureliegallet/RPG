package nl.rug.ai.oop.rpg.view.npc.fight_graphic_panel;

import javax.swing.*;

public class TestingFrame extends JFrame {
    FightNpcPanel fightNpcPanel;

    public TestingFrame() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        fightNpcPanel = new FightNpcPanel();
        this.setBounds(0,0,1000,600);
        this.add(fightNpcPanel);
        this.setVisible(true);
    }
}
