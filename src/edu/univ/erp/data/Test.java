package edu.univ.erp.data;

import javax.swing.*;
import com.toedter.calendar.JDateChooser;
import java.awt.*;

public class Test {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Date Picker");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDate(new java.util.Date());

        frame.add(dateChooser, BorderLayout.CENTER);
        frame.setSize(300, 200);
        frame.setVisible(true);
    }
}
