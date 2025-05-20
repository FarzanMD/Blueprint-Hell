package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.function.Supplier;

public class ShopWindow extends JDialog {
    private final JLabel coinLabel;
    private final Supplier<Integer> coinSupplier;

    public ShopWindow(JFrame parent, Supplier<Integer> coinSupplier,
                      ActionListener onAtar, ActionListener onAiryaman, ActionListener onAnahita) {

        super(parent, "Shop", true);
        this.coinSupplier = coinSupplier;

        setSize(300, 250);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout());

        coinLabel = new JLabel("Coins: " + coinSupplier.get(), SwingConstants.CENTER);
        coinLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(coinLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));

        JButton atarButton = new JButton("O' Atar (3 Coins)");
        JButton airyamanButton = new JButton("O' Airyaman (4 Coins)");
        JButton anahitaButton = new JButton("O' Anahita (5 Coins)");

        atarButton.addActionListener(e -> {
            onAtar.actionPerformed(e);
            updateCoins();
        });

        airyamanButton.addActionListener(e -> {
            onAiryaman.actionPerformed(e);
            updateCoins();
        });

        anahitaButton.addActionListener(e -> {
            onAnahita.actionPerformed(e);
            updateCoins();
        });

        buttonPanel.add(atarButton);
        buttonPanel.add(airyamanButton);
        buttonPanel.add(anahitaButton);

        add(buttonPanel, BorderLayout.CENTER);

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    public void updateCoins() {
        coinLabel.setText("Coins: " + coinSupplier.get());
    }
}
