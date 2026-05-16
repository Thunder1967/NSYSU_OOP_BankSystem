package nsysu.bank.gui;

import javax.swing.*;

/**
 * 銀行系統圖形化介面進入點
 */
public class BankSystem {
    public static void main(String[] args) {
        // 設定 Swing 的外觀為系統預設（會比預設的 Java 介面好看）
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 使用 EventDispatchThread 確保 GUI 執行緒安全
        SwingUtilities.invokeLater(() -> {
            try {
                // 啟動登入視窗
                LoginFrame loginFrame = new LoginFrame();
                loginFrame.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, 
                        "系統啟動失敗，請檢查資料庫連線：\n" + e.getMessage(), 
                        "錯誤", 
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
