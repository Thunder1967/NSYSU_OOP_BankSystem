package nsysu.bank.gui;

import nsysu.bank.HistoryRecord;
import nsysu.bank.account.BasicAccount;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class HistoryFrame extends JFrame {
    private BasicAccount account;

    public HistoryFrame(BasicAccount account) {
        this.account = account;

        setTitle("Transaction History - Account: " + account.getId());
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(700, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        initUI();
    }

    private void initUI() {
        JLabel titleLabel = new JLabel("History for " + account.getType() + " (" + account.getId() + ")", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        add(titleLabel, BorderLayout.NORTH);

        String[] columns = {"Date", "Type", "Amount", "Target Account", "Description"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<HistoryRecord> history = account.getHistory();

        for (HistoryRecord record : history) {
            String dateStr = sdf.format(record.getDate());
            double amount = record.getAmount();
            String typeStr = amount > 0 ? "IN (+)" : "OUT (-)";
            String targetAcc = (record.getAnotherId() == null || record.getAnotherId().isEmpty()) ? "-" : record.getAnotherId();
            
            model.addRow(new Object[]{
                    dateStr,
                    typeStr,
                    String.format("%.2f", Math.abs(amount)),
                    targetAcc,
                    record.getDescription()
            });
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        JButton closeBtn = new JButton("Close");
        closeBtn.addActionListener(e -> this.dispose());
        bottomPanel.add(closeBtn);
        add(bottomPanel, BorderLayout.SOUTH);
    }
}
