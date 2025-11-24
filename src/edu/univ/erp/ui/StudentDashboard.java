package edu.univ.erp.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.ResultSet;

import edu.univ.erp.service.StudentCommands;
import edu.univ.erp.access.MaintenanceMode;
import edu.univ.erp.data.DBConnection;

public class StudentDashboard extends JFrame {

    private final CardLayout cards = new CardLayout();
    private final JPanel cardPanel = new JPanel(cards);

    private final StudentCommands student = new StudentCommands();
    private final int studentId;
    private final String name;

    public StudentDashboard(String username) {

        this.studentId = getStudentId(username);
        this.name = username;

        setTitle("Student Dashboard - University ERP");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // ---------------- SIDEBAR ----------------
        JPanel sidebar = new JPanel(new GridLayout(0, 1, 10, 10));
        sidebar.setBackground(new Color(31, 67, 59));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 12, 20, 12));

        String[] items = {
                "Home",
                "Course Catalog",
                "Register",
                "Drop Course",
                "Timetable",
                "Grades"
        };

        for (String it : items) {
            JButton b = new JButton(it);
            styleSidebarButton(b);
            b.addActionListener(e -> showCard(it));
            sidebar.add(b);
        }

        // ---------------- PANELS ----------------
        cardPanel.add(makeHomePanel(), "Home");
        cardPanel.add(makeCourseCatalogPanel(), "Course Catalog");
        cardPanel.add(makeRegisterPanel(), "Register");
        cardPanel.add(makeDropPanel(), "Drop Course");
        cardPanel.add(makeTimetablePanel(), "Timetable");
        cardPanel.add(makeGradesPanel(), "Grades");

        add(sidebar, BorderLayout.WEST);
        add(cardPanel, BorderLayout.CENTER);

        showCard("Home");
        setVisible(true);
    }

    // --------------------------------------------------
    // UTILITY METHODS
    // --------------------------------------------------

    private void styleSidebarButton(JButton b) {
        b.setBackground(new Color(60, 135, 110));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        b.setFocusPainted(false);
    }

    private JPanel makeTopPanel(String titleText) {
        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(new EmptyBorder(12, 12, 12, 12));
        top.setBackground(new Color(245, 247, 246));

        JLabel t = new JLabel(titleText);
        t.setFont(new Font("SansSerif", Font.BOLD, 20));

        JButton logout = new JButton("Logout");
        logout.setBackground(new Color(200, 60, 60));
        logout.setForeground(Color.WHITE);
        logout.addActionListener(e -> {
            dispose();
            new LoginApp();
        });

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        right.setOpaque(false);
        right.add(logout);

        top.add(t, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);
        return top;
    }

    private int getStudentId(String username) {
        String sql = """
                SELECT s.user_id
                FROM students s
                JOIN auth_db.users u ON s.user_id = u.id
                WHERE u.username = ?
                """;

        try (var conn = DBConnection.getErpConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (var rs = stmt.executeQuery()) {
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

    // --------------------------------------------------
    // HOME PANEL
    // --------------------------------------------------

    private JPanel makeHomePanel() {
        JPanel home = new JPanel(new BorderLayout());
        home.add(makeTopPanel("Welcome Student "+name.toUpperCase()), BorderLayout.NORTH);

        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

        center.setBorder(new EmptyBorder(20, 40, 40, 40));
        center.setBackground(new Color(245, 247, 246));

        // --- Maintenance Mode Banner ---
        // --- Maintenance Mode Banner with Animation ---
        MaintenanceMode mm = new MaintenanceMode();
        if (mm.isEnabled()) {

            JPanel warnPanel = new JPanel(new BorderLayout());
            warnPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 25)); // ← half height
            warnPanel.setOpaque(true);
            warnPanel.setBackground(new Color(255, 200, 200));
            warnPanel.setBorder(new EmptyBorder(4, 10, 4, 10)); // tighten padding


            JLabel warn = new JLabel("⚠ The system is currently under Maintenance Mode.", SwingConstants.CENTER);
            warn.setFont(new Font("SansSerif", Font.BOLD, 16));
            warn.setForeground(Color.RED);

            warnPanel.add(warn, BorderLayout.CENTER);

            // --- Animation (Blink Effect Every 700 ms) ---
            Timer blink = new Timer(700, ev -> {
                if (warn.getForeground().equals(Color.RED))
                    warn.setForeground(new Color(180, 0, 0));  // darker red
                else
                    warn.setForeground(Color.RED);
            });
            blink.start();

            center.add(warnPanel, BorderLayout.NORTH);
        }


        // ----- Stats Row -----
        JPanel stats = new JPanel(new GridLayout(1, 2, 20, 20));
        stats.setOpaque(false);

        stats.add(makeStatCard("Courses Registered", countRegistered()));
        stats.add(makeStatCard("Credits Taken", computeCredits()));

        center.add(stats, BorderLayout.NORTH);

        // ----- Announcements + Actions -----
        JPanel lower = new JPanel(new GridLayout(1, 2, 20, 20));
        lower.setOpaque(false);
        lower.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

        // announcements
        JTextArea ann = new JTextArea(
                "• Course registration closes soon.\n" +
                "• Mid-semester exams approaching.\n" +
                "• Check timetables regularly.\n" +
                "• Grade reports update automatically.\n"
        );
        ann.setEditable(false);
        ann.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JPanel annPanel = new JPanel(new BorderLayout());
        annPanel.setBackground(Color.WHITE);
        annPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        annPanel.add(new JLabel("Announcements"), BorderLayout.NORTH);
        annPanel.add(ann, BorderLayout.CENTER);

        // quick actions
        JPanel actions = new JPanel();
        actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));
        actions.setBackground(Color.WHITE);
        actions.setBorder(new EmptyBorder(20, 20, 20, 20));

        JButton b1 = new JButton("Browse Catalog");
        JButton b2 = new JButton("Register for Section");
        JButton b3 = new JButton("Drop Section");
        JButton b4 = new JButton("View Timetable");
        JButton b5 = new JButton("View Grades");

        b1.addActionListener(e -> showCard("Course Catalog"));
        b2.addActionListener(e -> showCard("Register"));
        b3.addActionListener(e -> showCard("Drop Course"));
        b4.addActionListener(e -> showCard("Timetable"));
        b5.addActionListener(e -> showCard("Grades"));

        for (JButton bt : new JButton[]{b1, b2, b3, b4, b5}) {
            bt.setAlignmentX(Component.LEFT_ALIGNMENT);
            bt.setMaximumSize(new Dimension(240, 40));
            actions.add(bt);
            actions.add(Box.createVerticalStrut(10));
        }

        lower.add(annPanel);
        lower.add(actions);

        center.add(lower, BorderLayout.CENTER);
        home.add(center, BorderLayout.CENTER);

        return home;
    }

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

    private int countRegistered() {
        try (var conn = DBConnection.getErpConnection();
             var stmt = conn.prepareStatement(
                     "SELECT COUNT(*) FROM enrollments WHERE student_id = ?")) {

            stmt.setInt(1, studentId);
            var rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);

        } catch (Exception ignored) {}
        return 0;
    }

    private int computeCredits() {
        String sql = """
                SELECT SUM(c.credits)
                FROM enrollments e
                JOIN sections s ON e.section_id = s.section_id
                JOIN courses c ON s.course_id = c.code
                WHERE e.student_id = ?
                """;

        try (var conn = DBConnection.getErpConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, studentId);
            var rs = stmt.executeQuery();
            if (rs.next()) return rs.getInt(1);

        } catch (Exception ignored) {}
        return 0;
    }

    // --------------------------------------------------
    // COURSE CATALOG PANEL
    // --------------------------------------------------

    private JPanel makeCourseCatalogPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(makeTopPanel("Course Catalog"), BorderLayout.NORTH);

        try {
            ResultSet rs = student.browseCatalog();
            JTable table = TableUtils.buildStyledTable(rs);

            JScrollPane scroll = new JScrollPane(table);
            p.add(scroll, BorderLayout.CENTER);

        } catch (Exception ignored) {
            p.add(new JLabel("Failed to load catalog"), BorderLayout.CENTER);
        }

        return p;
    }

    // --------------------------------------------------
    // REGISTER PANEL
    // --------------------------------------------------

    private JPanel makeRegisterPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(makeTopPanel("Register for Section"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(2, 2, 10, 10));
        form.setBorder(new EmptyBorder(30, 200, 30, 200));

        JTextField sectionId = new JTextField();

        form.add(new JLabel("Section ID:"));
        form.add(sectionId);

        JButton reg = new JButton("Register");
        reg.addActionListener(e -> {
            MaintenanceMode mm = new MaintenanceMode();
            if (mm.isEnabled()) {
                JOptionPane.showMessageDialog(this, "System under maintenance.");
                return;
            }

            try {
                boolean ok = student.registerForSection(studentId,
                        Integer.parseInt(sectionId.getText().trim()));

                JOptionPane.showMessageDialog(this,
                        ok ? "Registered successfully." : "Failed. Either duplicate, full, or same course.");

                // AUTO REFRESH
                refreshHomeStats();
                refreshTimetable();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid section ID.");
            }
        });

        p.add(form, BorderLayout.CENTER);
        p.add(reg, BorderLayout.SOUTH);
        return p;
    }

    // --------------------------------------------------
    // DROP PANEL
    // --------------------------------------------------

    private JPanel makeDropPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(makeTopPanel("Drop Course"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(2, 2, 10, 10));
        form.setBorder(new EmptyBorder(30, 200, 30, 200));

        JTextField sectionId = new JTextField();

        form.add(new JLabel("Section ID:"));
        form.add(sectionId);

        JButton drop = new JButton("Drop");
        drop.addActionListener(e -> {
            MaintenanceMode mm = new MaintenanceMode();
            if (mm.isEnabled()) {
                JOptionPane.showMessageDialog(this, "System under maintenance.");
                return;
            }

            try {
                boolean ok = student.dropSection(studentId,
                        Integer.parseInt(sectionId.getText().trim()));

                JOptionPane.showMessageDialog(this,
                        ok ? "Course dropped." : "Drop denied.");

                // AUTO REFRESH
                refreshHomeStats();
                refreshTimetable();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid section ID.");
            }
        });

        p.add(form, BorderLayout.CENTER);
        p.add(drop, BorderLayout.SOUTH);
        return p;
    }

    // --------------------------------------------------
    // TIMETABLE PANEL
    // --------------------------------------------------

    private JPanel makeTimetablePanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(makeTopPanel("My Timetable"), BorderLayout.NORTH);

        try {
            ResultSet rs = student.viewTimetable(studentId);
            JTable table = TableUtils.buildStyledTable(rs);
            p.add(new JScrollPane(table), BorderLayout.CENTER);

        } catch (Exception ignored) {
            p.add(new JLabel("Failed to load timetable"), BorderLayout.CENTER);
        }

        return p;
    }

    // --------------------------------------------------
    // GRADES PANEL
    // --------------------------------------------------

    private JPanel makeGradesPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(makeTopPanel("My Grades"), BorderLayout.NORTH);

        JTable table;
        try {
            ResultSet rs = student.viewGrades(studentId);
            table = TableUtils.buildStyledTable(rs);

        } catch (Exception ex) {
            table = new JTable();
        }

        JScrollPane scroll = new JScrollPane(table);
        p.add(scroll, BorderLayout.CENTER);

        JButton export = new JButton("Export to CSV");
        export.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                boolean ok = student.exportGradesToCSV(studentId,
                        fc.getSelectedFile().getAbsolutePath());
                JOptionPane.showMessageDialog(this,
                        ok ? "Exported." : "Failed.");
            }
        });

        p.add(export, BorderLayout.SOUTH);
        return p;
    }

    // ---------------------------------------------------------
    // REFRESH HOME STATS
    // ---------------------------------------------------------
    private void refreshHomeStats() {
        JPanel home = (JPanel) cardPanel.getComponent(0);
        JPanel center = (JPanel) home.getComponent(1);
        JPanel statsRow = (JPanel) center.getComponent(0);

        statsRow.removeAll();

        statsRow.add(makeStatCard("Courses Registered", countRegistered()));
        statsRow.add(makeStatCard("Credits Taken", computeCredits()));

        statsRow.revalidate();
        statsRow.repaint();
    }

    // ---------------------------------------------------------
    // REFRESH TIMETABLE TABLE
    // ---------------------------------------------------------
    private void refreshTimetable() {
        JPanel timePanel = (JPanel) cardPanel.getComponent(4);

        // remove old table
        Component[] comps = timePanel.getComponents();
        for (Component c : comps) {
            if (c instanceof JScrollPane) {
                timePanel.remove(c);
            }
        }

        try {
            ResultSet rs = student.viewTimetable(studentId);
            JTable table = TableUtils.buildStyledTable(rs);
            timePanel.add(new JScrollPane(table), BorderLayout.CENTER);
        } catch (Exception ignored) {}

        timePanel.revalidate();
        timePanel.repaint();
    }

}
