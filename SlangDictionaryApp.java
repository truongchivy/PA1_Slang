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

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");

        JMenuItem searchSlang = new JMenuItem("Search by Slang");
        JMenuItem searchDefinition = new JMenuItem("Search by Definition");
        JMenuItem addSlang = new JMenuItem("Add Slang");
        JMenuItem editSlang = new JMenuItem("Edit Slang");
        JMenuItem deleteSlang = new JMenuItem("Delete Slang");
        JMenuItem historyView = new JMenuItem("View History");
        JMenuItem randomSlang = new JMenuItem("Random Slang");
        JMenuItem quiz = new JMenuItem("Quiz");
        JMenuItem resetSlang = new JMenuItem("Reset to Original");

        menu.add(searchSlang);
        menu.add(searchDefinition);
        menu.add(addSlang);
        menu.add(editSlang);
        menu.add(deleteSlang);
        menu.add(historyView);
        menu.add(randomSlang);
        menu.add(quiz);
        menu.add(resetSlang);

        menuBar.add(menu);
        setJMenuBar(menuBar);

        JPanel panel = new JPanel(new BorderLayout());
        JLabel welcomeLabel = new JLabel("Welcome to the Slang Dictionary!", SwingConstants.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(welcomeLabel, BorderLayout.CENTER);
        add(panel);

        searchSlang.addActionListener(e -> searchSlangWord());
        searchDefinition.addActionListener(e -> searchByDefinition());
        addSlang.addActionListener(e -> addNewSlang());
        editSlang.addActionListener(e -> editSlangWord());
        deleteSlang.addActionListener(e -> deleteSlangWord());
        historyView.addActionListener(e -> viewHistory());
        randomSlang.addActionListener(e -> showRandomSlang());
        quiz.addActionListener(e -> startQuiz());
        resetSlang.addActionListener(e -> resetSlangWords());
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
}
