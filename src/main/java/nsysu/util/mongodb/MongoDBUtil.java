package nsysu.util.mongodb;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import nsysu.util.enumtype.*;
import nsysu.util.exception.IdNotFindException;
import nsysu.util.exception.SameUserNameException;
import nsysu.util.exception.TargetNotFindException;
import nsysu.resources.ApplicationProperties;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

/**
 * 第一層：MongoDB 工具類別
 * 負責與 MongoDB 資料庫的所有底層操作（CRUD）
 * 使用 final class + private constructor，確保無法被繼承或實例化（工具類別模式）
 */
public final class MongoDBUtil {
    private static final MongoCollection<Document> accountsCollection;
    private static final MongoCollection<Document> usersCollection;
    private static final Random random = new Random();

    /** 用來區分要操作的是 users 還是 accounts 集合 */
    public enum CollectionType {
        USERS, ACCOUNTS
    }

    // 靜態初始化區塊：類別載入時自動連線 MongoDB，整個程式生命週期只執行一次
    static {
        String uri = ApplicationProperties.mongodbURL;
        MongoClient mongoClient = MongoClients.create(uri);
        accountsCollection = mongoClient.getDatabase(ApplicationProperties.Database)
                .getCollection(ApplicationProperties.Accounts);
        usersCollection = mongoClient.getDatabase(ApplicationProperties.Database)
                .getCollection(ApplicationProperties.Users);
    }

    private MongoDBUtil() {
    } // 私有建構子，防止外部 new MongoDBUtil()

    /** 根據 CollectionType 回傳對應的 MongoDB 集合 */
    private static MongoCollection<Document> getCollection(CollectionType type) {
        if (Objects.requireNonNull(type) == CollectionType.ACCOUNTS) {
            return accountsCollection;
        }
        return usersCollection;
    }

    /**
     * 泛型查詢方法：根據 id 和 target（欄位名稱）從資料庫取得資料
     * 
     * @param CC     回傳型別的 Class（例如 String.class、Double.class），用於型別轉換
     * @param target 使用 asTarget 介面，讓 AccountTarget 和 UserTarget 都能傳入（多型）
     */
    public static <T> T getData(CollectionType type, String id, asTarget target, Class<T> CC)
            throws IdNotFindException, TargetNotFindException {
        Document doc = getCollection(type).find(Filters.eq("id", id)).first();
        if (doc == null) {
            throw new IdNotFindException(); // 找不到此 id 的文件
        } else if (!doc.containsKey(target.getStr())) {
            throw new TargetNotFindException(); // 文件中沒有此欄位
        } else {
            return doc.get(target.getStr(), CC); // 取得欄位值並轉型為 T
        }
    }

    /** 設定（覆蓋）指定欄位的值 */
    public static <T> void setData(CollectionType type, String id, asTarget target, T value) throws IdNotFindException {
        UpdateResult result = getCollection(type).updateOne(Filters.eq("id", id), Updates.set(target.getStr(), value));
        if (result.getMatchedCount() == 0) {
            throw new IdNotFindException();
        }
    }

    /**
     * 數值遞增方法：使用 MongoDB 的 $inc 操作，將欄位值加上 value（可為負數）
     * 泛型限制 T extends Number，確保只接受數字型別
     */
    public static <T extends Number> void updateData(CollectionType type, String id, asTarget target, T value)
            throws IdNotFindException {
        UpdateResult result = getCollection(type).updateOne(Filters.eq("id", id), Updates.inc(target.getStr(), value));
        if (result.getMatchedCount() == 0) {
            throw new IdNotFindException();
        }
    }

    /** 將元素推入陣列欄位（例如：將帳戶 ID 加入使用者的 accounts 陣列） */
    public static <T> void addToList(CollectionType type, String id, asTarget target, T value)
            throws IdNotFindException {
        MongoCollection<Document> col = getCollection(type);

        UpdateResult result = col.updateOne(Filters.eq("id", id), Updates.push(target.getStr(), value));
        if (result.getMatchedCount() == 0) {
            throw new IdNotFindException();
        }
    }

    /**
     * 產生唯一 ID：前綴 + 隨機五位數（10000~99999）
     * 使用 do-while 迴圈確保不會與資料庫中的現有 ID 重複
     */
    private static String genUniqueId(MongoCollection<Document> collection, String name) {
        String newId;
        do {
            newId = name + (random.nextInt(90000) + 10000);
        } while (collection.countDocuments(Filters.eq("id", newId)) > 0);
        return newId;
    }

    /** 新增使用者：檢查使用者名稱是否重複，建立文件並插入 users 集合 */
    public static String addNewUser(String username, String password, RoleType role) throws SameUserNameException {
        // 檢查同名使用者是否已存在
        if (usersCollection.countDocuments(Filters.eq(UserTarget.UserName.getStr(), username)) > 0) {
            throw new SameUserNameException();
        }
        String newId = genUniqueId(usersCollection, "U"); // 使用者 ID 以 "U" 開頭
        // 建構 MongoDB Document（類似 JSON 物件）
        Document newUser = new Document("id", newId)
                .append(UserTarget.UserName.getStr(), username)
                .append(UserTarget.Password.getStr(), password)
                .append(UserTarget.Role.getStr(), role.getStr())
                .append(UserTarget.Status.getStr(), StatusType.Active.getStr())
                .append(UserTarget.Accounts.getStr(), new ArrayList<String>()); // 初始帳戶列表為空
        usersCollection.insertOne(newUser);
        return newId;
    }

    public static boolean deleteData(CollectionType type, String id) {
        DeleteResult result = getCollection(type).deleteOne(Filters.eq("id", id));
        return result.getDeletedCount() > 0;
    }

    /** 新增帳戶：建立帳戶文件，並將帳戶 ID 加入使用者的 accounts 陣列 */
    public static String addNewAccount(String userId, AccountType type) {
        String newId = genUniqueId(accountsCollection, ""); // 帳戶 ID 無前綴，純數字
        Document newAccount = new Document("id", newId)
                .append(AccountTarget.Balance.getStr(), 0D) // 初始餘額為 0
                .append(AccountTarget.TimeOfLastView.getStr(), new Date()) // 記錄建立時間（用於利息計算）
                .append(AccountTarget.Type.getStr(), type.getStr()) // 帳戶類型
                .append(AccountTarget.Status.getStr(), StatusType.Active.getStr())
                .append(AccountTarget.History.getStr(), new ArrayList<Document>()); // 交易紀錄為空
        accountsCollection.insertOne(newAccount);
        addToList(CollectionType.USERS, userId, UserTarget.Accounts, newId); // 將帳戶綁定到使用者
        return newId;
    }

    /** 新增一筆交易紀錄到帳戶的 history 陣列中 */
    public static void addNewHistory(String accountId, double amount, String anotherId, String description) {
        Document newHistory = new Document("date", new Date())
                .append("amount", amount) // 正數=收入，負數=支出
                .append("anotherId", anotherId) // 交易對象的帳戶 ID
                .append("description", description);
        MongoDBUtil.addToList(CollectionType.ACCOUNTS, accountId, AccountTarget.History, newHistory);
    }
}
