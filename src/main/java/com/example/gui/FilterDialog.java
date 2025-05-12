package com.example.gui;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
// import java.awt.event.*;
import java.util.*;
import java.util.List;

public class FilterDialog extends JDialog {
    private MainAppWindow mainWindow;
    private JComboBox<String> fieldComboBox;
    private JComboBox<String> operatorComboBox;
    private JTextField valueField;
    private JButton addButton, applyButton, clearButton, cancelButton;
    private JList<String> filtersList;
    private DefaultListModel<String> filtersModel;
    private List<FilterCondition> conditions = new ArrayList<>();
    private RowFilter<DefaultTableModel, Object> resultFilter = null;
    
    public FilterDialog(MainAppWindow mainWindow) {
        super(mainWindow, "Filter Packets", true);
        this.mainWindow = mainWindow;
        
        setSize(500, 400);
        setLocationRelativeTo(mainWindow);
        setLayout(new BorderLayout());
        
        initializeUI();
    }
    
    private void initializeUI() {
        // Create filter panel
        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Create Filter", 
            TitledBorder.LEFT, TitledBorder.TOP, null, Color.WHITE));
        filterPanel.setBackground(new Color(40, 40, 40));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Field selector
        JLabel fieldLabel = new JLabel("Field:");
        fieldLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 0;
        filterPanel.add(fieldLabel, gbc);
        
        String[] fields = {"Time", "Source", "Destination", "Protocol", "Length", "Info"};
        fieldComboBox = new JComboBox<>(fields);
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        filterPanel.add(fieldComboBox, gbc);
        
        // Operator selector
        JLabel operatorLabel = new JLabel("Operator:");
        operatorLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        filterPanel.add(operatorLabel, gbc);
        
        String[] operators = {"contains", "equals", "starts with", "ends with", "greater than", "less than"};
        operatorComboBox = new JComboBox<>(operators);
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        filterPanel.add(operatorComboBox, gbc);
        
        // Value field
        JLabel valueLabel = new JLabel("Value:");
        valueLabel.setForeground(Color.WHITE);
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        filterPanel.add(valueLabel, gbc);
        
        valueField = new JTextField();
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        filterPanel.add(valueField, gbc);
        
        // Add button
        addButton = new JButton("Add Condition");
        addButton.setBackground(new Color(0, 100, 0));
        addButton.setForeground(Color.WHITE);
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.EAST;
        filterPanel.add(addButton, gbc);
        
        // Create filters list panel
        JPanel filtersListPanel = new JPanel(new BorderLayout());
        filtersListPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), "Active Filters", 
            TitledBorder.LEFT, TitledBorder.TOP, null, Color.WHITE));
        filtersListPanel.setBackground(new Color(40, 40, 40));
        
        filtersModel = new DefaultListModel<>();
        filtersList = new JList<>(filtersModel);
        filtersList.setBackground(new Color(50, 50, 50));
        filtersList.setForeground(Color.WHITE);
        filtersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(filtersList);
        scrollPane.setPreferredSize(new Dimension(450, 150));
        filtersListPanel.add(scrollPane, BorderLayout.CENTER);
        
        JButton removeButton = new JButton("Remove Selected");
        removeButton.setBackground(new Color(100, 0, 0));
        removeButton.setForeground(Color.WHITE);
        removeButton.addActionListener(e -> removeSelectedFilter());
        filtersListPanel.add(removeButton, BorderLayout.SOUTH);
        
        // Create buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonsPanel.setBackground(new Color(40, 40, 40));
        
        applyButton = new JButton("Apply");
        applyButton.setBackground(new Color(0, 0, 100));
        applyButton.setForeground(Color.WHITE);
        
        clearButton = new JButton("Clear All");
        clearButton.setBackground(new Color(100, 0, 0));
        clearButton.setForeground(Color.WHITE);
        
        cancelButton = new JButton("Cancel");
        cancelButton.setBackground(new Color(70, 70, 70));
        cancelButton.setForeground(Color.WHITE);
        
        buttonsPanel.add(applyButton);
        buttonsPanel.add(clearButton);
        buttonsPanel.add(cancelButton);
        
        // Add action listeners
        addButton.addActionListener(e -> addFilterCondition());
        applyButton.addActionListener(e -> applyFilter());
        clearButton.addActionListener(e -> clearFilters());
        cancelButton.addActionListener(e -> dispose());
        
        // Add panels to dialog
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(new Color(30, 30, 30));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        mainPanel.add(filterPanel, BorderLayout.NORTH);
        mainPanel.add(filtersListPanel, BorderLayout.CENTER);
        mainPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        add(mainPanel);
    }
    
    private void addFilterCondition() {
        String field = (String) fieldComboBox.getSelectedItem();
        String operator = (String) operatorComboBox.getSelectedItem();
        String value = valueField.getText().trim();
        
        if (value.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter a value for the filter condition.", 
                "Missing Value", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int columnIndex = fieldComboBox.getSelectedIndex();
        FilterCondition condition = new FilterCondition(columnIndex, operator, value);
        conditions.add(condition);
        
        filtersModel.addElement(field + " " + operator + " '" + value + "'");
        valueField.setText("");
    }
    
    private void removeSelectedFilter() {
        int selectedIndex = filtersList.getSelectedIndex();
        if (selectedIndex != -1) {
            conditions.remove(selectedIndex);
            filtersModel.remove(selectedIndex);
        }
    }
    
    private void clearFilters() {
        conditions.clear();
        filtersModel.clear();
    }
    
    private void applyFilter() {
        if (conditions.isEmpty()) {
            resultFilter = null;
        } else {
            List<RowFilter<DefaultTableModel, Object>> filters = new ArrayList<>();
            
            for (FilterCondition condition : conditions) {
                RowFilter<DefaultTableModel, Object> filter = createFilterForCondition(condition);
                filters.add(filter);
            }
            
            resultFilter = RowFilter.andFilter(filters);
        }
        
        dispose();
    }
    
    private RowFilter<DefaultTableModel, Object> createFilterForCondition(FilterCondition condition) {
        switch (condition.operator) {
            case "contains":
                return RowFilter.regexFilter("(?i).*" + condition.value + ".*", condition.columnIndex);
            case "equals":
                return RowFilter.regexFilter("(?i)^" + condition.value + "$", condition.columnIndex);
            case "starts with":
                return RowFilter.regexFilter("(?i)^" + condition.value + ".*", condition.columnIndex);
            case "ends with":
                return RowFilter.regexFilter("(?i).*" + condition.value + "$", condition.columnIndex);
            case "greater than":
                return new RowFilter<DefaultTableModel, Object>() {
                    @Override
                    public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                        try {
                            int value = Integer.parseInt(entry.getStringValue(condition.columnIndex));
                            return value > Integer.parseInt(condition.value);
                        } catch (NumberFormatException e) {
                            return false;
                        }
                    }
                };
            case "less than":
                return new RowFilter<DefaultTableModel, Object>() {
                    @Override
                    public boolean include(Entry<? extends DefaultTableModel, ? extends Object> entry) {
                        try {
                            int value = Integer.parseInt(entry.getStringValue(condition.columnIndex));
                            return value < Integer.parseInt(condition.value);
                        } catch (NumberFormatException e) {
                            return false;
                        }
                    }
                };
            default:
                return null;
        }
    }
    
    public RowFilter<DefaultTableModel, Object> getFilter() {
        return resultFilter;
    }
    
    private static class FilterCondition {
        int columnIndex;
        String operator;
        String value;
        
        public FilterCondition(int columnIndex, String operator, String value) {
            this.columnIndex = columnIndex;
            this.operator = operator;
            this.value = value;
        }
    }
}
