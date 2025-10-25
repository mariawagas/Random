import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;


public class AccountingSystem extends JFrame {
    private ArrayList<Transaction> transactions = new ArrayList<>();
    private ArrayList<Account> accounts = new ArrayList<>();

    // GUI Components
    private JTable transactionTable;
    private JTable accountTable;
    private DefaultTableModel transactionModel;
    private DefaultTableModel accountModel;

    public AccountingSystem() {
        setTitle("Accounting System for IT Project");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        // Initialize accounts
        initializeAccounts();

        // Tabs
        JTabbedPane tabs = new JTabbedPane();
        tabs.add("New Transaction", createTransactionPanel());
        tabs.add("Transactions", createTransactionsListPanel());
        tabs.add("Accounts", createAccountsPanel());
        tabs.add("Balance Sheet", createBalanceSheetPanel());
        tabs.add("General Journal", createJournalPanel());
        tabs.add("General Ledger", createLedgerPanel());

        add(tabs);
    }

    private void initializeAccounts() {
        accounts.add(new Account("Cash", "ASSET"));
        accounts.add(new Account("Accounts Receivable", "ASSET"));
        accounts.add(new Account("Inventory", "ASSET"));
        accounts.add(new Account("Accounts Payable", "LIABILITY"));
        accounts.add(new Account("Owner's Capital", "EQUITY"));
        accounts.add(new Account("Sales Revenue", "INCOME"));
        accounts.add(new Account("Salaries Expense", "EXPENSE"));
    }

