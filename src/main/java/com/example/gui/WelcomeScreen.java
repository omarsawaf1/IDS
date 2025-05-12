package com.example.gui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.sound.sampled.*;
import java.io.InputStream;

public class WelcomeScreen extends JFrame {
    // Animation components
    private Timer pulseTimer;
    private Timer fadeInTimer;
    private Timer logoSpinTimer;
    private float alpha = 0.0f;
    private double angle = 0;
    private int pulseDirection = 1;
    private float pulseSize = 1.0f;
    private JPanel animationPanel;
    private BufferedImage logoImage;
    private JLabel welcomeText;
    private JButton continueBtn;
    private Color glowColor = new Color(255, 0, 0, 100);
    // Sound effect
    private Clip dragonRoarClip;

    public WelcomeScreen() {
        setTitle("Welcome to Kaomi Intrusion Detection");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setUndecorated(true); // Remove window decorations for a sleeker look
        setBackground(new Color(0, 0, 0, 0)); // Transparent background

        // Load dragon roar sound
        loadDragonRoarSound();

        // Create a rounded panel for the content
        JPanel mainPanel = new RoundedPanel(20, new Color(10, 10, 10, 240));
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setContentPane(mainPanel);

        // Load logo image
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/Dragon.png"));
            Image img = icon.getImage();
            logoImage = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = logoImage.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g2d.drawImage(img, 0, 0, 400, 400, null);
            g2d.dispose();
        } catch (Exception ex) {
            logoImage = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
        }

