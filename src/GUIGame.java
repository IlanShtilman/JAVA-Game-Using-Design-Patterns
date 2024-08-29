import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.io.File;
import java.util.*;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import javax.swing.table.DefaultTableModel;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

//Import from our enums
import game.States.*;
import game.arena.*;
import game.competition.*;
import game.entities.sportsman.*;
import game.enums.Discipline;
import game.enums.Gender;
import game.enums.League;
import game.enums.SnowSurface;
import game.enums.WeatherCondition;
import utilities.Point;


public class GUIGame extends Component implements PropertyChangeListener {
    // GUI Components
    private JPanel GUIGamePanel;
    private JLayeredPane leftPanel;
    private JPanel rightPanel, arenaPanel, competitorsPanel;
    private JTextField arenaLengthField, maxCompetitorsField, nameField, ageField, maxSpeedField, accelerationField;
    private JComboBox<SnowSurface> snowSurfaceCombo;
    private JComboBox<WeatherCondition> weatherConditionCombo;
    private JComboBox<String> competitionTypeCombo;
    private JComboBox<Discipline> disciplineCombo;
    private JComboBox<League> leagueCombo;
    private JComboBox<Gender> genderCombo;
    private JButton buildArenaButton, createCompetitionButton, addCompetitorButton, startCompetitionButton, showInfoButton, copyCompetitorButton,createDefaultRaceButton, customizeCompetitorButton;

    //Prototype Buttons
    private JButton colorChooserButton;
    private Color currentColor = Color.WHITE;

    //Factory-method
    private JComboBox<String> arenaTypeCombo;
    private ArenaFactory arenaFactory;

    // Game state
    private IArena arena;
    private Competition competition;
    private boolean competitionStarted = false;
    private boolean arenaBuilt = false;
    private boolean competitionCreated = false;

    // UI state
    private ArrayList<JLabel> competitorLabels;
    private int nextIconX = 5; // Starting X position
    private final int ICON_WIDTH = 32;
    private final int ICON_HEIGHT = 32;
    private JDialog infoDialog;
    private DefaultTableModel tableModel;

    //Thread Pool data
    public final static int THREAD_COUNT = 8;
    private ExecutorService pool;

    //Decorator pattern
    private IWinterSportsman currentCompetitor;

    //State pattern
    private List<AlertStateContext> competitorStates;

    public GUIGame() {
        initializeComponents();
        setupLayout();
        addListeners();
        tableModel = new DefaultTableModel(new String[]{"Name", "Speed", "Max speed", "Location", "Finished"}, 0);
        this.competitorStates = new ArrayList<>();
        pool = Executors.newFixedThreadPool(THREAD_COUNT);
    }

    //Methods For Initialization
    private void initializeComponents() {
        GUIGamePanel = new JPanel(new BorderLayout());
        leftPanel = new JLayeredPane();
        rightPanel = new JPanel();

        arenaLengthField = new JTextField(10);
        arenaTypeCombo = new JComboBox<>(new String[]{"Winter", "Summer"});
        arenaTypeCombo.addActionListener(e -> updateArenaFactory());
        snowSurfaceCombo = new JComboBox<>(SnowSurface.values());
        weatherConditionCombo = new JComboBox<>(WeatherCondition.values());
        buildArenaButton = new JButton("Build Arena");
        buildArenaButton.addActionListener(e -> buildArena());
        createDefaultRaceButton = new JButton("Create Default Race");
        createDefaultRaceButton.addActionListener(e -> createDefaultRace());

        competitionTypeCombo = new JComboBox<>(new String[]{"Ski","Snowboard" });
        maxCompetitorsField = new JTextField(10);
        disciplineCombo = new JComboBox<>(Discipline.values());
        leagueCombo = new JComboBox<>(League.values());
        genderCombo = new JComboBox<>(Gender.values());
        createCompetitionButton = new JButton("Create Competition");
        createCompetitionButton.addActionListener(e -> buildCompetition());
        nameField = new JTextField(10);
        ageField = new JTextField(5);
        maxSpeedField = new JTextField(5);
        accelerationField = new JTextField(5);
        colorChooserButton = new JButton("Choose Color");
        colorChooserButton.addActionListener(e -> chooseColor());
        addCompetitorButton = new JButton("Add Competitor");
        addCompetitorButton.addActionListener(e -> addCompetitorToCompetition());
        copyCompetitorButton = new JButton("Copy Competitor");
        copyCompetitorButton.addActionListener(e -> copyCompetitor());
        customizeCompetitorButton = new JButton("Customize Competitor");
        customizeCompetitorButton.addActionListener(e -> customizeCompetitor());

        startCompetitionButton = new JButton("Start Competition");
        showInfoButton = new JButton("Show Info");

        competitorLabels = new ArrayList<>();

        arenaPanel = new JPanel();
        arenaPanel.setOpaque(false);
        arenaPanel.setBounds(0, 0, 1000, 700);
        competitorsPanel = new JPanel();
        competitorsPanel.setLayout(null);  // Use null layout for absolute positioning
        competitorsPanel.setOpaque(false);
        competitorsPanel.setBounds(0, 0, 1000, 700);

        leftPanel.add(arenaPanel, Integer.valueOf(0));  // Background layer
        leftPanel.add(competitorsPanel, Integer.valueOf(1));
    }

