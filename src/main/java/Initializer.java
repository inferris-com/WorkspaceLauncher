import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.file.Files;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

public class Initializer {
    private final String LAUNCHER_TEXT = "Launcher";
    private JButton redisButton = null;
    private JButton workspaceButton = null;
    private static JFrame frame;
    private static boolean isMinimized;

    public void initialize() {
        frame = new JFrame("Inferris Launcher");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBackground(new Color(18, 18, 18));

        JLabel launcherLabel = new JLabel(LAUNCHER_TEXT);
        launcherLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        launcherLabel.setForeground(new Color(66, 135, 245));

        JLabel intellijLabel = new JLabel("Starting IntelliJ Idea");
        intellijLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        intellijLabel.setForeground(new Color(141, 144, 179));
        intellijLabel.setVisible(false);

        JLabel proxyLabel = new JLabel("Starting proxy...");
        proxyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        proxyLabel.setForeground(new Color(141, 144, 179));
        proxyLabel.setVisible(false);

        JLabel lobbyLabel = new JLabel("Starting lobby...");
        lobbyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        lobbyLabel.setForeground(new Color(141, 144, 179));
        lobbyLabel.setVisible(false);

        JLabel inferrisLabel = new JLabel("Starting inferris...");
        inferrisLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        inferrisLabel.setForeground(new Color(141, 144, 179));
        inferrisLabel.setVisible(false);

        JLabel success = new JLabel("Success. Happy coding! Program will exit in 5 seconds.");
        success.setAlignmentX(Component.CENTER_ALIGNMENT);
        success.setForeground(Color.GREEN);
        success.setVisible(false);

        redisButton = new JButton("Open Redis");
        redisButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        redisButton.setFocusPainted(false);
        redisButton.setBackground(new Color(222, 151, 151));

        workspaceButton = new JButton("Open workspace");
        workspaceButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        workspaceButton.setFocusPainted(false);
        workspaceButton.setBackground(Color.WHITE);

        container.add(Box.createVerticalStrut(10));
        container.add(launcherLabel);
        container.add(Box.createVerticalStrut(10));
        container.add(redisButton);
        container.add(Box.createVerticalStrut(10));
        container.add(workspaceButton);
        container.add(Box.createVerticalStrut(10));
        container.add(intellijLabel);
        container.add(Box.createVerticalStrut(10));
        container.add(proxyLabel);
        container.add(Box.createVerticalStrut(10));
        container.add(lobbyLabel);
        container.add(Box.createVerticalStrut(10));
        container.add(inferrisLabel);
        container.add(Box.createVerticalStrut(10));
        container.add(success);

        frame.add(container);
        frame.setSize(400, 300);
        frame.setVisible(true);

        /*
        Redis button
         */

        redisButton.addActionListener(e -> {
            openWSL(launcherLabel);
        });

        /*
        Workspace button
         */

        workspaceButton.addActionListener(e -> {
            openSoftware(Software.INTELLIJ_IDEA, intellijLabel);


            new Thread(() -> {
                sleep(5);
                openTerminal(Software.CMD_PROMPT_PROXY, proxyLabel);
                frame.requestFocus();
                sleep(5);
                openTerminal(Software.CMD_PROMPT_LOBBY, lobbyLabel);
                frame.requestFocus();
                sleep(5);
                openTerminal(Software.CMD_PROMPT_INFERRIS, inferrisLabel);
                sleep(4);
                success.setVisible(true);
                sleep(5);
                System.exit(0);
            }).start();

            new Thread(() -> {
                disableButton(workspaceButton, 30);
            }).start();
        });
    }

    private void openTerminal(Software software, JLabel label) {
        String directory = null;
        frame.requestFocus();
        switch (software) {
            case CMD_PROMPT_PROXY -> {
                directory = Launcher.getProperties().getProperty(Software.CMD_PROMPT_PROXY.getPath());
            }
            case CMD_PROMPT_LOBBY -> {
                directory = Launcher.getProperties().getProperty(Software.CMD_PROMPT_LOBBY.getPath());
            }
            case CMD_PROMPT_INFERRIS -> {
                directory = Launcher.getProperties().getProperty(Software.CMD_PROMPT_INFERRIS.getPath());
            }
        }
        label.setVisible(true);
        try {
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "start", "cmd.exe", "/K", "cd /d \"" + directory + "\" && start.bat");
            pb.start();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void openWSL(JLabel label) {
        frame.requestFocus();
        try {
            ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", "start", "cmd.exe", "/K", "wsl", "--cd", "/home");
            pb.start();

            temporarilyDisable(redisButton, label, "Opening Redis...", true, 3);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void openSoftware(Software software, JLabel label) {
        String path = null;
        frame.requestFocus();
        if (software == Software.INTELLIJ_IDEA) {
            path = Launcher.getProperties().getProperty(software.getPath());
            setText(label, "Opening IntelliJ Idea...", true);
        }
        label.setVisible(true);
//        try {
//            ProcessBuilder pb = new ProcessBuilder(path);
//            pb.start();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
    }

    private void sleep(int seconds) {
        try {
            TimeUnit.SECONDS.sleep(seconds);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void createConfig() {
        File pluginFolder = new File("plugins");
        File propertiesFile = Launcher.getPropertiesFile();
        Launcher.setPropertiesFile(new File(pluginFolder, "launcher.properties"));

        if (!propertiesFile.exists()) {
            try {
                InputStream defaultProperties = Launcher.class.getResourceAsStream("/launcher.properties");
                Files.copy(defaultProperties, propertiesFile.toPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Properties properties = new Properties();
        try (InputStream inputStream = new FileInputStream(propertiesFile)) {
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try (OutputStream outputStream = new FileOutputStream(propertiesFile)) {
            properties.store(outputStream, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Launcher.setProperties(properties);
    }

    private void temporarilyDisable(JButton button, JLabel label, String text, boolean isHeader, int seconds) {
        frame.requestFocus();
        button.setEnabled(false);
        label.setText(text);

        new Thread(() -> {
            sleep(seconds);

            button.setEnabled(true);
            if (isHeader)
                label.setText(LAUNCHER_TEXT);
            frame.requestFocus();
        }).start();
    }

    private void setText(JLabel label, String text, boolean requestFocus) {
        label.setText(text);

        if (requestFocus)
            frame.requestFocus();
    }

    private void temporarilyDisable(JLabel label, String text, boolean isHeader, int seconds, boolean allButtons) {
        if (allButtons) {
            frame.requestFocus();
            redisButton.setEnabled(false);
            workspaceButton.setEnabled(false);
            label.setText(text);

            new Thread(() -> {
                sleep(seconds);

                redisButton.setEnabled(true);
                workspaceButton.setEnabled(true);
                if (isHeader)
                    label.setText(LAUNCHER_TEXT);
                frame.requestFocus();
            }).start();
        }
    }

    private void disableButton(JButton button, int seconds) {
        frame.requestFocus();
        button.setEnabled(false);

        new Thread(() -> {
            sleep(seconds);
            button.setEnabled(true);

            frame.requestFocus();
        }).start();
    }
}