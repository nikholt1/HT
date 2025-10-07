package com.example.hometheater.controller;

import javax.swing.*;

import com.example.hometheater.HomeTheaterApplication;
import com.example.hometheater.repository.DesktopRepository;
import com.example.hometheater.service.DesktopService;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URI;
import java.net.URL;

@Controller
public class DesktopApplicationController {

    private static ConfigurableApplicationContext context;

    public DesktopApplicationController(){


    }

    public static void main(String[] args, ConfigurableApplicationContext cont) {
        context = cont;

        SwingUtilities.invokeLater(() -> {
            DesktopRepository desktopRepository = new DesktopRepository();
            DesktopService desktopService = new DesktopService(desktopRepository);

            JFrame frame = new JFrame("Refract Desktop Settings");
            frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            frame.setSize(900, 500);
            frame.setLocationRelativeTo(null);
            frame.getContentPane().setBackground(Color.BLACK);

            // --- Load window icon ---
            final URL iconUrl = DesktopApplicationController.class.getResource("/static/images/imagesTemplates/R.png");
            if (iconUrl != null) frame.setIconImage(new ImageIcon(iconUrl).getImage());

            // --- System tray ---
            if (SystemTray.isSupported()) {
                SystemTray tray = SystemTray.getSystemTray();
                Image trayImage = Toolkit.getDefaultToolkit().getImage(
                        DesktopApplicationController.class.getResource("/static/images/imagesTemplates/R.png")
                );
                PopupMenu popup = new PopupMenu();
                MenuItem restoreItem = new MenuItem("Restore");
                restoreItem.addActionListener(e -> { frame.setVisible(true); frame.setState(Frame.NORMAL); });
                MenuItem exitItem = new MenuItem("Exit");
                exitItem.addActionListener(e -> System.exit(0));
                popup.add(restoreItem);
                popup.add(exitItem);
                TrayIcon trayIcon = new TrayIcon(trayImage, "Refract Desktop", popup);
                trayIcon.setImageAutoSize(true);
                trayIcon.addActionListener(e -> { frame.setVisible(true); frame.setState(Frame.NORMAL); });
                try { tray.add(trayIcon); } catch (AWTException e) { e.printStackTrace(); }
            }

            // --- Background Image for center panel ---
            final URL bgUrl = DesktopApplicationController.class.getResource("/static/images/imagesTemplates/remote.png");
            final Image backgroundImage = (bgUrl != null) ? new ImageIcon(bgUrl).getImage() : null;

            JPanel centerPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    g.setColor(Color.BLACK);
                    g.fillRect(0, 0, getWidth(), getHeight());
                    if (backgroundImage != null) {
                        int w = getWidth(), h = getHeight();
                        double scale = Math.max((double) w / backgroundImage.getWidth(null),
                                (double) h / backgroundImage.getHeight(null));
                        int width = (int) (backgroundImage.getWidth(null) * scale);
                        int height = (int) (backgroundImage.getHeight(null) * scale);
                        g.drawImage(backgroundImage, (w - width) / 2, (h - height) / 2, width, height, this);
                    }
                }
            };
            centerPanel.setBackground(Color.BLACK);

            // --- Left Panel: Server controls + status ---
            JPanel leftPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    int w = getWidth();
                    int h = getHeight();
                    Color topColor = new Color(30, 30, 30);    // dark gray top
                    Color bottomColor = new Color(49, 78, 92, 255);
                    // blue-ish bottom
                    GradientPaint gp = new GradientPaint(0, 0, topColor, 0, h, bottomColor);
                    g2.setPaint(gp);
                    g2.fillRect(0, 0, w, h);
                }
            };
            leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
            leftPanel.setOpaque(false); // optional, gradient paints entire panel
            leftPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            // Status dot
            class StatusDot extends JPanel {
                private Color color = Color.ORANGE;

                public StatusDot() {
                    setOpaque(false); // very important! Makes the panel fully transparent
                }

                public void setDotColor(Color c) {
                    this.color = c;
                    repaint();
                }

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g); // does NOT fill background because opaque=false
                    g.setColor(color);
                    g.fillOval(0, 0, getWidth(), getHeight());
                }
            }



            StatusDot statusDot = new StatusDot();
            statusDot.setPreferredSize(new Dimension(10, 10));
            JLabel statusLabel = new JLabel("Loading...");
            statusLabel.setForeground(Color.WHITE);
            statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

            JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            statusPanel.setBackground(new Color(0x0000001, true));
            statusPanel.add(statusDot);
            statusPanel.add(statusLabel);

            leftPanel.add(statusPanel);
            leftPanel.add(Box.createVerticalStrut(20));

            // Timer to update status dot every 2 seconds
            new Timer(2000, e -> {
                if (context == null) {
                    SwingUtilities.invokeLater(() -> {
                        statusDot.setDotColor(Color.ORANGE);
                        statusLabel.setText("Loading...");
                    });
                    return;
                }
                boolean running = context.isRunning();
                SwingUtilities.invokeLater(() -> {
                    if (running) {
                        statusDot.setDotColor(Color.GREEN);
                        statusLabel.setText("Server Up");
                    } else {
                        statusDot.setDotColor(Color.RED);
                        statusLabel.setText("Not Running");
                    }
                });
            }).start();

            // Server toggle button
            JButton serverToggleButton = new JButton("Kill Server");
            serverToggleButton.setBackground(Color.RED);
            serverToggleButton.setForeground(Color.WHITE);
            serverToggleButton.setFocusPainted(false);
            serverToggleButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            serverToggleButton.addActionListener(e -> {
                if ("Kill Server".equals(serverToggleButton.getText())) {
                    if (context.isRunning()) context.close();
                    serverToggleButton.setText("Launch Server");
                    serverToggleButton.setBackground(Color.GREEN);
                } else {
                    context = SpringApplication.run(HomeTheaterApplication.class, args);
                    serverToggleButton.setText("Kill Server");
                    serverToggleButton.setBackground(Color.RED);
                }
            });
            leftPanel.add(serverToggleButton);

            // --- Right Panel: IP + folder + save ---
            JPanel rightPanel = new JPanel();
            rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
            rightPanel.setBackground(new Color(49, 78, 92, 255));

            rightPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

