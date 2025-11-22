package com.konbini.view.swing;

import com.konbini.util.UserSession;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class CustomerMenuPanel extends JPanel {
    private final JFrame parentFrame;
    private final Consumer<String> navigationCallback;
    private final Runnable logoutCallback;

    public CustomerMenuPanel(JFrame parentFrame, Consumer<String> navigationCallback, Runnable logoutCallback) {
        this.parentFrame = parentFrame;
        this.navigationCallback = navigationCallback;
        this.logoutCallback = logoutCallback;

        setLayout(new BorderLayout());
        initializeUI();
    }

    private void initializeUI() {
        // Title Panel
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JLabel titleLabel = new JLabel("Customer Portal");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        titlePanel.add(titleLabel);

        add(titlePanel, BorderLayout.NORTH);

        // Menu Panel
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        // Shopping Cart Button
        gbc.gridy = 0;
        JButton cartButton = createMenuButton("Shopping Cart", new Color(52, 152, 219));
        cartButton.addActionListener(e -> navigationCallback.accept("Cart"));
        menuPanel.add(cartButton, gbc);

        // Logout Button
        gbc.gridy = 1;
        JButton logoutButton = createMenuButton("Logout", new Color(231, 76, 60));
        logoutButton.addActionListener(e -> handleLogout());
        menuPanel.add(logoutButton, gbc);

        add(menuPanel, BorderLayout.CENTER);
    }

    private JButton createMenuButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(300, 60));
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        return button;
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
                parentFrame,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            UserSession.getInstance().logout();
            logoutCallback.run();
        }
    }
}
