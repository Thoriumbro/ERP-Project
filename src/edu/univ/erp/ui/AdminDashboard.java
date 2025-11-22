package edu.univ.erp.ui;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;

import edu.univ.erp.access.AdminCommands;
import edu.univ.erp.auth.MaintenanceMode;

/**
 * Admin dashboard - embedded forms + tables, logout available.
 * Does not change backend method calls.
 */
public class AdminDashboard extends JFrame {
    private CardLayout cards = new CardLayout();
    private JPanel cardPanel;
    private final AdminCommands admin = new AdminCommands();
    private JTable centerTable = new JTable();
    private JScrollPane centerScroll = new JScrollPane(centerTable);

    public AdminDashboard() {
        setTitle("Admin Dashboard - University ERP");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel sidebar = new JPanel(new GridLayout(0,1,10,10));
        sidebar.setBackground(new Color(31,67,59));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20,12,20,12));

        String[] items = {"Home", "Users", "Courses", "Sections", "Settings"};
        for (String it : items) {
            JButton b = new JButton(it);
            b.setBackground(new Color(60,135,110));
            b.setForeground(Color.WHITE);
            b.setFont(new Font("SansSerif", Font.BOLD, 14));
            b.setFocusPainted(false);
            b.addActionListener(e -> cards.show(cardPanel, it));
            sidebar.add(b);
        }

        cardPanel = new JPanel(cards);
        cardPanel.add(makeTopPanel("Admin Control Panel"), "Home");
        cardPanel.add(usersPanel(), "Users");
        cardPanel.add(coursesPanel(), "Courses");
        cardPanel.add(sectionsPanel(), "Sections");
        cardPanel.add(settingsPanel(), "Settings");

        add(sidebar, BorderLayout.WEST);
        add(cardPanel, BorderLayout.CENTER);

        cards.show(cardPanel, "Home");
        setVisible(true);
    }

    private JPanel makeTopPanel(String titleText) {
        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(BorderFactory.createEmptyBorder(12,12,12,12));
        top.setBackground(new Color(245,247,246));

        JLabel title = new JLabel(titleText);
        title.setFont(new Font("SansSerif", Font.BOLD, 20));

        JButton logout = new JButton("Logout");
        logout.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new LoginApp());
        });

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setOpaque(false);
        right.add(logout);

        top.add(title, BorderLayout.WEST);
        top.add(right, BorderLayout.EAST);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(top, BorderLayout.NORTH);
        centerTable.setModel(new javax.swing.table.DefaultTableModel());
        wrapper.add(centerScroll, BorderLayout.CENTER);
        return wrapper;
    }

    private JPanel usersPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(makeTopPanel("Manage Users"), BorderLayout.NORTH);

        JPanel formWrap = new JPanel(new GridLayout(1,2,12,12));
        JPanel left = new JPanel(); left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(BorderFactory.createTitledBorder("Add Student"));

        JTextField sName = new JTextField(); JTextField sUsername = new JTextField(); JPasswordField sPassword = new JPasswordField();
        JTextField sRoll = new JTextField(); JTextField sProgram = new JTextField(); JTextField sYear = new JTextField();

        addFormRow(left, "Name", sName);
        addFormRow(left, "Username", sUsername);
        addFormRow(left, "Password", sPassword);
        addFormRow(left, "Roll No", sRoll);
        addFormRow(left, "Program", sProgram);
        addFormRow(left, "Year", sYear);

        JButton createStudent = new JButton("Create Student");
        createStudent.addActionListener(e -> {
            try {
                boolean ok = admin.addStudent(sUsername.getText().trim(), new String(sPassword.getPassword()), sName.getText().trim(),
                                sRoll.getText().trim(), sProgram.getText().trim(), Integer.parseInt(sYear.getText().trim())
);

                JOptionPane.showMessageDialog(this, ok? "Student created." : "Failed");
                if (ok) { sName.setText(""); sUsername.setText(""); sPassword.setText(""); sRoll.setText(""); sProgram.setText(""); sYear.setText(""); }
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Fill fields correctly."); }
        });
        left.add(createStudent);

        JPanel right = new JPanel(); 
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBorder(BorderFactory.createTitledBorder("Add Instructor / Admin"));

        JTextField iName = new JTextField(); JTextField iUsername = new JTextField(); JPasswordField iPassword = new JPasswordField(); JTextField iDept = new JTextField();
        addFormRow(right, "Name", iName);
        addFormRow(right, "Username", iUsername);
        addFormRow(right, "Password", iPassword);
        addFormRow(right, "Department", iDept);
        JButton addInstructor = new JButton("Create Instructor");
        addInstructor.addActionListener(e -> {
            boolean ok = admin.addInstructor(iUsername.getText().trim(), new String(iPassword.getPassword()), iName.getText().trim(), iDept.getText().trim());
            JOptionPane.showMessageDialog(this, ok? "Instructor created." : "Failed");
            if (ok) { iName.setText(""); iUsername.setText(""); iPassword.setText(""); iDept.setText(""); }
        });
        right.add(addInstructor);

        right.add(Box.createVerticalStrut(20));

        JLabel adminTitle = new JLabel("Add Admin");
        adminTitle.setFont(new Font("SansSerif", Font.BOLD, 14));
        right.add(adminTitle);

        JTextField aUsername = new JTextField();
        JPasswordField aPassword = new JPasswordField();

        addFormRow(right, "Admin Username", aUsername);
        addFormRow(right, "Admin Password", aPassword);

        JButton addAdmin = new JButton("Create Admin");
        addAdmin.addActionListener(e -> {
            boolean ok = admin.addAdmin(
                    aUsername.getText().trim(),
                    new String(aPassword.getPassword())
            );
            JOptionPane.showMessageDialog(this, ok ? "Admin created." : "Failed");

            if (ok) {
                aUsername.setText("");
                aPassword.setText("");
            }
        });
        right.add(addAdmin);

        formWrap.add(left); formWrap.add(right);
        p.add(formWrap, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton loadUsers = new JButton("Load Users");
        bottom.add(loadUsers);
        p.add(bottom, BorderLayout.SOUTH);

        loadUsers.addActionListener(e -> {
            try {
                ResultSet rs = adminListUsers();
                if (rs != null) {
                    centerTable = TableUtils.buildStyledTable(rs);
                    centerScroll.setViewportView(centerTable);
                }
                else centerTable.setModel(new javax.swing.table.DefaultTableModel());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error loading users: " + ex.getMessage());
            }
        });

        return p;
    }

    private JPanel coursesPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(makeTopPanel("Courses"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(4,2,8,8));
        form.setBorder(BorderFactory.createEmptyBorder(20,120,20,120));
        JTextField code = new JTextField(); JTextField title = new JTextField(); JTextField credits = new JTextField();
        form.add(new JLabel("Code:")); form.add(code);
        form.add(new JLabel("Title:")); form.add(title);
        form.add(new JLabel("Credits:")); form.add(credits);
        JButton add = new JButton("Add Course");
        add.addActionListener(e -> {
            try {
                boolean ok = admin.addCourse(code.getText().trim(), title.getText().trim(), Integer.parseInt(credits.getText().trim()));
                JOptionPane.showMessageDialog(this, ok? "Course added." : "Failed");
                if (ok) { code.setText(""); title.setText(""); credits.setText(""); }
            } catch (Exception ex) { JOptionPane.showMessageDialog(this, "Enter valid credits."); }
        });
        p.add(form, BorderLayout.CENTER);
        p.add(add, BorderLayout.SOUTH);
        return p;
    }

    private JPanel sectionsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(makeTopPanel("Sections"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(8,2,8,8));
        form.setBorder(BorderFactory.createEmptyBorder(10,80,10,80));
        JTextField courseCode = new JTextField(); JTextField instructorId = new JTextField(); JTextField dayTime = new JTextField();
        JTextField room = new JTextField(); JTextField capacity = new JTextField(); JTextField semester = new JTextField(); JTextField year = new JTextField();
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
                boolean ok = admin.addSection(courseCode.getText().trim(), Integer.parseInt(instructorId.getText().trim()),
                        dayTime.getText().trim(), room.getText().trim(), Integer.parseInt(capacity.getText().trim()),
                        semester.getText().trim(), Integer.parseInt(year.getText().trim()));
                JOptionPane.showMessageDialog(this, ok? "Section added." : "Failed");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Fill fields correctly.");
            }
        });

        p.add(form, BorderLayout.CENTER);
        p.add(create, BorderLayout.SOUTH);
        return p;
    }

    private JPanel settingsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(makeTopPanel("Settings"), BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        JLabel note = new JLabel("Maintenance Mode");
        body.add(note);

        MaintenanceMode mm = new MaintenanceMode();
        boolean currentState = mm.isEnabled();

        JToggleButton toggle = new JToggleButton(currentState ? "On" : "Off");
        toggle.setSelected(currentState);

        toggle.addActionListener(e -> {
            if (toggle.isSelected()) {
                mm.enable();
                toggle.setText("On");
            } else {
                mm.disable();
                toggle.setText("Off");
            }
        });

        body.add(Box.createVerticalStrut(8));
        body.add(toggle);

        p.add(body, BorderLayout.CENTER);
        return p;
    }


    private void addFormRow(JPanel parent, String label, JComponent field) {
        JPanel row = new JPanel(new BorderLayout(6,6));
        row.add(new JLabel(label), BorderLayout.NORTH);
        field.setPreferredSize(new Dimension(220,26));
        row.add(field, BorderLayout.CENTER);
        parent.add(row);
        parent.add(Box.createVerticalStrut(6));
    }

    // helper to fetch users list - uses your existing DB structure (adjust if needed)
    private ResultSet adminListUsers() {
        try {
            // your AdminCommands doesn't have a listing method; if you have one, call it.
            // fallback: query directly (requires DBConnection)
            java.sql.Connection conn = edu.univ.erp.data.DBConnection.getErpConnection();
            java.sql.PreparedStatement stmt = conn.prepareStatement("SELECT id, username, role, last_login FROM auth_db.users");
            return stmt.executeQuery();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
