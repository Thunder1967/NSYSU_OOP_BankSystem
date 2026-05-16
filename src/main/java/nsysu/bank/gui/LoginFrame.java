package nsysu.bank.gui;

import nsysu.bank.role.Administrator;
import nsysu.bank.role.User;
import nsysu.util.enumtype.RoleType;
import nsysu.util.enumtype.StatusType;
import nsysu.util.exception.IdNotFindException;
import nsysu.util.sqlaccess.UserData;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField idField;
    private JPasswordField passwordField;

    public LoginFrame() {
        setTitle("NSYSU Bank System - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 200);
        setLocationRelativeTo(null); // 置中顯示
        setLayout(new BorderLayout());

        // 標題區
        JLabel titleLabel = new JLabel("NSYSU Bank System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        // 輸入區
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        inputPanel.add(new JLabel("User ID:"));
        idField = new JTextField();
        inputPanel.add(idField);

        inputPanel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        inputPanel.add(passwordField);

        add(inputPanel, BorderLayout.CENTER);

        // 按鈕區
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton loginButton = new JButton("Login");
        JButton exitButton = new JButton("Exit");

        buttonPanel.add(loginButton);
        buttonPanel.add(exitButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // 事件綁定
        exitButton.addActionListener(e -> System.exit(0));
        loginButton.addActionListener(e -> attemptLogin());
    }

    private void attemptLogin() {
        String userId = idField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (userId.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "請輸入帳號與密碼！", "錯誤", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            // 驗證狀態
            String status = UserData.getStatus(userId);
            if (StatusType.Closed.getStr().equals(status)) {
                JOptionPane.showMessageDialog(this, "此帳號已被註銷或停用！", "拒絕登入", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 驗證密碼
            String realPassword = UserData.getPassword(userId);
            if (!password.equals(realPassword)) {
                JOptionPane.showMessageDialog(this, "密碼錯誤！", "錯誤", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // 判斷權限並開啟對應視窗
            String role = UserData.getRole(userId);
            if (RoleType.Administrator.getStr().equals(role)) {
                Administrator admin = new Administrator(userId);
                new AdminDashboardFrame(admin, this).setVisible(true);
            } else {
                User user = new User(userId);
                new UserDashboardFrame(user, this).setVisible(true);
            }
            
            // 隱藏登入視窗
            this.setVisible(false);
            idField.setText("");
            passwordField.setText("");

        } catch (IdNotFindException ex) {
            JOptionPane.showMessageDialog(this, "找不到此帳號，請確認 ID 是否正確！", "錯誤", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "系統發生未預期錯誤：\n" + ex.getMessage(), "錯誤", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
