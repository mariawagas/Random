public class Transaction {
    private String date;
    private String description;
    private Account debit;
    private Account credit;
    private double amount;

    public Transaction(String date, String description, Account debit, Account credit, double amount) {
        this.date = date;
        this.description = description;
        this.debit = debit;
        this.credit = credit;
        this.amount = amount;
    }

    public String getDate() { return date; }
    public String getDescription() { return description; }
    public Account getDebit() { return debit; }
    public Account getCredit() { return credit; }
    public double getAmount() { return amount; }
}


















package AccountSystem_ITProject; 

// === Imports ===
import com.formdev.flatlaf.FlatLightLaf;          // For modern FlatLaf UI theme
import javax.swing.*;                             // Swing GUI components
import javax.swing.border.EmptyBorder;            // For padding/margins
import javax.swing.table.DefaultTableModel;       // For table data handling
import java.awt.*;                                // Core AWT classes (layout, color, etc.)
import java.text.DecimalFormat;                   // For formatting numbers
import java.time.LocalDate;                       // To get the current date
import java.util.ArrayList;                       // To store lists of accounts and transactions
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;



// === MAIN APPLICATION CLASS ===
public class MainApp extends JFrame {

    // === Color Theme Variables (for consistency and easier theme management) ===
	public static final Color PRIMARY = Color.decode("#00897B");     // Teal (main accent)
    public static final Color SECONDARY = Color.decode("#E0E0E0");   // Light gray (backgrounds)
    public static final Color ACCENT = Color.decode("#004D40");      // Dark teal (emphasis)
    public static final Color BACKGROUND = Color.decode("#FAFAFA");  // Soft white (main background)
    public static final Color TEXT = Color.decode("#212121");        // Dark text for readability

    // These are unused placeholders (could be deleted)
	public static final Color COLOR_PRIMARY = null;
	public static final Color COLOR_SECONDARY = null;
	public static final Color COLOR_ACCENT = null;
	public static final Color COLOR_BG = null;
	public static final Color COLOR_TEXT = null;

    // === Data Lists ===
    private final ArrayList<Account> accounts = new ArrayList<>();       // List of accounts
    private final ArrayList<Transaction> transactions = new ArrayList<>(); // List of transactions

    // === UI Panels ===
    private TransactionsPanel transactionsPanel;
    private AccountsPanel accountsPanel;
    private BalanceSheetPanel balanceSheetPanel;
    private LedgerPanel ledgerPanel;
    private JournalPanel journalPanel;
    private NewTransactionPanel newTransactionPanel;

    // === Navigation Buttons (stored in list for easy highlighting) ===
    private final ArrayList<JButton> navButtons = new ArrayList<>();

    // === MAIN FRAME CONSTRUCTOR ===
    public MainApp() {
        super("Accounting System"); // Window title
        setDefaultCloseOperation(EXIT_ON_CLOSE);            // Exit when window closes
        setSize(1200, 750); // Fixed size for laptop view
        setLocationRelativeTo(null); // Center on screen
        setResizable(true); // Disable resizing to keep layout stable
            // Start maximized
        initSampleData();                                   // Load initial data

        // Apply modern theme settings
        setTheme();

        // === Create Layout Components ===
        JPanel topBar = createTopBar();                     // Navigation bar at top
        JPanel cardsPanel = new JPanel(new CardLayout());   // Main area with switchable views

        // === Initialize Main Panels ===
        transactionsPanel = new TransactionsPanel(transactions);
        accountsPanel = new AccountsPanel();
        balanceSheetPanel = new BalanceSheetPanel();
        ledgerPanel = new LedgerPanel();
        journalPanel = new JournalPanel();
        newTransactionPanel = new NewTransactionPanel();

        // When a transaction is saved, update all dependent panels
        newTransactionPanel.setOnSave(t -> {
            transactions.add(t);
            t.getDebit().addBalance(t.getAmount());
            t.getCredit().addBalance(-t.getAmount());
            transactionsPanel.updateTable();                // Refresh transactions list
            accountsPanel = new AccountsPanel();            // Rebuild updated panels
            balanceSheetPanel = new BalanceSheetPanel();
            ledgerPanel = new LedgerPanel();
            journalPanel = new JournalPanel();
        });

        // === Add Panels to Card Layout (acts like tab pages) ===
        cardsPanel.add(transactionsPanel, "Transactions");
        cardsPanel.add(new NewTransactionPanel(), "New Transaction");
        cardsPanel.add(accountsPanel, "Accounts");
        cardsPanel.add(balanceSheetPanel, "Balance Sheet");
        cardsPanel.add(ledgerPanel, "Ledger");
        cardsPanel.add(journalPanel, "Journal");

        // === Navigation Button Logic ===
        for (JButton b : navButtons) {
            b.addActionListener(e -> {
                setActiveButton(b); // Highlight selected button
                ((CardLayout) cardsPanel.getLayout()).show(cardsPanel, b.getActionCommand());
            });
        }

        // === Final Layout Setup ===
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(topBar, BorderLayout.NORTH);

        // Wrap the cards panel in a scrollable container
        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // smoother scrolling
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        setVisible(true); // Show window
    }

