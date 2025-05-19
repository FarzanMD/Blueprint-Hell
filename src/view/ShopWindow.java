package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ShopWindow extends JDialog {
    public ShopWindow(JFrame parent, int currentCoins, ActionListener onAtar, ActionListener onAiryaman, ActionListener onAnahita) {
        super(parent, "Shop", true);
        setSize(300, 250);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        JLabel coinLabel = new JLabel("Coins: " + currentCoins, SwingConstants.CENTER);
        coinLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(coinLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));

        JButton atarButton = new JButton("O' Atar (3 Coins)");
        JButton airyamanButton = new JButton("O' Airyaman (4 Coins)");
        JButton anahitaButton = new JButton("O' Anahita (5 Coins)");

        atarButton.addActionListener(onAtar);
        airyamanButton.addActionListener(onAiryaman);
        anahitaButton.addActionListener(onAnahita);

        buttonPanel.add(atarButton);
        buttonPanel.add(airyamanButton);
        buttonPanel.add(anahitaButton);

        add(buttonPanel, BorderLayout.CENTER);

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setVisible(true);
    }
}
