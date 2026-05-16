package nsysu.bank.gui;

import nsysu.bank.account.BasicAccount;
import nsysu.bank.account.CanWithdraw;
import nsysu.bank.account.ForeignAccount;
import nsysu.bank.role.User;
import nsysu.util.enumtype.AccountType;
import nsysu.util.enumtype.StatusType;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserDashboardFrame extends JFrame {
    private User user;
    private JFrame loginFrame;
    private DefaultTableModel tableModel;
    private JTable accountTable;

    public UserDashboardFrame(User user, JFrame loginFrame) {
        this.user = user;
        this.loginFrame = loginFrame;

        setTitle("User Dashboard - " + user.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 500);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        initUI();
        refreshTableData();
    }

    private void initUI() {
        // Top Panel: User info and Logout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel welcomeLabel = new JLabel("Welcome, " + user.getName() + " (ID: " + user.getId() + ")");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(welcomeLabel, BorderLayout.WEST);

        JPanel topRightPanel = new JPanel(new FlowLayout());
        JButton refreshBtn = new JButton("Refresh");
        JButton logoutBtn = new JButton("Logout");
        topRightPanel.add(refreshBtn);
        topRightPanel.add(logoutBtn);
        topPanel.add(topRightPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Center Panel: Account Table
        String[] columns = {"Account ID", "Type", "Status", "Balance", "Notes"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 不允許直接編輯表格
            }
        };
        accountTable = new JTable(tableModel);
        accountTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(accountTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("My Accounts"));
        add(scrollPane, BorderLayout.CENTER);

        // Right Panel: Action Buttons
        JPanel actionPanel = new JPanel(new GridLayout(7, 1, 10, 10));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        JButton openAccountBtn = new JButton("Open New Account");
        JButton depositBtn = new JButton("Deposit");
        JButton withdrawBtn = new JButton("Withdraw");
        JButton transferBtn = new JButton("Transfer");
        JButton historyBtn = new JButton("View History");
        JButton closeAccountBtn = new JButton("Close Account");

        actionPanel.add(openAccountBtn);
        actionPanel.add(depositBtn);
        actionPanel.add(withdrawBtn);
        actionPanel.add(transferBtn);
        actionPanel.add(historyBtn);
        actionPanel.add(new JLabel("")); // Spacer
        actionPanel.add(closeAccountBtn);

        add(actionPanel, BorderLayout.EAST);

        // Event Listeners
        logoutBtn.addActionListener(e -> logout());
        refreshBtn.addActionListener(e -> refreshData());
        
        openAccountBtn.addActionListener(e -> openNewAccount());
        depositBtn.addActionListener(e -> handleDeposit());
        withdrawBtn.addActionListener(e -> handleWithdraw());
        transferBtn.addActionListener(e -> handleTransfer());
        historyBtn.addActionListener(e -> viewHistory());
        closeAccountBtn.addActionListener(e -> closeSelectedAccount());
    }

    private void refreshData() {
        try {
            user.refresh();
            refreshTableData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "重新載入失敗：" + ex.getMessage());
        }
    }

    private void refreshTableData() {
        tableModel.setRowCount(0); // 清空表格
        List<BasicAccount> accounts = user.getAccounts();
        for (BasicAccount acc : accounts) {
            // 過濾掉已經關閉的帳戶
            if (acc.getStatus().equals(StatusType.Closed.getStr())) {
                continue;
            }
            
            String notes = "";
            if (acc instanceof ForeignAccount) {
                ForeignAccount fa = (ForeignAccount) acc;
                notes = String.format("Rate: %.2f | TWD Eqv: %.2f", fa.getExchangeRate(), fa.getTwdEquivalentBalance());
            }
            
            tableModel.addRow(new Object[]{
                    acc.getId(),
                    acc.getType(),
                    acc.getStatus(),
                    String.format("%.3f", acc.getBalance()),
                    notes
            });
        }
    }

    private BasicAccount getSelectedAccount() {
        int row = accountTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "請先在表格中選擇一個帳戶！");
            return null;
        }
        String accountId = (String) tableModel.getValueAt(row, 0);
        for (BasicAccount acc : user.getAccounts()) {
            if (acc.getId().equals(accountId)) {
                return acc;
            }
        }
        return null;
    }

    private void openNewAccount() {
        String[] options = {
                AccountType.SavingsAccount.getStr(),
                AccountType.CheckingAccount.getStr(),
                AccountType.TimeDeposit.getStr(),
                AccountType.USDAccount.getStr()
        };
        String choice = (String) JOptionPane.showInputDialog(
                this,
                "請選擇要開立的帳戶類型：",
                "開立新帳戶",
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );

        if (choice != null) {
            AccountType type = AccountType.valueOf(choice); // Wait, valueOf matches the enum name, but choice is the string value!
            // We need to find the enum by string value
            AccountType selectedEnum = null;
            for (AccountType t : AccountType.values()) {
                if (t.getStr().equals(choice)) {
                    selectedEnum = t;
                    break;
                }
            }
            if (selectedEnum != null) {
                String newId = user.addAccount(selectedEnum);
                JOptionPane.showMessageDialog(this, "開戶成功！新帳號為：" + newId);
                refreshData();
            }
        }
    }

    private void handleDeposit() {
        BasicAccount acc = getSelectedAccount();
        if (acc == null) return;

        String input = JOptionPane.showInputDialog(this, "請輸入存款金額：\n(外幣帳戶請輸入台幣，將自動換匯)");
        if (input == null || input.trim().isEmpty()) return;

        try {
            double amount = Double.parseDouble(input);
            boolean success = acc.deposit(amount);
            if (success) {
                JOptionPane.showMessageDialog(this, "存款成功！");
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "存款失敗！(可能輸入負數或帳戶已被凍結)", "錯誤", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "請輸入有效的數字！", "錯誤", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleWithdraw() {
        BasicAccount acc = getSelectedAccount();
        if (acc == null) return;

        if (!(acc instanceof CanWithdraw)) {
            JOptionPane.showMessageDialog(this, "此類型帳戶（例如定存）不支援提款功能！", "錯誤", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String input = JOptionPane.showInputDialog(this, "請輸入提款金額：\n(外幣帳戶請輸入台幣，將自動扣除等值美金)");
        if (input == null || input.trim().isEmpty()) return;

        try {
            double amount = Double.parseDouble(input);
            boolean success = ((CanWithdraw) acc).withdraw(amount);
            if (success) {
                JOptionPane.showMessageDialog(this, "提款成功！");
                refreshData();
            } else {
                JOptionPane.showMessageDialog(this, "提款失敗！(餘額不足、輸入負數或帳戶已被凍結)", "錯誤", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "請輸入有效的數字！", "錯誤", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleTransfer() {
        BasicAccount acc = getSelectedAccount();
        if (acc == null) return;

        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        JTextField toIdField = new JTextField();
        JTextField amountField = new JTextField();
        panel.add(new JLabel("目標帳號 (To ID):"));
        panel.add(toIdField);
        panel.add(new JLabel("轉帳金額 (Amount):"));
        panel.add(amountField);

        int result = JOptionPane.showConfirmDialog(this, panel, "發起轉帳", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String toId = toIdField.getText().trim();
            String amountStr = amountField.getText().trim();
            if (toId.isEmpty() || amountStr.isEmpty()) return;

            try {
                double amount = Double.parseDouble(amountStr);
                String desc = JOptionPane.showInputDialog(this, "請輸入轉帳備註 (可留空):");
                if (desc == null) desc = "transfer";

                // 呼叫 User.transferMoney (包含了 3 萬元外部轉帳凍結檢查)
                boolean success = user.transferMoney(acc.getId(), toId, amount, desc);
                if (success) {
                    JOptionPane.showMessageDialog(this, "轉帳成功！");
                } else {
                    // 如果被凍結了，可以判斷狀態
                    acc.refresh();
                    if (acc.getStatus().equals(StatusType.Frozen.getStr())) {
                        JOptionPane.showMessageDialog(this, 
                            "【警告】外部轉帳金額超過 30,000 元！\n為了您的資金安全，此帳戶已暫時被凍結，轉帳失敗。\n請聯絡管理員解凍！", 
                            "帳戶凍結", JOptionPane.ERROR_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "轉帳失敗！請檢查餘額、目標帳號是否正確，且不能為負數。", "錯誤", JOptionPane.ERROR_MESSAGE);
                    }
                }
                refreshData();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "請輸入有效的數字金額！", "錯誤", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "轉帳發生錯誤：" + ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewHistory() {
        BasicAccount acc = getSelectedAccount();
        if (acc == null) return;
        
        acc.refresh(); // 確保拿到最新歷史紀錄
        new HistoryFrame(acc).setVisible(true);
    }

    private void closeSelectedAccount() {
        BasicAccount acc = getSelectedAccount();
        if (acc == null) return;

        int confirm = JOptionPane.showConfirmDialog(this, 
            "確定要關閉帳戶 [" + acc.getId() + "] 嗎？\n關閉後將無法再進行任何交易！", 
            "確認關閉", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            
        if (confirm == JOptionPane.YES_OPTION) {
            user.closeAccount(acc.getId());
            JOptionPane.showMessageDialog(this, "帳戶已成功關閉。");
            refreshData();
        }
    }

    private void logout() {
        this.dispose();
        loginFrame.setVisible(true);
    }
}
