package edu.univ.erp.ui;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;

public class TableUtils {

    public static JTable buildStyledTable(ResultSet rs) {
        JTable table = new JTable(resultSetToTableModel(rs));

        table.setFillsViewportHeight(true);
        table.setRowHeight(28);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(
                    JTable tbl, Object value, boolean isSelected,
                    boolean hasFocus, int row, int col) {

                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);

                if (isSelected) {
                    c.setBackground(new Color(140, 180, 210));
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(row % 2 == 0 ? new Color(245, 245, 245) : Color.WHITE);
                    c.setForeground(Color.BLACK);
                }
                setBorder(noFocusBorder);
                return c;
            }
        });

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(230, 230, 230));
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setForeground(Color.BLACK);
        header.setReorderingAllowed(false);

        return table;
    }

    public static DefaultTableModel resultSetToTableModel(ResultSet rs) {
        try {
            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();

            String[] cols = new String[colCount];
            for (int i = 1; i <= colCount; i++) cols[i - 1] = meta.getColumnLabel(i);

            DefaultTableModel model = new DefaultTableModel(cols, 0) {
                @Override
                public boolean isCellEditable(int r, int c) {
                    return false; 
                }
            };

            while (rs.next()) {
                Object[] row = new Object[colCount];
                for (int i = 1; i <= colCount; i++) row[i - 1] = rs.getObject(i);
                model.addRow(row);
            }

            return model;

        } catch (Exception e) {
            e.printStackTrace();
            return new DefaultTableModel();
        }
    }
}
