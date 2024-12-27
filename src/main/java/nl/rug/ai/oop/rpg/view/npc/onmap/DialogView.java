package nl.rug.ai.oop.rpg.view.npc.onmap;

import nl.rug.ai.oop.rpg.controler.npc.EnemyController;
import nl.rug.ai.oop.rpg.model.engine.GameEngine;
import nl.rug.ai.oop.rpg.view.GameView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static nl.rug.ai.oop.rpg.view.GameView.MAIN_LIGHT_COLOR;

public class DialogView extends JPanel{
    private JButton fightLabel, ignore, message;
    private final GameView gameView;
    private JPanel buttons;

    public DialogView(GameView gameView) {

        this.gameView = gameView;
        setBackground(MAIN_LIGHT_COLOR);
        setLayout(new BorderLayout());


        this.buttons = new JPanel();

        this.ignore = new JButton("ignore");
        buttons.add(ignore);

        this.fightLabel = new JButton("fight");
        buttons.add(fightLabel);

        add(buttons, BorderLayout.CENTER);
    }

    public void updateLanguage(GameEngine.Language language){
        if(language == GameEngine.Language.ENGLISH){
            ignore.setText("ignore");
            fightLabel.setText("fight");
        } else if (language == GameEngine.Language.DUTCH) {
            ignore.setText("negeren");
            fightLabel.setText("strijken");
        }
        revalidate();
    }

    public void setup(GameEngine gameEngine, EnemyController enemyController){
        ignore.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Ignore");
                gameView.removeDialogView();
            }
        });

        fightLabel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Fight");
                enemyController.startFight(gameEngine.getPlayerModel());
            }
        });
    }
}
