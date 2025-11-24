package edu.univ.erp.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.sql.ResultSet;

import edu.univ.erp.access.AdminCommands;
import edu.univ.erp.data.DBConnection;
import edu.univ.erp.auth.MaintenanceMode;

public class AdminDashboard extends JFrame {
    private CardLayout cards = new CardLayout();
    private JPanel cardPanel;
    private final AdminCommands admin = new AdminCommands();

    private JTable centerTable = new JTable();
    private final JScrollPane centerScroll = new JScrollPane(centerTable);

    public AdminDashboard() {
        setTitle("Admin Dashboard - University ERP");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel sidebar = new JPanel(new GridLayout(0, 1, 10, 10));
        sidebar.setBackground(new Color(31, 67, 59));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 12, 20, 12));

        String[] items = {"Home", "Users", "Courses", "Sections", "Settings"};
        for (String it : items) {
            JButton b = new JButton(it);
            styleSidebarButton(b);
            b.addActionListener(e -> showCard(it));
            sidebar.add(b);
        }

        cardPanel = new JPanel(cards);
        cardPanel.add(makeHomePanel(), "Home");
        cardPanel.add(makeUsersPanel(), "Users");
        cardPanel.add(makeCoursesPanel(), "Courses");
        cardPanel.add(makeSectionsPanel(), "Sections");
        cardPanel.add(makeSettingsPanel(), "Settings");

        add(sidebar, BorderLayout.WEST);
        add(cardPanel, BorderLayout.CENTER);

        showCard("Home");
        setVisible(true);
    }

    private void styleSidebarButton(JButton b) {
        b.setBackground(new Color(60, 135, 110));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("SansSerif", Font.BOLD, 14));
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
    }

    private JPanel makeTopPanel(String titleText) {
        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        top.setBackground(new Color(245, 247, 246));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));

        JButton logout = new JButton("Logout");
        logout.setBackground(new Color(200, 60, 60));
        logout.setForeground(Color.WHITE);
        logout.setFocusPainted(false);
        logout.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(LoginApp::new);
        });

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        right.setOpaque(false);
        right.add(logout);

        top.add(title, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(top, BorderLayout.NORTH);

        wrapper.add(centerScroll, BorderLayout.CENTER);
        return wrapper;
    }

    //-----------------------------------------------------------
    // HOME PANEL
    //-----------------------------------------------------------
    private JPanel makeHomePanel() {

    JPanel home = new JPanel(new BorderLayout());
    home.setBackground(new Color(245, 247, 246));

    // ---------------- TOP BAR ------------------
    JPanel top = new JPanel(new BorderLayout());
    top.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
    top.setBackground(new Color(245, 247, 246));

    JLabel title = new JLabel("Admin Dashboard");
    title.setFont(new Font("SansSerif", Font.BOLD, 26));

    JButton logout = new JButton("Logout");
    logout.setBackground(new Color(200, 60, 60));
    logout.setForeground(Color.WHITE);
    logout.addActionListener(e -> {
        dispose();
        new LoginApp();
    });

    JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    right.setOpaque(false);
    right.add(logout);

    top.add(title, BorderLayout.WEST);
    top.add(right, BorderLayout.EAST);
    home.add(top, BorderLayout.NORTH);


    // ---------------- CENTER AREA ------------------
    JPanel center = new JPanel();
    center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));

    center.setBorder(BorderFactory.createEmptyBorder(20, 40, 40, 40));
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
                    warn.setForeground(new Color(180, 0, 0));   // darker red
                else
                    warn.setForeground(Color.RED);
            });
            blink.start();

            center.add(warnPanel, BorderLayout.NORTH);
        }


    // ----------- Big Stats Row ---------------
    JPanel statsRow = new JPanel(new GridLayout(1, 3, 20, 20));
    statsRow.setOpaque(false);

    statsRow.add(makeStatCard("Total Users", getCount("auth_db.users")));
    statsRow.add(makeStatCard("Total Courses", getCount("erp_db.courses")));
    statsRow.add(makeStatCard("Total Sections", getCount("erp_db.sections")));

    center.add(statsRow, BorderLayout.NORTH);


    // ------------ LOWER GRID (Announcements + Actions) ----------------
    JPanel lower = new JPanel(new GridLayout(1, 2, 20, 20));
    lower.setOpaque(false);
    lower.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));


    // ----------- Announcements Panel ----------------
    JPanel ann = new JPanel();
    ann.setLayout(new BorderLayout());
    ann.setBackground(Color.WHITE);
    ann.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    JLabel aTitle = new JLabel("Announcements");
    aTitle.setFont(new Font("SansSerif", Font.BOLD, 16));

    JTextArea msgs = new JTextArea(
            "• System running normally.\n" +
            "• Next enrollment window opens soon.\n"+
            "• Remember to review course deadlines.\n"+
            "• Maintenance scheduled for next weekend."
    );
    msgs.setEditable(false);
    msgs.setFont(new Font("SansSerif", Font.PLAIN, 14));
    msgs.setBackground(new Color(250, 250, 250));

    ann.add(aTitle, BorderLayout.NORTH);
    ann.add(msgs, BorderLayout.CENTER);


    // ----------- Quick Actions Panel ----------------
    JPanel actions = new JPanel();
    actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));
    actions.setBackground(Color.WHITE);
    actions.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    JLabel actTitle = new JLabel("Quick Actions");
    actTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
    actTitle.setAlignmentX(Component.LEFT_ALIGNMENT);

    JButton b1 = new JButton("Manage Users");
    JButton b2 = new JButton("Manage Courses");
    JButton b3 = new JButton("Manage Sections");

    for (JButton btn : new JButton[]{b1, b2, b3}) {
        btn.setMaximumSize(new Dimension(200, 40));
        btn.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    b1.addActionListener(e -> showCard("Users"));
    b2.addActionListener(e -> showCard("Courses"));
    b3.addActionListener(e -> showCard("Sections"));

    actions.add(actTitle);
    actions.add(Box.createVerticalStrut(15));
    actions.add(b1);
    actions.add(Box.createVerticalStrut(10));
    actions.add(b2);
    actions.add(Box.createVerticalStrut(10));
    actions.add(b3);

    lower.add(ann);
    lower.add(actions);


    center.add(lower, BorderLayout.CENTER);

    home.add(center, BorderLayout.CENTER);
    return home;
}


    private JPanel makeStatCard(String title, int count) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setBackground(Color.WHITE);
        card.setPreferredSize(new Dimension(200, 150));

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

    private int getCount(String tablePath) {
        try {
            String sql = "SELECT COUNT(*) FROM " + tablePath;

            java.sql.Connection conn =
                    tablePath.startsWith("auth_db")
                            ? DBConnection.getAuthConnection()
                            : DBConnection.getErpConnection();

            java.sql.PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    //-----------------------------------------------------------
    // USERS PANEL
    //-----------------------------------------------------------
    private JPanel makeUsersPanel() {

    JPanel main = new JPanel(new BorderLayout());
    main.add(makeTopPanel("Manage Users"), BorderLayout.NORTH);

    // ======================================================
    // 1) CREATE A CARDLAYOUT FOR: SELECTION SCREEN + 3 FORMS
    // ======================================================
    CardLayout userCards = new CardLayout();
    JPanel container = new JPanel(userCards);

    // ------------------------------------------------------
    // SCREEN A: SELECTION PAGE (3 SKY-BLUE OPTION BUBBLES)
    // ------------------------------------------------------
    JPanel selectScreen = new JPanel(new GridBagLayout());
    selectScreen.setBackground(new Color(245, 247, 246));

    JPanel optionsRow = new JPanel(new GridLayout(1, 3, 30, 0));
    optionsRow.setOpaque(false);

    optionsRow.add(makeUserBubble("Add Student", () -> userCards.show(container, "student")));
    optionsRow.add(makeUserBubble("Add Instructor", () -> userCards.show(container, "instructor")));
    optionsRow.add(makeUserBubble("Add Admin", () -> userCards.show(container, "admin")));

    selectScreen.add(optionsRow);

    // ------------------------------------------------------
    // SCREEN B1: STUDENT FORM
    // ------------------------------------------------------
    JPanel studentForm = new JPanel(new BorderLayout(12,12));
    studentForm.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

    JPanel left = new JPanel();
    left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
    left.setBorder(BorderFactory.createTitledBorder("Add Student"));

    JTextField sName = new JTextField();
    JTextField sUsername = new JTextField();
    JPasswordField sPassword = new JPasswordField();
    JTextField sRoll = new JTextField();
    JTextField sProgram = new JTextField();
    JTextField sYear = new JTextField();

    addFormRow(left, "Name", sName);
    addFormRow(left, "Username", sUsername);
    addFormRow(left, "Password", sPassword);
    addFormRow(left, "Roll No", sRoll);
    addFormRow(left, "Program", sProgram);
    addFormRow(left, "Year", sYear);

    JButton createStudent = new JButton("Create Student");
    createStudent.addActionListener(e -> {
        try {
            boolean ok = admin.addStudent(
                    sUsername.getText().trim(),
                    new String(sPassword.getPassword()),
                    sName.getText().trim(),
                    sRoll.getText().trim(),
                    sProgram.getText().trim(),
                    Integer.parseInt(sYear.getText().trim())
            );
            JOptionPane.showMessageDialog(this, ok ? "Student created." : "Failed.");
            if (ok) loadUsers();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: Enter valid data.");
        }
    });

    left.add(createStudent);
    left.add(Box.createVerticalStrut(15));

    JButton back1 = new JButton("Back");
    back1.addActionListener(e -> userCards.show(container, "select"));
    left.add(back1);

    studentForm.add(left, BorderLayout.CENTER);


    // ------------------------------------------------------
    // SCREEN B2: INSTRUCTOR FORM
    // ------------------------------------------------------
    JPanel instructorForm = new JPanel(new BorderLayout(12,12));
    instructorForm.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

    JPanel rightI = new JPanel();
    rightI.setLayout(new BoxLayout(rightI, BoxLayout.Y_AXIS));
    rightI.setBorder(BorderFactory.createTitledBorder("Add Instructor"));

    JTextField iName = new JTextField();
    JTextField iUsername = new JTextField();
    JPasswordField iPassword = new JPasswordField();
    JTextField iDept = new JTextField();

    addFormRow(rightI, "Name", iName);
    addFormRow(rightI, "Username", iUsername);
    addFormRow(rightI, "Password", iPassword);
    addFormRow(rightI, "Department", iDept);

    JButton createInstructor = new JButton("Create Instructor");
    createInstructor.addActionListener(e -> {
        boolean ok = admin.addInstructor(
                iUsername.getText().trim(),
                new String(iPassword.getPassword()),
                iName.getText().trim(),
                iDept.getText().trim()
        );
        JOptionPane.showMessageDialog(this, ok ? "Instructor created." : "Failed.");
        if (ok) loadUsers();
    });
    rightI.add(createInstructor);

    JButton back2 = new JButton("Back");
    back2.addActionListener(e -> userCards.show(container, "select"));
    rightI.add(back2);

    instructorForm.add(rightI, BorderLayout.CENTER);


    // ------------------------------------------------------
    // SCREEN B3: ADMIN FORM
    // ------------------------------------------------------
    JPanel adminForm = new JPanel(new BorderLayout(12,12));
    adminForm.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

    JPanel rightA = new JPanel();
    rightA.setLayout(new BoxLayout(rightA, BoxLayout.Y_AXIS));
    rightA.setBorder(BorderFactory.createTitledBorder("Add Admin"));

    JTextField aUsername = new JTextField();
    JPasswordField aPassword = new JPasswordField();

    addFormRow(rightA, "Admin Username", aUsername);
    addFormRow(rightA, "Admin Password", aPassword);

    JButton createAdmin = new JButton("Create Admin");
    createAdmin.addActionListener(e -> {
        boolean ok = admin.addAdmin(aUsername.getText().trim(), new String(aPassword.getPassword()));
        JOptionPane.showMessageDialog(this, ok ? "Admin created." : "Failed.");
        if (ok) loadUsers();
    });
    rightA.add(createAdmin);

    JButton back3 = new JButton("Back");
    back3.addActionListener(e -> userCards.show(container, "select"));
    rightA.add(back3);

    adminForm.add(rightA, BorderLayout.CENTER);


    // ------------------------------------------------------
    // ADD ALL SCREENS TO CARDLAYOUT
    // ------------------------------------------------------
    container.add(selectScreen, "select");
    container.add(studentForm, "student");
    container.add(instructorForm, "instructor");
    container.add(adminForm, "admin");

    userCards.show(container, "select"); // default first screen

    main.add(container, BorderLayout.CENTER);
    return main;
}

    private JPanel makeUserBubble(String text, Runnable callback) {

    JPanel bubble = new JPanel();
    bubble.setPreferredSize(new Dimension(180, 180));
    bubble.setBackground(new Color(173, 216, 230)); // sky blue
    bubble.setBorder(BorderFactory.createLineBorder(new Color(120, 170, 200), 2));
    bubble.setLayout(new GridBagLayout());

    JLabel lbl = new JLabel(text);
    lbl.setFont(new Font("SansSerif", Font.BOLD, 16));

    bubble.add(lbl);

    bubble.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

    bubble.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent e) {
            callback.run();
        }
    });

    return bubble;
}


    //-----------------------------------------------------------
    // COURSES PANEL (DEADLINE INCLUDED)
    //-----------------------------------------------------------
    private JPanel makeCoursesPanel() {
        JPanel p = new JPanel(new BorderLayout(12, 12));
        p.add(makeTopPanel("Courses"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(4, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(20, 120, 20, 120));

        JTextField code = new JTextField();
        JTextField title = new JTextField();
        JTextField credits = new JTextField();
        JTextField deadline = new JTextField();
        deadline.setToolTipText("Format: YYYY-MM-DD");

        form.add(new JLabel("Code:")); form.add(code);
        form.add(new JLabel("Title:")); form.add(title);
        form.add(new JLabel("Credits:")); form.add(credits);
        form.add(new JLabel("Deadline (YYYY-MM-DD):")); form.add(deadline);

        JButton add = new JButton("Add Course");
        add.addActionListener(e -> {
            try {
                java.time.LocalDate parsed =
                        java.time.LocalDate.parse(deadline.getText().trim());

                boolean ok = admin.addCourse(
                        code.getText().trim(),
                        title.getText().trim(),
                        Integer.parseInt(credits.getText().trim()),
                        java.sql.Date.valueOf(parsed)
                );

                JOptionPane.showMessageDialog(this, ok ? "Course added." : "Failed.");
                if (ok) loadCourses();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Enter valid data.");
            }
        });

        p.add(form, BorderLayout.CENTER);
        p.add(add, BorderLayout.SOUTH);

                //-----------------------------------------------------------
        // EDIT COURSE PANEL (ADDED)
        //-----------------------------------------------------------
        JPanel editPanel = new JPanel(new GridLayout(4, 2, 8, 8));
        editPanel.setBorder(BorderFactory.createTitledBorder("Edit Course"));

        String[] allowedCourseFields = {"title", "credits", "deadline"};
        JComboBox<String> courseField = new JComboBox<>(allowedCourseFields);
        JTextField courseNewValue = new JTextField();
        JTextField courseCodeField = new JTextField();

        editPanel.add(new JLabel("Field:")); editPanel.add(courseField);
        editPanel.add(new JLabel("New Value:")); editPanel.add(courseNewValue);
        editPanel.add(new JLabel("Course Code:")); editPanel.add(courseCodeField);

        JButton applyCourseEdit = new JButton("Apply Update");
        applyCourseEdit.addActionListener(e -> {
            try {
                String fld = (String) courseField.getSelectedItem();
                String codeVal = courseCodeField.getText().trim();
                String newVal = courseNewValue.getText().trim();

                Object parsedValue = newVal;
                if ("credits".equals(fld)) {
                    parsedValue = Integer.parseInt(newVal);
                } else if ("deadline".equals(fld)) {
                    parsedValue = java.time.LocalDate.parse(newVal);
                }

                boolean ok = admin.editCourse(fld, parsedValue, codeVal);
                JOptionPane.showMessageDialog(this, ok ? "Course updated." : "Update failed.");

                if (ok) loadCourses();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid data.");
            }
        });

        editPanel.add(new JLabel());
        editPanel.add(applyCourseEdit);

        p.add(editPanel, BorderLayout.EAST);


        return p;
    }

    //-----------------------------------------------------------
    // SECTIONS PANEL
    //-----------------------------------------------------------
    private JPanel makeSectionsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(makeTopPanel("Sections"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(7, 2, 8, 8));
        form.setBorder(BorderFactory.createEmptyBorder(20, 120, 20, 120));

        JTextField courseCode = new JTextField();
        JTextField instructorId = new JTextField();
        JTextField dayTime = new JTextField();
        JTextField room = new JTextField();
        JTextField capacity = new JTextField();
        JTextField semester = new JTextField();
        JTextField year = new JTextField();

        form.add(new JLabel("Course Code:")); form.add(courseCode);
        form.add(new JLabel("Instructor ID:")); form.add(instructorId);
        form.add(new JLabel("Day/Time:")); form.add(dayTime);
        form.add(new JLabel("Room:")); form.add(room);
        form.add(new JLabel("Capacity:")); form.add(capacity);
        form.add(new JLabel("Semester:")); form.add(semester);
        form.add(new JLabel("Year:")); form.add(year);

        JButton create = new JButton("Create Section");
        create.addActionListener(e -> {
            try {
                boolean ok = admin.addSection(
                        courseCode.getText().trim(),
                        Integer.parseInt(instructorId.getText().trim()),
                        dayTime.getText().trim(),
                        room.getText().trim(),
                        Integer.parseInt(capacity.getText().trim()),
                        semester.getText().trim(),
                        Integer.parseInt(year.getText().trim())
                );
                JOptionPane.showMessageDialog(this, ok ? "Section added." : "Failed.");
                if (ok) loadSections();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input.");
            }
        });

        p.add(form, BorderLayout.CENTER);
        p.add(create, BorderLayout.SOUTH);

        //-----------------------------------------------------------
        // EDIT SECTION PANEL (ADDED)
        //-----------------------------------------------------------
        JPanel editPanel = new JPanel(new GridLayout(4, 2, 8, 8));
        editPanel.setBorder(BorderFactory.createTitledBorder("Edit Section"));

        String[] allowedSecFields = {"day_time", "room", "capacity", "semester", "year", "instructor_id"};
        JComboBox<String> secField = new JComboBox<>(allowedSecFields);
        JTextField secNewValue = new JTextField();
        JTextField secId = new JTextField();

        editPanel.add(new JLabel("Field:")); editPanel.add(secField);
        editPanel.add(new JLabel("New Value:")); editPanel.add(secNewValue);
        editPanel.add(new JLabel("Section ID:")); editPanel.add(secId);

        JButton applySectionEdit = new JButton("Apply Update");
        applySectionEdit.addActionListener(e -> {
            try {
                String fld = (String) secField.getSelectedItem();
                String newVal = secNewValue.getText().trim();
                int idVal = Integer.parseInt(secId.getText().trim());

                Object parsedValue = newVal;
                if (fld.equals("capacity") || fld.equals("year") || fld.equals("instructor_id")) {
                    parsedValue = Integer.parseInt(newVal);
                }

                boolean ok = admin.editSection(fld, parsedValue, idVal);
                JOptionPane.showMessageDialog(this, ok ? "Section updated." : "Update failed.");

                if (ok) loadSections();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid data.");
            }
        });

        editPanel.add(new JLabel());
        editPanel.add(applySectionEdit);

        p.add(editPanel, BorderLayout.EAST);


        return p;
    }

    //-----------------------------------------------------------
    // SETTINGS PANEL
    //-----------------------------------------------------------
    private JPanel makeSettingsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(makeTopPanel("Settings"), BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        JLabel note = new JLabel("Maintenance Mode");
        body.add(note);

        MaintenanceMode mm = new MaintenanceMode();
        boolean enabled = mm.isEnabled();

        JToggleButton toggle = new JToggleButton(enabled ? "On" : "Off");
        toggle.setSelected(enabled);

        toggle.addActionListener(e -> {
            if (toggle.isSelected()) { mm.enable(); toggle.setText("On"); }
            else { mm.disable(); toggle.setText("Off"); }
        });

        body.add(Box.createVerticalStrut(10));
        body.add(toggle);

        p.add(body, BorderLayout.CENTER);
        return p;
    }

    //-----------------------------------------------------------
    // UTILITY: Add form row
    //-----------------------------------------------------------
    private void addFormRow(JPanel parent, String label, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(6, 6));
        row.setOpaque(false);

        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("SansSerif", Font.PLAIN, 13));

        field.setPreferredSize(new Dimension(220, 26));

        row.add(lbl, BorderLayout.NORTH);
        row.add(field, BorderLayout.CENTER);

        parent.add(row);
        parent.add(Box.createVerticalStrut(6));
    }

    //-----------------------------------------------------------
    // DATA LOADING HELPERS
    //-----------------------------------------------------------
    private void loadUsers() {
        try (var conn = DBConnection.getAuthConnection();
             var stmt = conn.prepareStatement("SELECT id, username, role, last_login FROM auth_db.users");
             var rs = stmt.executeQuery()) {

            centerTable = TableUtils.buildStyledTable(rs);
            centerScroll.setViewportView(centerTable);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load users.");
        }
    }

    private void loadCourses() {
        try (var conn = DBConnection.getErpConnection();
             var stmt = conn.prepareStatement("SELECT code, title, credits, deadline FROM erp_db.courses");
             var rs = stmt.executeQuery()) {

            centerTable = TableUtils.buildStyledTable(rs);
            centerScroll.setViewportView(centerTable);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load courses.");
        }
    }

    private void loadSections() {
        try (var conn = DBConnection.getErpConnection();
             var stmt = conn.prepareStatement(
                     "SELECT section_id, course_id, instructor_id, day_time, room, capacity, semester, year FROM erp_db.sections");
             var rs = stmt.executeQuery()) {

            centerTable = TableUtils.buildStyledTable(rs);
            centerScroll.setViewportView(centerTable);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Failed to load sections.");
        }
    }

    //-----------------------------------------------------------
    // ROUTING: Show panel + preload its data
    //-----------------------------------------------------------
    private void showCard(String name) {
        switch (name) {
            case "Users":    loadUsers(); break;
            case "Courses":  loadCourses(); break;
            case "Sections": loadSections(); break;
        }
        cards.show(cardPanel, name);
    }
}
