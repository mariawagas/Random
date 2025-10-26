package ACCTG;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;

public class GeneralJournal extends JPanel {
    private JTable journalTable;
    private DefaultTableModel journalModel;

    public GeneralJournal() {
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        // ===== HEADER BAR =====
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(140, 70, 180)));

        JLabel title = new JLabel("General Journal");
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(new Color(40, 40, 40));
        title.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 10));
        headerPanel.add(title, BorderLayout.WEST);

        add(headerPanel, BorderLayout.NORTH);

        // ===== MAIN CONTENT =====
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel subtitle = new JLabel("Chronological debit/credit entries");
        subtitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        subtitle.setForeground(new Color(60, 60, 60));
        subtitle.setBorder(BorderFactory.createEmptyBorder(0, 5, 8, 0));
        contentPanel.add(subtitle, BorderLayout.NORTH);

        // ===== TABLE SETUP =====
        String[] columns = {"Date", "Description", "Account", "Debit", "Credit"};
        journalModel = new DefaultTableModel(columns, 0);
        journalTable = createStyledTable(journalModel);

        JScrollPane scrollPane = new JScrollPane(journalTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        contentPanel.add(scrollPane, BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);
    }

    // ===== TABLE DESIGN =====
    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
                Component c = super.prepareRenderer(renderer, row, col);
                if (c instanceof JComponent jc) {
                    jc.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(180, 140, 220)));
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
        header.setBackground(new Color(140, 70, 180));
        header.setForeground(Color.WHITE);
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setPreferredSize(new Dimension(header.getWidth(), 32));

        return table;
    }
}

