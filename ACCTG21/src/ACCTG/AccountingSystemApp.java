package ACCTG;

import javax.swing.*;
import ACCTG.BalanceSheet;
import ACCTG.Accounts;

import javax.swing.table.DefaultTableModel;
import java.time.LocalDate;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;


public class AccountingSystemApp extends JFrame {
    private JPanel mainPanel;
    private JLabel statusLabel; 
    private JButton[] navButtons;
    private CardLayout cardLayout;

    // ✅ Shared transactions list
    private ArrayList<Transaction> transactions = new ArrayList<>();
    private ArrayList<Accounts> accounts = new ArrayList<>();


    // Keep a reference to the transaction table model
    private DefaultTableModel transactionTableModel;

    public AccountingSystemApp() {
        setTitle("Accounting System");
        setSize(1300, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

     // ===== NAVIGATION BAR =====
                JPanel navBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 2)) {
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        Graphics2D g2 = (Graphics2D) g.create();
                        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                        g2.setColor(new Color(220, 220, 220));
                        g2.fillRect(0, getHeight() - 2, getWidth(), 2); // bottom divider line
                        g2.dispose();
                    }
                };
                navBar.setBackground(new Color(240, 240, 240)); // neutral modern background
                navBar.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(0, 180, 180)));
                
                String[] sections = {"New Transaction", "Transactions", "Accounts", "Balance Sheet", "General Journal", "General Ledger"};
                navButtons = new JButton[sections.length];

                // === Chrome-like color palette ===
                Color tabDefault = new Color(240, 240, 240);
                Color tabHover = new Color(250, 250, 250);
                Color tabActive = new Color(255, 255, 255);
                Color borderActive = new Color(0, 170, 170);
                Color shadow = new Color(200, 200, 200);
