import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class SpiceToolkitUI extends JFrame {

    private JTextArea logArea;
    private final List<EventCard> eventCards = new ArrayList<>();
    private EventCard selectedCard;

    public SpiceToolkitUI() {
        setTitle("SPICE-Based Geometric Events Computation Toolkit");
        setSize(1400, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(createTopBar(), BorderLayout.NORTH);
        add(createMainContent(), BorderLayout.CENTER);
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 225, 230)));
        topBar.setPreferredSize(new Dimension(1400, 65));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 12));
        left.setOpaque(false);

        JLabel logo1 = new JLabel("🛰");
        logo1.setFont(new Font("SansSerif", Font.PLAIN, 28));

        JLabel title = new JLabel("<html><div style='font-size:18px; font-weight:bold;'>SPICE-BASED GEOMETRIC EVENTS COMPUTATION TOOLKIT</div>"
                + "<div style='font-size:11px; color:#6c7a89;'>\"Precision Engineering for Space Missions\"</div></html>");

        left.add(logo1);
        left.add(title);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 15));
        right.setOpaque(false);

        JLabel settings = new JLabel("⚙");
        settings.setFont(new Font("SansSerif", Font.PLAIN, 18));
        right.add(settings);

        topBar.add(left, BorderLayout.WEST);
        topBar.add(right, BorderLayout.EAST);

        return topBar;
    }

    private JPanel createMainContent() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(new Color(245, 247, 250));

        main.add(createSidebar(), BorderLayout.WEST);
        main.add(createCenterPanel(), BorderLayout.CENTER);

        return main;
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(300, 700));
        sidebar.setBackground(new Color(248, 250, 252));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(18, 18, 18, 18));

        sidebar.add(sectionLabel("SATELLITE & KERNELS"));
        sidebar.add(createLabeledField("3-CHAR SAT ID", "TDS"));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createLabeledField("SPK FILE", "satellite_ephem.bsp"));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createLabeledField("CENTRAL BODY", "EARTH"));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createLabeledField("REFERENCE FRAME", "ITRF93"));

        sidebar.add(Box.createVerticalStrut(18));
        sidebar.add(sectionLabel("ORBIT NUMBERING (OPTIONAL)"));
        sidebar.add(createLabeledField("Orbit binary file", "orbit_numbers.bin"));
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(createLabeledField("Output directory", "./results"));

        sidebar.add(Box.createVerticalStrut(25));

        JButton validateBtn = createActionButton("LOAD KERNELS & VALIDATE", new Color(48, 138, 255));
        JButton runBtn = createActionButton("RUN ALL EVENTS (Option 9)", new Color(32, 175, 140));

        validateBtn.addActionListener(e -> log("> SPICE kernel pool ready\n> Input validation completed successfully."));
        runBtn.addActionListener(e -> {
            if (selectedCard != null) {
                log("> Running: " + selectedCard.getTitleText());
            } else {
                log("> No event selected.");
            }
        });

        sidebar.add(validateBtn);
        sidebar.add(Box.createVerticalStrut(12));
        sidebar.add(runBtn);

        return sidebar;
    }

    private JPanel createCenterPanel() {
        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(new Color(245, 247, 250));
        center.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel upper = new JPanel();
        upper.setOpaque(false);
        upper.setLayout(new BoxLayout(upper, BoxLayout.Y_AXIS));

        JPanel headingPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headingPanel.setOpaque(false);
        JLabel heading = new JLabel("EVENT SELECTION");
        heading.setFont(new Font("SansSerif", Font.BOLD, 15));
        heading.setForeground(new Color(67, 85, 107));
        headingPanel.add(heading);

        upper.add(headingPanel);
        upper.add(Box.createVerticalStrut(15));
        upper.add(createEventGrid());
        upper.add(Box.createVerticalStrut(20));
        upper.add(createLogPanel());

        center.add(upper, BorderLayout.NORTH);

        return center;
    }

    private JPanel createEventGrid() {
        JPanel grid = new JPanel(new GridLayout(3, 4, 16, 16));
        grid.setOpaque(false);

        addEventCard(grid, "1. Solar/Lunar Eclipses", "Umbra, penumbra, lunar partial", false);
        addEventCard(grid, "2. Equatorial Crossings", "Ascending / Descending nodes", false);
        addEventCard(grid, "3. Apogee/Perigee", "Min & max distance", false);
        addEventCard(grid, "4. Polar Crossings", "North & South pole times", false);

        addEventCard(grid, "5. Ground Track", "Lat/Lon/Alt subsatellite", false);
        addEventCard(grid, "6. Orbit Numbering", "Absolute orbit numbers", false);
        addEventCard(grid, "7. Station Visibility", "AOS/LOS multiple stations", false);
        addEventCard(grid, "8. State Vectors & Track", "Vel, heading, ground speed", false);

        addEventCard(grid, "9. ALL EVENTS (Batch)", "Compute every geometric event sequentially", true);

        grid.add(new JPanel());
        grid.add(new JPanel());
        grid.add(new JPanel());

        return grid;
    }

    private void addEventCard(JPanel parent, String title, String subtitle, boolean initiallySelected) {
        EventCard card = new EventCard(title, subtitle, initiallySelected);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectCard(card);
            }
        });

        eventCards.add(card);
        parent.add(card);

        if (initiallySelected) {
            selectedCard = card;
        }
    }

    private void selectCard(EventCard clickedCard) {
        for (EventCard card : eventCards) {
            card.setSelected(false);
        }

        clickedCard.setSelected(true);
        selectedCard = clickedCard;

        log("> Selected event: " + clickedCard.getTitleText());
    }

    private JPanel createLogPanel() {
        JPanel outer = new RoundedPanel(22, new Color(8, 25, 60));
        outer.setLayout(new BorderLayout());
        outer.setPreferredSize(new Dimension(950, 250));

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.setBorder(new EmptyBorder(12, 18, 8, 18));

        JLabel title = new JLabel("SPICE EVENT LOG");
        title.setForeground(new Color(190, 210, 255));
        title.setFont(new Font("SansSerif", Font.BOLD, 13));

        JLabel realtime = new JLabel("real-time");
        realtime.setForeground(new Color(160, 180, 220));
        realtime.setFont(new Font("SansSerif", Font.PLAIN, 12));

        top.add(title, BorderLayout.WEST);
        top.add(realtime, BorderLayout.EAST);

        logArea = new JTextArea();
        logArea.setBackground(new Color(8, 25, 60));
        logArea.setForeground(new Color(114, 255, 163));
        logArea.setCaretColor(Color.WHITE);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        logArea.setEditable(false);
        logArea.setBorder(new EmptyBorder(10, 18, 18, 18));
        logArea.setText("> SPICE kernel pool ready\n\n> Waiting for event selection...");

        JScrollPane scrollPane = new JScrollPane(logArea);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(8, 25, 60));

        outer.add(top, BorderLayout.NORTH);
        outer.add(scrollPane, BorderLayout.CENTER);

        return outer;
    }

    private JLabel sectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("SansSerif", Font.BOLD, 13));
        label.setForeground(new Color(84, 101, 122));
        label.setBorder(new EmptyBorder(0, 0, 10, 0));
        return label;
    }

    private JPanel createLabeledField(String labelText, String value) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel label = new JLabel(labelText);
        label.setFont(new Font("SansSerif", Font.PLAIN, 12));
        label.setForeground(new Color(100, 116, 139));
        label.setBorder(new EmptyBorder(0, 0, 5, 0));

        JTextField field = new JTextField(value);
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        field.setPreferredSize(new Dimension(240, 38));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 230), 1, true),
                new EmptyBorder(8, 10, 8, 10)
        ));

        panel.add(label);
        panel.add(field);

        return panel;
    }

    private JButton createActionButton(String text, Color color) {
        JButton btn = new JButton(text);
        btn.setFocusPainted(false);
        btn.setForeground(Color.WHITE);
        btn.setBackground(color);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        btn.setPreferredSize(new Dimension(240, 42));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        return btn;
    }

    private void log(String text) {
        logArea.append("\n\n" + text);
    }

    static class RoundedPanel extends JPanel {
        private final int radius;
        private final Color bgColor;

        public RoundedPanel(int radius, Color bgColor) {
            this.radius = radius;
            this.bgColor = bgColor;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    static class EventCard extends RoundedPanel {
        private boolean selected;
        private final String titleText;
        private final JLabel iconLabel;
        private final JLabel titleLabel;
        private final JLabel subtitleLabel;

        public EventCard(String title, String subtitle, boolean selected) {
            super(20, selected ? new Color(20, 113, 224) : Color.WHITE);
            this.titleText = title;
            this.selected = selected;

            setLayout(new BorderLayout());
            setPreferredSize(new Dimension(240, 95));
            setBorder(new EmptyBorder(14, 14, 14, 14));
            setCursor(new Cursor(Cursor.HAND_CURSOR));

            JPanel iconPanel = new JPanel();
            iconPanel.setOpaque(false);
            iconPanel.setPreferredSize(new Dimension(44, 44));

            iconLabel = new JLabel(selected ? "∞" : "◉");
            iconLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
            iconPanel.add(iconLabel);

            JPanel textPanel = new JPanel();
            textPanel.setOpaque(false);
            textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));

            titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 15));

            subtitleLabel = new JLabel(subtitle);
            subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

            textPanel.add(titleLabel);
            textPanel.add(Box.createVerticalStrut(6));
            textPanel.add(subtitleLabel);

            add(iconPanel, BorderLayout.WEST);
            add(textPanel, BorderLayout.CENTER);

            updateStyle();
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
            updateStyle();
            repaint();
        }

        public String getTitleText() {
            return titleText;
        }

        private void updateStyle() {
            Color selectedBg = new Color(20, 113, 224);
            Color normalBg = Color.WHITE;

            if (selected) {
                setBackgroundPanel(selectedBg);
                iconLabel.setText("∞");
                iconLabel.setForeground(Color.WHITE);
                titleLabel.setForeground(Color.WHITE);
                subtitleLabel.setForeground(new Color(225, 238, 255));
                setBorder(new EmptyBorder(14, 14, 14, 14));
            } else {
                setBackgroundPanel(normalBg);
                iconLabel.setText("◉");
                iconLabel.setForeground(new Color(58, 120, 210));
                titleLabel.setForeground(new Color(50, 64, 84));
                subtitleLabel.setForeground(new Color(110, 124, 145));
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(228, 232, 238), 1, true),
                        new EmptyBorder(14, 14, 14, 14)
                ));
            }
        }

        private void setBackgroundPanel(Color color) {
            try {
                java.lang.reflect.Field field = RoundedPanel.class.getDeclaredField("bgColor");
                field.setAccessible(true);
                field.set(this, color);
            } catch (Exception ignored) {
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
            new SpiceToolkitUI().setVisible(true);
        });
    }
}