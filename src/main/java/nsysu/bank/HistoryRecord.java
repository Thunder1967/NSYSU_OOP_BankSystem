package nsysu.bank;

import org.bson.Document;

import java.util.Date;

public class HistoryRecord implements Comparable<HistoryRecord>{
    private final Date date;
    private final int amount;
    private final String accountId;
    private final String anotherId;
    private final String description;

    public HistoryRecord(Document doc,String accountId) {
        this.date = doc.getDate("date");
        this.amount = doc.getInteger("amount");
        this.anotherId = doc.getString("anotherId");
        this.description = doc.getString("description");
        this.accountId = accountId;
    }

    public Date getDate() {
        return date;
    }

    public int getAmount() {
        return amount;
    }

    public String getAnotherId() {
        return anotherId;
    }

    public String getDescription() {
        return description;
    }

    public String getAccountId() {
        return accountId;
    }

    @Override
    public int compareTo(HistoryRecord other) {
        return other.date.compareTo(this.date);
    }

    @Override
    public String toString() {
        if(amount>0){
            return String.format("[%s] %d (from %s to %s) - %s", date, amount, anotherId, accountId, description);
        }
        else{
            return String.format("[%s] %d (from %s to %s) - %s", date, amount, accountId, anotherId, description);
        }
    }
}