    private void setupLayout() {
        leftPanel.setPreferredSize(new Dimension(600, 600));  // Set a preferred size for the arena display
        // Right panel
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));

        // BUILD ARENA section
        JPanel buildArenaPanel = createSection("BUILD ARENA",
                new JLabel("Arena Type"), arenaTypeCombo,
                new JLabel("Arena length"), arenaLengthField,
                new JLabel("Snow surface"), snowSurfaceCombo,
                new JLabel("Weather condition"), weatherConditionCombo,
                buildArenaButton,createDefaultRaceButton);

        // CREATE COMPETITION section
        JPanel createCompetitionPanel = createSection("CREATE COMPETITION",
                new JLabel("Choose competition"), competitionTypeCombo,
                new JLabel("Max competitors number"), maxCompetitorsField,
                new JLabel("Discipline"), disciplineCombo,
                new JLabel("League"), leagueCombo,
                new JLabel("Gender"), genderCombo,
                createCompetitionButton);

        // ADD COMPETITOR section
        JPanel addCompetitorPanel = createSection("ADD COMPETITOR",
                new JLabel("Name"), nameField,
                new JLabel("Age"), ageField,
                new JLabel("Max speed"), maxSpeedField,
                new JLabel("Acceleration"), accelerationField,
                new JLabel("Color"), colorChooserButton,
                addCompetitorButton, copyCompetitorButton,customizeCompetitorButton);

        // Control buttons panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        controlPanel.add(startCompetitionButton);
        controlPanel.add(showInfoButton);

        // Add all sections to the right panel
        rightPanel.add(buildArenaPanel);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(createCompetitionPanel);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(addCompetitorPanel);
        rightPanel.add(Box.createVerticalStrut(10));
        rightPanel.add(controlPanel);

        // Main panel layout
        GUIGamePanel.add(leftPanel, BorderLayout.CENTER);
        GUIGamePanel.add(rightPanel, BorderLayout.EAST);
    }

    private void addListeners() {
        buildArenaButton.removeActionListener(buildArenaButton.getActionListeners()[0]);
        createCompetitionButton.removeActionListener(createCompetitionButton.getActionListeners()[0]);
        addCompetitorButton.removeActionListener(addCompetitorButton.getActionListeners()[0]);

        buildArenaButton.addActionListener(e -> buildArena());
        createCompetitionButton.addActionListener(e -> buildCompetition());
        addCompetitorButton.addActionListener(e -> addCompetitorToCompetition());
        startCompetitionButton.addActionListener(e -> startCompetition());
        showInfoButton.addActionListener(e -> showCompetitorInfo());

        // Add document listeners to text fields for real-time validation
        arenaLengthField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                validateArenaText();
            }

            public void removeUpdate(DocumentEvent e) {
                validateArenaText();
            }

            public void insertUpdate(DocumentEvent e) {
                validateArenaText();
            }
        });

        maxCompetitorsField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                validateCompetitionText();
            }

            public void removeUpdate(DocumentEvent e) {
                validateCompetitionText();
            }

            public void insertUpdate(DocumentEvent e) {
                validateCompetitionText();
            }
        });
    }

    private JPanel createSection(String title, Component... components) {
    JPanel panel = new JPanel();
    panel.setLayout(new GridBagLayout());
    panel.setBorder(BorderFactory.createTitledBorder(title));

    GridBagConstraints gbc = new GridBagConstraints();
    gbc.anchor = GridBagConstraints.WEST;
    gbc.insets = new Insets(2, 2, 2, 2);

    int row = 0;
    for (int i = 0; i < components.length; i += 2) {
        if (components[i] != null) {
            gbc.gridx = 0;
            gbc.gridy = row;
            panel.add(components[i], gbc);

            if (i + 1 < components.length && components[i + 1] != null) {
                gbc.gridx = 1;
                gbc.gridy = row;
                panel.add(components[i + 1], gbc);
            }
            row++;
        }
    }

    // If there's an odd number of components, add the last one spanning both columns
    if (components.length % 2 != 0 && components[components.length - 1] != null) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        panel.add(components[components.length - 1], gbc);
    }

    return panel;
}

    //A method for updating at each part of the game the buttons (to handle cases we don't want the user will enter at)
    private void updateButtonStates() {
    boolean hasArena = arena != null;
    boolean hasCompetition = competition != null;
    boolean hasActiveCompetitors = hasCompetition && !competition.getActiveCompetitors().isEmpty();

    buildArenaButton.setEnabled(!competitionStarted);
    createDefaultRaceButton.setEnabled(!competitionStarted && !arenaBuilt);
    createCompetitionButton.setEnabled(hasArena && !competitionStarted && !competitionCreated);
    addCompetitorButton.setEnabled(hasCompetition && !competitionStarted);
    startCompetitionButton.setEnabled(hasActiveCompetitors && !competitionStarted);
    showInfoButton.setEnabled(hasCompetition);
    customizeCompetitorButton.setEnabled(hasActiveCompetitors && !competitionStarted);

    // Enable/disable input fields for arena creation
    boolean canEditArena = !arenaBuilt && !competitionStarted;
    arenaLengthField.setEnabled(canEditArena);
    arenaTypeCombo.setEnabled(canEditArena);
    snowSurfaceCombo.setEnabled(canEditArena);
    weatherConditionCombo.setEnabled(canEditArena);

    // Disable Copy Competitor button when competition has started
    copyCompetitorButton.setEnabled(hasActiveCompetitors && !competitionStarted);

    // Enable/disable input fields for competition creation
    boolean canEditCompetition = hasArena && !competitionCreated && !competitionStarted;
    competitionTypeCombo.setEnabled(canEditCompetition);
    maxCompetitorsField.setEnabled(canEditCompetition);
    disciplineCombo.setEnabled(canEditCompetition);
    leagueCombo.setEnabled(canEditCompetition);
    genderCombo.setEnabled(canEditCompetition);

    // Enable/disable input fields for adding competitors
    boolean canAddCompetitors = hasCompetition && !competitionStarted;
    nameField.setEnabled(canAddCompetitors);
    ageField.setEnabled(canAddCompetitors);
    maxSpeedField.setEnabled(canAddCompetitors);
    accelerationField.setEnabled(canAddCompetitors);
    colorChooserButton.setEnabled(canAddCompetitors);
}

    //Methods For Arena
    private void buildArena() {
    if (competitionStarted) {
        showErrorMessage("Cannot build a new arena during an active competition.");
        return;
    }

    // Reset the game state before building a new arena, we calling it here because the user can create another games after the first
    resetGameState();

    try {
        int length = Integer.parseInt(arenaLengthField.getText());
        SnowSurface surface = (SnowSurface) snowSurfaceCombo.getSelectedItem();
        WeatherCondition weather = (WeatherCondition) weatherConditionCombo.getSelectedItem();

        updateArenaFactory(); // Using the Factory-Method
        arena = arenaFactory.createArena(length, surface, weather);

        if (arena instanceof WinterArena) {
            DisplayArena(length, weather);
            arenaBuilt = true;
            updateButtonStates();
            JOptionPane.showMessageDialog(GUIGamePanel, "Arena built successfully!");
        } else {
            throw new UnsupportedOperationException("Only Winter Arena is supported for this competition.");
        }
    } catch (NumberFormatException ex) {
        showErrorMessage("Invalid arena length!");
    } catch (UnsupportedOperationException ex) {
        showErrorMessage(ex.getMessage());
    } catch (Exception ex) {
        showErrorMessage("Error creating arena: " + ex.getMessage());
    }
}

    //Function for get the correct images for our arena annd add them to our panel
    private void DisplayArena(int length, WeatherCondition weather) {
        String condition = "";
        switch (weather) {
            case SUNNY:
                condition = "Sunny";
                break;
            case CLOUDY:
                condition = "Cloudy";
                break;
            case STORMY:
                condition = "Stormy";
                break;
        }

        String imagePath = "src/icons/" + condition + ".jpg";
        File imageFile = new File(imagePath);

        if (!imageFile.exists()) {
            System.out.println("Image file not found: " + imagePath);
            return;
        }

        ImageIcon icon = new ImageIcon(imagePath);

        int panelWidth = arenaPanel.getWidth();
        int panelHeight = arenaPanel.getHeight();

        Image scaledImage = icon.getImage().getScaledInstance(panelWidth, panelHeight, Image.SCALE_SMOOTH);
        JLabel backgroundLabel = new JLabel(new ImageIcon(scaledImage));

        arenaPanel.removeAll();
        arenaPanel.setLayout(new BorderLayout());
        arenaPanel.add(backgroundLabel, BorderLayout.CENTER);
        arenaPanel.revalidate();
        arenaPanel.repaint();
    }

    //A function that checking for us if the length of our arena is valid
    private void validateArenaText() {
        try {
            int length = Integer.parseInt(arenaLengthField.getText());
            if (length >= 700 && length <= 900) {
                arenaLengthField.setBackground(Color.WHITE);
                buildArenaButton.setEnabled(true);
            } else {
                arenaLengthField.setBackground(Color.PINK);
                buildArenaButton.setEnabled(false);
            }
        } catch (NumberFormatException ex) {
            arenaLengthField.setBackground(Color.PINK);
            buildArenaButton.setEnabled(false);
        }
    }

    private void updateArenaFactory() {
        String selectedType = (String) arenaTypeCombo.getSelectedItem();
        if ("Winter".equals(selectedType)) {
            arenaFactory = new WinterArenaFactory();
        } else if ("Summer".equals(selectedType)) {
            arenaFactory = new SummerArenaFactory();
        }
    }

    //The rest game -when the user want to create the arena at every place at the game he can, there only 1 rule - the competition hasn't started yet
    private void resetGameState() {
    boolean buildingNewArena = !arenaBuilt;

    arena = null;
    competition = null;
    clearCompetitorIcons();
    arenaBuilt = false;
    competitionCreated = false;
    competitionStarted = false;
    competitorStates.clear();

    // Reset UI elements
    if (!buildingNewArena) {
        arenaLengthField.setText("");
        arenaTypeCombo.setSelectedIndex(0);
        snowSurfaceCombo.setSelectedIndex(0);
        weatherConditionCombo.setSelectedIndex(0);
    }
    competitionTypeCombo.setSelectedIndex(0);
    maxCompetitorsField.setText("");
    disciplineCombo.setSelectedIndex(0);
    leagueCombo.setSelectedIndex(0);
    genderCombo.setSelectedIndex(0);
    nameField.setText("");
    ageField.setText("");
    maxSpeedField.setText("");
    accelerationField.setText("");

    // Update UI
    updateCompetitorInfo();
    competitorsPanel.repaint();
    updateButtonStates();
}

    //Methods For Competition

    //Here we used Dynamic class loader for building our competition
    private void buildCompetition() {
        if (!arenaBuilt) {
            showErrorMessage("Please build an arena first.");
            return;
        }

        String max = maxCompetitorsField.getText();
        int maxPlayers = Integer.parseInt(max);
        String selectedCompetition = (String) competitionTypeCombo.getSelectedItem();
        Discipline discipline = (Discipline) disciplineCombo.getSelectedItem();
        League league = (League) leagueCombo.getSelectedItem();
        Gender gender = (Gender) genderCombo.getSelectedItem();

        try {
            Class<?> cls;
            ClassLoader cl = ClassLoader.getSystemClassLoader();
            Constructor<?> ctor;

            if (selectedCompetition.equals("Snowboard")) {
                cls = cl.loadClass("game.competition.SnowboardCompetition");
            } else if (selectedCompetition.equals("Ski")) {
                cls = cl.loadClass("game.competition.SkiCompetition");
            } else {
                showErrorMessage("Selected competition type not supported.");
                return;
            }

            ctor = cls.getConstructor(WinterArena.class, int.class, Discipline.class, League.class, Gender.class);
            competition = (Competition) ctor.newInstance(arena, maxPlayers, discipline, league, gender);
            competitorStates.clear();

            competitionCreated = true;
            updateButtonStates();
            JOptionPane.showMessageDialog(GUIGamePanel, "Competition created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        } catch (ClassNotFoundException ex) {
            showErrorMessage("Class not found for the selected competition type.");
            ex.printStackTrace();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                 InvocationTargetException ex) {
            showErrorMessage("Error creating competition instance.");
            ex.printStackTrace();
        } catch (NumberFormatException ex) {
            showErrorMessage("Invalid number format for max competitors.");
        } catch (Exception ex) {
            showErrorMessage("An unexpected error occurred while building the competition.");
            ex.printStackTrace();
        }
    }

    //Same as the arena, checking if the number of our competitors are valid
    private void validateCompetitionText() {
        try {
            int number = Integer.parseInt(maxCompetitorsField.getText());
            if (number >= 1 && number <= 20) {
                maxCompetitorsField.setBackground(Color.WHITE);
                createCompetitionButton.setEnabled(true);
            } else {
                maxCompetitorsField.setBackground(Color.PINK);
                createCompetitionButton.setEnabled(false);
            }
        } catch (NumberFormatException ex) {
            maxCompetitorsField.setBackground(Color.PINK);
            createCompetitionButton.setEnabled(false);
        }
    }

    //Methods For Competitor
    //The main idea here - Create the Skier/Snowboarder, add them to the competition, add their state, their icon.
    //We're doing that with class loader same as we did with competition
    private void addCompetitorToCompetition() {
    if (!competitionCreated) {
        showErrorMessage("Please create a competition first.");
        return;
    }

    if (competitionStarted) {
        showErrorMessage("Cannot add competitors once the competition has started.");
        return;
    }

    if (competition.getActiveCompetitors().size() >= competition.getMaxCompetitors()) {
        showErrorMessage("Maximum number of competitors reached.");
        return;
    }

    String name = nameField.getText();
    if (!CheckInput(name)) {
        showErrorMessage("Please enter a valid name for the competitor.");
        return;
    }

    Gender competitionGender = ((WinterCompetition) competition).getGender();

    try {
        int age = Integer.parseInt(ageField.getText());
        double maxSpeed = Double.parseDouble(maxSpeedField.getText());
        double acceleration = Double.parseDouble(accelerationField.getText());
        Gender gender = (Gender) genderCombo.getSelectedItem();
        Discipline discipline = (Discipline) disciplineCombo.getSelectedItem();
        String selectedCompetition = (String) competitionTypeCombo.getSelectedItem();
        League league = ((WinterCompetition) competition).getLeague();

        if (!league.isInLeague(age)) {
            showErrorMessage("Competitor's age does not match the selected league.");
            return;
        }

        if (gender != competitionGender) {
        showErrorMessage("Competitor's gender must match the competition gender: " + competitionGender);
        return;
        }

        WinterSportsman competitor = null;
        Class<?> cls;
        ClassLoader cl = ClassLoader.getSystemClassLoader();
        Constructor<?> ctor;

        if (selectedCompetition.equals("Ski")) {
            cls = cl.loadClass("game.entities.sportsman.Skier");
            ctor = cls.getConstructor(String.class, double.class, Gender.class, double.class, double.class, Discipline.class);
            competitor = (WinterSportsman) ctor.newInstance(name, age, gender, acceleration, maxSpeed, discipline);
        } else if (selectedCompetition.equals("Snowboard")) {
            cls = cl.loadClass("game.entities.sportsman.SnowBorder");
            ctor = cls.getConstructor(String.class, double.class, Gender.class, double.class, double.class, Discipline.class);
            competitor = (WinterSportsman) ctor.newInstance(name, age, gender, acceleration, maxSpeed, discipline);
        } else {
            showErrorMessage("Selected competition type not supported yet!");
            return;
        }

        competitor.setArenaLength(arena.getLength());
        int newOrder = competition.getActiveCompetitors().size() + competition.getFinishedCompetitors().size();
        competitor.setOrder(newOrder);
        competitor.setColor(currentColor);
        competitor.setLocation(new Point(0, 0));

        competition.addCompetitor(competitor);
        competitor.addPropertyChangeListener(this);

         // Create and add AlertStateContext for the new competitor
        AlertStateContext stateContext = new AlertStateContext(competitor.getNumber());
        competitorStates.add(stateContext);

        int xPosition = nextIconX;

        DisplayCompetitor(selectedCompetition, gender, xPosition, 10, currentColor);
        nextIconX += ICON_WIDTH + 10;

        JOptionPane.showMessageDialog(
                GUIGamePanel,
                "Competitor successfully added: " + competitor.getName(),
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );

        // Clear input fields
        clearInputFields();
        currentCompetitor = (IWinterSportsman) competitor;
        customizeCompetitorButton.setEnabled(true);

    } catch (NumberFormatException ex) {
        showErrorMessage("Invalid number format in one of the fields.");
    } catch (ClassNotFoundException ex) {
        showErrorMessage("Class not found for the selected competition type.");
    } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
             InvocationTargetException ex) {
        showErrorMessage("Error creating competitor instance.");
    } catch (IllegalStateException ex) {
        showErrorMessage("The competition has already reached its maximum number of competitors!");
    } catch (IllegalArgumentException ex) {
        showErrorMessage("Competitor does not meet the competition's requirements.");
    } catch (Exception ex) {
        showErrorMessage("An unexpected error occurred while adding the competitor.");
        ex.printStackTrace();
    }
    updateButtonStates();
}

    //Our first of 2 functions for add the icons display. here we first decide which it will be and sending the positions of the wanted icon
    private void DisplayCompetitor(String competitionType, Gender gender, int xPosition, int yPosition, Color color) {
    String iconName = "";
    if ("Snowboard".equals(competitionType)) {
        iconName = (gender == Gender.MALE) ? "Snowboardboy" : "Snowboardgirl";
    } else if ("Ski".equals(competitionType)) {
        iconName = (gender == Gender.MALE) ? "Skiboy" : "Skigirl";
    }
    addCompetitorIcon(iconName, color, xPosition, yPosition);
}

    //Second part, with using ImageIcon and g2d we manage to add our competitor to the screen
    public void addCompetitorIcon(String iconName, Color color, int xPosition, int yPosition) {
    String imagePath = "src/icons/" + iconName + ".png";
    ImageIcon icon = new ImageIcon(imagePath);
    Image image = icon.getImage();
    BufferedImage bufferedImage = new BufferedImage(ICON_WIDTH, ICON_HEIGHT, BufferedImage.TYPE_INT_ARGB);
    Graphics2D g2d = bufferedImage.createGraphics();
    g2d.drawImage(image, 0, 0, ICON_WIDTH, ICON_HEIGHT, null);

    // Apply color tint to the whole icon
    g2d.setComposite(AlphaComposite.SrcAtop.derive(0.3f));
    g2d.setColor(color);
    g2d.fillRect(0, 0, ICON_WIDTH, ICON_HEIGHT);

    // Add a solid color bar at the bottom
    g2d.setComposite(AlphaComposite.SrcOver);
    g2d.fillRect(0, ICON_HEIGHT - 5, ICON_WIDTH, 5);

    g2d.dispose();
    ImageIcon coloredIcon = new ImageIcon(bufferedImage);

    JLabel competitorIconLabel = new JLabel(coloredIcon);
    competitorIconLabel.setBounds(xPosition, yPosition, ICON_WIDTH, ICON_HEIGHT);
    competitorsPanel.add(competitorIconLabel);
    competitorLabels.add(competitorIconLabel);

    competitorsPanel.revalidate();
    competitorsPanel.repaint();
}

    //An important one. the function that manage to update the icons based on the location of the current competitor
    //We're using here length to check we didn't reach the end of the arena
    private void updateCompetitorIcons() {
    if (competition == null || arena == null) return;

    double arenaLength = arena.getLength();
    int panelHeight = arenaPanel.getHeight() - ICON_HEIGHT;

    List<Competitor> allCompetitors = new ArrayList<>(competition.getActiveCompetitors());
    allCompetitors.addAll(competition.getFinishedCompetitors());

    // Sort competitors by their original order
    allCompetitors.sort(Comparator.comparingInt(c -> ((WinterSportsman)c).getOrder()));

    competitorsPanel.removeAll();

    for (int i = 0; i < allCompetitors.size(); i++) {
        Competitor competitor = allCompetitors.get(i);
        if (competitor instanceof WinterSportsman) {
            WinterSportsman sportsman = (WinterSportsman) competitor;
            double progress = Math.min(sportsman.getLocation().getX() / arenaLength, 1.0);
            int newY = (int) (progress * panelHeight);

            String competitionType = sportsman instanceof Skier ? "Ski" : "Snowboard";
            int xPosition = 5 + (i * (ICON_WIDTH + 10));  // Maintain original horizontal position
            DisplayCompetitor(competitionType, sportsman.getGender(), xPosition, newY, sportsman.getColor());

            // Check if the competitor has finished
            if (sportsman.getLocation().getX() >= arenaLength && !competition.getFinishedCompetitors().contains(sportsman)) {
                competition.getFinishedCompetitors().add(sportsman);
                competition.getActiveCompetitors().remove(sportsman);
            }
        }
    }

    competitorsPanel.revalidate();
    competitorsPanel.repaint();
}

    //Function to "clean" the screen from competitors
    public void clearCompetitorIcons() {
        competitorsPanel.removeAll();
        competitorLabels.clear();
        nextIconX = 10; // Reset the starting position
        competitorsPanel.revalidate();
        competitorsPanel.repaint();
    }

    private void chooseColor() {
    Color newColor = JColorChooser.showDialog(GUIGamePanel, "Choose Competitor Color", currentColor);
    if (newColor != null) {
        currentColor = newColor;
        colorChooserButton.setBackground(currentColor);
    }
}

    //Threads
    @Override
    public void propertyChange(PropertyChangeEvent evt) {}

    //Initiates the competition, starting competitor threads and updating the UI.
    private void startCompetition() {
    if (pool.isShutdown()) {
        pool = Executors.newFixedThreadPool(THREAD_COUNT);
    }
    if (competition == null || competition.getActiveCompetitors().isEmpty()) {
        showErrorMessage("Cannot start competition. Please check arena, competition, and competitors.");
        return;
    }
    competitionStarted = true;
    updateButtonStates();

    // Use the thread pool to submit tasks for each competitor
    for (Competitor competitor : competition.getActiveCompetitors()) {
        if (competitor instanceof Runnable) {
            pool.submit((Runnable) competitor);
        }
    }

    // Set up timer to update display and states
    Timer timer = new Timer(100, e -> {
        updateCompetitorIcons();
        updateCompetitorStates();
        updateCompetitorInfo(); // This should update your table
        if (competition.getActiveCompetitors().isEmpty()) {
            ((Timer)e.getSource()).stop();
            endCompetition();
        }
    });
    timer.start();
}

    //Ends the competition, shows results, and resets the game state.
    private void endCompetition() {
        competitionStarted = false;
        showFinalResults();
        clearCompetitorIcons();
        System.out.println("Competition ended!");
        // Shutdown the thread pool
        shutdownThreadPool();

        // Reset the game state completely
        resetGameState();
    JOptionPane.showMessageDialog(GUIGamePanel, "The race has ended. You can now create a new competition.", "Race Ended", JOptionPane.INFORMATION_MESSAGE);
    }

    //Show Info
    //Displays a dialog with current competitor information
    private void showCompetitorInfo() {
    if (infoDialog == null) {
        // First time setup
        tableModel = new DefaultTableModel(new String[]{"Name", "Speed", "Max Speed", "Location", "State"}, 0);
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(600, 200));

        infoDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(GUIGamePanel), "Competitors Information", false);
        infoDialog.setLayout(new BorderLayout());
        infoDialog.add(scrollPane, BorderLayout.CENTER);
        infoDialog.pack();
        infoDialog.setLocationRelativeTo(GUIGamePanel);
    }

    updateCompetitorInfo();
    infoDialog.setVisible(true);
}

    private void addCompetitorToTable(Competitor competitor) {
    if (competitor instanceof WinterSportsman) {
        WinterSportsman sportsman = (WinterSportsman) competitor;
        AlertStateContext stateContext = competition.getStateContextForCompetitor(competitor);
        String stateInfo = getStateInfo(stateContext, sportsman);

        tableModel.addRow(new Object[]{
            sportsman.getName(),
            String.format("%.2f", sportsman.getSpeed()),
            String.format("%.2f", sportsman.getMaxSpeed()),
            String.format("%.2f", sportsman.getLocation().getX()),
            stateInfo
        });
    }
}
    //Updates the competitor information table with current race data
    public void updateCompetitorInfo() {
    if (competition == null) {
        tableModel.setRowCount(0);
        return;
    }

    SwingUtilities.invokeLater(() -> {
        tableModel.setRowCount(0);

        List<Competitor> allCompetitors = new ArrayList<>(competition.getActiveCompetitors());
        allCompetitors.addAll(competition.getFinishedCompetitors());

        // Sort competitors based on their state and position
        allCompetitors.sort((c1, c2) -> {
            AlertStateContext state1 = competition.getStateContextForCompetitor(c1);
            AlertStateContext state2 = competition.getStateContextForCompetitor(c2);

            int stateOrder1 = getStateOrder(state1.getState());
            int stateOrder2 = getStateOrder(state2.getState());

            if (stateOrder1 != stateOrder2) {
                return Integer.compare(stateOrder1, stateOrder2);
            } else if (stateOrder1 == 0) { // Both finished
                return Integer.compare(
                    competition.getFinishedCompetitors().indexOf(c1),
                    competition.getFinishedCompetitors().indexOf(c2)
                );
            } else {
                // For non-finished competitors, sort by position
                return Double.compare(((WinterSportsman)c2).getLocation().getX(),
                                      ((WinterSportsman)c1).getLocation().getX());
            }
        });

        for (Competitor competitor : allCompetitors) {
            if (competitor instanceof WinterSportsman) {
                WinterSportsman sportsman = (WinterSportsman) competitor;
                AlertStateContext stateContext = competition.getStateContextForCompetitor(competitor);
                String stateInfo = getStateInfo(stateContext, sportsman);

                tableModel.addRow(new Object[]{
                    sportsman.getName(),
                    String.format("%.2f", sportsman.getSpeed()),
                    String.format("%.2f", sportsman.getMaxSpeed()),
                    String.format("%.2f", sportsman.getLocation().getX()),
                    stateInfo
                });
            }
        }
    });
}

    private int getStateOrder(MobileAlertState state) {
    if (state instanceof FinishedState) return 0;
    if (state instanceof ActiveState) return 1;
    if (state instanceof InjuredState) return 2;
    if (state instanceof DisabledState) return 3;
    return 4; // Unknown state
}

    //Displays the final results of the competition.
    private void showFinalResults() {
    StringBuilder results = new StringBuilder("Final Results:\n\n");
    List<Competitor> allCompetitors = new ArrayList<>(competition.getActiveCompetitors());
    allCompetitors.addAll(competition.getFinishedCompetitors());

    // Sort competitors
    allCompetitors.sort((c1, c2) -> {
        AlertStateContext state1 = competition.getStateContextForCompetitor(c1);
        AlertStateContext state2 = competition.getStateContextForCompetitor(c2);

        int stateOrder1 = getStateOrder(state1.getState());
        int stateOrder2 = getStateOrder(state2.getState());

        if (stateOrder1 != stateOrder2) {
            return Integer.compare(stateOrder1, stateOrder2);
        } else if (stateOrder1 == 0) { // Both finished
            return Integer.compare(
                competition.getFinishedCompetitors().indexOf(c1),
                competition.getFinishedCompetitors().indexOf(c2)
            );
        } else {
            // For non-finished competitors, sort by position
            return Double.compare(((WinterSportsman)c2).getLocation().getX(),
                                  ((WinterSportsman)c1).getLocation().getX());
        }
    });

    int rank = 1;
    for (Competitor competitor : allCompetitors) {
        if (competitor instanceof WinterSportsman) {
            WinterSportsman sportsman = (WinterSportsman) competitor;
            AlertStateContext stateContext = competition.getStateContextForCompetitor(competitor);
            String stateInfo = getStateInfo(stateContext, sportsman);
            results.append(String.format("%d. %s - %s\n", rank++, sportsman.getName(), stateInfo));
        }
    }
    JOptionPane.showMessageDialog(this, results.toString(), "Competition Results", JOptionPane.INFORMATION_MESSAGE);
}

    //Useful Methods
    //Displays an information message to the user
    private void showInfoMessage(String message) {
    JOptionPane.showMessageDialog(
        this,  // or use 'GUIGamePanel' if that's your main panel
        message,
        "Information",
        JOptionPane.INFORMATION_MESSAGE
    );
}

    //Displays an error message to the user.
    private void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(
                GUIGamePanel,
                message,
                "Invalid Input",
                JOptionPane.ERROR_MESSAGE
        );
    }

    //Clears all input fields in the UI
    private void clearInputFields() {
        nameField.setText("");
        ageField.setText("");
        maxSpeedField.setText("");
        accelerationField.setText("");
    }
    /**
 * Validates the input name for a competitor.
 * @param name The name to check
 * @return true if the name is valid, false otherwise
 */
    private boolean CheckInput(String name) {
        if (name.isEmpty()) {
            showErrorMessage("Please enter a name for the competitor!");
            return false;
        }
        if(name.contains("") && name.length()==1){
           showErrorMessage("Please Give name for the competitor!");
            return false;
        }
        return true;
    }

    //Prototype Methods
    private void copyCompetitor() {
    if (competition == null || competition.getActiveCompetitors().isEmpty()) {
        showErrorMessage("No competitors to copy.");
        return;
    }

    // Create a list of competitor names
    String[] competitorNames = competition.getActiveCompetitors().stream()
            .map(c -> ((WinterSportsman)c).getName())
            .toArray(String[]::new);

    // Let the user choose a competitor
    String selectedName = (String) JOptionPane.showInputDialog(
            GUIGamePanel,
            "Choose a competitor to copy:",
            "Copy Competitor",
            JOptionPane.QUESTION_MESSAGE,
            null,
            competitorNames,
            competitorNames[0]);

    if (selectedName == null) return;  // User cancelled

    // Find the selected competitor
    WinterSportsman originalCompetitor = (WinterSportsman) competition.getActiveCompetitors().stream()
            .filter(c -> ((WinterSportsman)c).getName().equals(selectedName))
            .findFirst()
            .orElse(null);

    if (originalCompetitor == null) return;

    try {
        // Clone the competitor
        WinterSportsman clonedCompetitor = (WinterSportsman) originalCompetitor.clone();

        // Set the new name for the cloned competitor
        clonedCompetitor.setName(clonedCompetitor.getName() + "_copy");

        // Set the new color for the cloned competitor
        clonedCompetitor.setColor(new Color(currentColor.getRGB()));

        // Set the order for the cloned competitor
        int newOrder = competition.getActiveCompetitors().size() + competition.getFinishedCompetitors().size();
        clonedCompetitor.setOrder(newOrder);

        // Reset the starting position
        clonedCompetitor.setLocation(new Point(0, 0));

        // Add the cloned competitor to the competition
        competition.addCompetitor(clonedCompetitor);
        clonedCompetitor.addPropertyChangeListener(this);

        // Update the GUI
        updateCompetitorDisplay();
        updateCompetitorInfo();
        updateButtonStates();

        JOptionPane.showMessageDialog(GUIGamePanel, "Competitor copied successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    } catch (CloneNotSupportedException e) {
        showErrorMessage("Failed to copy competitor: " + e.getMessage());
    }
}

    //Builder Methods
    //Creates a default race with predefined settings.
    private void createDefaultRace() {
    int maxCompetitors = 10;
    int initialCompetitors = 6;

    SkiCompetitionPlan plan = new SkiCompetitionPlan();
    plan.setArenaLength(800);
    plan.setSurface(SnowSurface.POWDER);
    plan.setCondition(WeatherCondition.SUNNY);
    plan.setDiscipline(Discipline.DOWNHILL);
    plan.setLeague(League.JUNIOR);
    plan.setGender(Gender.MALE);
    plan.setMaxCompetitors(maxCompetitors);

    SkiCompetitionBuilder skiBuilder = new SkiCompetitionBuilder();
    CompetitionDirector director = new CompetitionDirector(skiBuilder);

    director.constructCompetition(plan, initialCompetitors);

    competition = (SkiCompetition) director.getCompetition();
    arena = (WinterArena) competition.getArena();

    // Set up competitors for the race
    for (Competitor competitor : competition.getActiveCompetitors()) {
        if (competitor instanceof WinterSportsman) {
            WinterSportsman sportsman = (WinterSportsman) competitor;
            sportsman.setArenaLength(arena.getLength());
            sportsman.addPropertyChangeListener(this);
        }
    }

    arenaBuilt = true;
    competitionCreated = true;
    competitionStarted = false;

    updateButtonStates();
    disableCreateCompetitionSection();

    // Display the arena
    DisplayArena((int) arena.getLength(), ((WinterArena) arena).getCondition());

    // Clear existing competitors and display new ones
    clearCompetitorIcons();
    for (Competitor competitor : competition.getActiveCompetitors()) {
        if (competitor instanceof Skier) {
            Skier skier = (Skier) competitor;
            DisplayCompetitor("Ski", skier.getGender(), nextIconX, 10,Color.WHITE);
            nextIconX += ICON_WIDTH + 10;
        }
    }

    updateCompetitorInfo();
    buildArenaButton.setEnabled(true);
    updateButtonStates();
    JOptionPane.showMessageDialog(GUIGamePanel, "Default race created successfully!");
}

    //Disables UI elements in the competition creation section.
    private void disableCreateCompetitionSection() {
    competitionTypeCombo.setEnabled(false);
    maxCompetitorsField.setEnabled(false);
    disciplineCombo.setEnabled(false);
    leagueCombo.setEnabled(false);
    genderCombo.setEnabled(false);
    createCompetitionButton.setEnabled(false);
}

    //Thread Pool
    private void shutdownThreadPool() {
    pool.shutdown();
    try {
        if (!pool.awaitTermination(60, TimeUnit.SECONDS)) {
            pool.shutdownNow();
        }
    } catch (InterruptedException ex) {
        pool.shutdownNow();
        Thread.currentThread().interrupt();
    }
}

    //Decorator Methods
    private IWinterSportsman chooseCompetitor() {
    List<Competitor> competitors = competition.getActiveCompetitors();
    String[] competitorNames = competitors.stream()
            .map(c -> ((IWinterSportsman)c).getName())
            .toArray(String[]::new);

    String selectedName = (String) JOptionPane.showInputDialog(
            null, "Choose a competitor to customize:",
            "Select Competitor", JOptionPane.QUESTION_MESSAGE,
            null, competitorNames, competitorNames[0]);

    if (selectedName == null) return null;

    return (IWinterSportsman) competitors.stream()
            .filter(c -> ((IWinterSportsman)c).getName().equals(selectedName))
            .findFirst()
            .orElse(null);
}

    //Allows customization of a selected competitor
    private void customizeCompetitor() {
    if (competition == null || competition.getActiveCompetitors().isEmpty()) {
        showErrorMessage("No competitors to customize.");
        return;
    }
    IWinterSportsman decoratedCompetitor = chooseCompetitor();
    if (decoratedCompetitor == null) return;

    boolean keepCustomizing = true;
    while (keepCustomizing) {
        String[] options = {"Add Speed Boost", "Change Color", "Finish Customization"};
        int choice = JOptionPane.showOptionDialog(null, "Customize " + decoratedCompetitor.getName() + ":",
                "Customize Competitor", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                null, options, options[0]);
        switch (choice) {
            case 0:
                decoratedCompetitor = new SpeedySportsman(decoratedCompetitor);
                showInfoMessage("Speed Boost Added");
                break;
            case 1:
                Color newColor = JColorChooser.showDialog(null, "Choose Color", decoratedCompetitor.getColor());
                if (newColor != null) {
                    decoratedCompetitor = new ColoredSportsman(decoratedCompetitor, newColor);
                }
                break;
            case 2:
                keepCustomizing = false;
                break;
        }
    }
    updateCompetitorInCompetition(decoratedCompetitor);
    updateCompetitorDisplay();
    updateButtonStates();
    }

    //We take the decorator object and change it back to Winter sportsman object
    private void updateCompetitorInCompetition(IWinterSportsman updatedCompetitor) {
    List<Competitor> competitors = competition.getActiveCompetitors();
    for (int i = 0; i < competitors.size(); i++) {
        Competitor competitor = competitors.get(i);
        if (competitor instanceof IWinterSportsman) {
            IWinterSportsman winterSportsman = (IWinterSportsman) competitor;
            if (winterSportsman.getName().equals(updatedCompetitor.getName())) {
                // If the updatedCompetitor is a WSDecorator, we need to get the core competitor
                while (updatedCompetitor instanceof WSDecorator) {
                    updatedCompetitor = ((WSDecorator) updatedCompetitor).getDecoratedSportsman();
                }
                competitors.set(i, (Competitor) updatedCompetitor);
                break;
            }
        }
    }
}

   // Updates the visual display of default competitors in the U
    private void updateCompetitorDisplay() {
    competitorsPanel.removeAll();
    competitorLabels.clear();
    int xPosition = 5;
    int yPosition = 10;

    for (Competitor competitor : competition.getActiveCompetitors()) {
        if (competitor instanceof WinterSportsman) {
            WinterSportsman sportsman = (WinterSportsman) competitor;
            DisplayCompetitor(sportsman instanceof Skier ? "Ski" : "Snowboard",
                              sportsman.getGender(), xPosition, yPosition, sportsman.getColor());
            xPosition += ICON_WIDTH + 10;
            if (xPosition + ICON_WIDTH > competitorsPanel.getWidth()) {
                xPosition = 5;
                yPosition += ICON_HEIGHT + 10;
            }
        }
    }

    competitorsPanel.revalidate();
    competitorsPanel.repaint();
}

    //State Methods
    //The Update Methods for our states
    private void updateCompetitorStates() {
    if (competition != null) {
        for (AlertStateContext stateContext : competition.getCompetitorStates()) {
            stateContext.updateStatus(competition);
            stateContext.checkStateChange();
        }
    }
}

    //Getting each string of our state
    private String getStateInfo(AlertStateContext stateContext, WinterSportsman sportsman) {
    if (stateContext == null) return "Unknown";

    MobileAlertState state = stateContext.getState();
    if (state instanceof FinishedState) {
        return "Finished";
    } else if (state instanceof ActiveState) {
        if (competition.getArena().isFinished(sportsman)) {
            return "Finished"; // In case the state hasn't been updated yet
        }
        return "Active - Rating: " + competition.getCurrentRating(sportsman);
    } else if (state instanceof InjuredState) {
        return "Injured at " + ((InjuredState) state).getInjuryTime();
    } else if (state instanceof DisabledState) {
        return "Failed";
    }
    return "Unknown";
}

    //Main Function
    public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        JFrame frame = new JFrame("Winter Sports Competition");
        frame.setContentPane(new GUIGame().GUIGamePanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setSize(1000, 700);
        frame.setVisible(true);
    });

    }
}
