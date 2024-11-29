import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class SlangDictionaryApp extends JFrame {
    private HashMap<String, String> slangMap = new HashMap<>();
    private List<String> history = new ArrayList<>();
    private final String originalFilePath = "slang.txt";
    private final String workingFilePath = "new_slang.txt";

    public SlangDictionaryApp() {
        initUI();
        loadFromFile();
    }

    private void initUI() {
        setTitle("Slang Dictionary");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JLabel headerLabel = new JLabel("Slang Dictionary", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 32));
        add(headerLabel, BorderLayout.NORTH);

        // Buttons for functions
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 3, 10, 10)); // 3 rows, 3 columns, with spacing

        JButton searchSlangBtn = new JButton("Search by Slang");
        JButton searchDefinitionBtn = new JButton("Search by Definition");
        JButton addSlangBtn = new JButton("Add Slang");
        JButton editSlangBtn = new JButton("Edit Slang");
        JButton deleteSlangBtn = new JButton("Delete Slang");
        JButton viewHistoryBtn = new JButton("View History");
        JButton randomSlangBtn = new JButton("Random Slang");
        JButton quizBtn = new JButton("Quiz");
        JButton resetBtn = new JButton("Reset to Original");

        // Add buttons to panel
        buttonPanel.add(searchSlangBtn);
        buttonPanel.add(searchDefinitionBtn);
        buttonPanel.add(addSlangBtn);
        buttonPanel.add(editSlangBtn);
        buttonPanel.add(deleteSlangBtn);
        buttonPanel.add(viewHistoryBtn);
        buttonPanel.add(randomSlangBtn);
        buttonPanel.add(quizBtn);
        buttonPanel.add(resetBtn);

        add(buttonPanel, BorderLayout.CENTER);

        // Button Actions
        searchSlangBtn.addActionListener(e -> searchSlangWord());
        searchDefinitionBtn.addActionListener(e -> searchByDefinition());
        addSlangBtn.addActionListener(e -> addNewSlang());
        editSlangBtn.addActionListener(e -> editSlangWord());
        deleteSlangBtn.addActionListener(e -> deleteSlangWord());
        viewHistoryBtn.addActionListener(e -> viewHistory());
        randomSlangBtn.addActionListener(e -> showRandomSlang());
        quizBtn.addActionListener(e -> startQuiz());
        resetBtn.addActionListener(e -> resetSlangWords());
    }

    private void loadFromFile() {
        File newSlangFile = new File(workingFilePath);
        File originalFile = new File(originalFilePath);

        try {
            if (!newSlangFile.exists()) {
                // Create new_slang.txt by copying slang.txt
                copyFile(originalFile, newSlangFile);
            }

            slangMap.clear();
            try (BufferedReader br = new BufferedReader(new FileReader(newSlangFile))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split("`", 2);
                    if (parts.length == 2) {
                        slangMap.put(parts[0], parts[1]);
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading file: " + e.getMessage());
        }
    }
    private void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(workingFilePath))) {
            for (Map.Entry<String, String> entry : slangMap.entrySet()) {
                bw.write(entry.getKey() + "`" + entry.getValue());
                bw.newLine();
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error saving file: " + e.getMessage());
        }
    }

    private void copyFile(File source, File destination) throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(source));
             BufferedWriter bw = new BufferedWriter(new FileWriter(destination))) {
            String line;
            while ((line = br.readLine()) != null) {
                bw.write(line);
                bw.newLine();
            }
        }
    }
    private void searchSlangWord() {
        String slang = JOptionPane.showInputDialog(this, "Enter a slang word:");
        if (slang == null || slang.isEmpty()) return;

        history.add(slang);
        String definition = slangMap.getOrDefault(slang, "Not Found");
        JOptionPane.showMessageDialog(this, slang + " means: " + definition);
    }

    private void searchByDefinition() {
        String keyword = JOptionPane.showInputDialog(this, "Enter a definition keyword:");
        if (keyword == null || keyword.isEmpty()) return;

        List<String> results = new ArrayList<>();
        for (Map.Entry<String, String> entry : slangMap.entrySet()) {
            if (entry.getValue().toLowerCase().contains(keyword.toLowerCase())) {
                results.add(entry.getKey() + " = " + entry.getValue());
            }
        }

        if (results.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No matches found!");
            return;
        }

        JList<String> resultList = new JList<>(results.toArray(new String[0]));
        JScrollPane scrollPane = new JScrollPane(resultList);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        JOptionPane.showMessageDialog(this, scrollPane, "Search Results", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void addNewSlang() {
        String slang = JOptionPane.showInputDialog(this, "Enter a new slang word:");
        if (slang == null || slang.isEmpty()) return;

        if (slangMap.containsKey(slang)) {
            String[] options = {"Overwrite", "Duplicate", "Cancel"};
            int response = JOptionPane.showOptionDialog(
                    this,
                    "Slang word already exists. What would you like to do?",
                    "Duplicate or Overwrite?",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );

            if (response == 0) { // Overwrite
                String definition = JOptionPane.showInputDialog(this, "Enter the new definition:");
                if (definition == null || definition.isEmpty()) return;
                slangMap.put(slang, definition);
                saveToFile();
                JOptionPane.showMessageDialog(this, "Slang word overwritten successfully!");
            } else if (response == 1) { // Duplicate
                String newSlang = JOptionPane.showInputDialog(this, "Enter a new slang word for duplication:");
                if (newSlang == null || newSlang.isEmpty()) return;

                if (slangMap.containsKey(newSlang)) {
                    JOptionPane.showMessageDialog(this, "Duplicate slang word already exists! Operation canceled.");
                    return;
                }

                String definition = slangMap.get(slang); // Keep the same definition as the original
                slangMap.put(newSlang, definition);
                saveToFile();
                JOptionPane.showMessageDialog(this, "Duplicate slang word added successfully!");
            }
        } else {
            String definition = JOptionPane.showInputDialog(this, "Enter the definition:");
            if (definition == null || definition.isEmpty()) return;

            slangMap.put(slang, definition);
            saveToFile();
            JOptionPane.showMessageDialog(this, "Slang word added successfully!");
        }
    }
    
    private void editSlangWord() {
        String slang = JOptionPane.showInputDialog(this, "Enter the slang word to edit:");
        if (slang == null || slang.isEmpty() || !slangMap.containsKey(slang)) {
            JOptionPane.showMessageDialog(this, "Slang not found!");
            return;
        }

        String definition = JOptionPane.showInputDialog(this, "Enter the new definition:");
        if (definition == null || definition.isEmpty()) return;

        slangMap.put(slang, definition);
        saveToFile();
        JOptionPane.showMessageDialog(this, "Slang updated successfully!");
    }

    private void deleteSlangWord() {
        String slang = JOptionPane.showInputDialog(this, "Enter the slang word to delete:");
        if (slang == null || slang.isEmpty() || !slangMap.containsKey(slang)) {
            JOptionPane.showMessageDialog(this, "Slang not found!");
            return;
        }

        int response = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete?");
        if (response == JOptionPane.YES_OPTION) {
            slangMap.remove(slang);
            saveToFile();
            JOptionPane.showMessageDialog(this, "Slang deleted successfully!");
        }
    }

    private void viewHistory() {
        String message = history.isEmpty() ? "No history available!" : String.join("\n", history);
        JOptionPane.showMessageDialog(this, message);
    }

    private void showRandomSlang() {
        List<String> keys = new ArrayList<>(slangMap.keySet());
        if (keys.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No slang words available!");
            return;
        }

        String randomSlang = keys.get(new Random().nextInt(keys.size()));
        JOptionPane.showMessageDialog(this, randomSlang + " = " + slangMap.get(randomSlang));
    }

    private void startQuiz() {
        List<String> keys = new ArrayList<>(slangMap.keySet());
        if (keys.size() < 4) {
            JOptionPane.showMessageDialog(this, "Not enough slang words for a quiz!");
            return;
        }

        boolean askForSlang = new Random().nextBoolean();
        String correctSlang = keys.get(new Random().nextInt(keys.size()));
        String correctDefinition = slangMap.get(correctSlang);

        List<String> options = new ArrayList<>(keys);
        Collections.shuffle(options);
        options = options.subList(0, 4);

        if (askForSlang) {
            if (!options.contains(correctSlang)) {
                options.set(new Random().nextInt(4), correctSlang);
            }
            String[] choices = options.toArray(new String[0]);
            String selected = (String) JOptionPane.showInputDialog(
                    this, "Which slang word matches this definition?\n" + correctDefinition,
                    "Quiz", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]
            );

            if (correctSlang.equals(selected)) {
                JOptionPane.showMessageDialog(this, "Correct!");
            } else {
                JOptionPane.showMessageDialog(this, "Wrong! The correct answer is: " + correctSlang);
            }
        } else {
            if (!options.contains(correctSlang)) {
                options.set(new Random().nextInt(4), correctSlang);
            }
            String[] choices = options.stream().map(slangMap::get).toArray(String[]::new);
            String selected = (String) JOptionPane.showInputDialog(
                    this, "What does the slang word \"" + correctSlang + "\" mean?",
                    "Quiz", JOptionPane.QUESTION_MESSAGE, null, choices, choices[0]
            );

            if (correctDefinition.equals(selected)) {
                JOptionPane.showMessageDialog(this, "Correct!");
            } else {
                JOptionPane.showMessageDialog(this, "Wrong! The correct answer is: " + correctDefinition);
            }
        }
    }

    private void resetSlangWords() {
        int response = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to reset the slang words to the original list? This will overwrite all changes.",
                "Confirm Reset",
                JOptionPane.YES_NO_OPTION
        );

        if (response == JOptionPane.YES_OPTION) {
            try {
                File originalFile = new File(originalFilePath);
                File newSlangFile = new File(workingFilePath);

                copyFile(originalFile, newSlangFile); // Overwrite new_slang.txt with slang.txt
                slangMap.clear();
                loadFromFile(); // Reload slangMap from the new_slang.txt
                JOptionPane.showMessageDialog(this, "Slang words reset to original list successfully!");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error resetting slang words: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SlangDictionaryApp app = new SlangDictionaryApp();
            app.setVisible(true);
        });
    }
}