    private JPanel createTransactionPanel() {
        JPanel panel = new JPanel(new GridLayout(6, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 100, 20, 100));

        JTextField dateField = new JTextField();
        JTextField descField = new JTextField();
        JComboBox<Account> debitBox = new JComboBox<>(accounts.toArray(new Account[0]));
        JComboBox<Account> creditBox = new JComboBox<>(accounts.toArray(new Account[0]));
        JTextField amountField = new JTextField();

        JButton addBtn = new JButton("Add Transaction");

        addBtn.addActionListener(e -> {
            try {
                String date = dateField.getText();
                String desc = descField.getText();
                Account debit = (Account) debitBox.getSelectedItem();
                Account credit = (Account) creditBox.getSelectedItem();
                double amount = Double.parseDouble(amountField.getText());

                Transaction t = new Transaction(date, desc, debit, credit, amount);
                transactions.add(t);

                debit.addBalance(amount);
                credit.addBalance(-amount);

                JOptionPane.showMessageDialog(this, "Transaction added successfully!");
                updateTables();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        });

        panel.add(new JLabel("Date (YYYY-MM-DD):"));
        panel.add(dateField);
        panel.add(new JLabel("Description:"));
        panel.add(descField);
        panel.add(new JLabel("Debit Account:"));
        panel.add(debitBox);
        panel.add(new JLabel("Credit Account:"));
        panel.add(creditBox);
        panel.add(new JLabel("Amount:"));
        panel.add(amountField);
        panel.add(addBtn);

        return panel;
    }

    private JPanel createTransactionsListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] cols = {"Date", "Description", "Debit Account", "Credit Account", "Amount"};
        transactionModel = new DefaultTableModel(cols, 0);
        transactionTable = new JTable(transactionModel);
        panel.add(new JScrollPane(transactionTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAccountsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        String[] cols = {"Account Name", "Type", "Balance"};
        accountModel = new DefaultTableModel(cols, 0);
        accountTable = new JTable(accountModel);
        panel.add(new JScrollPane(accountTable), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createBalanceSheetPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea text = new JTextArea();
        JButton showBtn = new JButton("Generate Balance Sheet");
        showBtn.addActionListener(e -> {
            StringBuilder sb = new StringBuilder("Balance Sheet\n\nAssets:\n");
            double totalAssets = 0, totalLiabilities = 0, totalEquity = 0;
            for (Account a : accounts) {
                if (a.getType().equals("ASSET")) {
                    sb.append(a.getName()).append(": ").append(a.getBalance()).append("\n");
                    totalAssets += a.getBalance();
                }
            }
            sb.append("\nLiabilities & Equity:\n");
            for (Account a : accounts) {
                if (a.getType().equals("LIABILITY")) {
                    sb.append(a.getName()).append(": ").append(a.getBalance()).append("\n");
                    totalLiabilities += a.getBalance();
                } else if (a.getType().equals("EQUITY")) {
                    sb.append(a.getName()).append(": ").append(a.getBalance()).append("\n");
                    totalEquity += a.getBalance();
                }
            }
            sb.append("\nTotal Assets: ").append(totalAssets);
            sb.append("\nTotal Liabilities & Equity: ").append(totalLiabilities + totalEquity);
            text.setText(sb.toString());
        });
        panel.add(showBtn, BorderLayout.NORTH);
        panel.add(new JScrollPane(text), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createJournalPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea text = new JTextArea();
        JButton btn = new JButton("Show Journal");
        btn.addActionListener(e -> {
            StringBuilder sb = new StringBuilder("General Journal Entries:\n\n");
            for (Transaction t : transactions) {
                sb.append(t.getDate()).append(" - ").append(t.getDescription()).append("\n");
                sb.append("   DR: ").append(t.getDebit().getName()).append(" ").append(t.getAmount()).append("\n");
                sb.append("   CR: ").append(t.getCredit().getName()).append(" ").append(t.getAmount()).append("\n\n");
            }
            text.setText(sb.toString());
        });
        panel.add(btn, BorderLayout.NORTH);
        panel.add(new JScrollPane(text), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createLedgerPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea text = new JTextArea();
        JButton btn = new JButton("Show General Ledger");
        btn.addActionListener(e -> {
            StringBuilder sb = new StringBuilder("General Ledger\n\n");
            for (Account a : accounts) {
                sb.append(a.getName()).append(" (").append(a.getType()).append(")\n");
                for (Transaction t : transactions) {
                    if (t.getDebit() == a) {
                        sb.append("  ").append(t.getDate()).append(" DR ").append(t.getAmount()).append("\n");
                    } else if (t.getCredit() == a) {
                        sb.append("  ").append(t.getDate()).append(" CR ").append(t.getAmount()).append("\n");
                    }
                }
                sb.append("  Balance: ").append(a.getBalance()).append("\n\n");
            }
            text.setText(sb.toString());
        });
        panel.add(btn, BorderLayout.NORTH);
        panel.add(new JScrollPane(text), BorderLayout.CENTER);
        return panel;
    }

    private void updateTables() {
        transactionModel.setRowCount(0);
        for (Transaction t : transactions) {
            transactionModel.addRow(new Object[]{
                    t.getDate(),
                    t.getDescription(),
                    t.getDebit().getName(),
                    t.getCredit().getName(),
                    t.getAmount()
            });
        }

        accountModel.setRowCount(0);
        for (Account a : accounts) {
            accountModel.addRow(new Object[]{
                    a.getName(),
                    a.getType(),
                    a.getBalance()
            });
        }
    }
}





// AnimalTest.java
// Demonstrates OOP Concepts: Encapsulation, Inheritance, and Polymorphism

// Superclass
class Animal {
    // Encapsulated attributes
    private String name;
    private int age;

    // Constructor
    public Animal(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // Getter methods (Encapsulation)
    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    // Method to display basic information
    public void displayInfo() {
        System.out.println("Name: " + name);
        System.out.println("Age: " + age + " years old");
    }

    // Method meant to be overridden (Polymorphism)
    public void move() {
        System.out.println("This animal moves in some way.");
    }
}

// Subclass: Dog
class Dog extends Animal {
    public Dog(String name, int age) {
        super(name, age);
    }

    // Overriding move() method
    @Override
    public void move() {
        System.out.println(getName() + " runs on four legs.");
    }
}

// Subclass: Fish
class Fish extends Animal {
    public Fish(String name, int age) {
        super(name, age);
    }

    // Overriding move() method
    @Override
    public void move() {
        System.out.println(getName() + " swims in water.");
    }
}

// Main application
public class AnimalTest {
    public static void main(String[] args) {
        // Create subclass objects
        Animal dog = new Dog("Buddy", 3);
        Animal fish = new Fish("Goldie", 1);

        // Store them in an array (Polymorphism)
        Animal[] animals = { dog, fish };

        // Iterate and display info + movement
        for (Animal a : animals) {
            a.displayInfo();
            a.move();
            System.out.println("----------------------");
        }
    }
}