//                Color textDefault = new Color(80, 80, 80);

                for (int i = 0; i < sections.length; i++) {
                    JButton btn = new JButton(sections[i]) {
                        @Override
                        protected void paintComponent(Graphics g) {
                            Graphics2D g2 = (Graphics2D) g.create();
                            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                            // Shadow under tab for 3D look
                            if (getClientProperty("active") == Boolean.TRUE) {
                                g2.setColor(shadow);
                                g2.fillRoundRect(2, getHeight() - 4, getWidth() - 4, 4, 8, 8);
                            }

                            // Rounded tab shape
                            g2.setColor(getBackground());
                            g2.fillRoundRect(0, 0, getWidth(), getHeight() - 2, 12, 12);

                            // Active bottom border (highlight line)
                            if (getClientProperty("active") == Boolean.TRUE) {
                                g2.setColor(borderActive);
                                g2.fillRect(0, getHeight() - 2, getWidth(), 2);
                            }

                            g2.dispose();
                            super.paintComponent(g);
                        }
                    };

                    // === Base Button Styling ===
                    btn.setFocusPainted(false);
                    btn.setContentAreaFilled(false);
                    btn.setOpaque(false);
                    btn.setBorder(BorderFactory.createEmptyBorder(3, 16, 3, 16));
                    btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
                    btn.setForeground(Color.BLACK);
                    btn.setBackground(tabDefault);

                    int index = i;

                    // === Hover Effect ===
                    btn.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseEntered(MouseEvent e) {
                            if (btn.getClientProperty("active") != Boolean.TRUE) {
                                btn.setBackground(tabHover);
                                btn.repaint();
                            }
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            if (btn.getClientProperty("active") != Boolean.TRUE) {
                                btn.setBackground(tabDefault);
                                btn.repaint();
                            }
                        }
                    });

                    // === Click (Active State) ===
                    btn.addActionListener(e -> {
                        for (JButton b : navButtons) {
                            b.putClientProperty("active", false);
                            b.setBackground(tabDefault);
                            b.repaint();
                        }
                        btn.putClientProperty("active", true);
                        btn.setBackground(tabActive);
                        btn.repaint();
                        switchPanel(sections[index]);
                    });

                    navButtons[i] = btn;
                    navBar.add(btn);
                }
                
             // Make "New Transaction" the default active tab
                navButtons[0].putClientProperty("active", true);
                navButtons[0].setBackground(tabActive);
                navButtons[0].repaint();


                add(navBar, BorderLayout.NORTH);

                // ===== MAIN CONTENT (Card Layout) =====
                mainPanel = new JPanel(new CardLayout());
                JPanel newTransactionPanel = createNewTransactionPanel();
                JPanel transactionsPanel = createTransactionPanel();
                JPanel accountsPanel = new Accounts(); 
                JPanel BalanceSheetPanel = new BalanceSheet();
                JPanel generalJournalPanel = new GeneralJournal();
                JPanel generalLedgerPanel = new GeneralLedger();
                mainPanel.add(newTransactionPanel, "New Transaction");
                mainPanel.add(transactionsPanel, "Transactions");
                mainPanel.add(accountsPanel, "Accounts");
                mainPanel.add(BalanceSheetPanel, "Balance Sheet");
                mainPanel.add(generalJournalPanel, "General Journal");
                mainPanel.add(generalLedgerPanel, "General Ledger");
                add(mainPanel, BorderLayout.CENTER);

                switchPanel("New Transaction");
                setVisible(true);
            }

            private void switchPanel(String name) {
                CardLayout cl = (CardLayout) (mainPanel.getLayout());
                cl.show(mainPanel, name);
            }
    
 // 🔘 Highlight which section is active
    private void highlightActiveButton(int activeIndex) {
        Color baseColor = new Color(0, 130, 130);
        Color activeColor = new Color(0, 180, 180);

        for (int i = 0; i < navButtons.length; i++) {
            if (i == activeIndex) {
                navButtons[i].setBackground(activeColor);
                navButtons[i].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0, 180, 180), 3),
                    BorderFactory.createEmptyBorder(5, 20, 5, 20)
                ));
            } else {
                navButtons[i].setBackground(baseColor);
                navButtons[i].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(0, 130, 130), 2),
                    BorderFactory.createEmptyBorder(5, 20, 5, 20)
                ));
            }
        }
    }


    // 🧾 NEW TRANSACTION PANEL
    private JPanel createNewTransactionPanel() {
//    	private JPanel createNewTransactionPanel() {
    	    JPanel panel = new JPanel(new BorderLayout());
    	    panel.setBackground(new Color(255, 255, 255));

    	    // Inner panel to hold form fields at upper center
    	    JPanel formPanel = new JPanel(null);
    	    formPanel.setPreferredSize(new Dimension(600, 350));
    	    formPanel.setBackground(Color.WHITE);

    	    // CENTER CONTAINER to keep it top-center
    	    JPanel topCenterWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 40));
    	    topCenterWrapper.setBackground(Color.WHITE);
    	    topCenterWrapper.add(formPanel);
    	    panel.add(topCenterWrapper, BorderLayout.NORTH);
    	    
    	    JLabel titleLabel = new JLabel("Add a New Transaction");
    	    titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
    	    titleLabel.setForeground(new Color(0, 150, 150)); 
    	    titleLabel.setBounds(130, 20, 250, 25); 
    	    formPanel.add(titleLabel);

    	    JLabel dateLabel = new JLabel("Date (YYYY-MM-DD):");
    	    dateLabel.setForeground(Color.BLACK);
    	    dateLabel.setBounds(130, 60, 150, 25);
    	    formPanel.add(dateLabel);

    	    JTextField dateField = new JTextField(LocalDate.now().toString());
    	    dateField.setBounds(260, 60, 230, 25);
    	    formPanel.add(dateField);

    	    JLabel descLabel = new JLabel("Description:");
    	    descLabel.setForeground(Color.BLACK);
    	    descLabel.setBounds(130, 100, 150, 25);
    	    formPanel.add(descLabel);

    	    JTextField descField = new JTextField();
    	    descField.setBounds(260, 100, 230, 25);
    	    formPanel.add(descField);

    	    JLabel debitLabel = new JLabel("Debit Account:");
    	    debitLabel.setForeground(Color.BLACK);
    	    debitLabel.setBounds(130, 140, 150, 25);
    	    formPanel.add(debitLabel);

    	    JComboBox<String> debitAccount = new JComboBox<>(new String[]{
    	        "Cash [ASSET]", "Petty Cash [ASSET]", "Accounts Receivable [ASSET]",
    	        "Supplies [ASSET]", "Prepaid Rent [ASSET]", "Equipment [ASSET]",
    	        "Furniture [ASSET]", "Accumulated Depreciation [ASSET]",
    	        "Accounts Payable [LIABILITY]", "Salaries Payable [LIABILITY]",
    	        "Unearned Revenue [LIABILITY]", "Notes Payable [LIABILITY]",
    	        "Owner's Capital [EQUITY]", "Owner's Withdrawals [EQUITY]",
    	        "Service Revenue [INCOME]", "Interest Income [INCOME]",
    	        "Sales Revenue [INCOME]", "Rent Expense [EXPENSE]",
    	        "Salaries Expense [EXPENSE]", "Supplies Expense [EXPENSE]",
    	        "Utilities Expense [EXPENSE]", "Depreciation Expense [EXPENSE]",
    	        "Miscellaneous Expense [EXPENSE]"
    	    });
    	    debitAccount.setBounds(260, 140, 230, 25);
            debitAccount.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    	    formPanel.add(debitAccount);

    	    JLabel creditLabel = new JLabel("Credit Account:");
    	    creditLabel.setForeground(Color.BLACK);
    	    creditLabel.setBounds(130, 180, 150, 25);
    	    formPanel.add(creditLabel);

    	    JComboBox<String> creditAccount = new JComboBox<>(new String[]{
    	        "Cash [ASSET]", "Petty Cash [ASSET]", "Accounts Receivable [ASSET]",
    	        "Supplies [ASSET]", "Prepaid Rent [ASSET]", "Equipment [ASSET]",
    	        "Furniture [ASSET]", "Accumulated Depreciation [ASSET]",
    	        "Accounts Payable [LIABILITY]", "Salaries Payable [LIABILITY]",
    	        "Unearned Revenue [LIABILITY]", "Notes Payable [LIABILITY]",
    	        "Owner's Capital [EQUITY]", "Owner's Withdrawals [EQUITY]",
    	        "Service Revenue [INCOME]", "Interest Income [INCOME]",
    	        "Sales Revenue [INCOME]", "Rent Expense [EXPENSE]",
    	        "Salaries Expense [EXPENSE]", "Supplies Expense [EXPENSE]",
    	        "Utilities Expense [EXPENSE]", "Depreciation Expense [EXPENSE]",
    	        "Miscellaneous Expense [EXPENSE]"
    	    });
    	    creditAccount.setBounds(260, 180, 230, 25);
    	    creditAccount.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    	    formPanel.add(creditAccount);

    	    JLabel amountLabel = new JLabel("Amount:");
    	    amountLabel.setForeground(Color.BLACK);
    	    amountLabel.setBounds(130, 220, 150, 25);
    	    formPanel.add(amountLabel);

    	    JTextField amountField = new JTextField();
    	    amountField.setBounds(260, 220, 230, 25);
    	    formPanel.add(amountField);

    	    RoundedButton addBtn = new RoundedButton("Add Transaction");
    	    addBtn.setBounds(260, 260, 120, 24);
    	    formPanel.add(addBtn);

    	    RoundedButton clearBtn = new RoundedButton("Clear");
    	    clearBtn.setBounds(390, 260, 100, 24);
    	    formPanel.add(clearBtn);

    	    statusLabel = new JLabel("");
    	    statusLabel.setForeground(new Color(0, 255, 0));
    	    statusLabel.setBounds(260, 290, 300, 25);
    	    formPanel.add(statusLabel);

        // Keep all your existing button logic unchanged 👇
        addBtn.addActionListener(e -> {
            try {
                String date = dateField.getText().trim();
                String desc = descField.getText().trim();
                String debit = debitAccount.getSelectedItem().toString();
                String credit = creditAccount.getSelectedItem().toString();
                double amount = Double.parseDouble(amountField.getText().trim());

                Transaction t = new Transaction(date, desc, debit, credit, amount);
                transactions.add(t);

                if (transactionTableModel != null) {
                    transactionTableModel.addRow(new Object[]{
                        t.getDate(), t.getDescription(), t.getDebitAccount(), t.getCreditAccount(), t.getAmount()
                    });
                }
                statusLabel.setForeground( new Color (0, 170, 170));
                statusLabel.setText("✅ Transaction Saved Successfully");
                Timer timer = new Timer(3000, ev -> statusLabel.setText(""));
                timer.setRepeats(false);
                timer.start();

                descField.setText("");
                amountField.setText("");
            } catch (Exception ex) {
                statusLabel.setForeground(Color.RED);
                statusLabel.setText("❌ Invalid input. Please check all fields.");
                Timer timer = new Timer(3000, ev -> statusLabel.setText(""));
                timer.setRepeats(false);
                timer.start();
            }
        });

        clearBtn.addActionListener(e -> {
            dateField.setText(LocalDate.now().toString());
            descField.setText("");
            amountField.setText("");
            debitAccount.setSelectedIndex(0);
            creditAccount.setSelectedIndex(0);
            statusLabel.setText("");
        });
        
        

        // ⬇ Add formPanel centered in the parent panel