    // === INITIAL DATA LOADING ===
    private void initSampleData() {
        // Add some default accounts to start with
        accounts.add(new Account("Cash", Account.Type.ASSET, 50000.00));
        accounts.add(new Account("Accounts Receivable", Account.Type.ASSET, 25000.00));
        accounts.add(new Account("Inventory", Account.Type.ASSET, 0));
        accounts.add(new Account("Prepaid Expenses", Account.Type.ASSET, 0));
        accounts.add(new Account("Equipment", Account.Type.ASSET, 0));
        accounts.add(new Account("Accounts Payable", Account.Type.LIABILITY, -10000.00));
        accounts.add(new Account("Notes Payable", Account.Type.LIABILITY, 0));
        accounts.add(new Account("Owner's Equity", Account.Type.EQUITY, 65000.00));
        accounts.add(new Account("Sales Revenue", Account.Type.INCOME, 0));
        accounts.add(new Account("Service Revenue", Account.Type.INCOME, 0));
        accounts.add(new Account("Cost of Goods Sold", Account.Type.EXPENSE, 0));
        accounts.add(new Account("Rent Expense", Account.Type.EXPENSE, 0));
        accounts.add(new Account("Salaries Expense", Account.Type.EXPENSE, 0));
        accounts.add(new Account("Utilities Expense", Account.Type.EXPENSE, 0));
    }

    // === TOP NAVIGATION BAR CREATION ===

    
    private JPanel createTopBar() {
        JPanel bar = new JPanel(new GridLayout(1, 0, 10, 0)); // 1 row, auto columns
        bar.setBackground(SECONDARY);
        bar.setBorder(new EmptyBorder(10, 10, 10, 10));

        String[] labels = {
            "New Transaction", "Transactions", "Accounts",
            "Balance Sheet", "General Ledger", "General Journal"
        };
        String[] icons = {
            "new.png", "transactions.png", "accounts.png",
            "balance.png", "ledger.png", "journal.png"
        };

        for (int i = 0; i < labels.length; i++) {
            JButton b = new JButton(labels[i], scaleIcon(loadIcon(icons[i]), 24, 24));
            b.setActionCommand(labels[i]);
            styleNavButton(b);
            navButtons.add(b);
            bar.add(b);
        }

        setActiveButton(navButtons.get(0));

        // add responsive resize listener
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustNavButtonSizes(bar.getWidth());
            }
        });

        return bar;
    }
    
    
    private void adjustNavButtonSizes(int barWidth) {
        int buttonCount = navButtons.size();
        int perButtonWidth = barWidth / buttonCount;
        int iconSize = Math.max(16, Math.min(32, perButtonWidth / 10));
        int fontSize = Math.max(10, Math.min(16, perButtonWidth / 50 + 10));

        for (JButton b : navButtons) {
            b.setFont(new Font("Segoe UI", Font.PLAIN, fontSize));
            ImageIcon icon = (ImageIcon) b.getIcon();
            if (icon != null) b.setIcon(scaleIcon(icon, iconSize, iconSize));
        }
    }

    private void styleNavButton(JButton b) {
        b.setFocusPainted(false);
        b.setBackground(new Color(45, 45, 45));
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));
        b.setHorizontalAlignment(SwingConstants.CENTER);
        b.setVerticalTextPosition(SwingConstants.BOTTOM);
        b.setHorizontalTextPosition(SwingConstants.CENTER);
    }

    private void setActiveButton(JButton active) {
        for (JButton b : navButtons) {
            if (b == active) {
                b.setBackground(PRIMARY);
                b.setForeground(Color.WHITE);
            } else {
                b.setBackground(SECONDARY);
                b.setForeground(TEXT);
            }
        }
    }


    
