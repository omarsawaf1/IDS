package com.example.gui;

import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages the security rules for the Intrusion Detection System.
 * Provides functionality to import/export rules to a custom format.
 */
public class RuleManager {
    private static final String FIELD_SEPARATOR = "::";
    private static final String RECORD_SEPARATOR = "##";
    
    /**
     * Exports the rules to a file.
     *
     * @param rules The list of rules to export
     * @param filePath The path to save the file
     * @return true if export was successful, false otherwise
     */
    public static boolean exportRules(List<Rule> rules, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Rule rule : rules) {
                StringBuilder sb = new StringBuilder();
                sb
                  .append(rule.getAction()).append(FIELD_SEPARATOR)
                  .append(rule.getProtocol()).append(FIELD_SEPARATOR)
                  .append(rule.getSource()).append(FIELD_SEPARATOR)
                  .append(rule.getSource()).append(FIELD_SEPARATOR)
                  .append(rule.getPort()).append(FIELD_SEPARATOR)
                  .append(rule.getDestination()).append(FIELD_SEPARATOR)
                  .append(rule.getDestination()).append(FIELD_SEPARATOR)
                  .append(rule.getDestination()).append(FIELD_SEPARATOR)
                  .append(rule.getDescription()).append(RECORD_SEPARATOR);
                writer.write(sb.toString());
                writer.newLine();
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Imports rules from a file.
     *
     * @param filePath The path to the file
     * @return A list of imported rules, or null if import failed
     */
    public static List<Rule> importRules(String filePath) {
        List<Rule> importedRules = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                // Remove the record separator at the end
                if (line.endsWith(RECORD_SEPARATOR)) {
                    line = line.substring(0, line.length() - RECORD_SEPARATOR.length());
                }
                String[] fields = line.split(FIELD_SEPARATOR);
                if (fields.length >= 10) {
                    try {
                        boolean enabled = Boolean.parseBoolean(fields[0]);
                        String action = fields[1];
                        String protocol = fields[2];
                        String sourceMac = fields[3];
                        String sourceIp = fields[4];
                        String sourcePort = fields[5];
                        String destMac = fields[6];
                        String destIp = fields[7];
                        String destPort = fields[8];
                        String description = fields[9];
                        
                        Rule rule = new Rule(
                            enabled, action, protocol, sourceMac, sourceIp, sourcePort,
                            destMac, destIp, destPort, description
                        );
                        importedRules.add(rule);
                    } catch (Exception e) {
                        System.err.println("Error parsing rule: " + e.getMessage());
                    }
                }
            }
            return importedRules;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Updates the table model with the current rules.
     *
     * @param model The table model to update
     * @param rules The list of rules
     */
    public static void updateTableModel(DefaultTableModel model, List<Rule> rules) {
        // Clear existing rows
        model.setRowCount(0);
        
        // Add all rules
        for (Rule rule : rules) {
            model.addRow(new Object[]{
                rule.getAction(),
                rule.getProtocol(),
                rule.getSource(),
                rule.getSource(),
                rule.getPort(),
                rule.getDestination(),
                rule.getDestination(),
                rule.getDestination(),
                rule.getDescription()
            });
        }
    }
    
    /**
     * Applies a rule to a packet.
     *
     * @param rule The rule to apply
     * @param protocol The packet protocol
     * @param sourceMac The packet source MAC
     * @param sourceIp The packet source IP
     * @param sourcePort The packet source port
     * @param destMac The packet destination MAC
     * @param destIp The packet destination IP
     * @param destPort The packet destination port
     * @return The action to take, or null if the rule doesn't apply
     */
    public static String applyRule(Rule rule, String protocol, 
                                  String sourceMac, String sourceIp, String sourcePort,
                                  String destMac, String destIp, String destPort) {
       
        
        // Check if rule applies to this packet
        boolean protocolMatch = rule.getProtocol().equals("Any") ||
                rule.getProtocol().equals(protocol);
        
        boolean sourceMacMatch = rule.getSource().equals("Any") ||
                rule.getSource().equals(sourceMac);
        
        boolean sourceIpMatch = rule.getSource().equals("Any") ||
                rule.getSource().equals(sourceIp);
        
        boolean sourcePortMatch = rule.getPort().equals("Any") ||
                rule.getPort().equals(sourcePort);
        
        boolean destMacMatch = rule.getDestination().equals("Any") ||
                rule.getDestination().equals(destMac);
        
        boolean destIpMatch = rule.getDestination().equals("Any") ||
                rule.getDestination().equals(destIp);
        
        boolean destPortMatch = rule.getDestination().equals("Any") ||
                rule.getDestination().equals(destPort);
        
        if (protocolMatch && sourceMacMatch && sourceIpMatch && sourcePortMatch &&
            destMacMatch && destIpMatch && destPortMatch) {
            return rule.getAction();
        }
        
        return null;
    }

    public Rule showDialog() {
        throw new UnsupportedOperationException("Unimplemented method 'showDialog'");
    }
}

