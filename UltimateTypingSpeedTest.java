package ultimatetypingspeedtest;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import java.util.Timer;
import java.util.List;
import java.util.ArrayList;
import java.io.*;

public class UltimateTypingSpeedTest {
    private JFrame frame;
    private JTextArea textArea, inputArea, leaderboardArea;
    private JLabel timerLabel, accuracyLabel, wpmLabel, wordCounterLabel, difficultyLabel, progressBarLabel;
    private JButton startButton, retryButton, pauseButton, finishButton, leaderboardButton, exportButton, profileButton, themeButton, timeButton, difficultyButton;
    private JProgressBar progressBar;
    private Timer timer;
    private int timeLeft = 60;
    private String currentText;
    private boolean isTyping = false;
    private boolean isPaused = false;
    private String difficultyLevel = "Medium";
    private String theme = "Light";
    private List<String[]> leaderboard = new ArrayList<>();
    private Map<String, String> userProfiles = new HashMap<>();
    private int correctWords = 0, incorrectWords = 0;

    public UltimateTypingSpeedTest() {
        frame = new JFrame("Ultimate Typing Speed Test");
        frame.setSize(1300, 900);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JLabel instructionLabel = new JLabel("Welcome to the Ultimate Typing Speed Test", JLabel.CENTER);
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 20));
        frame.add(instructionLabel, BorderLayout.NORTH);

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setBorder(BorderFactory.createTitledBorder("Text to Type"));
        textArea = new JTextArea();
        textArea.setFont(new Font("Serif", Font.PLAIN, 16));
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        textPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        frame.add(textPanel, BorderLayout.WEST);

        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createTitledBorder("Your Input"));
        inputArea = new JTextArea();
        inputArea.setFont(new Font("Serif", Font.PLAIN, 16));
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setEnabled(false);
        inputArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                updateProgressBar();
            }
        });

        progressBar = new JProgressBar();
        progressBar.setStringPainted(true);
        progressBar.setMaximum(100);
        progressBar.setValue(0);

        inputPanel.add(new JScrollPane(inputArea), BorderLayout.CENTER);
        inputPanel.add(progressBar, BorderLayout.SOUTH);
        frame.add(inputPanel, BorderLayout.CENTER);

        JPanel leaderboardPanel = new JPanel(new BorderLayout());
        leaderboardPanel.setBorder(BorderFactory.createTitledBorder("Leaderboard"));
        leaderboardArea = new JTextArea();
        leaderboardArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        leaderboardArea.setEditable(false);
        leaderboardPanel.add(new JScrollPane(leaderboardArea), BorderLayout.CENTER);
        frame.add(leaderboardPanel, BorderLayout.EAST);

        JPanel statusPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        timerLabel = new JLabel("Time Left: 60s", JLabel.CENTER);
        accuracyLabel = new JLabel("Accuracy: 0%", JLabel.CENTER);
        wpmLabel = new JLabel("WPM: 0", JLabel.CENTER);
        wordCounterLabel = new JLabel("Words: 0 Correct, 0 Incorrect", JLabel.CENTER);
        difficultyLabel = new JLabel("Difficulty: Medium", JLabel.CENTER);
        progressBarLabel = new JLabel("Progress: 0%", JLabel.CENTER);

        statusPanel.add(timerLabel);
        statusPanel.add(difficultyLabel);
        statusPanel.add(accuracyLabel);
        statusPanel.add(wpmLabel);
        statusPanel.add(wordCounterLabel);
        statusPanel.add(progressBarLabel);
        frame.add(statusPanel, BorderLayout.SOUTH);

        JPanel buttonPanel = new JPanel(new GridLayout(9, 1, 10, 10));
        startButton = new JButton("Start");
        startButton.addActionListener(e -> startTest());
        buttonPanel.add(startButton);

        retryButton = new JButton("Retry");
        retryButton.setEnabled(false);
        retryButton.addActionListener(e -> resetTest());
        buttonPanel.add(retryButton);

        pauseButton = new JButton("Pause");
        pauseButton.setEnabled(false);
        pauseButton.addActionListener(e -> togglePause());
        buttonPanel.add(pauseButton);

        finishButton = new JButton("Finish");
        finishButton.setEnabled(false);
        finishButton.addActionListener(e -> finishTest());
        buttonPanel.add(finishButton);

        leaderboardButton = new JButton("See Leaderboard");
        leaderboardButton.addActionListener(e -> showLeaderboard());
        buttonPanel.add(leaderboardButton);

        exportButton = new JButton("Export Results");
        exportButton.addActionListener(e -> exportResults());
        buttonPanel.add(exportButton);

        themeButton = new JButton("Toggle Theme");
        themeButton.addActionListener(e -> toggleTheme());
        buttonPanel.add(themeButton);

        timeButton = new JButton("Set Time");
        timeButton.addActionListener(e -> setTime());
        buttonPanel.add(timeButton);

        difficultyButton = new JButton("Set Difficulty");
        difficultyButton.addActionListener(e -> setDifficulty());
        buttonPanel.add(difficultyButton);

        frame.add(buttonPanel, BorderLayout.EAST);

        frame.setVisible(true);
    }

    private void startTest() {
        if (isPaused) {
            resumeTest();
            return;
        }
        startButton.setEnabled(false);
        retryButton.setEnabled(false);
        pauseButton.setEnabled(true);
        finishButton.setEnabled(true);
        inputArea.setEnabled(true);
        inputArea.setText("");
        isTyping = true;
        timerLabel.setText("Time Left: " + timeLeft + "s");

        currentText = getRandomText();
        textArea.setText(currentText);

        inputArea.requestFocus();

        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (timeLeft > 0) {
                    timeLeft--;
                    timerLabel.setText("Time Left: " + timeLeft + "s");
                } else {
                    endTest();
                    timer.cancel();
                }
            }
        }, 1000, 1000);
    }

    private void finishTest() {
        timer.cancel();
        timeLeft = 60;
        timerLabel.setText("Time Left: " + timeLeft + "s");
        inputArea.setEnabled(false);
        pauseButton.setEnabled(false);
        finishButton.setEnabled(false);
        calculateResults();
        retryButton.setEnabled(true);
        String name = JOptionPane.showInputDialog(frame, "Enter your name for the leaderboard:");
        if (name != null && !name.trim().isEmpty()) {
            leaderboard.add(new String[]{name, wpmLabel.getText(), accuracyLabel.getText()});
            updateLeaderboard();
        }
    }

    private void endTest() {
        isTyping = false;
        inputArea.setEnabled(false);
        pauseButton.setEnabled(false);
        finishButton.setEnabled(false);
        calculateResults();
        retryButton.setEnabled(true);
        String name = JOptionPane.showInputDialog(frame, "Enter your name for the leaderboard:");
        if (name != null && !name.trim().isEmpty()) {
            leaderboard.add(new String[]{name, wpmLabel.getText(), accuracyLabel.getText()});
            updateLeaderboard();
        }
    }

    private void resetTest() {
        startButton.setEnabled(true);
        retryButton.setEnabled(false);
        inputArea.setText("");
        textArea.setText("");
        timerLabel.setText("Time Left: " + timeLeft + "s");
        correctWords = 0;
        incorrectWords = 0;
        timeLeft = 60;
    }

    private void togglePause() {
        if (isTyping) {
            isTyping = false;
            isPaused = true;
            inputArea.setEnabled(false);
            pauseButton.setText("Resume");
            timer.cancel();
        } else {
            resumeTest();
        }
    }

    private void resumeTest() {
        isTyping = true;
        isPaused = false;
        inputArea.setEnabled(true);
        pauseButton.setText("Pause");
        startTest();
    }

    private void showLeaderboard() {
        StringBuilder sb = new StringBuilder();
        sb.append("Leaderboard\n");
        sb.append("==============\n");
        for (String[] entry : leaderboard) {
            sb.append(String.join(" | ", entry)).append("\n");
        }
        JOptionPane.showMessageDialog(frame, sb.toString(), "Leaderboard", JOptionPane.INFORMATION_MESSAGE);
    }

    private void updateLeaderboard() {
        StringBuilder sb = new StringBuilder();
        sb.append("Name\tWPM\tAccuracy\n");
        sb.append("=========================\n");
        for (String[] entry : leaderboard) {
            sb.append(String.join("\t", entry)).append("\n");
        }
        leaderboardArea.setText(sb.toString());
    }

    private void exportResults() {
        try (FileWriter writer = new FileWriter("TypingSpeedTestResults.txt")) {
            for (String[] entry : leaderboard) {
                writer.write(String.join(", ", entry) + "\n");
            }
            JOptionPane.showMessageDialog(frame, "Results exported successfully!");
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "Error exporting results: " + e.getMessage());
        }
    }

    private void toggleTheme() {
        if (theme.equals("Light")) {
            theme = "Dark";
            frame.getContentPane().setBackground(Color.DARK_GRAY);
            textArea.setBackground(Color.BLACK);
            textArea.setForeground(Color.WHITE);
            inputArea.setBackground(Color.BLACK);
            inputArea.setForeground(Color.WHITE);
        } else {
            theme = "Light";
            frame.getContentPane().setBackground(Color.WHITE);
            textArea.setBackground(Color.WHITE);
            textArea.setForeground(Color.BLACK);
            inputArea.setBackground(Color.WHITE);
            inputArea.setForeground(Color.BLACK);
        }
    }

    private void calculateResults() {
        String typedText = inputArea.getText();
        String[] originalWords = currentText.split("\\s+");
        String[] typedWords = typedText.split("\\s+");

        correctWords = 0;
        incorrectWords = 0;

        for (int i = 0; i < Math.min(originalWords.length, typedWords.length); i++) {
            if (originalWords[i].equals(typedWords[i])) {
                correctWords++;
            } else {
                incorrectWords++;
            }
        }

        double accuracy = (double) correctWords / originalWords.length * 100;
        int wpm = correctWords;

        accuracyLabel.setText("Accuracy: " + String.format("%.2f", accuracy) + "%");
        wpmLabel.setText("WPM: " + wpm);
        wordCounterLabel.setText("Words: " + correctWords + " Correct, " + incorrectWords + " Incorrect");
    }

    private void updateProgressBar() {
        String typedText = inputArea.getText();
        int progress = Math.min((int) ((double) typedText.length() / currentText.length() * 100), 100);
        progressBar.setValue(progress);
        progressBarLabel.setText("Progress: " + progress + "%");
    }

    private void setTime() {
        String timeInput = JOptionPane.showInputDialog(frame, "Enter time (in seconds):");
        try {
            int newTime = Integer.parseInt(timeInput);
            timeLeft = newTime;
            timerLabel.setText("Time Left: " + timeLeft + "s");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Invalid time input. Please enter a valid number.");
        }
    }

    private void setDifficulty() {
        String[] options = {"Easy", "Medium", "Hard"};
        String choice = (String) JOptionPane.showInputDialog(frame, "Select Difficulty Level:", "Difficulty", JOptionPane.QUESTION_MESSAGE, null, options, difficultyLevel);
        if (choice != null) {
            difficultyLevel = choice;
            difficultyLabel.setText("Difficulty: " + difficultyLevel);
        }
    }

    private String getRandomText() {
        String easyText = "The quick brown fox jumps over the lazy dog.";
        String mediumText = "Typing is an essential skill for modern life, especially with computers.";
        String hardText = "The juxtaposition of paradoxical metaphors perplexed the analytical philosopher.";
        return switch (difficultyLevel) {
            case "Easy" -> easyText;
            case "Medium" -> mediumText;
            case "Hard" -> hardText;
            default -> mediumText;
        };
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(UltimateTypingSpeedTest::new);
    }
}
