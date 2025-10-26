package ACCTG;

public class Transaction {
    private String date, description, debitAccount, creditAccount;
    private double amount;

    public Transaction(String date, String description, String debitAccount, String creditAccount, double amount) {
        this.date = date;
        this.description = description;
        this.debitAccount = debitAccount;
        this.creditAccount = creditAccount;
        this.amount = amount;
    }

    public String getDate() { return date; }
    public String getDescription() { return description; }
    public String getDebitAccount() { return debitAccount; }
    public String getCreditAccount() { return creditAccount; }
    public double getAmount() { return amount; }
}
