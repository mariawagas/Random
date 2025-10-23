/*
  AccountingSystem.java
  Single-file Swing accounting GUI built for Eclipse/JGRASP.
  - FlatLaf dark theme (if available on classpath)
  - Tabs: New Transaction, Transactions, Accounts, Balance Sheet, General Journal, General Ledger
  - No Add/Edit/Delete/Clear/Save buttons (use Enter in Amount field to submit)
  - In-memory storage (no DB / no files)
*/

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class AccountingSystem extends JFrame {

    // Data models (in-memory)
    private final List<Account> accounts = new ArrayList<>();
    private final List<Transaction> transactions = new ArrayList<>();

    // GUI components that need to be referenced
    private DefaultTableModel transactionsTableModel;
    private DefaultTableModel accountsTableModel;
    private JComboBox<Object> debitCombo;
    private JComboBox<Object> creditCombo;
    private JTextField dateField;
    private JTextField descField;
    private JTextField amountField;
    private JTextArea balanceSheetArea;
    private JTextArea journalArea;
    private JTextArea ledgerArea;

    public AccountingSystem() {
        super("Accounting System — Modern Dark");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Try to set FlatLaf Dark if available
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatDarkLaf");
            // Update component UIs after setting LAF
            SwingUtilities.updateComponentTreeUI(this);
        } catch (Exception ex) {
            // FlatLaf not present — fall back silently to default LAF
            System.err.println("FlatLaf not found on classpath; using default L&F. (Optional: add flatlaf jar.)");
        }

        initializeAccounts();
        initGui();
    }

    private void initializeAccounts() {
        // common accounts (empty balances)
        accounts.add(new Account("Cash", "ASSET"));
        accounts.add(new Account("Accounts Receivable", "ASSET"));
        accounts.add(new Account("Inventory", "ASSET"));
        accounts.add(new Account("Prepaid Expenses", "ASSET"));
        accounts.add(new Account("Equipment", "ASSET"));

        accounts.add(new Account("Accounts Payable", "LIABILITY"));
        accounts.add(new Account("Notes Payable", "LIABILITY"));

        accounts.add(new Account("Owner's Capital", "EQUITY"));

        accounts.add(new Account("Sales Revenue", "INCOME"));
        accounts.add(new Account("Service Revenue", "INCOME"));

        accounts.add(new Account("Cost of Goods Sold", "EXPENSE"));
        accounts.add(new Account("Rent Expense", "EXPENSE"));
        accounts.add(new Account("Salaries Expense", "EXPENSE"));
        accounts.add(new Account("Utilities Expense", "EXPENSE"));
    }

    private void initGui() {
        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("New Transaction", createNewTransactionPanel());
        tabs.addTab("Transactions", createTransactionsPanel());
        tabs.addTab("Accounts", createAccountsPanel());
        tabs.addTab("Balance Sheet", createBalanceSheetPanel());
        tabs.addTab("General Journal", createJournalPanel());
        tabs.addTab("General Ledger", createLedgerPanel());

        // update content when user switches tabs
        tabs.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                int idx = tabs.getSelectedIndex();
                String title = tabs.getTitleAt(idx);
                if ("Transactions".equals(title)) {
                    refreshTransactionsTable();
                } else if ("Accounts".equals(title)) {
                    refreshAccountsTable();
                } else if ("Balance Sheet".equals(title)) {
                    generateBalanceSheet();
                } else if ("General Journal".equals(title)) {
                    generateJournal();
                } else if ("General Ledger".equals(title)) {
                    generateLedger();
                }
            }
        });

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createNewTransactionPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(24, 24, 24, 24));
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(8, 8, 8, 8);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 0.0;

        // Row 1: Date
        gc.gridx = 0; gc.gridy = 0;
        form.add(new JLabel("Date (YYYY-MM-DD):"), gc);
        dateField = new JTextField();
        gc.gridx = 1; gc.gridy = 0; gc.weightx = 1.0;
        form.add(dateField, gc);

        // Row 2: Description
        gc.gridx = 0; gc.gridy = 1; gc.weightx = 0.0;
        form.add(new JLabel("Description:"), gc);
        descField = new JTextField();
        gc.gridx = 1; gc.gridy = 1; gc.weightx = 1.0;
        form.add(descField, gc);

        // Row 3: Debit account
        gc.gridx = 0; gc.gridy = 2; gc.weightx = 0.0;
        form.add(new JLabel("Debit Account:"), gc);
        debitCombo = new JComboBox<>();
        populateAccountCombo(debitCombo);
        gc.gridx = 1; gc.gridy = 2; gc.weightx = 1.0;
        form.add(debitCombo, gc);

        // Row 4: Credit account
        gc.gridx = 0; gc.gridy = 3; gc.weightx = 0.0;
        form.add(new JLabel("Credit Account:"), gc);
        creditCombo = new JComboBox<>();
        populateAccountCombo(creditCombo);
        gc.gridx = 1; gc.gridy = 3; gc.weightx = 1.0;
        form.add(creditCombo, gc);

        // Row 5: Amount -- pressing Enter in this field will submit
        gc.gridx = 0; gc.gridy = 4; gc.weightx = 0.0;
        form.add(new JLabel("Amount:"), gc);
        amountField = new JTextField();
        gc.gridx = 1; gc.gridy = 4; gc.weightx = 1.0;
        form.add(amountField, gc);

        // Instruction label
        gc.gridx = 0; gc.gridy = 5; gc.gridwidth = 2;
        JLabel hint = new JLabel("<html><i>Enter values above. Press <b>Enter</b> while focused in Amount to submit.</i></html>");
        hint.setForeground(Color.LIGHT_GRAY);
        form.add(hint, gc);

        // Submit by pressing Enter while in amountField (no button)
        amountField.addActionListener(e -> tryAddTransaction());

        outer.add(form, BorderLayout.NORTH);
        return outer;
    }

    private void populateAccountCombo(JComboBox<Object> combo) {
        combo.removeAllItems();
        combo.addItem("Select account...");
        for (Account a : accounts) combo.addItem(a);
        combo.setSelectedIndex(0);
        // render accounts nicely
        combo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value,int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Account) {
                    Account a = (Account) value;
                    setText(a.getName() + " [" + a.getType() + "]");
                }
                return this;
            }
        });
    }

    private JPanel createTransactionsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        String[] cols = {"Date", "Description", "Debit", "Credit", "Amount"};
        transactionsTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable table = new JTable(transactionsTableModel);
        table.setFillsViewportHeight(true);
        p.add(new JScrollPane(table), BorderLayout.CENTER);
        return p;
    }

    private JPanel createAccountsPanel() {
        JPanel p = new JPanel(new BorderLayout());
        String[] cols = {"Account Name", "Type", "Balance"};
        accountsTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        JTable t = new JTable(accountsTableModel);
        t.setFillsViewportHeight(true);
        p.add(new JScrollPane(t), BorderLayout.CENTER);
        return p;
    }

    private JPanel createBalanceSheetPanel() {
        JPanel p = new JPanel(new BorderLayout());
        balanceSheetArea = createReadOnlyTextArea();
        p.add(new JScrollPane(balanceSheetArea), BorderLayout.CENTER);
        return p;
    }

    private JPanel createJournalPanel() {
        JPanel p = new JPanel(new BorderLayout());
        journalArea = createReadOnlyTextArea();
        p.add(new JScrollPane(journalArea), BorderLayout.CENTER);
        return p;
    }

    private JPanel createLedgerPanel() {
        JPanel p = new JPanel(new BorderLayout());
        ledgerArea = createReadOnlyTextArea();
        p.add(new JScrollPane(ledgerArea), BorderLayout.CENTER);
        return p;
    }

    private JTextArea createReadOnlyTextArea() {
        JTextArea ta = new JTextArea();
        ta.setEditable(false);
        ta.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        ta.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        return ta;
    }

    // Attempt to add a transaction (called from Amount field Enter)
    private void tryAddTransaction() {
        String date = dateField.getText().trim();
        String desc = descField.getText().trim();
        Object debitSel = debitCombo.getSelectedItem();
        Object creditSel = creditCombo.getSelectedItem();
        String amountText = amountField.getText().trim();

        // Validation
        if (date.isEmpty()) {
            showError("Please enter Date (YYYY-MM-DD).");
            dateField.requestFocus(); return;
        }
        if (desc.isEmpty()) {
            showError("Please enter Description.");
            descField.requestFocus(); return;
        }
        if (!(debitSel instanceof Account)) {
            showError("Please select a Debit Account.");
            debitCombo.requestFocus(); return;
        }
        if (!(creditSel instanceof Account)) {
            showError("Please select a Credit Account.");
            creditCombo.requestFocus(); return;
        }
        double amount;
        try {
            amount = Double.parseDouble(amountText);
            if (amount <= 0) throw new NumberFormatException("Amount must be positive.");
        } catch (NumberFormatException e) {
            showError("Please enter a valid positive numeric Amount.");
            amountField.requestFocus(); return;
        }

        Account debit = (Account) debitSel;
        Account credit = (Account) creditSel;

        // Create transaction and update balances
        Transaction t = new Transaction(date, desc, debit, credit, amount);
        transactions.add(t);

        // Debit increases asset/expense; credit decreases them — we will keep sign convention simple:
        // add amount to debit account, subtract amount from credit account.
        debit.addBalance(amount);
        credit.addBalance(-amount);

        // Clear fields for next entry (no prefilled values)
        dateField.setText("");
        descField.setText("");
        amountField.setText("");
        debitCombo.setSelectedIndex(0);
        creditCombo.setSelectedIndex(0);

        // Optional: quick modest feedback (non-button flow)
        JOptionPane.showMessageDialog(this, "Transaction recorded.", "OK", JOptionPane.INFORMATION_MESSAGE);

        // Keep table displays in sync if currently visible
        refreshTransactionsTable();
        refreshAccountsTable();
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Validation", JOptionPane.WARNING_MESSAGE);
    }

    // Refresh functions
    private void refreshTransactionsTable() {
        if (transactionsTableModel == null) return;
        transactionsTableModel.setRowCount(0);
        for (Transaction t : transactions) {
            transactionsTableModel.addRow(new Object[]{
                    t.getDate(), t.getDescription(),
                    t.getDebit().getName(), t.getCredit().getName(),
                    String.format("%.2f", t.getAmount())
            });
        }
    }

    private void refreshAccountsTable() {
        if (accountsTableModel == null) return;
        accountsTableModel.setRowCount(0);
        for (Account a : accounts) {
            accountsTableModel.addRow(new Object[]{
                    a.getName(), a.getType(), String.format("%.2f", a.getBalance())
            });
        }
    }

    private void generateBalanceSheet() {
        if (balanceSheetArea == null) return;
        StringBuilder sb = new StringBuilder();
        sb.append("BALANCE SHEET\n\nASSETS:\n");
        double assets = 0, liabilities = 0, equity = 0;
        for (Account a : accounts) {
            if ("ASSET".equals(a.getType())) {
                sb.append(String.format("%-30s %12.2f\n", a.getName(), a.getBalance()));
                assets += a.getBalance();
            }
        }
        sb.append("\nLIABILITIES & EQUITY:\n");
        for (Account a : accounts) {
            if ("LIABILITY".equals(a.getType())) {
                sb.append(String.format("%-30s %12.2f\n", a.getName(), a.getBalance()));
                liabilities += a.getBalance();
            }
        }
        for (Account a : accounts) {
            if ("EQUITY".equals(a.getType())) {
                sb.append(String.format("%-30s %12.2f\n", a.getName(), a.getBalance()));
                equity += a.getBalance();
            }
        }
        sb.append("\n");
        sb.append(String.format("%-30s %12.2f\n", "Total Assets:", assets));
        sb.append(String.format("%-30s %12.2f\n", "Total Liabilities & Equity:", liabilities + equity));
        balanceSheetArea.setText(sb.toString());
    }

    private void generateJournal() {
        if (journalArea == null) return;
        StringBuilder sb = new StringBuilder();
        sb.append("GENERAL JOURNAL\n\n");
        for (Transaction t : transactions) {
            sb.append(String.format("%s  %s\n", t.getDate(), t.getDescription()));
            sb.append(String.format("   DR %-25s %10.2f\n", t.getDebit().getName(), t.getAmount()));
            sb.append(String.format("   CR %-25s %10.2f\n\n", t.getCredit().getName(), t.getAmount()));
        }
        journalArea.setText(sb.toString());
    }

    private void generateLedger() {
        if (ledgerArea == null) return;
        StringBuilder sb = new StringBuilder();
        sb.append("GENERAL LEDGER\n\n");
        for (Account a : accounts) {
            sb.append(a.getName()).append(" (").append(a.getType()).append(")\n");
            double running = 0.0;
            for (Transaction t : transactions) {
                if (t.getDebit() == a) {
                    running += t.getAmount();
                    sb.append(String.format("  %s  DR  %10.2f   Bal: %10.2f\n", t.getDate(), t.getAmount(), running));
                } else if (t.getCredit() == a) {
                    running -= t.getAmount();
                    sb.append(String.format("  %s  CR  %10.2f   Bal: %10.2f\n", t.getDate(), t.getAmount(), running));
                }
            }
            sb.append(String.format("  Ending Balance: %10.2f\n\n", a.getBalance()));
        }
        ledgerArea.setText(sb.toString());
    }

    // --- Inner model classes for single-file convenience ---
    private static class Account {
        private final String name;
        private final String type;
        private double balance;

        public Account(String name, String type) {
            this.name = name;
            this.type = type;
            this.balance = 0.0;
        }

        public String getName() { return name; }
        public String getType() { return type; }
        public double getBalance() { return balance; }
        public void addBalance(double delta) { this.balance += delta; }

        @Override
        public String toString() {
            return name + " [" + type + "]";
        }
    }

    private static class Transaction {
        private final String date;
        private final String description;
        private final Account debit;
        private final Account credit;
        private final double amount;

        public Transaction(String date, String description, Account debit, Account credit, double amount) {
            this.date = date; this.description = description;
            this.debit = debit; this.credit = credit; this.amount = amount;
        }

        public String getDate() { return date; }
        public String getDescription() { return description; }
        public Account getDebit() { return debit; }
        public Account getCredit() { return credit; }
        public double getAmount() { return amount; }
    }

    // --- Main ---
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AccountingSystem app = new AccountingSystem();
            app.setVisible(true);
        });
    }
}
