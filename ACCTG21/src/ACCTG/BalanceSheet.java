package ACCTG;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class BalanceSheet extends JPanel {
    private JTable assetsTable;
    private JTable liabilitiesTable;
    private DefaultTableModel assetsModel;
    private DefaultTableModel liabilitiesModel;

    public BalanceSheet() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // ===== HEADER BAR =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0, 150, 150)));

        JLabel title = new JLabel("Balance Sheet");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(40, 40, 40));
        title.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 10));
        headerPanel.add(title, BorderLayout.WEST);

        // Print button (styled)
        JButton printButton = new JButton("🖨 Print");
        printButton.setFocusPainted(false);
        printButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        printButton.setBackground(new Color(0, 150, 150));
        printButton.setForeground(Color.WHITE);
        printButton.setBorder(BorderFactory.createEmptyBorder(6, 14, 6, 14));
        printButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        printButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 120, 120), 1),
                BorderFactory.createEmptyBorder(6, 14, 6, 14)
        ));
        printButton.setFocusable(false);

        headerPanel.add(printButton, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // ===== MAIN CONTENT =====
        JPanel contentPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));

        // ===== LEFT SIDE: ASSETS =====
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);

        JLabel assetLabel = new JLabel("Assets");
        assetLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        assetLabel.setForeground(new Color(60, 60, 60));
        assetLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 8, 0));
        leftPanel.add(assetLabel, BorderLayout.NORTH);

        String[] assetCols = {"Asset", "Amount"};
        assetsModel = new DefaultTableModel(assetCols, 0);
        assetsTable = createStyledTable(assetsModel);
        JScrollPane assetScroll = new JScrollPane(assetsTable);
        assetScroll.setBorder(BorderFactory.createEmptyBorder());
        leftPanel.add(assetScroll, BorderLayout.CENTER);

        // Add total row
        assetsModel.addRow(new Object[]{"Total Assets", String.format("%.2f", 0.00)});

        // ===== RIGHT SIDE: LIABILITIES & EQUITY =====
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);

        JLabel liabLabel = new JLabel("Liabilities & Equity");
        liabLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        liabLabel.setForeground(new Color(60, 60, 60));
        liabLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 8, 0));
        rightPanel.add(liabLabel, BorderLayout.NORTH);

        String[] liabCols = {"Liabilities & Equity", "Amount"};
        liabilitiesModel = new DefaultTableModel(liabCols, 0);
        liabilitiesTable = createStyledTable(liabilitiesModel);
        JScrollPane liabScroll = new JScrollPane(liabilitiesTable);
        liabScroll.setBorder(BorderFactory.createEmptyBorder());
        rightPanel.add(liabScroll, BorderLayout.CENTER);

        // Add total row
        liabilitiesModel.addRow(new Object[]{"Total Liabilities & Equity", String.format("%.2f", 0.00)});

        // Add both panels to main content
        contentPanel.add(leftPanel);
        contentPanel.add(rightPanel);
        add(contentPanel, BorderLayout.CENTER);
    }

    // ===== TABLE DESIGN =====
    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (c instanceof JComponent jc) {
                    jc.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0, 150, 150))); // underline rows
                }
                c.setBackground(Color.WHITE);
                return c;
            }
        };

        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setForeground(Color.DARK_GRAY);
        table.setRowHeight(28);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(0, 150, 150));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(header.getWidth(), 32));
        return table;
    }
}