// Load the image
// 🔑 Helper class to offset icons vertically
            class OffsetIcon extends ImageIcon {
                private final int yOffset;

                public OffsetIcon(Image image, int yOffset) {
                    super(image);
                    this.yOffset = yOffset;
                }

                @Override
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    // Apply vertical shift
                    super.paintIcon(c, g, x, y + yOffset);
                }
            }

// Load the image
            URL imageUrl = DesktopApplicationController.class.getResource("/static/images/imagesTemplates/Refractio-removebg-preview.png");
            ImageIcon icon = null;
            if (imageUrl != null) {
                ImageIcon baseIcon = new ImageIcon(imageUrl);
                // Resize the icon if needed
                Image img = baseIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);

                // Move icon up by 10px (use negative for up, positive for down)
                icon = new OffsetIcon(img, -15);
            }

// Create label with text on left, icon on right
            JLabel welcomeLabel = new JLabel("Welcome To", icon, JLabel.CENTER);
            welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 25));
            welcomeLabel.setForeground(Color.WHITE);
            welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            welcomeLabel.setIconTextGap(10); // space between text and image
            welcomeLabel.setHorizontalTextPosition(SwingConstants.LEFT); // text left, icon right

            rightPanel.add(welcomeLabel);
            rightPanel.add(Box.createVerticalStrut(5));




            // IP Label
            String ipv4 = desktopService.getIpv4Address();
            String urlText = (ipv4 != null ? ipv4 : "Unavailable") + ":8080";
            JLabel ipLabel = new JLabel("<html>Streaming service: <a href=''>" + urlText + "</a></html>");
            ipLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
            ipLabel.setForeground(Color.WHITE);
            ipLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            ipLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            ipLabel.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (ipv4 != null) {
                        try { Desktop.getDesktop().browse(new URI("http://" + urlText)); }
                        catch (Exception ex) { JOptionPane.showMessageDialog(frame, "Failed to open browser: " + ex.getMessage()); }
                    }
                }
                @Override
                public void mouseEntered(MouseEvent e) { ipLabel.setText("<html>Streaming service: <a href='' style='color:#aaddff;'>" + urlText + "</a></html>"); }
                @Override
                public void mouseExited(MouseEvent e) { ipLabel.setText("<html>Streaming service: <a href=''>" + urlText + "</a></html>"); }
            });
            rightPanel.add(ipLabel);
            rightPanel.add(Box.createVerticalStrut(10));



            // Folder label
            JLabel folderLabel = new JLabel("Video Folder Path:");
            folderLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            folderLabel.setForeground(Color.WHITE);
            folderLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            rightPanel.add(folderLabel);
            rightPanel.add(Box.createVerticalStrut(5));

            // Folder text field
            JTextField folderField = new JTextField(desktopService.getFolderPath());
            folderField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
            folderField.setFont(new Font("Segoe UI", Font.BOLD, 14));
            folderField.setAlignmentX(Component.LEFT_ALIGNMENT);
            rightPanel.add(folderField);
            rightPanel.add(Box.createVerticalStrut(10));

            // Save button
            JButton saveButton = new JButton("Save");
            saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
            saveButton.setForeground(Color.WHITE);
            saveButton.setBackground(new Color(0, 120, 215));
            saveButton.setFocusPainted(false);
            saveButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            saveButton.addActionListener(e -> {
                boolean success = desktopService.updateFolderPath(folderField.getText().trim());
                JOptionPane.showMessageDialog(frame, success ? "Folder path updated successfully!" : "Invalid folder path.",
                        "Info", success ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
            });
            rightPanel.add(saveButton);






            // --- Split panes ---
            JSplitPane leftSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, centerPanel);
            leftSplit.setDividerLocation(200);
            leftSplit.setDividerSize(3);
            leftSplit.setOpaque(false);

// --- Split pane: directly between left and right ---
            JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
            mainSplit.setDividerLocation(180); // adjust width of left panel
            mainSplit.setDividerSize(3);
            mainSplit.setOpaque(false);

            frame.setContentPane(mainSplit);
            frame.setVisible(true);

        });
    }








}



