package nl.rug.ai.oop.rpg.view.npc.fight_graphic_panel;

import nl.rug.ai.oop.rpg.view.npc.fight_graphic_panel.AnimatedPanel;

import javax.swing.*;
import java.awt.*;

public class FightNpcPanel extends JPanel {
    private final Image bgImage;
    private final JButton waterButton, woodButton, fireButton;
    private final JProgressBar progressBarNPC, progressBarPlayer;

    private final JLabel playerLabel, npcLabel;
    private final JLabel playerFactionLabel, npcFactionLabel;
    private int direction = 1;
    private int maxHPNPC = 500;
    private int maxHPPlayer = 500;
    public FightNpcPanel() {
        Color backgroundColor = new Color(90,120,150, 0);
        this.setBounds(0,0,1000,600);
        String imagePath = getClass().getResource("/photos/bright.png").getPath();
        Image image = new ImageIcon(imagePath).getImage();
        bgImage = image.getScaledInstance(1000, -1, Image.SCALE_SMOOTH);
        this.setBackground(Color.BLACK);
        this.setLayout(new GridLayout(3, 1));


        JPanel hpPanel = new JPanel();
        hpPanel.setBackground(backgroundColor);
        hpPanel.setLayout(new GridLayout());
        this.add(hpPanel);

        progressBarNPC = new JProgressBar(0, maxHPNPC);
        progressBarNPC.setSize(500, 30);
        progressBarNPC.setStringPainted(true);
        progressBarNPC.setValue(maxHPNPC);
        progressBarNPC.setBackground(backgroundColor);
        hpPanel.add(progressBarNPC);

        progressBarPlayer = new JProgressBar(0, maxHPPlayer);
        progressBarPlayer.setSize(500, 30);
        progressBarPlayer.setStringPainted(true);
        progressBarPlayer.setBackground(backgroundColor);
        hpPanel.add(progressBarPlayer);


        JPanel imagePanel = new JPanel();
        imagePanel.setBackground(backgroundColor);
        imagePanel.setLayout(new GridLayout(1, 5));
        this.add(imagePanel);

        ImageIcon playerImage = addStretchedImageIcon(
                "/skins/redguy.png", 120, 120);
        playerLabel = new JLabel(playerImage);
        imagePanel.add(playerLabel);


        ImageIcon playerFactionImage = addImageIcon("/emojis/fire.png");
        playerFactionLabel = new JLabel(playerFactionImage);
        playerFactionLabel.setBackground(backgroundColor);
        imagePanel.add(playerFactionLabel);

        imagePanel.add(new AnimatedPanel("/emojis/fire.png", direction));

        ImageIcon npcFactionImage = addImageIcon("/emojis/water.png");
        npcFactionLabel = new JLabel(npcFactionImage);
        imagePanel.add(npcFactionLabel);


        ImageIcon npcImage = addStretchedImageIcon(
                "/skins/redguy.png", 120, 120);
        npcLabel = new JLabel(npcImage);
        imagePanel.add(npcLabel);



        JPanel factionPanel = new JPanel();
        factionPanel.setBackground(backgroundColor);
        waterButton = new JButton("Water");
        waterButton.setBackground(backgroundColor);
        factionPanel.add(waterButton);
        woodButton = new JButton("Wood");
        woodButton.setBackground(backgroundColor);
        factionPanel.add(woodButton);
        fireButton = new JButton("Fire");
        fireButton.setBackground(backgroundColor);
        factionPanel.add(fireButton);
        this.add(factionPanel);
    }


    public int getMaxHPNPC() {
        return maxHPNPC;
    }

    public void setMaxHPNPC(int maxHPNPC) {
        this.maxHPNPC = maxHPNPC;
    }

    public int getMaxHPPlayer() {
        return maxHPPlayer;
    }

    public void setMaxHPPlayer(int maxHPPlayer) {
        this.maxHPPlayer = maxHPPlayer;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(bgImage, 0, 0, this);
    }

    private ImageIcon addImageIcon(String imagePath) {
        return new ImageIcon(getClass().getResource(imagePath).getPath());
    }

    private ImageIcon addStretchedImageIcon(String imagePath, int width, int height) {
        return new ImageIcon(new ImageIcon(
                getClass().getResource(imagePath).getPath()).
                getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
    }

    private void fillProgressbar(JProgressBar progressBar, int value) {
        progressBar.setValue(value);
        if (value <= 0) {
            progressBar.setString("Done!");
        }
    }
}
