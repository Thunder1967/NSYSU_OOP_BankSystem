package nsysu.bank.gui;

import nsysu.bank.role.Administrator;
import nsysu.util.enumtype.RoleType;

import javax.swing.*;
import java.awt.*;

public class AdminDashboardFrame extends JFrame {
    private Administrator admin;
    private JFrame loginFrame;

    public AdminDashboardFrame(Administrator admin, JFrame loginFrame) {
        this.admin = admin;
        this.loginFrame = loginFrame;

        setTitle("Administrator Dashboard - " + admin.getName());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        initUI();
    }

    private void initUI() {
        // Top Panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel welcomeLabel = new JLabel("System Admin: " + admin.getName() + " (" + admin.getId() + ")");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(welcomeLabel, BorderLayout.WEST);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> logout());
        topPanel.add(logoutBtn, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);

        // Center Panel (Action Buttons)
        JPanel actionPanel = new JPanel(new GridLayout(3, 1, 15, 15));
        actionPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        JButton addUserBtn = new JButton("Add New User");
        JButton deactivateUserBtn = new JButton("Deactivate User");
        JButton unfreezeAccountBtn = new JButton("Unfreeze Account");

        actionPanel.add(addUserBtn);
        actionPanel.add(deactivateUserBtn);
        actionPanel.add(unfreezeAccountBtn);

        add(actionPanel, BorderLayout.CENTER);

        // Event Listeners
        addUserBtn.addActionListener(e -> handleAddUser());
        deactivateUserBtn.addActionListener(e -> handleDeactivateUser());
        unfreezeAccountBtn.addActionListener(e -> handleUnfreezeAccount());
    }

    private void handleAddUser() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 5, 5));
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{
                RoleType.User.getStr(),
                RoleType.Administrator.getStr()
        });

        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(new JLabel("Role:"));
        panel.add(roleCombo);

        int result = JOptionPane.showConfirmDialog(this, panel, "Create New User", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword());
            String roleStr = (String) roleCombo.getSelectedItem();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "請填寫完整資訊！", "錯誤", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                RoleType role = roleStr.equals(RoleType.Administrator.getStr()) ? RoleType.Administrator : RoleType.User;
                String newId = admin.addNewUser(username, password, role);
                JOptionPane.showMessageDialog(this, "使用者建立成功！\n配發的 User ID 為： " + newId + "\n請妥善保管。");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "建立失敗：" + ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleDeactivateUser() {
        String userId = JOptionPane.showInputDialog(this, "請輸入要停用的 User ID：\n(警告：此動作將關閉該使用者所有帳戶！)");
        if (userId != null && !userId.trim().isEmpty()) {
            int confirm = JOptionPane.showConfirmDialog(this, "確定要停用 [" + userId + "] 嗎？", "確認", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    admin.deactivateUser(userId.trim());
                    JOptionPane.showMessageDialog(this, "使用者 [" + userId + "] 已成功停用，其名下所有帳戶已關閉。");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "停用失敗：" + ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void handleUnfreezeAccount() {
        String accountId = JOptionPane.showInputDialog(this, "請輸入要解凍的 Account ID：");
        if (accountId != null && !accountId.trim().isEmpty()) {
            try {
                admin.unfreezeAccount(accountId.trim());
                JOptionPane.showMessageDialog(this, "帳戶 [" + accountId + "] 已成功解凍！");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "解凍失敗：" + ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void logout() {
        this.dispose();
        loginFrame.setVisible(true);
    }
}
