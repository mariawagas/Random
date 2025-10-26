package ACCTG;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class Accounts extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    
    private String name;
    private String type; // ASSET, LIABILITY, EQUITY, INCOME, EXPENSE
    private double balance;

    public Accounts(String name, String type, double balance) {
        this.name = name;
        this.type = type;
        this.balance = balance;
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public void debit(double amount) {
        if (type.equals("ASSET") || type.equals("EXPENSE"))
            balance += amount;
        else
            balance -= amount;
    }

    public void credit(double amount) {
        if (type.equals("ASSET") || type.equals("EXPENSE"))
            balance -= amount;
        else
            balance += amount;
    }


    public Accounts() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // === HEADER BAR ===
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0, 150, 150))); // bottom border line

        JLabel title = new JLabel("Accounts");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(40, 40, 40));
        title.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 10));
        headerPanel.add(title, BorderLayout.WEST);

        JLabel desc = new JLabel("Chart of accounts with current balances");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        desc.setForeground(new Color(90, 90, 90));
        desc.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 15));
        headerPanel.add(desc, BorderLayout.CENTER);

        add(headerPanel, BorderLayout.NORTH);

        // === TABLE SETUP ===
        String[] columns = {"Account", "Type", "Balance"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);

                // Row coloring by account type
                String type = (String) getValueAt(row, 1);
                if (type != null) {
                    switch (type) {
                        case "ASSET":
                            c.setBackground(new Color(230, 255, 230)); // light green
                            break;
                        case "LIABILITY":
                            c.setBackground(new Color(255, 240, 200)); // light orange
                            break;
                        case "EQUITY":
                            c.setBackground(new Color(230, 240, 255)); // light blue
                            break;
                        case "INCOME":
                            c.setBackground(new Color(245, 235, 255)); // light purple
                            break;
                        case "EXPENSE":
                            c.setBackground(new Color(255, 230, 230)); // light red
                            break;
                        default:
                            c.setBackground(Color.WHITE);
                            break;
                    }
                }

                if (isRowSelected(row)) {
                    c.setBackground(new Color(220, 250, 250));
                }

                if (c instanceof JComponent jc) {
                    jc.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0, 150, 150))); // underline
                }

                return c;
            }
        };

        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setForeground(Color.DARK_GRAY);
        table.setGridColor(new Color(0, 150, 150));
        table.setRowHeight(28);
        table.setShowHorizontalLines(true);
        table.setShowVerticalLines(false);
        table.setFillsViewportHeight(true);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(Color.WHITE);

        // === TABLE HEADER ===
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(0, 150, 150));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(header.getWidth(), 32));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        add(scrollPane, BorderLayout.CENTER);

        // === LOAD DEFAULT ACCOUNTS ===
        addDefaultAccounts();
    }

    private void addDefaultAccounts() {
        Object[][] defaultAccounts = {
            {"Cash", "ASSET", 0.00},
            {"Accounts Receivable", "ASSET", 0.00},
            {"Inventory", "ASSET", 0.00},
            {"Prepaid Expenses", "ASSET", 0.00},
            {"Equipment", "ASSET", 0.00},
            {"Accounts Payable", "LIABILITY", 0.00},
            {"Notes Payable", "LIABILITY", 0.00},
            {"Owner's Capital", "EQUITY", 0.00},
            {"Sales Revenue", "INCOME", 0.00},
            {"Service Revenue", "INCOME", 0.00},
            {"Cost of Goods Sold", "EXPENSE", 0.00},
            {"Rent Expense", "EXPENSE", 0.00},
            {"Salaries Expense", "EXPENSE", 0.00},
            {"Utilities Expense", "EXPENSE", 0.00}
        };

        for (Object[] row : defaultAccounts) {
            model.addRow(row);
        }
    }
}
