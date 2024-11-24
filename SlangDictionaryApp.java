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
}