        // Create animation panel
        animationPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                // Set transparency
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
                // Draw glow effect
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                // Draw pulsing glow
                int glowSize = (int)(200 * pulseSize);
                RadialGradientPaint glow = new RadialGradientPaint(
                        centerX, centerY, glowSize,
                        new float[]{0.0f, 0.7f, 1.0f},
                        new Color[]{
                                new Color(255, 0, 0, 100),
                                new Color(150, 0, 0, 50),
                                new Color(0, 0, 0, 0)
                        }
                );
                g2d.setPaint(glow);
                g2d.fillOval(centerX - glowSize, centerY - glowSize, glowSize * 2, glowSize * 2);
                // Draw rotating logo
                if (logoImage != null) {
                    AffineTransform transform = new AffineTransform();
                    transform.translate(centerX, centerY);
                    transform.rotate(-angle); // Negative angle for opposite direction
                    transform.scale(pulseSize, pulseSize);
                    transform.translate(-logoImage.getWidth() / 2, -logoImage.getHeight() / 2);
                    g2d.drawImage(logoImage, transform, null);
                }
                g2d.dispose();
            }
        };
        animationPanel.setOpaque(false);
        animationPanel.setPreferredSize(new Dimension(400, 400));
        mainPanel.add(animationPanel, BorderLayout.CENTER);

        // Create welcome text with fade-in effect
        welcomeText = new JLabel("KAOMI INTRUSION DETECTION", SwingConstants.CENTER);
        welcomeText.setForeground(new Color(255, 0, 0, 0)); // Start invisible
        welcomeText.setFont(new Font("Orbitron", Font.BOLD, 28));
        welcomeText.setBorder(new EmptyBorder(20, 0, 20, 0));
        mainPanel.add(welcomeText, BorderLayout.NORTH);

        // Create tagline
        JLabel tagline = new JLabel("Advanced Network Security System", SwingConstants.CENTER);
        tagline.setForeground(new Color(200, 200, 200, 0)); // Start invisible
        tagline.setFont(new Font("Orbitron", Font.PLAIN, 16));

        // Create continue button with glow effect
        continueBtn = new JButton("LAUNCH SYSTEM") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Create gradient background
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(120, 0, 0),
                        0, getHeight(), new Color(80, 0, 0)
                );
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                // Draw glow effect on hover
                if (getModel().isRollover()) {
                    g2d.setColor(new Color(255, 50, 50, 50));
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                }
                // Draw text
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getHeight();
                g2d.drawString(getText(), (getWidth() - textWidth) / 2,
                        (getHeight() + textHeight / 2) / 2);
                g2d.dispose();
            }
        };
        continueBtn.setFont(new Font("Orbitron", Font.BOLD, 18));
        continueBtn.setForeground(Color.WHITE);
        continueBtn.setBackground(new Color(0, 0, 0, 0)); // Transparent background
        continueBtn.setFocusPainted(false);
        continueBtn.setBorderPainted(false);
        continueBtn.setContentAreaFilled(false);
        continueBtn.setOpaque(false);
        continueBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        continueBtn.setPreferredSize(new Dimension(220, 50));

        // Add hover effect with dragon roar sound
        continueBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                pulseDirection = 2; // Increase pulse speed on hover
                playDragonRoar(); // Play dragon roar on hover
            }
            @Override
            public void mouseExited(MouseEvent e) {
                pulseDirection = 1; // Return to normal pulse speed
            }
        });

        continueBtn.addActionListener(e -> {
            // Start fade-out animation
            Timer fadeOutTimer = new Timer(20, new ActionListener() {
                float fadeOut = 1.0f;
                @Override
                public void actionPerformed(ActionEvent e) {
                    fadeOut -= 0.05f;
                    if (fadeOut <= 0) {
                        fadeOut = 0;
                        ((Timer)e.getSource()).stop();
                        dispose();
                        new SignInScreen();
                    }
                    // Apply fade out to all components
                    mainPanel.setBackground(new Color(10, 10, 10, (int)(fadeOut * 240)));
                    welcomeText.setForeground(new Color(255, 0, 0, (int)(fadeOut * 255)));
                    tagline.setForeground(new Color(200, 200, 200, (int)(fadeOut * 255)));
                    alpha = fadeOut;
                    repaint();
                }
            });
            fadeOutTimer.start();
            // Stop other animations
            pulseTimer.stop();
            logoSpinTimer.stop();
        });

        // Create bottom panel with button and tagline
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.add(tagline, BorderLayout.NORTH);
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel.setOpaque(false);
        btnPanel.add(continueBtn);
        bottomPanel.add(btnPanel, BorderLayout.CENTER);

        // Add version info
        JLabel versionLabel = new JLabel("v1.0.0", SwingConstants.RIGHT);
        versionLabel.setForeground(new Color(100, 100, 100, 0)); // Start invisible
        versionLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        bottomPanel.add(versionLabel, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Start animations
        startAnimations(tagline, versionLabel);

        // Add window drag capability
        addWindowDragCapability();

        // Show the frame
        setVisible(true);
    }

    private void loadDragonRoarSound() {
        try {
            InputStream soundStream = getClass().getResourceAsStream("/dragonroar.wav");
            if (soundStream != null) {
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundStream);
                dragonRoarClip = AudioSystem.getClip();
                dragonRoarClip.open(audioStream);
            }
        } catch (Exception e) {
            System.err.println("Could not load dragon roar sound: " + e.getMessage());
        }
    }

    private void playDragonRoar() {
        if (dragonRoarClip != null && !dragonRoarClip.isRunning()) {
            dragonRoarClip.setFramePosition(0);
            dragonRoarClip.start();
        }
    }

    private void startAnimations(JLabel tagline, JLabel versionLabel) {
        // Fade-in animation
        fadeInTimer = new Timer(40, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                alpha += 0.05f;
                if (alpha >= 1.0f) {
                    alpha = 1.0f;
                    fadeInTimer.stop();
                }
                // Fade in text elements with delay
                if (alpha > 0.3f) {
                    welcomeText.setForeground(new Color(255, 0, 0, Math.min(255, (int)((alpha - 0.3f) * 1.5f * 255))));
                }
                if (alpha > 0.5f) {
                    tagline.setForeground(new Color(200, 200, 200, Math.min(255, (int)((alpha - 0.5f) * 2f * 255))));
                    versionLabel.setForeground(new Color(100, 100, 100, Math.min(255, (int)((alpha - 0.5f) * 2f * 255))));
                }
                animationPanel.repaint();
            }
        });

        // Pulse animation
        pulseTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pulseSize += 0.01f * pulseDirection;
                if (pulseSize > 1.1f) {
                    pulseSize = 1.1f;
                    pulseDirection = -1;
                } else if (pulseSize < 0.9f) {
                    pulseSize = 0.9f;
                    pulseDirection = 1;
                }
                animationPanel.repaint();
            }
        });

        // Logo spin animation (counter-clockwise)
        logoSpinTimer = new Timer(50, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                angle += 0.02;
                if (angle > 2 * Math.PI) {
                    angle -= 2 * Math.PI;
                }
                animationPanel.repaint();
            }
        });

        // Start animations
        fadeInTimer.start();
        pulseTimer.start();
        logoSpinTimer.start();
    }

    private void addWindowDragCapability() {
        // Allow dragging the window
        Point dragPoint = new Point();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragPoint.x = e.getX();
                dragPoint.y = e.getY();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point location = getLocation();
                setLocation(location.x + e.getX() - dragPoint.x,
                        location.y + e.getY() - dragPoint.y);
            }
        });
    }

    // Custom rounded panel
    private class RoundedPanel extends JPanel {
        private int cornerRadius;
        private Color backgroundColor;
        public RoundedPanel(int radius, Color bgColor) {
            super();
            this.cornerRadius = radius;
            this.backgroundColor = bgColor;
            setOpaque(false);
        }
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            // Draw background with rounded corners
                     g2d.setColor(backgroundColor);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            // Add subtle border
            g2d.setColor(new Color(70, 0, 0));
            g2d.setStroke(new BasicStroke(2f));
            g2d.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, cornerRadius, cornerRadius);
            g2d.dispose();
        }
    }

    public static void main(String[] args) {
        // Set system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Use SwingUtilities to ensure thread safety
        SwingUtilities.invokeLater(() -> new WelcomeScreen());
    }
}

