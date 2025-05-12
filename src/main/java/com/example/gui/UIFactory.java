package com.example.gui;
import javax.swing.*;
import java.awt.*;

public class UIFactory {
    
    // Color scheme
    public static final Color BACKGROUND_COLOR = new Color(20, 20, 20);
    public static final Color SECONDARY_BG_COLOR = new Color(30, 30, 30);
    public static final Color TERTIARY_BG_COLOR = new Color(40, 40, 40);
    public static final Color ACCENT_COLOR = new Color(180, 0, 0);
    public static final Color ACCENT_COLOR_LIGHT = new Color(220, 0, 0);
    public static final Color TEXT_COLOR = new Color(220, 220, 220);
    public static final Color TEXT_COLOR_SECONDARY = new Color(180, 180, 180);
    
    // Light mode colors
    public static final Color LIGHT_BACKGROUND_COLOR = new Color(240, 240, 240);
    public static final Color LIGHT_SECONDARY_BG_COLOR = new Color(230, 230, 230);
    public static final Color LIGHT_TERTIARY_BG_COLOR = new Color(220, 220, 220);
    public static final Color LIGHT_TEXT_COLOR = new Color(20, 20, 20);
    public static final Color LIGHT_TEXT_COLOR_SECONDARY = new Color(60, 60, 60);
    
    // Current theme state
    private static boolean isDarkMode = true;
    
    public static void toggleTheme() {
        isDarkMode = !isDarkMode;
    }
    
    public static boolean isDarkMode() {
        return isDarkMode;
    }
    
    public static Color getBackgroundColor() {
        return isDarkMode ? BACKGROUND_COLOR : LIGHT_BACKGROUND_COLOR;
    }
    
    public static Color getSecondaryBackgroundColor() {
        return isDarkMode ? SECONDARY_BG_COLOR : LIGHT_SECONDARY_BG_COLOR;
    }
    
    public static Color getTertiaryBackgroundColor() {
        return isDarkMode ? TERTIARY_BG_COLOR : LIGHT_TERTIARY_BG_COLOR;
    }
    
    public static Color getTextColor() {
        return isDarkMode ? TEXT_COLOR : LIGHT_TEXT_COLOR;
    }
    
    public static Color getSecondaryTextColor() {
        return isDarkMode ? TEXT_COLOR_SECONDARY : LIGHT_TEXT_COLOR_SECONDARY;
    }
    
