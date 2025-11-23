package com.konbini.view.swing;

import com.konbini.util.UserSession;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * CustomerMenuPanel provides a graphical user interface for the customer portal menu.
 * This panel displays navigation options for customers, including access to the shopping cart
 * and logout functionality. It integrates with the application's navigation system and
 * user session management.
 *
 * The panel features a clean, centered layout with styled buttons for different customer
 * actions and includes confirmation dialogs for logout operations.
 */
public class CustomerMenuPanel extends JPanel {
    /** The parent frame containing this panel for dialog positioning */
    private final JFrame parentFrame;

    /** Callback function for navigating to different sections of the application */
    private final Consumer<String> navigationCallback;

    /** Callback function to execute when user logs out */
    private final Runnable logoutCallback;

    /**
     * Constructs a new CustomerMenuPanel with the specified parameters.
     * Initializes the UI components and sets up the panel layout.
     *
     * @param parentFrame the parent JFrame that contains this panel, used for dialog positioning
     * @param navigationCallback a Consumer that accepts navigation commands as Strings,
     *                          used to switch between different application views
     * @param logoutCallback a Runnable that executes logout procedures when invoked
     * @throws IllegalArgumentException if any of the parameters are null
     */
    public CustomerMenuPanel(JFrame parentFrame, Consumer<String> navigationCallback, Runnable logoutCallback) {
        if (parentFrame == null || navigationCallback == null || logoutCallback == null) {
            throw new IllegalArgumentException("Parameters cannot be null");
        }

        this.parentFrame = parentFrame;
        this.navigationCallback = navigationCallback;
        this.logoutCallback = logoutCallback;

        setLayout(new BorderLayout());
        initializeUI();
    }

    /**
     * Initializes the user interface components of the panel.
     * Creates and arranges the title section and menu buttons in a organized layout.
     *
     * <p>The UI consists of:
     * <ul>
     *   <li>A title panel with the application name</li>
     *   <li>A menu panel with navigation buttons</li>
     * </ul>
     * </p>
     */
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

    /**
     * Creates a styled menu button with consistent appearance.
     *
     * @param text the text to display on the button
     * @param color the background color for the button
     * @return a configured JButton with the specified text and styling
     */
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

    /**
     * Handles the logout process by displaying a confirmation dialog.
     * If the user confirms logout, it clears the user session and executes
     * the logout callback.
     *
     * <p>This method shows a confirmation dialog to prevent accidental logouts.
     * Only proceeds with logout if the user explicitly confirms.</p>
     */
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