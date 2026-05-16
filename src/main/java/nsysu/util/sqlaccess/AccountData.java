package nsysu.util.sqlaccess;

import nsysu.util.enumtype.AccountTarget;
import nsysu.util.enumtype.StatusType;
import nsysu.util.exception.IdNotFindException;
import nsysu.util.exception.NegativeBalanceException;
import nsysu.util.mongodb.MongoDBUtil;
import nsysu.bank.HistoryRecord;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * 第二層：帳戶資料存取類別
 * 封裝 MongoDBUtil，提供帳戶相關的高階查詢方法
 * 使用 AccountTarget enum 作為欄位 key，確保編輯器能驗證欄位名稱是否正確
 */
public final class AccountData {
    private AccountData(){}
    public static String getType(String id) throws IdNotFindException{
        return MongoDBUtil.getData(MongoDBUtil.CollectionType.ACCOUNTS,id, AccountTarget.Type, String.class);
    }
    public static double getBalance(String id) throws IdNotFindException{
        return MongoDBUtil.getData(MongoDBUtil.CollectionType.ACCOUNTS,id, AccountTarget.Balance, Double.class);
    }
    public static Date getLastView(String id) throws IdNotFindException{
        return MongoDBUtil.getData(MongoDBUtil.CollectionType.ACCOUNTS,id, AccountTarget.TimeOfLastView, Date.class);
    }
    /**
     * 取得帳戶的交易歷史紀錄
     * 從 MongoDB 取出 Document 列表，逐筆轉換為 HistoryRecord 物件
     * 排序後回傳（最新的在前）
     */
    public static List<HistoryRecord> getHistory(String id) throws IdNotFindException{
        List<Document> history = MongoDBUtil.getData(MongoDBUtil.CollectionType.ACCOUNTS,id, AccountTarget.History, List.class);
        List<HistoryRecord> record = new ArrayList<>();
        for(Document doc:history){
            record.add(new HistoryRecord(doc,id));
        }
        Collections.sort(record);
        return record;
    }
    public static String getStatus(String id) throws IdNotFindException{
        return MongoDBUtil.getData(MongoDBUtil.CollectionType.ACCOUNTS,id, AccountTarget.Status, String.class);
    }

    /**
     * 增減帳戶餘額（value 可正可負）
     * 先檢查更新後是否會變成負數，若是則拋出例外（防止透支）
     */
    public static void incBalance(String id,double value) throws IdNotFindException,NegativeBalanceException{
        if(AccountData.getBalance(id)+value<0){
            throw new NegativeBalanceException();
        }
        MongoDBUtil.updateData(MongoDBUtil.CollectionType.ACCOUNTS,id, AccountTarget.Balance, value);
    }
    public static void setLastView(String id) throws IdNotFindException{
        MongoDBUtil.setData(MongoDBUtil.CollectionType.ACCOUNTS,id, AccountTarget.TimeOfLastView, new Date());
    }
    public static void addOneHistory(String id, double amount, String anotherId, String description) throws IdNotFindException{
        MongoDBUtil.addNewHistory(id,amount,anotherId,description);
    }
    public static void setStatus(String id, StatusType type) throws IdNotFindException{
        MongoDBUtil.setData(MongoDBUtil.CollectionType.ACCOUNTS,id, AccountTarget.Status, type.getStr());
    }
    /** 檢查帳戶是否可進行交易（只有 Active 狀態的帳戶才可以） */
    public static boolean transferable(String id){
        return AccountData.getStatus(id).equals(StatusType.Active.getStr());
    }
}
