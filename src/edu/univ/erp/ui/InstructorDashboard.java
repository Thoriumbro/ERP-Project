package edu.univ.erp.ui;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.util.Map;
import edu.univ.erp.auth.MaintenanceMode;
import edu.univ.erp.access.InstructorCommands;

/**
 * Instructor dashboard with in-panel tables and logout.
 * Preserves all backend method calls.
 */
public class InstructorDashboard extends JFrame {
    private CardLayout cards = new CardLayout();
    private JPanel cardPanel;
    private InstructorCommands instructor = new InstructorCommands();
    private int instructorId;
    private JTable centerTable = new JTable();
    private JScrollPane centerScroll = new JScrollPane(centerTable);

    public InstructorDashboard(String username) {
        this.instructorId = getInstructorIdByUsername(username);

        setTitle("Instructor Dashboard - University ERP");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // sidebar
        JPanel sidebar = new JPanel(new GridLayout(0,1,10,10));
        sidebar.setBackground(new Color(31,67,59));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20,12,20,12));
        String[] labels = {"Home", "My Sections", "Enter Scores", "Final Grades", "Stats"};
        for (String s : labels) {
            JButton b = new JButton(s);
            b.setBackground(new Color(60,135,110));
            b.setForeground(Color.WHITE);
            b.setFont(new Font("SansSerif", Font.BOLD, 14));
            b.setFocusPainted(false);
            b.addActionListener(e -> cards.show(cardPanel, s));
            sidebar.add(b);
        }

        cardPanel = new JPanel(cards);
        cardPanel.add(makeTopPanel("Welcome, Instructor"), "Home");
        cardPanel.add(mySectionsPanel(), "My Sections");
        cardPanel.add(enterScoresPanel(), "Enter Scores");
        cardPanel.add(finalGradesPanel(), "Final Grades");
        cardPanel.add(statsPanel(), "Stats");

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

    private JPanel mySectionsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(makeTopPanel("My Sections"), BorderLayout.NORTH);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(new JLabel("Semester:"));
        JTextField sem = new JTextField(8);
        controls.add(sem);
        controls.add(new JLabel("Year:"));
        JTextField year = new JTextField(6);
        controls.add(year);
        JButton load = new JButton("Load");
        controls.add(load);
        p.add(controls, BorderLayout.NORTH);

        load.addActionListener(e -> {
            try {
                ResultSet rs = instructor.getMySections(instructorId, sem.getText().trim(), Integer.parseInt(year.getText().trim()));
                if (rs != null) {
                    centerTable = TableUtils.buildStyledTable(rs);
                    centerScroll.setViewportView(centerTable);
                }
                else {
                    centerTable.setModel(new javax.swing.table.DefaultTableModel());
                    JOptionPane.showMessageDialog(this, "No sections found.");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        return p;
    }

    private JPanel enterScoresPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(makeTopPanel("Enter Scores"), BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(6,2,10,10));
        form.setBorder(BorderFactory.createEmptyBorder(20,80,20,80));
        JTextField sectionId = new JTextField();
        JTextField studentId = new JTextField();
        JTextField assessment = new JTextField();
        JTextField score = new JTextField();
        JTextField weight = new JTextField();

        form.add(new JLabel("Section ID:")); form.add(sectionId);
        form.add(new JLabel("Student ID:")); form.add(studentId);
        form.add(new JLabel("Assessment:")); form.add(assessment);{
            
        }
        form.add(new JLabel("Score:")); form.add(score);
        form.add(new JLabel("Weight (%):")); form.add(weight);

        JButton save = new JButton("Save Score");
        form.add(new JLabel()); form.add(save);

        save.addActionListener(e -> {
        try {
            MaintenanceMode mm = new MaintenanceMode();

            if (mm.isEnabled()) {
                JOptionPane.showMessageDialog(this, "System is currently in maintenance mode.\nGo away.");
                return;
            }

            boolean ok = instructor.saveScore(
                    Integer.parseInt(sectionId.getText().trim()),
                    Integer.parseInt(studentId.getText().trim()),
                    assessment.getText().trim(),
                    Double.parseDouble(score.getText().trim()),
                    Double.parseDouble(weight.getText().trim())
            );

            JOptionPane.showMessageDialog(this, ok ? "Saved" : "Failed");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please fill fields correctly.");
        }
    });


        p.add(form, BorderLayout.CENTER);
        return p;
    }

    private JPanel finalGradesPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(makeTopPanel("Compute Final Grades"), BorderLayout.NORTH);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField sect = new JTextField(8);
        top.add(new JLabel("Section ID:")); top.add(sect);
        JButton load = new JButton("Compute");
        top.add(load);
        p.add(top, BorderLayout.NORTH);

        load.addActionListener(e -> {
            try {
                var map = instructor.computeFinalGrades(Integer.parseInt(sect.getText().trim()));
                if (map.isEmpty()) {
                    centerTable.setModel(new javax.swing.table.DefaultTableModel());
                    JOptionPane.showMessageDialog(this, "No grades found.");
                    return;
                }
                // turn map into table model
                javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(new String[]{"Student ID","Final Grade"}, 0);
                map.forEach((k,v) -> model.addRow(new Object[]{k, String.format("%.2f", v)}));
                centerTable.setModel(model);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid section id.");
            }
        });

        return p;
    }

    private JPanel statsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        p.add(makeTopPanel("Class Stats"), BorderLayout.NORTH);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JTextField sect = new JTextField(8);
        top.add(new JLabel("Section ID:")); top.add(sect);
        JButton load = new JButton("Load Stats");
        top.add(load);
        p.add(top, BorderLayout.NORTH);

        load.addActionListener(e -> {
            try {
                Map<String, Double> stats = instructor.getClassStats(Integer.parseInt(sect.getText().trim()));
                if (stats.isEmpty()) {
                    centerTable.setModel(new javax.swing.table.DefaultTableModel());
                    JOptionPane.showMessageDialog(this, "No data.");
                    return;
                }
                javax.swing.table.DefaultTableModel model = new javax.swing.table.DefaultTableModel(new String[]{"Metric","Value"}, 0);
                model.addRow(new Object[]{"Average", String.format("%.2f", stats.get("average"))});
                model.addRow(new Object[]{"Min", String.format("%.2f", stats.get("min"))});
                model.addRow(new Object[]{"Max", String.format("%.2f", stats.get("max"))});
                centerTable.setModel(model);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Enter a valid section id.");
            }
        });

        return p;
    }

    // preserve your original resolver
    private int getInstructorIdByUsername(String username) {
    String sql =
        "SELECT i.user_id FROM instructors i " +
        "JOIN auth_db.users u ON i.user_id = u.id " +
        "WHERE u.username = ?";

    try (java.sql.Connection conn = edu.univ.erp.data.DBConnection.getErpConnection();
         java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, username);
        java.sql.ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getInt("user_id");
        }
    } catch (Exception e) {
        e.printStackTrace();
    }
    return -1;
}

}
