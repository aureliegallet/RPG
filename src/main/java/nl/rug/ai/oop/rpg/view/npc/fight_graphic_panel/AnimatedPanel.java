package nl.rug.ai.oop.rpg.view.npc.fight_graphic_panel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AnimatedPanel extends JPanel implements ActionListener {
    private final int panelWidth = 500;
    private final int panelHeight = 500;
    Image image;
    Timer timer;
    int xVelocity = 1;
    int yVelocity = 0;
    int x = 0;
    int y = 0;

    AnimatedPanel(String imagePath, int direction) {
        this.setPreferredSize(new Dimension(panelWidth, panelHeight));
        this.setBackground(new Color(90,120,150, 0));
        x = direction > 0 ? 0 : image.getWidth(null);

        image = new ImageIcon(getClass().getResource(imagePath).getPath()).getImage();
        timer = new Timer(5, this);
        timer.start();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D graphics2D = (Graphics2D) g;
        graphics2D.drawImage(image, x, y, null);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        x += xVelocity;
        y += yVelocity;
        repaint();
    }
}
