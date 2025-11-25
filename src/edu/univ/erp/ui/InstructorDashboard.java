package edu.univ.erp.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.*;
import java.util.Map;

import edu.univ.erp.service.InstructorCommands;
import edu.univ.erp.access.MaintenanceMode;
import edu.univ.erp.data.DBConnection;

public class InstructorDashboard extends JFrame {

    private CardLayout cards = new CardLayout();
    private JPanel cardPanel;
    private final InstructorCommands instructor = new InstructorCommands();

    private final int instructorId;
    private final String name;

    public InstructorDashboard(String username) {

        this.instructorId = getInstructorIdByUsername(username);
        this.name = username;

        setTitle("Instructor Dashboard - University ERP");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Sidebar 
        JPanel sidebar = new JPanel(new GridLayout(0, 1, 10, 10));
        sidebar.setBackground(new Color(31, 67, 59));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 12, 20, 12));

        String[] items = {
                "Home",
                "My Sections",
                "Enter Scores",
                "Final Grades",
                "Stats",
                "Edit Section"
        };

        for (String it : items) {
            JButton b = new JButton(it);
            styleSidebarButton(b);
            b.addActionListener(e -> showCard(it));
            sidebar.add(b);
        }

        cardPanel = new JPanel(cards);

        cardPanel.add(makeHomePanel(), "Home");
        cardPanel.add(makeMySectionsPanel(), "My Sections");
        cardPanel.add(makeEnterScoresPanel(), "Enter Scores");
        cardPanel.add(makeFinalGradesPanel(), "Final Grades");
        cardPanel.add(makeStatsPanel(), "Stats");
        cardPanel.add(makeEditSectionPanel(), "Edit Section");

        add(sidebar, BorderLayout.WEST);
        add(cardPanel, BorderLayout.CENTER);

        showCard("Home");
        setVisible(true);
    }

    // Styling
    private void styleSidebarButton(JButton b) {
        b.setBackground(new Color(60, 135, 110));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
    }

    // Top Panel
    private JPanel makeTopPanel(String titleText) {
        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(new EmptyBorder(12, 12, 12, 12));
        top.setBackground(new Color(245, 247, 246));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));

        JButton logout = new JButton("Logout");
        logout.setBackground(new Color(200, 60, 60));
        logout.setForeground(Color.WHITE);
        logout.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(LoginApp::new); 
        });

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        right.setOpaque(false);
        right.add(logout);

        top.add(title, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);

        return top;
    }

    // HOME PAGE
    private JPanel makeHomePanel() {

        JPanel home = new JPanel(new BorderLayout());
        home.setBackground(new Color(245, 247, 246));

        JPanel top = makeTopPanel("Welcome Instructor "+ name.toUpperCase());
        top.getComponent(0).setFont(new Font("SansSerif", Font.BOLD, 26)); // set larger font for title
        home.add(top, BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        center.setBorder(new EmptyBorder(20, 40, 40, 40));
        center.setBackground(new Color(245, 247, 246));

        // Maintenance Mode
        MaintenanceMode mm = new MaintenanceMode();
        if (mm.isEnabled()) {

            JPanel warnPanel = new JPanel(new BorderLayout());
            warnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25)); 
            warnPanel.setOpaque(true);
            warnPanel.setBackground(new Color(255, 200, 200));
            warnPanel.setBorder(new EmptyBorder(4, 10, 4, 10)); 

            JLabel warn = new JLabel("⚠ The system is currently under Maintenance Mode.", SwingConstants.CENTER);
            warn.setFont(new Font("SansSerif", Font.BOLD, 16));
            warn.setForeground(Color.RED);

            warnPanel.add(warn, BorderLayout.CENTER);

            Timer blink = new Timer(700, ev -> {
                if (warn.getForeground().equals(Color.RED))
                    warn.setForeground(new Color(180, 0, 0));   
                else
                    warn.setForeground(Color.RED);
            });
            blink.start();

            center.add(warnPanel, BorderLayout.NORTH);
        }

        JPanel statsRow = new JPanel(new GridLayout(1, 2, 20, 20));
        statsRow.setOpaque(false);

        statsRow.add(makeStatCard("My Sections", getInstructorSectionCount()));
        statsRow.add(makeStatCard("Students Enrolled", getInstructorStudentCount()));

        center.add(statsRow, BorderLayout.NORTH);

        JPanel lower = new JPanel(new GridLayout(1, 2, 20, 20));
        lower.setOpaque(false);
        lower.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        // Announcements 
        JPanel ann = new JPanel(new BorderLayout());
        ann.setBackground(Color.WHITE);
        ann.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel aTitle = new JLabel("Instructor Announcements");
        aTitle.setFont(new Font("SansSerif", Font.BOLD, 16));

        JTextArea msgs = new JTextArea(
                "• Grade submission deadlines soon.\n" +
                "• Prepare upcoming evaluations.\n" +
                "• Department faculty meet next week.\n" +
                "• Update attendance regularly.\n"
        );
        
        msgs.setEditable(false);
        msgs.setBackground(new Color(250, 250, 250));
        msgs.setFont(new Font("SansSerif", Font.PLAIN, 14));


        ann.add(aTitle, BorderLayout.NORTH);
        ann.add(msgs, BorderLayout.CENTER);


        // Quick Actions
        JPanel actions = new JPanel();
        actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));
        actions.setBackground(Color.WHITE);
        actions.setBorder(new EmptyBorder(20, 20, 20, 20));
        actions.setBorder(BorderFactory.createTitledBorder("Quick Actions"));

        JButton b1 = new JButton("View My Sections");
        JButton b2 = new JButton("Enter Scores");
        JButton b3 = new JButton("Final Grades");
        JButton b4 = new JButton("Class Stats");
        JButton b5 = new JButton("Edit Section Details");

        b1.addActionListener(e -> showCard("My Sections"));
        b2.addActionListener(e -> showCard("Enter Scores"));
        b3.addActionListener(e -> showCard("Final Grades"));
        b4.addActionListener(e -> showCard("Stats"));
        b5.addActionListener(e -> showCard("Edit Section"));

        for (JButton bt : new JButton[]{b1, b2, b3, b4, b5}) {
            bt.setMaximumSize(new Dimension(240, 40));
            bt.setAlignmentX(Component.LEFT_ALIGNMENT);
        }

        actions.add(b1);
        actions.add(Box.createVerticalStrut(10));
        actions.add(b2);
        actions.add(Box.createVerticalStrut(10));
        actions.add(b3);
        actions.add(Box.createVerticalStrut(10));
        actions.add(b4);
        actions.add(Box.createVerticalStrut(10));
        actions.add(b5);

        lower.add(ann);
        lower.add(actions);

        center.add(lower, BorderLayout.CENTER);
        home.add(center, BorderLayout.CENTER);

        return home;
    }

    // Stats
    private JPanel makeStatCard(String title, int count) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel t = new JLabel(title);
        t.setFont(new Font("SansSerif", Font.BOLD, 16));
        t.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel c = new JLabel(String.valueOf(count));
        c.setFont(new Font("SansSerif", Font.BOLD, 36));
        c.setForeground(new Color(30, 130, 100));
        c.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalGlue());
        card.add(t);
        card.add(Box.createVerticalStrut(10));
        card.add(c);
        card.add(Box.createVerticalGlue());

        return card;
    }

    private int getInstructorSectionCount() {
        String sql = "SELECT COUNT(*) FROM sections WHERE instructor_id = ?";
        try (Connection conn = DBConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, instructorId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    private int getInstructorStudentCount() {
        String sql = "SELECT COUNT(DISTINCT e.student_id) FROM enrollments e " +
                "JOIN sections s ON e.section_id = s.section_id " +
                "WHERE s.instructor_id = ?";
        try (Connection conn = DBConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, instructorId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    // MY SECTIONS PANEL
    private JPanel makeMySectionsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        JPanel top = makeTopPanel("My Sections");

        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> loadSectionsTable(p));

        // Refresh button
        ((JPanel) top.getComponent(1)).add(refresh);
        p.add(top, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(2, 1));
        p.add(centerPanel, BorderLayout.CENTER);

        loadSectionsTable(p);

        return p;
    }

    private void loadSectionsTable(JPanel panel) {
        try {
            String sql = "SELECT section_id, course_id, day_time, room, capacity, semester, year " +
                         "FROM sections WHERE instructor_id = ?";

            try (Connection conn = DBConnection.getErpConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, instructorId);
                try (ResultSet rs = stmt.executeQuery()) {
                    JTable table = TableUtils.buildStyledTable(rs);
                    JScrollPane newScroll = new JScrollPane(table);

                    JPanel centerPanel = (JPanel) panel.getComponent(1); 
                    Component oldTop = centerPanel.getComponentCount() > 0 ? centerPanel.getComponent(0) : null;
                    if (oldTop != null) centerPanel.remove(oldTop);

                    centerPanel.add(newScroll, 0);
                    centerPanel.revalidate();
                    centerPanel.repaint();

                    loadEnrolledStudentsTable(centerPanel);
                }
            }
        } catch (Exception e) {
            JTable emptyTable = new JTable(new javax.swing.table.DefaultTableModel());
            JScrollPane emptyScroll = new JScrollPane(emptyTable);

            Component centerComponent = ((BorderLayout) panel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
            if (centerComponent instanceof JScrollPane) {
                panel.remove(centerComponent);
            }
            panel.add(emptyScroll, BorderLayout.CENTER);
            panel.revalidate();
            panel.repaint();
        }
    }

    private void loadEnrolledStudentsTable(JPanel centerPanel) {

        try {
            String sql =
                "SELECT e.section_id, e.student_id, s.name AS student_name " +
                "FROM enrollments e " +
                "JOIN students s ON e.student_id = s.user_id " +
                "JOIN sections sec ON e.section_id = sec.section_id " +
                "WHERE sec.instructor_id = ? " +
                "ORDER BY e.section_id, e.student_id";

            try (Connection conn = DBConnection.getErpConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setInt(1, instructorId);

                try (ResultSet rs = stmt.executeQuery()) {

                    JTable table = TableUtils.buildStyledTable(rs);
                    JScrollPane scroll = new JScrollPane(table);

                    JPanel wrapper = new JPanel(new BorderLayout());
                    wrapper.setBorder(new EmptyBorder(20, 10, 10, 10));

                    JLabel title = new JLabel("Enrolled Students");
                    title.setFont(new Font("SansSerif", Font.BOLD, 16));
                    title.setBorder(new EmptyBorder(0, 5, 10, 0));

                    wrapper.add(title, BorderLayout.NORTH);
                    wrapper.add(scroll, BorderLayout.CENTER);

                    if (centerPanel.getComponentCount() > 1)
                        centerPanel.remove(1);

                    centerPanel.add(wrapper, 1);
                    centerPanel.revalidate();
                    centerPanel.repaint();
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // SCORES
    private JPanel makeEnterScoresPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(makeTopPanel("Enter Scores"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(6, 2, 10, 10));
        form.setBorder(new EmptyBorder(20, 80, 20, 80));

        JTextField sectionId = new JTextField();
        JTextField studentId = new JTextField();
        JTextField assessment = new JTextField();
        JTextField score = new JTextField();
        JTextField weight = new JTextField();

        form.add(new JLabel("Section ID:")); form.add(sectionId);
        form.add(new JLabel("Student ID:")); form.add(studentId);
        form.add(new JLabel("Assessment:")); form.add(assessment);
        form.add(new JLabel("Score (0-100):")); form.add(score);
        form.add(new JLabel("Weight (%):")); form.add(weight);

        JButton save = new JButton("Save Score");
        form.add(new JLabel());
        form.add(save);

        save.addActionListener(e -> {
            try {
                MaintenanceMode mm = new MaintenanceMode();
                if (mm.isEnabled()) {
                    JOptionPane.showMessageDialog(this, "System under maintenance.");
                    return;
                }

                int secId = Integer.parseInt(sectionId.getText());

                boolean ok = instructor.saveScore(
                        secId,
                        Integer.parseInt(studentId.getText()),
                        assessment.getText().trim(),
                        Double.parseDouble(score.getText()),
                        Double.parseDouble(weight.getText())
                );
                JOptionPane.showMessageDialog(this, ok ? "Score saved." : "Operation failed. Check section/student ID.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input. Please check all fields.");
            }
        });

        p.add(form, BorderLayout.CENTER);
        return p;
    }

    // FINAL GRADES PANEL
    private JPanel makeFinalGradesPanel() {
        JPanel p = new JPanel(new BorderLayout());
        JPanel top = makeTopPanel("Final Grades");

        JButton refresh = new JButton("Refresh Grades");
        refresh.addActionListener(e -> loadFinalGradesTable(p));
        ((JPanel) top.getComponent(1)).add(refresh);

        p.add(top, BorderLayout.NORTH);

        loadFinalGradesTable(p);
        return p;
    }

    private void loadFinalGradesTable(JPanel panel) {
        javax.swing.table.DefaultTableModel model =
            new javax.swing.table.DefaultTableModel(
                new String[]{"Section ID", "Student ID", "Final Grade"}, 0
            );

        try (Connection conn = DBConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT section_id FROM sections WHERE instructor_id = ?")) {

            stmt.setInt(1, instructorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int sec = rs.getInt(1);
                    Map<Integer, Double> grades = instructor.computeFinalGrades(sec);

                    for (var entry : grades.entrySet()) {
                        model.addRow(new Object[]{
                                sec,
                                entry.getKey(),
                                String.format("%.2f", entry.getValue())
                        });
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JTable table = new JTable(model);
        JScrollPane newScroll = new JScrollPane(table);

        Component centerComponent = ((BorderLayout) panel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (centerComponent instanceof JScrollPane) {
            panel.remove(centerComponent);
        }
        panel.add(newScroll, BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();
    }

    // CLASS STATS
    private JPanel makeStatsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        JPanel top = makeTopPanel("Class Stats");

        JButton refresh = new JButton("Refresh Stats");
        refresh.addActionListener(e -> loadStatsTable(p));
        ((JPanel) top.getComponent(1)).add(refresh);

        p.add(top, BorderLayout.NORTH);

        loadStatsTable(p);
        return p;
    }

    private void loadStatsTable(JPanel panel) {
        javax.swing.table.DefaultTableModel model =
            new javax.swing.table.DefaultTableModel(
                new String[]{"Section ID", "Average Score", "Min Score", "Max Score"}, 0
            );

        try (Connection conn = DBConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT section_id FROM sections WHERE instructor_id = ?")) {

            stmt.setInt(1, instructorId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int sec = rs.getInt(1);
                    Map<String, Double> stats = instructor.getClassStats(sec);

                    if (!stats.isEmpty()) {
                        model.addRow(new Object[]{
                                sec,
                                String.format("%.2f", stats.get("average")),
                                String.format("%.2f", stats.get("min")),
                                String.format("%.2f", stats.get("max"))
                        });
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JTable table = new JTable(model);
        JScrollPane newScroll = new JScrollPane(table);

        Component centerComponent = ((BorderLayout) panel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (centerComponent instanceof JScrollPane) {
            panel.remove(centerComponent);
        }
        panel.add(newScroll, BorderLayout.CENTER);
        panel.revalidate();
        panel.repaint();
    }

    // EDIT SECTION PANEL
    private JPanel makeEditSectionPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(makeTopPanel("Edit Section"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(4, 2, 10, 10));
        form.setBorder(new EmptyBorder(20, 80, 20, 80));

        JComboBox<String> fieldBox = new JComboBox<>(new String[]{"day_time", "capacity"});
        JTextField value = new JTextField();
        JTextField sectionId = new JTextField();

        form.add(new JLabel("Field to Edit:")); form.add(fieldBox);
        form.add(new JLabel("New Value:")); form.add(value);
        form.add(new JLabel("Section ID:")); form.add(sectionId);

        JButton update = new JButton("Apply Update");
        form.add(new JLabel());
        form.add(update);

        update.addActionListener(e -> {
            try {
                MaintenanceMode mm = new MaintenanceMode();
                if (mm.isEnabled()) {
                    JOptionPane.showMessageDialog(this, "System under maintenance. Cannot modify section data.");
                    return;
                }
                
                String field = fieldBox.getSelectedItem().toString();
                Object val;

                if (field.equals("capacity")) {

                    int cap;
                    try {
                        cap = Integer.parseInt(value.getText().trim());
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(this, "Capacity must be a valid number.");
                        return;
                    }

                    if (cap <= 0) {
                        JOptionPane.showMessageDialog(this, "Capacity must be greater than 0.");
                        return;
                    }

                    val = cap;
                } 
                else {
                    val = value.getText().trim();
                }


                boolean ok = instructor.editSection(field, val, Integer.parseInt(sectionId.getText().trim()));
                JOptionPane.showMessageDialog(this, ok ? "Section updated." : "Update failed. Check Section ID.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid data. Check Section ID and value format.");
            }
        });

        p.add(form, BorderLayout.CENTER);
        return p;
    }

    private int getInstructorIdByUsername(String username) {
        String sql = "SELECT i.user_id FROM instructors i JOIN auth_db.users u ON i.user_id = u.id WHERE u.username = ?";
        try (Connection conn = DBConnection.getErpConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt("user_id");
            }
        } catch (Exception e) {
             e.printStackTrace();
        }
        return -1;
    }

    private void showCard(String name) {
        cards.show(cardPanel, name);
    }
}