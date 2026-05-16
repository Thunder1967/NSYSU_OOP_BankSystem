package nsysu.test;

import nsysu.util.enumtype.RoleType;
import nsysu.util.sqlaccess.UserData;

/**
 * 系統初始化腳本
 * 用於在空資料庫中建立第一位「超級管理員」
 */
public class InitSystem {
    public static void main(String[] args) {
        try {
            System.out.println("正在初始化資料庫...");
            
            // 建立一組預設的管理員帳號
            String adminId = UserData.addNewUser("RootAdmin", "admin123", RoleType.Administrator);
            
            System.out.println("===================================");
            System.out.println("✅ 成功建立初始管理員！");
            System.out.println("👉 登入 ID : " + adminId);
            System.out.println("👉 登入密碼: admin123");
            System.out.println("===================================");
            System.out.println("請使用上方配發的 ID 去執行 BankSystem 進行登入！");
            
        } catch (Exception e) {
            System.err.println("建立失敗，可能該使用者已存在或其他連線問題：");
            e.printStackTrace();
        }
    }
}
