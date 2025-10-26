package ACCTG;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class GeneralLedger extends JPanel {
    private JComboBox<String> accountDropdown;
    private JTable ledgerTable;
    private DefaultTableModel ledgerModel;

    public GeneralLedger() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // ===== HEADER BAR =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0, 150, 150)));

        JLabel title = new JLabel("General Ledger");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(40, 40, 40));
        title.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 10));
        headerPanel.add(title, BorderLayout.WEST);

        // Right side: account selector
        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        rightHeader.setBackground(Color.WHITE);

        JLabel selectLabel = new JLabel("Select Account:");
        selectLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        accountDropdown = new JComboBox<>(new String[] {
                "Cash [ASSET]",
                "Accounts Payable [LIABILITY]",
                "Owner’s Equity [EQUITY]",
                "Service Revenue [INCOME]",
                "Rent Expense [EXPENSE]"
        });
        accountDropdown.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        accountDropdown.setBackground(Color.WHITE);
        accountDropdown.setPreferredSize(new Dimension(220, 28));

        rightHeader.add(selectLabel);
        rightHeader.add(accountDropdown);
        headerPanel.add(rightHeader, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        // ===== MAIN CONTENT =====
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel subtitle = new JLabel("Account activity and running balance");
        subtitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        subtitle.setForeground(new Color(60, 60, 60));
        subtitle.setBorder(BorderFactory.createEmptyBorder(0, 5, 8, 0));
        contentPanel.add(subtitle, BorderLayout.NORTH);

        // ===== TABLE SETUP =====
        String[] cols = {"Date", "Description", "Debit Account", "Credit Account", "Amount", "Balance"};
        ledgerModel = new DefaultTableModel(cols, 0);
        ledgerTable = createStyledTable(ledgerModel);

        JScrollPane scroll = new JScrollPane(ledgerTable);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        contentPanel.add(scroll, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
    }

    // ===== TABLE DESIGN =====
    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                if (c instanceof JComponent jc) {
                    jc.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0, 150, 150)));
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