//    private JPanel createTopBar() {
//        // FlowLayout with left alignment, automatic wrapping when window narrows
//        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 8)) {
//            @Override
//            public Dimension getPreferredSize() {
//                // Make sure nav bar doesn’t eat too much vertical space
//                Dimension d = super.getPreferredSize();
//                d.height = Math.min(d.height, 100);
//                return d;
//            }
//        };
//        bar.setBackground(SECONDARY);
//        bar.setBorder(new EmptyBorder(10, 10, 10, 10));
//
//        // Navigation labels and icons
//        String[] labels = {
//            "New Transaction", "Transactions", "Accounts",
//            "Balance Sheet", "General Ledger", "General Journal"
//        };
//        String[] icons = {
//            "new.png", "transactions.png", "accounts.png",
//            "balance.png", "ledger.png", "journal.png"
//        };
//
//        // Build navigation buttons
//        for (int i = 0; i < labels.length; i++) {
//            JButton b = new JButton(labels[i]);
//            b.setActionCommand(labels[i]);
//            b.setIcon(loadIcon(icons[i]));
//            styleNavButton(b);
//            navButtons.add(b);
//            bar.add(b);
//        }
//
//        setActiveButton(navButtons.get(0)); // Highlight first
//        return bar;
//    }


    // === Styling for Navigation Buttons ===
