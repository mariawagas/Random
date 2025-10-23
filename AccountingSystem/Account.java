public class Account {
    private String name;
    private String type;
    private double balance;

    public Account(String name, String type) {
        this.name = name;
        this.type = type;
        this.balance = 0.0;
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public double getBalance() { return balance; }

    public void addBalance(double amount) {
        this.balance += amount;
    }

    @Override
    public String toString() {
        return name + " [" + type + "]";
    }
}



import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class Account extends JPanel {

    private Map<String, Double> accounts;
    private JPanel cardsPanel;

    public Account() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel header = new JLabel("Account Summary", SwingConstants.LEFT);
        header.setFont(new Font("Segoe UI", Font.BOLD, 22));
        add(header, BorderLayout.NORTH);

        // --- Initialize account data ---
        accounts = new LinkedHashMap<>();
        accounts.put("Cash", 50000.00);
        accounts.put("Accounts Receivable", 25000.00);
        accounts.put("Accounts Payable", 10000.00);
        accounts.put("Equity", 65000.00);

        // --- Card layout for accounts ---
        cardsPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        cardsPanel.setBackground(Color.WHITE);
        refreshAccountCards();

        add(cardsPanel, BorderLayout.CENTER);

        // --- Footer Panel ---
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setBackground(Color.WHITE);
        JButton updateBtn = new JButton("Update Balances");
        updateBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        updateBtn.setBackground(new Color(0, 123, 255));
        updateBtn.setForeground(Color.WHITE);
        updateBtn.setFocusPainted(false);
        updateBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        footer.add(updateBtn);
        add(footer, BorderLayout.SOUTH);

        updateBtn.addActionListener(e -> updateAccounts());
    }

    private void refreshAccountCards() {
        cardsPanel.removeAll();
        for (Map.Entry<String, Double> entry : accounts.entrySet()) {
            cardsPanel.add(createAccountCard(entry.getKey(), entry.getValue()));
        }
        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private JPanel createAccountCard(String name, double balance) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(new Color(245, 247, 250));
        card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 1));
        card.setPreferredSize(new Dimension(200, 120));

        JLabel nameLbl = new JLabel(name, SwingConstants.CENTER);
        nameLbl.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLbl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel balanceLbl = new JLabel("₱" + String.format("%,.2f", balance), SwingConstants.CENTER);
        balanceLbl.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        balanceLbl.setForeground(new Color(0, 102, 0));

        card.add(nameLbl, BorderLayout.NORTH);
        card.add(balanceLbl, BorderLayout.CENTER);

        return card;
    }

    private void updateAccounts() {
        String input = JOptionPane.showInputDialog(
            this,
            "Enter account name to update (e.g., Cash):",
            "Update Balance",
            JOptionPane.PLAIN_MESSAGE
        );

        if (input != null && accounts.containsKey(input)) {
            String newBal = JOptionPane.showInputDialog(this, "Enter new balance for " + input + ":");
            try {
                double value = Double.parseDouble(newBal);
                accounts.put(input, value);
                refreshAccountCards();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid amount entered.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (input != null) {
            JOptionPane.showMessageDialog(this, "Account not found.", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }
}
