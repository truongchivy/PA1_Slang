import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.*;

public class SlangDictionaryApp extends JFrame {
    private HashMap<String, List<String>> slangMap = new HashMap<>(); // Stores slang and multiple definitions
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

        String[] buttonTexts = {
            "Search by Slang", "Search by Definition", "Add Slang",
            "Edit Slang", "Delete Slang", "View History",
            "Random Slang", "Quiz", "Reset to Original"
        };

        JButton[] buttons = new JButton[buttonTexts.length];
        for (int i = 0; i < buttonTexts.length; i++) {
            buttons[i] = new JButton(buttonTexts[i]);
            buttons[i].setFont(new Font("Arial", Font.PLAIN, 20));
            buttonPanel.add(buttons[i]);
        }

        add(buttonPanel, BorderLayout.CENTER);

        // Button Actions
        buttons[0].addActionListener(e -> searchSlangWord());
        buttons[1].addActionListener(e -> searchByDefinition());
        buttons[2].addActionListener(e -> addNewSlang());
        buttons[3].addActionListener(e -> editSlangWord());
        buttons[4].addActionListener(e -> deleteSlangWord());
        buttons[5].addActionListener(e -> viewHistory());
        buttons[6].addActionListener(e -> showRandomSlang());
        buttons[7].addActionListener(e -> startQuiz());
        buttons[8].addActionListener(e -> resetSlangWords());
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
                        slangMap.computeIfAbsent(parts[0], k -> new ArrayList<>()).add(parts[1]);
                    }
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error loading file: " + e.getMessage());
        }
    }


    private void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(workingFilePath))) {
            for (Map.Entry<String, List<String>> entry : slangMap.entrySet()) {
                for (String definition : entry.getValue()) {
                    bw.write(entry.getKey() + "`" + definition);
                    bw.newLine();
                }
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
        // Search case-insensitively
        List<String> definitions = null;
        for (String key : slangMap.keySet()) {
            if (key.equalsIgnoreCase(slang)) {
                definitions = slangMap.get(key);
                break;
            }
        }
    
        if (definitions == null || definitions.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No slang found!");
            return;
        }
    
        // Show definitions split by '|'
        List<String> formattedDefinitions = new ArrayList<>();
        for (String definition : definitions) {
            formattedDefinitions.addAll(Arrays.asList(definition.split("\\| ")));
        }
    
        JList<String> resultList = new JList<>(formattedDefinitions.toArray(new String[0]));
        JScrollPane scrollPane = new JScrollPane(resultList);
        scrollPane.setPreferredSize(new Dimension(400, 300));
        JOptionPane.showMessageDialog(this, scrollPane, "Search Results", JOptionPane.INFORMATION_MESSAGE);
    }    


    private void searchByDefinition() {
        String keyword = JOptionPane.showInputDialog(this, "Enter a definition keyword:");
        if (keyword == null || keyword.isEmpty()) return;

        List<String> results = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : slangMap.entrySet()) {
            for (String definition : entry.getValue()) {
                if (definition.toLowerCase().contains(keyword.toLowerCase())) {
                    results.add(entry.getKey() + " = " + definition);
                }
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
    
        String definition = JOptionPane.showInputDialog(this, "Enter the definition:");
        if (definition == null || definition.isEmpty()) return;
    
        // Search for existing slang (case-insensitive)
        String existingSlang = null;
        for (String key : slangMap.keySet()) {
            if (key.equalsIgnoreCase(slang)) {
                existingSlang = key;
                break;
            }
        }
    
        if (existingSlang != null) {
            String[] options = {"Overwrite", "Add to Existing"};
            int response = JOptionPane.showOptionDialog(
                    this,
                    "Slang word already exists (as " + existingSlang + "). What would you like to do?",
                    "Duplicate or Overwrite?",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    options,
                    options[0]
            );
    
            if (response == 0) { // Overwrite
                slangMap.put(existingSlang, new ArrayList<>(Collections.singletonList(definition)));
            } else if (response == 1) { // Add to Existing
                List<String> existingDefinitions = slangMap.get(existingSlang);
                String current = existingDefinitions.get(0); // Original format with '|'
                if (!current.contains(definition)) { // Avoid duplicate meanings
                    current += "| " + definition;
                    existingDefinitions.set(0, current); // Update the definition
                }
            }
        } else {
            slangMap.put(slang, new ArrayList<>(Collections.singletonList(definition)));
        }
    
        saveToFile();
        JOptionPane.showMessageDialog(this, "Slang added successfully!");
    }        


    private void editSlangWord() {
        String slang = JOptionPane.showInputDialog(this, "Enter the slang word to edit:");
        if (slang == null || slang.isEmpty()) return;
    
        // Search for existing slang (case-insensitive)
        String existingSlang = null;
        for (String key : slangMap.keySet()) {
            if (key.equalsIgnoreCase(slang)) {
                existingSlang = key;
                break;
            }
        }
    
        if (existingSlang == null) {
            JOptionPane.showMessageDialog(this, "Slang not found!");
            return;
        }
    
        String newDefinition = JOptionPane.showInputDialog(this, "Enter the new definition:");
        if (newDefinition == null || newDefinition.isEmpty()) return;
    
        slangMap.put(existingSlang, new ArrayList<>(Collections.singletonList(newDefinition)));
        saveToFile();
        JOptionPane.showMessageDialog(this, "Slang updated successfully!");
    }
    

    private void deleteSlangWord() {
        String slang = JOptionPane.showInputDialog(this, "Enter the slang word to delete:");
        if (slang == null || slang.isEmpty()) return;
    
        // Search for existing slang (case-insensitive)
        String existingSlang = null;
        for (String key : slangMap.keySet()) {
            if (key.equalsIgnoreCase(slang)) {
                existingSlang = key;
                break;
            }
        }
    
        if (existingSlang == null) {
            JOptionPane.showMessageDialog(this, "Slang not found!");
            return;
        }
    
        int response = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete the slang: " + existingSlang + "?");
        if (response == JOptionPane.YES_OPTION) {
            slangMap.remove(existingSlang);
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
        List<String> definitions = slangMap.get(randomSlang);
        String message = randomSlang + " = " + String.join(", ", definitions);
        JOptionPane.showMessageDialog(this, message);
    }


    private void startQuiz() {
        List<String> keys = new ArrayList<>(slangMap.keySet());
        if (keys.size() < 4) {
            JOptionPane.showMessageDialog(this, "Not enough slang words for a quiz!");
            return;
        }

        String correctSlang = keys.get(new Random().nextInt(keys.size()));
        List<String> definitions = slangMap.get(correctSlang);
        String correctDefinition = definitions.get(new Random().nextInt(definitions.size()));

        List<String> options = new ArrayList<>(keys);
        Collections.shuffle(options);
        options = options.subList(0, 4);

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

                copyFile(originalFile, newSlangFile);
                slangMap.clear();
                loadFromFile();
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