    public static JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.red);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(150, 40));
        return button;
    }
    
    public static JTextField createStyledTextField(String text) {
        JTextField textField = new JTextField(text);
        textField.setBackground(isDarkMode ? new Color(50, 50, 50) : new Color(255, 255, 255));
        textField.setForeground(getTextColor());
        textField.setCaretColor(getTextColor());
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(isDarkMode ? new Color(70, 70, 70) : new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        return textField;
    }
    
    public static JTextField createSearchTextField() {
        JTextField searchField = new JTextField();
        searchField.setBackground(isDarkMode ? new Color(50, 50, 50) : new Color(255, 255, 255));
        searchField.setForeground(getTextColor());
        searchField.setCaretColor(getTextColor());
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(isDarkMode ? new Color(70, 70, 70) : new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        searchField.putClientProperty("JTextField.placeholderText", "Search...");
        return searchField;
    }
    
    public static JComboBox<String> createStyledComboBox(String[] items) {
        JComboBox<String> comboBox = new JComboBox<>(items);
        comboBox.setBackground(isDarkMode ? new Color(50, 50, 50) : new Color(255, 255, 255));
        comboBox.setForeground(getTextColor());
        comboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        return comboBox;
    }
    
    public static JPanel createSection(String title, String description) {
        JPanel section = new JPanel();
        section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
        section.setBackground(getSecondaryBackgroundColor());
        section.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(getTertiaryBackgroundColor()),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        section.setAlignmentX(Component.LEFT_ALIGNMENT);
                
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(getTextColor());
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        descLabel.setForeground(getSecondaryTextColor());
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                
        section.add(titleLabel);
        section.add(Box.createRigidArea(new Dimension(0, 5)));
        section.add(descLabel);
        section.add(Box.createRigidArea(new Dimension(0, 15)));
                
        return section;
    }
    
    public static JPanel createSettingsPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(getBackgroundColor());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        return panel;
    }
    
    public static void addSettingSection(JPanel panel, String title) {
        // Add some space before new section (except for the first one)
        if (panel.getComponentCount() > 0) {
            panel.add(Box.createRigidArea(new Dimension(0, 20)));
        }
                
        JLabel sectionLabel = new JLabel(title);
        sectionLabel.setFont(new Font("Arial", Font.BOLD, 16));
        sectionLabel.setForeground(getTextColor());
        sectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                
        panel.add(sectionLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
                
        // Add separator
        JSeparator separator = new JSeparator();
        separator.setForeground(getTertiaryBackgroundColor());
        separator.setAlignmentX(Component.LEFT_ALIGNMENT);
        separator.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                
        panel.add(separator);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
    }
    
    public static void addSettingItem(JPanel panel, String label, JComponent component) {
        JPanel itemPanel = new JPanel(new BorderLayout(10, 0));
        itemPanel.setBackground(getBackgroundColor());
        itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        itemPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                
        JLabel itemLabel = new JLabel(label);
        itemLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        itemLabel.setForeground(getSecondaryTextColor());
        itemPanel.add(itemLabel, BorderLayout.WEST);
        itemPanel.add(component, BorderLayout.CENTER);
        
        panel.add(itemPanel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
    }
    
    public static void applyTheme(JComponent component) {
        if (component instanceof JPanel) {
            component.setBackground(getBackgroundColor());
        } else if (component instanceof JTextField) {
            component.setBackground(isDarkMode ? new Color(50, 50, 50) : new Color(255, 255, 255));
            component.setForeground(getTextColor());
            ((JTextField) component).setCaretColor(getTextColor());
        } else if (component instanceof JTextArea) {
            component.setBackground(isDarkMode ? new Color(50, 50, 50) : new Color(255, 255, 255));
            component.setForeground(getTextColor());
            ((JTextArea) component).setCaretColor(getTextColor());
        } else if (component instanceof JLabel) {
            component.setForeground(getTextColor());
        } else if (component instanceof JButton) {
            // Keep button styling as is
        } else if (component instanceof JComboBox) {
            component.setBackground(isDarkMode ? new Color(50, 50, 50) : new Color(255, 255, 255));
            component.setForeground(getTextColor());
        } else if (component instanceof JTable) {
            component.setBackground(isDarkMode ? new Color(50, 50, 50) : new Color(255, 255, 255));
            component.setForeground(getTextColor());
            ((JTable) component).setGridColor(isDarkMode ? new Color(70, 70, 70) : new Color(200, 200, 200));
            ((JTable) component).getTableHeader().setBackground(getTertiaryBackgroundColor());
            ((JTable) component).getTableHeader().setForeground(getTextColor());
        } else if (component instanceof JScrollPane) {
            component.setBackground(getBackgroundColor());
            component.setBorder(BorderFactory.createEmptyBorder());
            
            // Apply theme to viewport and scrolled component
            JViewport viewport = ((JScrollPane) component).getViewport();
            if (viewport != null) {
                viewport.setBackground(getBackgroundColor());
                Component view = viewport.getView();
                if (view instanceof JComponent) {
                    applyTheme((JComponent) view);
                }
            }
        } else if (component instanceof JMenuBar) {
            component.setBackground(getTertiaryBackgroundColor());
            component.setBorder(BorderFactory.createEmptyBorder());
            
            // Apply theme to all menus
            for (Component menuComp : ((JMenuBar) component).getComponents()) {
                if (menuComp instanceof JMenu) {
                    JMenu menu = (JMenu) menuComp;
                    menu.setForeground(getTextColor());
                    
                    // Apply theme to all menu items
                    for (int i = 0; i < menu.getItemCount(); i++) {
                        JMenuItem item = menu.getItem(i);
                        if (item != null) {
                            item.setBackground(getSecondaryBackgroundColor());
                            item.setForeground(getTextColor());
                        }
                    }
                }
            }
        } else if (component instanceof JTabbedPane) {
            component.setBackground(getBackgroundColor());
            component.setForeground(getTextColor());
            
            // Apply theme to all tabs
            JTabbedPane tabbedPane = (JTabbedPane) component;
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                Component tab = tabbedPane.getComponentAt(i);
                if (tab instanceof JComponent) {
                    applyTheme((JComponent) tab);
                }
            }
        } else {
            // For other components, just set basic colors
            component.setBackground(getBackgroundColor());
            component.setForeground(getTextColor());
        }
        
        // Apply theme recursively to all child components
        for (Component child : component.getComponents()) {
            if (child instanceof JComponent) {
                applyTheme((JComponent) child);
            }
        }
    }
    
    public static void applyThemeToFrame(JFrame frame) {
        frame.getContentPane().setBackground(getBackgroundColor());
        
        // Apply theme to all components in the frame
        for (Component comp : frame.getContentPane().getComponents()) {
            if (comp instanceof JComponent) {
                applyTheme((JComponent) comp);
            }
        }
        
        // Apply theme to menu bar if exists
        JMenuBar menuBar = frame.getJMenuBar();
        if (menuBar != null) {
            applyTheme(menuBar);
        }
        
        // Update the UI
        SwingUtilities.updateComponentTreeUI(frame);
    }
    
    public static void applyThemeToDialog(JDialog dialog) {
        dialog.getContentPane().setBackground(getBackgroundColor());
        
        // Apply theme to all components in the dialog
        for (Component comp : dialog.getContentPane().getComponents()) {
            if (comp instanceof JComponent) {
                applyTheme((JComponent) comp);
            }
        }
        
        // Update the UI
        SwingUtilities.updateComponentTreeUI(dialog);
    }

    public static Object getIcon(String string) {
        throw new UnsupportedOperationException("Unimplemented method 'getIcon'");
    }

    public static JButton createToolBarButton(String string, Object icon) {
        throw new UnsupportedOperationException("Unimplemented method 'createToolBarButton'");
    }
}