//        panel.add(formPanel);
        return panel;
    }

 // 📋 TRANSACTIONS PANEL
    private JPanel createTransactionPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 2));
        panel.setBackground(Color.WHITE);

        // ===== UPPER BAR (Title + Search) =====
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(0, 150, 150))); // underline bar

        // 🔹 Left side: Title
        JLabel title = new JLabel("Transactions");
        title.setFont(new Font("Segoe UI", Font.BOLD, 18));
        title.setForeground(new Color(0, 150, 150));
        title.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 0));
        topPanel.add(title, BorderLayout.WEST);

        // 🔍 Right side: Search bar + Clear button
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 8));
        searchPanel.setOpaque(false);

        JLabel searchLabel = new JLabel("Search:");
        JTextField searchField = new JTextField(20);
        RoundedButton clearButton = new RoundedButton("Clear");

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(clearButton);

        topPanel.add(searchPanel, BorderLayout.EAST);
        panel.add(topPanel, BorderLayout.NORTH);

        // ===== TABLE SECTION =====
        String[] columnNames = {"Date", "Description", "Debit Account", "Credit Account", "Amount"};
        transactionTableModel = new DefaultTableModel(columnNames, 0);
        JTable table = new JTable(transactionTableModel);

        // 🔹 Table design — white background + teal underline & horizontal border
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setBackground(Color.WHITE);
        table.setForeground(Color.BLACK);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(28);
        table.setFillsViewportHeight(true);

        // 🔹 Custom renderer for underline and border
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                JLabel cell = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                cell.setBackground(Color.WHITE);
                cell.setForeground(Color.BLACK);
                cell.setHorizontalAlignment(SwingConstants.LEFT);
                cell.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(0, 150, 150))); // bottom border

                if (isSelected) {
                    cell.setBackground(new Color(220, 250, 250)); // subtle highlight
                }
                return cell;
            }
        });

        // 🧩 Header styling
        table.getTableHeader().setBackground(new Color(0, 150, 150));
        table.getTableHeader().setForeground(Color.WHITE);
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setOpaque(true);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(0, 150, 150)));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        panel.add(scrollPane, BorderLayout.CENTER);

        // ===== SEARCH LOGIC =====
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String search = searchField.getText().toLowerCase();
                transactionTableModel.setRowCount(0);
                for (Transaction t : transactions) {
                    if (t.getDescription().toLowerCase().contains(search) ||
                        t.getDebitAccount().toLowerCase().contains(search) ||
                        t.getCreditAccount().toLowerCase().contains(search)) {
                        transactionTableModel.addRow(new Object[]{
                            t.getDate(),
                            t.getDescription(),
                            t.getDebitAccount(),
                            t.getCreditAccount(),
                            t.getAmount()
                        });
                    }
                }
            }
        });

        // 🧹 Clear search
        clearButton.addActionListener(e -> {
            searchField.setText("");
            refreshTransactionTable();
        });

        return panel;
    }

 // 🔁 Helper to refresh the table 
    private void refreshTransactionTable() { transactionTableModel.setRowCount(0); for (Transaction t : transactions) { transactionTableModel.addRow(new Object[]{ t.getDate(), t.getDescription(), t.getDebitAccount(), t.getCreditAccount(), t.getAmount() }); } }

    
  
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(AccountingSystemApp::new);
    }
}