//    private void styleNavButton(JButton b) {
//        b.setFocusPainted(false);
//        b.setBackground(SECONDARY);
//        b.setForeground(TEXT);
//        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
//        b.setPreferredSize(new Dimension(160, 36));         // Button size
//        b.setHorizontalAlignment(SwingConstants.LEFT);      // Align text & icon to left
//        b.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10)); // Add padding
//    }
    

    private ImageIcon loadIcon(String filename) {
        if (filename == null || filename.isBlank()) return null;
        java.net.URL imgURL = getClass().getResource("/AccountSystem_ITProject/" + filename);
        if (imgURL != null) return new ImageIcon(imgURL);

        java.io.File f = new java.io.File("src/AccountSystem_ITProject/" + filename);
        if (f.exists()) return new ImageIcon(f.getAbsolutePath());

        System.err.println("⚠ Icon not found: " + filename);
        return null;
    }

    private ImageIcon scaleIcon(ImageIcon icon, int width, int height) {
        if (icon == null) return null;
        Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(img);
    }

    // === Apply Theme ===
    private void setTheme() {
        UIManager.put("Panel.background", BACKGROUND);
        UIManager.put("Label.foreground", TEXT);
        UIManager.put("Button.background", SECONDARY);
        UIManager.put("Button.foreground", TEXT);
        UIManager.put("TextField.background", Color.WHITE);
        UIManager.put("TextField.foreground", TEXT);
    }

    // === ACCOUNT MODEL ===
    static class Account {
        enum Type { ASSET, LIABILITY, EQUITY, INCOME, EXPENSE } // Account types

        private final String name;
        private final Type type;
        private double balance;

        public Account(String name, Type type, double balance) {
            this.name = name;
            this.type = type;
            this.balance = balance;
        }

        // Basic getters/setters
        public String getName() { return name; }
        public Type getType() { return type; }
        public double getBalance() { return balance; }
        public void addBalance(double delta) { balance += delta; }
        public String toString() { return name; } // For ComboBox display
    }

    // === TRANSACTION MODEL ===
    static class Transaction {
        private final String date, description;
        private final Account debit, credit;
        private final double amount;

        public Transaction(String date, String description, Account debit, Account credit, double amount) {
            this.date = date;
            this.description = description;
            this.debit = debit;
            this.credit = credit;
            this.amount = amount;
        }

        // Getters
        public String getDate() { return date; }
        public String getDescription() { return description; }
        public Account getDebit() { return debit; }
        public Account getCredit() { return credit; }
        public double getAmount() { return amount; }
    }

    // === NEW TRANSACTION PANEL ===
    class NewTransactionPanel extends JPanel {
        // Form fields
        private final JTextField dateField = new JTextField();
        private final JTextField descField = new JTextField();
        private final JComboBox<Account> debitCombo;
        private final JComboBox<Account> creditCombo;
        private final JTextField amountField = new JTextField();
        private OnSaveListener saveListener; // Callback interface

        public NewTransactionPanel() {
            setLayout(new GridBagLayout()); // Flexible form layout
            setBorder(new EmptyBorder(20, 40, 20, 40));
            setBackground(BACKGROUND);

            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10); // Padding between fields
            gbc.fill = GridBagConstraints.HORIZONTAL;

            int y = 0; // Row tracker for layout

            // === Date Field ===
            gbc.gridx = 0; gbc.gridy = y;
            add(label("Date (YYYY-MM-DD):"), gbc);
            gbc.gridx = 1;
            dateField.setText(LocalDate.now().toString()); // Default today
            add(dateField, gbc);

            // === Description Field ===
            gbc.gridx = 0; gbc.gridy = ++y;
            add(label("Description:"), gbc);
            gbc.gridx = 1;
            add(descField, gbc);

            // === Debit Account Combo ===
            gbc.gridx = 0; gbc.gridy = ++y;
            add(label("Debit Account:"), gbc);
            gbc.gridx = 1;
            debitCombo = new JComboBox<>(accounts.toArray(new Account[0]));
            add(debitCombo, gbc);

            // === Credit Account Combo ===
            gbc.gridx = 0; gbc.gridy = ++y;
            add(label("Credit Account:"), gbc);
            gbc.gridx = 1;
            creditCombo = new JComboBox<>(accounts.toArray(new Account[0]));
            add(creditCombo, gbc);

            // === Amount Field ===
            gbc.gridx = 0; gbc.gridy = ++y;
            add(label("Amount:"), gbc);
            gbc.gridx = 1;
            add(amountField, gbc);

//            // === Buttons (Save & Clear) ===
//            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
//            btnPanel.setBackground(BACKGROUND);
//            JButton save = new JButton("Save Transaction");
//            JButton clear = new JButton("Clear");
//            save.setBackground(PRIMARY);
//            save.setForeground(Color.WHITE);
//            clear.setBackground(SECONDARY);
//            clear.setForeground(TEXT);
            
         // Buttons below Amount field
            JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
            btnPanel.setBackground(BACKGROUND);

            // Create buttons
            JButton save = new JButton("Save Transaction");
            JButton clear = new JButton("Clear");

            // Load icons (make sure to have "save.png" and "clear.png" in your resources folder)
            ImageIcon saveIcon = scaleIcon("save.png", 20, 20);
            ImageIcon clearIcon = scaleIcon("clear.png", 20, 20);

            // Set icons if they were found
            if (saveIcon != null) save.setIcon(saveIcon);
            if (clearIcon != null) clear.setIcon(clearIcon);

            // Style buttons
            save.setBackground(PRIMARY);
            save.setForeground(Color.WHITE);
            save.setFocusPainted(false);

            clear.setBackground(SECONDARY);
            clear.setForeground(TEXT);
            clear.setFocusPainted(false);

            // Make sure icon stays on the LEFT of the text
            save.setHorizontalTextPosition(SwingConstants.RIGHT);
            save.setIconTextGap(8); // spacing between icon and text

            clear.setHorizontalTextPosition(SwingConstants.RIGHT);
            clear.setIconTextGap(8);

            // Add listeners
            save.addActionListener(e -> onSave());
            clear.addActionListener(e -> clearFields());

            // Add to panel
            btnPanel.add(save);
            btnPanel.add(clear);


            // Add actions
            save.addActionListener(e -> onSave());
            clear.addActionListener(e -> clearFields());
            btnPanel.add(save);
            btnPanel.add(clear);

            // Place button panel in last row
            gbc.gridx = 0; gbc.gridy = ++y;
            gbc.gridwidth = 2;
            add(btnPanel, gbc);
        }

        // Helper for label creation
        private JLabel label(String text) {
            JLabel l = new JLabel(text);
            l.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            l.setForeground(TEXT);
            return l;
        }

        // Handle save click
        private void onSave() {
            try {
                String date = dateField.getText().trim();
                String desc = descField.getText().trim();
                Account debit = (Account) debitCombo.getSelectedItem();
                Account credit = (Account) creditCombo.getSelectedItem();
                double amount = Double.parseDouble(amountField.getText().trim());

                // Basic validation
                if (date.isEmpty() || desc.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "All fields are required.");
                    return;
                }
                if (amount <= 0) {
                    JOptionPane.showMessageDialog(this, "Amount must be positive.");
                    return;
                }

                // Create and send transaction back to main app
                Transaction t = new Transaction(date, desc, debit, credit, amount);
                if (saveListener != null) saveListener.onSave(t);
                JOptionPane.showMessageDialog(this, "Transaction saved!");
                clearFields();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }

        // Reset all input fields
        private void clearFields() {
            dateField.setText(LocalDate.now().toString());
            descField.setText("");
            amountField.setText("");
            debitCombo.setSelectedIndex(0);
            creditCombo.setSelectedIndex(0);
        }

        // Assign listener for saving
        public void setOnSave(OnSaveListener listener) { this.saveListener = listener; }

        // Interface for save callback
        interface OnSaveListener { void onSave(Transaction t); }
    }

    // === TRANSACTIONS TABLE PANEL ===
    static class TransactionsPanel extends JPanel {
        private final DefaultTableModel model;
        private final JTable table;
        private final ArrayList<Transaction> transactions;

        public TransactionsPanel(ArrayList<Transaction> transactions) {
            this.transactions = transactions;
            setLayout(new BorderLayout());
            setBorder(new EmptyBorder(20, 20, 20, 20));

            // Title
            JLabel title = new JLabel("Transactions", SwingConstants.CENTER);
            title.setFont(new Font("Segoe UI", Font.BOLD, 22));
            add(title, BorderLayout.NORTH);

            // Table model setup
            model = new DefaultTableModel(new Object[]{"Date", "Description", "Debit", "Credit", "Amount"}, 0);
            table = new JTable(model);
            add(new JScrollPane(table), BorderLayout.CENTER);

            updateTable();
        }

        // Refresh table with transaction list
        public void updateTable() {
            model.setRowCount(0);
            DecimalFormat df = new DecimalFormat("#,##0.00");
            for (Transaction t : transactions) {
                model.addRow(new Object[]{
                        t.getDate(),
                        t.getDescription(),
                        t.getDebit().getName(),
                        t.getCredit().getName(),
                        "₱" + df.format(t.getAmount())
                });
            }
        }
    }

    // === Placeholder Panels for future features ===
    class AccountsPanel extends JPanel { /* To be implemented */ }
    class BalanceSheetPanel extends JPanel { /* To be implemented */ }
    class LedgerPanel extends JPanel { /* To be implemented */ }
    class JournalPanel extends JPanel { /* To be implemented */ }

    // === MAIN METHOD (Program Entry Point) ===
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> { // Run GUI safely on event thread
            try {
                UIManager.setLookAndFeel(new FlatLightLaf()); // Apply modern FlatLaf theme
            } catch (Exception e) {
                System.err.println("FlatLaf failed to load.");
            }
            new MainApp(); // Launch the app
        });
    }

	public ImageIcon scaleIcon(String string, int width, int height) {
		// TODO Auto-generated method stub
		return null;
	}
}

